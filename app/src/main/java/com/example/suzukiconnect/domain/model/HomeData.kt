package com.example.suzukiconnect.domain.model

enum class IndicatorType { AIR_CONDITIONER, HAZARD, HEADLAMP, BATTERY, DOOR_LOCK, DOOR }

data class Indicator(
    val type: IndicatorType,
    val isActive: Boolean
)

data class VehicleStatus(
    val fuelBars: Int,
    val totalFuelBars: Int,
    val rangeKm: Int,
    val odometerKm: Int,
    val indicators: List<Indicator>
)

data class HomeData(
    val lastActivity: String,
    val vehicleStatus: VehicleStatus,
    val statusMessage: String,
    val location: String
)
