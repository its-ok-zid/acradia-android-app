package com.example.suzukiconnect.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.suzukiconnect.R
import com.example.suzukiconnect.domain.model.Indicator
import com.example.suzukiconnect.domain.model.IndicatorType
import com.example.suzukiconnect.domain.model.VehicleStatus
import com.example.suzukiconnect.presentation.components.AppBackground
import com.example.suzukiconnect.presentation.components.BottomTab
import com.example.suzukiconnect.presentation.components.SuzukiBottomBar
import com.example.suzukiconnect.presentation.theme.AccentBlue
import com.example.suzukiconnect.presentation.theme.IconInactive
import com.example.suzukiconnect.presentation.theme.PanelBlue
import com.example.suzukiconnect.presentation.theme.PillBlue
import com.example.suzukiconnect.presentation.theme.SuccessGreen
import com.example.suzukiconnect.presentation.theme.TextMuted
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onVehicleHealthClick: () -> Unit,
    onBottomTabClick: (BottomTab) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    launch { snackbarHostState.showSnackbar(event.message) }
                }
            }
        }
    }

    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                SuzukiBottomBar(
                    selectedTab = BottomTab.HOME,
                    onTabClick = onBottomTabClick
                )
            }
        ) { padding ->
            val data = state.homeData
            if (data != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .statusBarsPadding()
                ) {
                    HomeTopBar(lastActivity = data.lastActivity)
                    Spacer(Modifier.height(16.dp))
                    StatusCard(
                        status = data.vehicleStatus,
                        modifier = Modifier.padding(horizontal = 26.dp)
                    )
                    Spacer(Modifier.height(19.dp))
                    Text(
                        text = data.statusMessage,
                        color = TextMuted,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.car_image),
                            contentDescription = "Grand Vitara",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth(0.78f)
                        )
                    }
                    LocationBar(location = data.location)
                    Spacer(Modifier.height(40.dp))
                    ActionPanel(
                        isAcOn = state.isAcOn,
                        isDoorLocked = state.isDoorLocked,
                        onVehicleHealthClick = onVehicleHealthClick,
                        onAcClick = viewModel::onAcClick,
                        onLockCancelClick = viewModel::onLockCancelClick
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(lastActivity: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFF6B7380))
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Icon(
            Icons.Filled.Settings,
            contentDescription = "Settings",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun StatusCard(status: VehicleStatus, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
            .padding(horizontal = 30.dp, vertical = 26.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fuel", color = TextMuted, fontSize = 14.sp)
                    FuelBars(filled = status.fuelBars, total = status.totalFuelBars)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("Range", color = TextMuted, fontSize = 14.sp)
                    ValueWithUnit(value = status.rangeKm.toString(), unit = "km")
                }
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.4f))
            )
            Column(modifier = Modifier.weight(0.9f)) {
                Text("Odometer", color = TextMuted, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                ValueWithUnit(value = status.odometerKm.toString(), unit = "km")
            }
        }
        Spacer(Modifier.height(15.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            status.indicators.forEach { IndicatorIcon(it) }
        }
    }
}

@Composable
private fun FuelBars(filled: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (index < filled) SuccessGreen else IconInactive.copy(alpha = 0.4f))
            )
        }
    }
}

@Composable
private fun ValueWithUnit(value: String, unit: String) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(value, color = TextMuted, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(" $unit", color = TextMuted, fontSize = 14.sp, modifier = Modifier.padding(bottom = 2.dp))
    }
}

@Composable
private fun IndicatorIcon(indicator: Indicator) {
    val icon: ImageVector = when (indicator.type) {
        IndicatorType.AIR_CONDITIONER -> Icons.Filled.Air
        IndicatorType.HAZARD -> Icons.Filled.Warning
        IndicatorType.HEADLAMP -> Icons.Filled.Lightbulb
        IndicatorType.BATTERY -> Icons.Filled.BatteryFull
        IndicatorType.DOOR_LOCK -> Icons.Filled.Lock
        IndicatorType.DOOR -> Icons.Filled.DirectionsCar
    }
    Icon(
        imageVector = icon,
        contentDescription = indicator.type.name,
        tint = if (indicator.isActive) SuccessGreen else IconInactive,
        modifier = Modifier.size(26.dp)
    )
}

@Composable
private fun LocationBar(location: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0B2247).copy(alpha = 0.85f))
            .padding(start = 26.dp, end = 26.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.LocationOn,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = location,
            color = TextMuted,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Filled.Share,
            contentDescription = "Share location",
            tint = AccentBlue,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun ActionPanel(
    isAcOn: Boolean,
    isDoorLocked: Boolean,
    onVehicleHealthClick: () -> Unit,
    onAcClick: () -> Unit,
    onLockCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(108.dp)
                .height(17.dp)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(PanelBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PanelBlue)
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
        ) {
            ActionPill("Vehicle Health", Icons.Filled.FactCheck, false, onVehicleHealthClick)
            //ActionPill("A/C", Icons.Filled.Air, isAcOn, onAcClick)
            ActionPill(
                "Lock/ Unlock",
                if (isDoorLocked) Icons.Filled.Lock else Icons.Filled.LockOpen,
                isDoorLocked,
                onLockCancelClick
            )
        }
    }
}

@Composable
private fun ActionPill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) AccentBlue.copy(alpha = 0.55f) else PillBlue,
        modifier = Modifier
            .width(108.dp)
            .height(60.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(2.dp))
            Text(label, color = Color.White, fontSize = 12.sp, maxLines = 1)
        }
    }
}