package com.example.suzukiconnect.domain.usecase

import com.example.suzukiconnect.domain.model.NotificationScreenData
import com.example.suzukiconnect.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetNotificationsUseCase(private val repository: VehicleRepository) {
    /** Emits again every time a new push notification is stored. */
    operator fun invoke(): Flow<NotificationScreenData> = repository.observeNotifications()
}