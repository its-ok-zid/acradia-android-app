package com.example.suzukiconnect.presentation.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suzukiconnect.domain.model.VehicleHealth
import com.example.suzukiconnect.domain.usecase.GetVehicleHealthUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

 data class VehicleHealthUiState(
    val health: VehicleHealth? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class VehicleHealthViewModel(
    private val getVehicleHealth: GetVehicleHealthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleHealthUiState())
    val uiState: StateFlow<VehicleHealthUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val health = getVehicleHealth()
                _uiState.update { it.copy(health = health, isLoading = false, errorMessage = null) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to load vehicle health. Please try again."
                    )
                }
            }
        }
    }
}
