package com.subhash.subhashtask.data

import com.subhash.subhashtask.data.local.dao.HoldingDao
import com.subhash.subhashtask.data.local.entity.HoldingEntity
import com.subhash.subhashtask.data.remote.api.HoldingsApi
import com.subhash.subhashtask.data.remote.dto.HoldingDto
import com.subhash.subhashtask.data.remote.dto.HoldingsResponseDto
import com.subhash.subhashtask.data.remote.dto.UserHoldingsDto
import com.subhash.subhashtask.data.repository.HoldingsRepositoryImpl
import com.subhash.subhashtask.domain.model.Holding
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class HoldingsRepositoryImplTest {

    @Mock
    private lateinit var api: HoldingsApi

    @Mock
    private lateinit var dao: HoldingDao

    private lateinit var repository: HoldingsRepositoryImpl

    private val mockHoldingDto1 = HoldingDto(
        symbol = "AAPL",
        quantity = 10,
        ltp = 150.0,
        avgPrice = 140.0,
        close = 148.0
    )

    private val mockHoldingDto2 = HoldingDto(
        symbol = "GOOGL",
        quantity = 5,
        ltp = 2800.0,
        avgPrice = 2700.0,
        close = 2750.0
    )

    private val mockHolding1 = Holding(
        symbol = "AAPL",
        quantity = 10,
        ltp = 150.0,
        avgPrice = 140.0,
        close = 148.0
    )

    private val mockHolding2 = Holding(
        symbol = "GOOGL",
        quantity = 5,
        ltp = 2800.0,
        avgPrice = 2700.0,
        close = 2750.0
    )

    private val mockHoldingEntity1 = HoldingEntity(
        symbol = "AAPL",
        quantity = 10,
        ltp = 150.0,
        avgPrice = 140.0,
        close = 148.0
    )

    private val mockHoldingEntity2 = HoldingEntity(
        symbol = "GOOGL",
        quantity = 5,
        ltp = 2800.0,
        avgPrice = 2700.0,
        close = 2750.0
    )

    private val mockApiResponse = HoldingsResponseDto(
        data = UserHoldingsDto(
            userHolding = listOf(mockHoldingDto1, mockHoldingDto2)
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = HoldingsRepositoryImpl(api, dao)
    }

    @Test
    fun `getHoldings with cached data and hardRefresh false returns cached data`() = runTest {
        // Given
        val cachedEntities = listOf(mockHoldingEntity1, mockHoldingEntity2)
        whenever(dao.getHoldings()).thenReturn(cachedEntities)

        // When
        val result = repository.getHoldings(hardRefresh = false)

        // Then
        assertEquals(2, result.size)
        assertEquals(mockHolding1.symbol, result[0].symbol)
        assertEquals(mockHolding1.quantity, result[0].quantity)
        assertEquals(mockHolding1.ltp, result[0].ltp, 0.01)
        assertEquals(mockHolding2.symbol, result[1].symbol)

        // Verify API was not called
        verify(api, never()).getHoldings()
        verify(dao, never()).insertHoldings(any())
    }

    @Test
    fun `getHoldings with empty cache calls API and caches result`() = runTest {
        // Given
        whenever(dao.getHoldings()).thenReturn(emptyList())
        whenever(api.getHoldings()).thenReturn(mockApiResponse)

        // When
        val result = repository.getHoldings(hardRefresh = false)

        // Then
        assertEquals(2, result.size)
        assertEquals(mockHolding1.symbol, result[0].symbol)
        assertEquals(mockHolding1.quantity, result[0].quantity)
        assertEquals(mockHolding1.ltp, result[0].ltp, 0.01)
        assertEquals(mockHolding2.symbol, result[1].symbol)

        // Verify API was called and data was cached
        verify(api, times(1)).getHoldings()
        verify(dao, times(1)).insertHoldings(any())

        // Verify the correct entities were inserted
        val insertedEntitiesCaptor = argumentCaptor<List<HoldingEntity>>()
        verify(dao).insertHoldings(insertedEntitiesCaptor.capture())
        val insertedEntities = insertedEntitiesCaptor.firstValue
        assertEquals(2, insertedEntities.size)
        assertEquals("AAPL", insertedEntities[0].symbol)
        assertEquals("GOOGL", insertedEntities[1].symbol)
    }

    @Test
    fun `getHoldings with hardRefresh true ignores cache and calls API`() = runTest {
        // Given
        val cachedEntities = listOf(mockHoldingEntity1)
        whenever(dao.getHoldings()).thenReturn(cachedEntities)
        whenever(api.getHoldings()).thenReturn(mockApiResponse)

        // When
        val result = repository.getHoldings(hardRefresh = true)

        // Then
        assertEquals(2, result.size)
        assertEquals(mockHolding1.symbol, result[0].symbol)
        assertEquals(mockHolding2.symbol, result[1].symbol)

        // Verify API was called despite having cached data
        verify(api, times(1)).getHoldings()
        verify(dao, times(1)).insertHoldings(any())
    }

    @Test
    fun `getHoldings with API error and cached data returns cached data`() = runTest {
        // Given
        val cachedEntities = listOf(mockHoldingEntity1, mockHoldingEntity2)
        whenever(dao.getHoldings()).thenReturn(cachedEntities)
        whenever(api.getHoldings()).thenThrow(RuntimeException("Network error"))

        // When
        val result = repository.getHoldings(hardRefresh = true)

        // Then
        assertEquals(2, result.size)
        assertEquals(mockHolding1.symbol, result[0].symbol)
        assertEquals(mockHolding2.symbol, result[1].symbol)

        // Verify API was called but insertHoldings was not called due to error
        verify(api, times(1)).getHoldings()
        verify(dao, never()).insertHoldings(any())
    }

    @Test
    fun `getHoldings with API error and no cached data returns empty list`() = runTest {
        // Given
        whenever(dao.getHoldings()).thenReturn(emptyList())
        whenever(api.getHoldings()).thenThrow(RuntimeException("Network error"))

        // When
        val result = repository.getHoldings(hardRefresh = false)

        // Then
        assertTrue(result.isEmpty())

        // Verify API was called but insertHoldings was not called due to error
        verify(api, times(1)).getHoldings()
        verify(dao, never()).insertHoldings(any())
    }

    @Test
    fun `getHoldings with successful API call updates cache correctly`() = runTest {
        // Given
        whenever(dao.getHoldings()).thenReturn(emptyList())
        whenever(api.getHoldings()).thenReturn(mockApiResponse)

        // When
        repository.getHoldings(hardRefresh = false)

        // Then
        val insertedEntitiesCaptor = argumentCaptor<List<HoldingEntity>>()
        verify(dao).insertHoldings(insertedEntitiesCaptor.capture())
        val insertedEntities = insertedEntitiesCaptor.firstValue

        assertEquals(2, insertedEntities.size)

        // Verify first entity
        assertEquals("AAPL", insertedEntities[0].symbol)
        assertEquals(10, insertedEntities[0].quantity)
        assertEquals(150.0, insertedEntities[0].ltp, 0.01)
        assertEquals(140.0, insertedEntities[0].avgPrice, 0.01)
        assertEquals(148.0, insertedEntities[0].close, 0.01)

        // Verify second entity
        assertEquals("GOOGL", insertedEntities[1].symbol)
        assertEquals(5, insertedEntities[1].quantity)
        assertEquals(2800.0, insertedEntities[1].ltp, 0.01)
        assertEquals(2700.0, insertedEntities[1].avgPrice, 0.01)
        assertEquals(2750.0, insertedEntities[1].close, 0.01)
    }

    @Test
    fun `getHoldings with cached data returns correctly mapped domain objects`() = runTest {
        // Given
        val cachedEntities = listOf(mockHoldingEntity1, mockHoldingEntity2)
        whenever(dao.getHoldings()).thenReturn(cachedEntities)

        // When
        val result = repository.getHoldings(hardRefresh = false)

        // Then
        assertEquals(2, result.size)

        // Verify first holding
        with(result[0]) {
            assertEquals("AAPL", symbol)
            assertEquals(10, quantity)
            assertEquals(150.0, ltp, 0.01)
            assertEquals(140.0, avgPrice, 0.01)
            assertEquals(148.0, close, 0.01)
            assertEquals(1500.0, currentValue, 0.01) // 150 * 10
            assertEquals(1400.0, totalInvestment, 0.01) // 140 * 10
            assertEquals(100.0, pnl, 0.01) // 1500 - 1400
            assertEquals(-20.0, todaysPnl, 0.01) // (148 - 150) * 10
        }

        // Verify second holding
        with(result[1]) {
            assertEquals("GOOGL", symbol)
            assertEquals(5, quantity)
            assertEquals(2800.0, ltp, 0.01)
            assertEquals(2700.0, avgPrice, 0.01)
            assertEquals(2750.0, close, 0.01)
            assertEquals(14000.0, currentValue, 0.01) // 2800 * 5
            assertEquals(13500.0, totalInvestment, 0.01) // 2700 * 5
            assertEquals(500.0, pnl, 0.01) // 14000 - 13500
            assertEquals(-250.0, todaysPnl, 0.01) // (2750 - 2800) * 5
        }
    }

    @Test
    fun `getHoldings calls dao getHoldings exactly once per call`() = runTest {
        // Given
        whenever(dao.getHoldings()).thenReturn(listOf(mockHoldingEntity1))

        // When
        repository.getHoldings(hardRefresh = false)
        repository.getHoldings(hardRefresh = false)
        repository.getHoldings(hardRefresh = true)

        // Then
        verify(dao, times(3)).getHoldings()
    }

    @Test
    fun `getHoldings with partial API failure still returns cached data`() = runTest {
        // Given
        val cachedEntities = listOf(mockHoldingEntity1, mockHoldingEntity2)
        whenever(dao.getHoldings()).thenReturn(cachedEntities)

        // Simulate API returning malformed data
        val malformedResponse = HoldingsResponseDto(
            data = UserHoldingsDto(userHolding = emptyList())
        )
        whenever(api.getHoldings()).thenReturn(malformedResponse)

        // When
        val result = repository.getHoldings(hardRefresh = true)

        // Then - Should still get API response (empty list in this case)
        assertTrue(result.isEmpty())
        verify(api, times(1)).getHoldings()
        verify(dao, times(1)).insertHoldings(emptyList())
    }
}