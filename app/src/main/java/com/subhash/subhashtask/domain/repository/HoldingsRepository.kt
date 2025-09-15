package com.subhash.subhashtask.domain.repository

import com.subhash.subhashtask.domain.model.Holding

interface HoldingsRepository {
    suspend fun getHoldings(hardRefresh: Boolean = false): List<Holding>
}