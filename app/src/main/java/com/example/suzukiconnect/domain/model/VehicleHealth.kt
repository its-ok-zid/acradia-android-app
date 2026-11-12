package com.example.suzukiconnect.domain.model

data class HealthSystem(
    val name: String,
    val status: String
)

data class VehicleHealth(
    val overallStatus: String,
    val statusDate: String,
    val systems: List<HealthSystem>
)
