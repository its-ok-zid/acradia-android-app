package com.example.suzukiconnect

import android.app.Application
import android.util.Log
import com.example.suzukiconnect.di.AppContainer
import com.example.suzukiconnect.fcm.NotificationHelper
import com.google.firebase.messaging.FirebaseMessaging

class SuzukiConnectApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Device token: ${task.result}")
            } else {
                Log.w("FCM", "Fetching FCM token failed", task.exception)
            }
        }
    }
}