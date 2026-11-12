package com.example.suzukiconnect.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suzukiconnect.data.trips.TripRepository
import com.example.suzukiconnect.domain.trips.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

 data class TripsUiState(
    val trips: List<Trip> = emptyList(),
    val filteredTrips: List<Trip> = emptyList(),
    val search: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val selectedTrip: Trip? = null,
    val detailLoading: Boolean = false
)

class TripsViewModel(private val repository: TripRepository, private val vin: String) : ViewModel() {
    private val _state = MutableStateFlow(TripsUiState(loading = true))
    val state: StateFlow<TripsUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        runCatching { repository.getTrips(vin) }
            .onSuccess { trips -> _state.value = _state.value.copy(trips = trips, filteredTrips = filter(trips, _state.value.search), loading = false) }
            .onFailure { e -> _state.value = _state.value.copy(loading = false, error = e.message ?: "Unable to load trips") }
    }

    fun onSearch(value: String) {
        _state.value = _state.value.copy(search = value, filteredTrips = filter(_state.value.trips, value))
    }

    fun openTrip(trip: Trip) = viewModelScope.launch {
        _state.value = _state.value.copy(detailLoading = true, selectedTrip = trip)
        runCatching { repository.getTrip(vin, trip.id) }
            .onSuccess { _state.value = _state.value.copy(selectedTrip = it, detailLoading = false) }
            .onFailure { _state.value = _state.value.copy(detailLoading = false, error = it.message ?: "Unable to load trip details") }
    }

    fun closeDetails() { _state.value = _state.value.copy(selectedTrip = null) }

    private fun filter(trips: List<Trip>, query: String) = if (query.isBlank()) trips else trips.filter {
        it.startAddress.contains(query, true) || it.endAddress.contains(query, true)
    }
}
