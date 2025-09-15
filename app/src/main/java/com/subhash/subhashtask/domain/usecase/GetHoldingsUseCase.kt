package com.subhash.subhashtask.domain.usecase

import com.subhash.subhashtask.domain.model.Holding
import com.subhash.subhashtask.domain.model.PortfolioSummary
import com.subhash.subhashtask.domain.repository.HoldingsRepository
import javax.inject.Inject

class GetHoldingsUseCase @Inject constructor(
    private val repository: HoldingsRepository
) {
    suspend operator fun invoke(hardRefresh: Boolean = false): Result<Pair<List<Holding>, PortfolioSummary>> {
        return try {
            val holdings = repository.getHoldings(hardRefresh)
            val summary = PortfolioSummary.fromHoldings(holdings)
            Result.success(holdings to summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}