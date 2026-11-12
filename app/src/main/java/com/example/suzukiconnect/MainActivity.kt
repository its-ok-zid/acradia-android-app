package com.example.suzukiconnect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.suzukiconnect.fcm.NotificationHelper
import com.example.suzukiconnect.presentation.navigation.AppNavHost
import com.example.suzukiconnect.presentation.navigation.Screen
import com.example.suzukiconnect.presentation.theme.SuzukiConnectTheme
import com.example.suzukiconnect.fcm.PushPayload

class MainActivity : ComponentActivity() {

    private var pendingRoute by mutableStateOf<String?>(null)

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* result not needed */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        requestNotificationPermission()

        if (savedInstanceState == null) {
            saveSystemTrayPush(intent)
            pendingRoute = routeFrom(intent)
        }

        val container = (application as SuzukiConnectApp).container
        setContent {
            SuzukiConnectTheme {
                AppNavHost(
                    container = container,
                    pendingRoute = pendingRoute,
                    onPendingRouteHandled = { pendingRoute = null }
                )
            }
        }
    }

    // App already open and a notification is tapped (launchMode = singleTop)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        saveSystemTrayPush(intent)
        pendingRoute = routeFrom(intent)
    }

    private fun routeFrom(intent: Intent?): String? {
        intent ?: return null
        // 1) Our own notifications and FCM "custom data" key/values arrive as intent extras
        intent.getStringExtra(NotificationHelper.EXTRA_ROUTE)?.let { return it }
        // 2) A system-shown FCM notification tapped without a route -> open Notifications
        if (intent.hasExtra("google.message_id")) return Screen.Notification.route
        return null
    }

    /**
     * If the app was in the background and the backend sent a *notification* message, Android shows it
     * itself and onMessageReceived() never runs. Its data keys arrive here as extras when the user taps it,
     * so we save it now. (Duplicates are ignored by the store.)
     */
    private fun saveSystemTrayPush(intent: Intent?) {
        val extras = intent?.extras ?: return
        if (!extras.containsKey("google.message_id")) return
        val data = extras.keySet()
            .filterNot { it.startsWith("google.") || it.startsWith("gcm.") || it == "from" || it == "collapse_key" }
            .mapNotNull { key -> extras.getString(key)?.let { key to it } }
            .toMap()
        val stored = PushPayload.toStoredNotification(extras.getString("google.message_id"), data) ?: return
        (application as SuzukiConnectApp).container.notificationStore.add(stored)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}