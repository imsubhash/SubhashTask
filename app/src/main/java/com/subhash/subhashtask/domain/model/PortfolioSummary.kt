package com.subhash.subhashtask.domain.model

data class PortfolioSummary(
    val currentValue: Double,
    val totalInvestment: Double,
    val totalPnl: Double,
    val todaysPnl: Double
) {
    companion object {
        fun fromHoldings(holdings: List<Holding>): PortfolioSummary {
            return PortfolioSummary(
                currentValue = holdings.sumOf { it.currentValue },
                totalInvestment = holdings.sumOf { it.totalInvestment },
                totalPnl = holdings.sumOf { it.pnl },
                todaysPnl = holdings.sumOf { it.todaysPnl }
            )
        }
    }
}
