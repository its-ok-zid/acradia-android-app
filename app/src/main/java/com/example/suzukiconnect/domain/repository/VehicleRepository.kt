package com.example.suzukiconnect.domain.repository

import com.example.suzukiconnect.domain.model.HomeData
import com.example.suzukiconnect.domain.model.NotificationScreenData
import com.example.suzukiconnect.domain.model.VehicleHealth
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    suspend fun getHomeData(): HomeData
    suspend fun getVehicleHealth(): VehicleHealth
    fun observeNotifications(): Flow<NotificationScreenData>
}