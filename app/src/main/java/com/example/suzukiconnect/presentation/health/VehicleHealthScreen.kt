package com.example.suzukiconnect.presentation.health

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.suzukiconnect.presentation.components.AppBackground
import com.example.suzukiconnect.presentation.components.BottomTab
import com.example.suzukiconnect.presentation.components.SuzukiBottomBar
import com.example.suzukiconnect.presentation.theme.TextMuted

@Composable
fun VehicleHealthScreen(
    viewModel: VehicleHealthViewModel,
    onBackClick: () -> Unit,
    onBottomTabClick: (BottomTab) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .statusBarsPadding()
            ) {
                HealthTopBar(onBackClick)

                val health = state.health
                when {
                    state.isLoading && health == null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    state.errorMessage != null && health == null -> {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(state.errorMessage ?: "Unable to load vehicle health", color = Color.White)
                            Button(onClick = viewModel::refresh) { Text("Retry") }
                        }
                    }
                }
                if (health != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 22.dp, end = 22.dp, top = 30.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(health.overallStatus, color = TextMuted, fontSize = 20.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(health.statusDate, color = TextMuted, fontSize = 12.sp)
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.Help,
                            contentDescription = "Help",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(end = 14.dp)
                                .size(24.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        val lastIndex = (health.systems.size - 1).coerceAtLeast(1)
                        health.systems.forEachIndexed { index, system ->
                            val cardColor = lerp(
                                Color(0xFF182B45),
                                Color(0xFF1A3A62),
                                index / lastIndex.toFloat()
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(cardColor, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 21.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = system.name,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = system.status,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            SuzukiBottomBar(
                selectedTab = BottomTab.VEHICLE_HEALTH,
                onTabClick = onBottomTabClick
            )
        }
    }
}

@Composable
private fun HealthTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(onClick = onBackClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("Back", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = "Vehicle Health",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
        Icon(
            Icons.Filled.Headset,
            contentDescription = "Support",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.CenterEnd)
        )
    }
}