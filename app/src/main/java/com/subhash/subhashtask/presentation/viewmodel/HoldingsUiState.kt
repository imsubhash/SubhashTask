package com.subhash.subhashtask.presentation.viewmodel

import com.subhash.subhashtask.domain.model.Holding
import com.subhash.subhashtask.domain.model.PortfolioSummary

data class HoldingsUiState(
    val isLoading: Boolean = false,
    val holdings: List<Holding> = emptyList(),
    val portfolioSummary: PortfolioSummary? = null,
    val isSummaryExpanded: Boolean = false,
    val error: String? = null
)