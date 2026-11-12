package com.example.suzukiconnect.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object VehicleHealth : Screen("vehicle_health")
    data object Notification : Screen("notification")
    data object Trips : Screen("trips")
}