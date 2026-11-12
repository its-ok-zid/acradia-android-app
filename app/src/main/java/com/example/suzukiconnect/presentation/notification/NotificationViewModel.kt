package com.example.suzukiconnect.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suzukiconnect.domain.model.NotificationScreenData
import com.example.suzukiconnect.domain.usecase.GetNotificationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationUiState(val data: NotificationScreenData? = null)

class NotificationViewModel(
    private val getNotifications: GetNotificationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        // Keeps collecting, so a push received while this screen is open appears at the top right away
        viewModelScope.launch {
            getNotifications().collect { data ->
                _uiState.update { it.copy(data = data) }
            }
        }
    }
}