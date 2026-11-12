package com.example.suzukiconnect.fcm

import com.example.suzukiconnect.data.notifications.StoredNotification
import com.example.suzukiconnect.domain.model.NotificationType
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Turns an FCM message into a [StoredNotification].
 *
 * Expected data keys from the backend (all strings):
 *   command     DOOR_LOCK / DOOR_UNLOCK
 *   commandId   cmd-a1234567
 *   status      SUCCESS
 *   executedAt  2026-11-04T10:30:05+05:30
 *   title, body (optional – defaults are used for door commands)
 *   route       (optional – screen to open on tap, defaults to "notification")
 */
object PushPayload {

    fun toStoredNotification(
        messageId: String?,
        data: Map<String, String>,
        notificationTitle: String? = null,
        notificationBody: String? = null
    ): StoredNotification? {
        val command = data["command"]
        val type = when (command) {
            "DOOR_LOCK" -> NotificationType.DOOR_LOCKED
            "DOOR_UNLOCK" -> NotificationType.DOOR_UNLOCKED
            else -> NotificationType.GENERAL
        }
        val title = notificationTitle ?: data["title"] ?: defaultTitle(type) ?: return null
        val body = notificationBody ?: data["body"] ?: defaultBody(type) ?: return null
        val receivedAt = data["executedAt"]
            ?.let { runCatching { OffsetDateTime.parse(it).toInstant().toEpochMilli() }.getOrNull() }
            ?: System.currentTimeMillis()

        return StoredNotification(
            // commandId first, so the same command is never stacked twice
            id = data["commandId"] ?: messageId ?: UUID.randomUUID().toString(),
            type = type.name,
            title = title,
            message = body,
            receivedAtMillis = receivedAt
        )
    }

    private fun defaultTitle(type: NotificationType) = when (type) {
        NotificationType.DOOR_LOCKED -> "Door Locked"
        NotificationType.DOOR_UNLOCKED -> "Door Unlocked"
        else -> null
    }

    private fun defaultBody(type: NotificationType) = when (type) {
        NotificationType.DOOR_LOCKED -> "Door(s) of your car were locked remotely."
        NotificationType.DOOR_UNLOCKED -> "Door(s) of your car were unlocked remotely."
        else -> null
    }
}