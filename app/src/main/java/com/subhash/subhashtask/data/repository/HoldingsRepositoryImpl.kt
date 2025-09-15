package com.subhash.subhashtask.data.repository

import com.subhash.subhashtask.data.local.dao.HoldingDao
import com.subhash.subhashtask.data.local.entity.HoldingEntity
import com.subhash.subhashtask.data.mapper.toDomain
import com.subhash.subhashtask.data.remote.api.HoldingsApi
import com.subhash.subhashtask.domain.model.Holding
import com.subhash.subhashtask.domain.repository.HoldingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HoldingsRepositoryImpl @Inject constructor(
    private val api: HoldingsApi,
    private val dao: HoldingDao
) : HoldingsRepository {


    override suspend fun getHoldings(hardRefresh: Boolean): List<Holding> {
        val cachedHolding = dao.getHoldings()
        if (cachedHolding.isNotEmpty() && !hardRefresh) {
            return cachedHolding.map { it.toDomain() }
        }

        return try {
            val apiResponse = api.getHoldings().data.userHolding.map { it.toDomain() }
            val entities = apiResponse.map { HoldingEntity.fromDomain(it) }
            dao.insertHoldings(entities)

            apiResponse
        } catch (e: Exception) {
            cachedHolding.map { it.toDomain() }
        }
    }
}