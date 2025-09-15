package com.subhash.subhashtask.domain.usecase

import com.subhash.subhashtask.domain.model.Holding
import com.subhash.subhashtask.domain.repository.HoldingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetHoldingsUseCaseTest {

    @Mock
    private lateinit var repository: HoldingsRepository

    private lateinit var useCase: GetHoldingsUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetHoldingsUseCase(repository)
    }

    @Test
    fun `invoke returns success with holdings and summary`() = runTest {
        // Given
        val holdings = listOf(
            Holding("AAPL", 10, 150.0, 140.0, 148.0),
            Holding("GOOGL", 5, 2800.0, 2700.0, 2750.0)
        )
        whenever(repository.getHoldings()).thenReturn(holdings)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        val (returnedHoldings, summary) = result.getOrThrow()
        assertEquals(holdings, returnedHoldings)
        assertEquals(15500.0, summary.currentValue, 0.01)
    }

    @Test
    fun `invoke returns failure when repository throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        whenever(repository.getHoldings()).thenThrow(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}