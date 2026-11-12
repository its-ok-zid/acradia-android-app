package com.example.suzukiconnect.domain.trips

data class Trip(
    val id: String,
    val vin: String,
    val startAddress: String,
    val endAddress: String,
    val engineStartTime: String,
    val engineEndTime: String,
    val kmDriven: Double,
    val createdAt: String? = null
)
