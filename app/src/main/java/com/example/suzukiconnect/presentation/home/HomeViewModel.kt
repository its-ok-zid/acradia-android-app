package com.example.suzukiconnect.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suzukiconnect.data.commands.RemoteCommandRepository
import com.example.suzukiconnect.domain.model.HomeData
import com.example.suzukiconnect.domain.model.RemoteCommand
import com.example.suzukiconnect.domain.usecase.GetHomeDataUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val homeData: HomeData? = null,
    val isAcOn: Boolean = false,
    val isDoorLocked: Boolean = false,
    val isSendingAcCommand: Boolean = false,
    val isSendingDoorCommand: Boolean = false
)

sealed interface HomeEvent {
    data class ShowMessage(val message: String) : HomeEvent
}

class HomeViewModel(
    private val getHomeData: GetHomeDataUseCase,
    private val remoteCommands: RemoteCommandRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val data = getHomeData()
            _uiState.update { it.copy(homeData = data) }
        }
    }

    /**
     * Sends CLIMATE_START or CLIMATE_STOP (API 1). Once the backend accepts it (HTTP 202)
     * the button flips and the "A/C turned ON/OFF" snackbar shows.
     */
    fun onAcClick() {
        if (_uiState.value.isSendingAcCommand) return // ignore double taps while a request is in flight
        val turnOn = !_uiState.value.isAcOn
        val command = if (turnOn) RemoteCommand.CLIMATE_START else RemoteCommand.CLIMATE_STOP

        _uiState.update { it.copy(isSendingAcCommand = true) }
        viewModelScope.launch {
            runCatching { remoteCommands.send(command) }
                .onSuccess {
                    _uiState.update { it.copy(isAcOn = turnOn, isSendingAcCommand = false) }
                    _events.trySend(HomeEvent.ShowMessage(if (turnOn) "A/C turned ON" else "A/C turned OFF"))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSendingAcCommand = false) }
                    val action = if (turnOn) "turn ON" else "turn OFF"
                    _events.trySend(
                        HomeEvent.ShowMessage("Couldn't $action the A/C. ${error.message.orEmpty()}".trim())
                    )
                }
        }
    }

    /**
     * Sends DOOR_LOCK or DOOR_UNLOCK (API 1). Once the backend accepts it (HTTP 202) the button flips
     * and the snackbar shows. The backend then sends a push notification when the car confirms SUCCESS.
     */
    fun onLockCancelClick() {
        if (_uiState.value.isSendingDoorCommand) return // ignore double taps while a request is in flight
        val lockDoor = !_uiState.value.isDoorLocked
        val command = if (lockDoor) RemoteCommand.DOOR_LOCK else RemoteCommand.DOOR_UNLOCK

        _uiState.update { it.copy(isSendingDoorCommand = true) }
        viewModelScope.launch {
            runCatching { remoteCommands.send(command) }
                .onSuccess {
                    _uiState.update { it.copy(isDoorLocked = lockDoor, isSendingDoorCommand = false) }
                    _events.trySend(HomeEvent.ShowMessage(if (lockDoor) "Door locked" else "Door unlocked"))
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSendingDoorCommand = false) }
                    val action = if (lockDoor) "lock" else "unlock"
                    _events.trySend(
                        HomeEvent.ShowMessage("Couldn't $action the door. ${error.message.orEmpty()}".trim())
                    )
                }
        }
    }
}