package com.example.suzukiconnect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.suzukiconnect.presentation.theme.AccentBlue
import com.example.suzukiconnect.presentation.theme.BottomBarBg

enum class BottomTab(val icon: ImageVector, val label: String) {
    HOME(Icons.Filled.Home, "Home"),
    VEHICLE_HEALTH(Icons.Filled.DirectionsCar, "Vehicle Health"),
    TRIPS(Icons.Filled.AltRoute, "Trips"),
    SERVICES(Icons.Filled.CarRepair, "Services"),
    NOTIFICATIONS(Icons.Filled.Notifications, "Notifications")
}

@Composable
fun SuzukiBottomBar(
    selectedTab: BottomTab,
    onTabClick: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BottomBarBg)
            .navigationBarsPadding()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomTab.values().forEach { tab ->
            IconButton(onClick = { onTabClick(tab) }) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = if (tab == selectedTab) AccentBlue else Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
