package com.example.suzukiconnect.domain.model

enum class NotificationType { CAR_NOT_LOCKED, DOOR_LOCKED, DOOR_UNLOCKED, GENERAL }
data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: String
)

data class NotificationScreenData(
    val category: String,
    val vehicleName: String,
    val notifications: List<NotificationItem>
)