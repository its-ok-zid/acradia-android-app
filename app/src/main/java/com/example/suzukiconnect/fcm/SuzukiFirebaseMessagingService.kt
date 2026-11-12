package com.example.suzukiconnect.fcm

import android.util.Log
import com.example.suzukiconnect.SuzukiConnectApp
import com.example.suzukiconnect.presentation.navigation.Screen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SuzukiFirebaseMessagingService : FirebaseMessagingService() {

    /** Called when FCM issues a new device token (first install, reinstall, token refresh). */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        // TODO: send the token to your backend when you have one
    }

    /**
     * Called for data-only messages in any app state, and for notification messages
     * while the app is in the FOREGROUND.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message from: ${message.from}, data: ${message.data}")

        val stored = PushPayload.toStoredNotification(
            messageId = message.messageId,
            data = message.data,
            notificationTitle = message.notification?.title,
            notificationBody = message.notification?.body
        ) ?: return

        // 1) Stack it on the Notification screen
        (applicationContext as SuzukiConnectApp).container.notificationStore.add(stored)

        // 2) Show it in the system tray
        val route = message.data[NotificationHelper.EXTRA_ROUTE] ?: Screen.Notification.route
        NotificationHelper.show(this, stored.title, stored.message, route)
    }

    private companion object {
        const val TAG = "FCM"
    }
}