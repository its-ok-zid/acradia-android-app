package com.example.suzukiconnect.data.trips

import com.example.suzukiconnect.domain.trips.Trip

interface TripRepository {
    suspend fun getTrips(vin: String): List<Trip>
    suspend fun getTrip(vin: String, tripId: String): Trip
    suspend fun saveTrip(vin: String, request: SaveTripRequest): Trip
}

class TripRepositoryImpl(private val api: TripApi) : TripRepository {
    override suspend fun getTrips(vin: String) = api.getTrips(vin).map { it.toDomain() }
    override suspend fun getTrip(vin: String, tripId: String) = api.getTrip(vin, tripId).toDomain()
    override suspend fun saveTrip(vin: String, request: SaveTripRequest) = api.saveTrip(vin, request).toDomain()
}

private fun TripDto.toDomain() = Trip(id, vin, startAddress, endAddress, engineStartTime, engineEndTime, kmDriven, createdAt)
