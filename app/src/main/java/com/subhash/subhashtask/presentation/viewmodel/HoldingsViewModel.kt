package com.subhash.subhashtask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subhash.subhashtask.domain.usecase.GetHoldingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoldingsViewModel @Inject constructor(
    private val getHoldingsUseCase: GetHoldingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HoldingsUiState())
    val uiState: StateFlow<HoldingsUiState> = _uiState

    init {
        loadHoldings()
    }

    fun loadHoldings(hardRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getHoldingsUseCase(hardRefresh).fold(
                onSuccess = { (holdings, summary) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        holdings = holdings,
                        portfolioSummary = summary,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    fun toggleSummaryExpanded() {
        _uiState.value = _uiState.value.copy(
            isSummaryExpanded = !_uiState.value.isSummaryExpanded
        )
    }
}
