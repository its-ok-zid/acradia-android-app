package com.example.suzukiconnect.presentation.trips

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.suzukiconnect.domain.trips.Trip
import com.example.suzukiconnect.presentation.components.BottomTab
import com.example.suzukiconnect.presentation.components.SuzukiBottomBar
import com.example.suzukiconnect.presentation.theme.*
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(viewModel: TripsViewModel, onBottomTabClick: (BottomTab) -> Unit, onBackClick: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    AppTripsBackground {
        Scaffold(containerColor = Color.Transparent, bottomBar = {
            SuzukiBottomBar(selectedTab = BottomTab.TRIPS, onTabClick = onBottomTabClick)
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 22.dp)) {
                Spacer(Modifier.height(16.dp))
                Text("Trips", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("VIN - ${BuildVin.display}", color = Color.White, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Trip History", "Driving Analysis", "Leaderboard").forEachIndexed { index, label ->
                        Column(Modifier.weight(1f).clickable { selectedTab = index }.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(label, color = if (selectedTab == index) AccentBlue else TextMuted, fontSize = 15.sp, maxLines = 1)
                            Spacer(Modifier.height(12.dp))
                            Box(Modifier.fillMaxWidth().height(if (selectedTab == index) 3.dp else 1.dp).background(if (selectedTab == index) AccentBlue else TextMuted.copy(alpha = .25f)))
                        }
                    }
                }
                if (selectedTab == 0) {
                    OutlinedTextField(value = state.search, onValueChange = viewModel::onSearch, modifier = Modifier.fillMaxWidth().padding(top = 18.dp), placeholder = { Text("Search by start or end address", color = Color.Gray) }, leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.DarkGray) }, singleLine = true, shape = RoundedCornerShape(18.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedTextColor = Color.DarkGray, unfocusedTextColor = Color.DarkGray, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent))
                    when {
                        state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentBlue) }
                        state.error != null && state.trips.isEmpty() -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(state.error ?: "Unable to load trips", color = Color.White); TextButton(onClick = viewModel::refresh) { Text("Retry") } }
                        else -> LazyColumn(contentPadding = PaddingValues(top = 18.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            items(state.filteredTrips, key = { it.id }) { trip -> TripCard(trip, Modifier.fillMaxWidth().clickable { viewModel.openTrip(trip) }) }
                        }
                    }
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(if (selectedTab == 1) "Driving Analysis" else "Leaderboard", color = Color.White, fontSize = 20.sp) }
                }
            }
        }
    }
    state.selectedTrip?.let { trip ->
        ModalBottomSheet(onDismissRequest = viewModel::closeDetails, containerColor = CardBlue) {
            Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Trip Details", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                if (state.detailLoading) CircularProgressIndicator(color = AccentBlue)
                DetailLine("Trip ID", trip.id); DetailLine("VIN", trip.vin); DetailLine("Start address", trip.startAddress); DetailLine("End address", trip.endAddress); DetailLine("Engine start time", trip.engineStartTime); DetailLine("Engine end time", trip.engineEndTime); DetailLine("Distance", "${formatKm(trip.kmDriven)} km"); DetailLine("Created at", trip.createdAt ?: "—")
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable private fun AppTripsBackground(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(PanelBlue)) { content() }
}

@Composable private fun TripCard(trip: Trip, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text("${formatDate(trip.engineStartTime)} | ${formatTime(trip.engineStartTime)} - ${formatTime(trip.engineEndTime)}", color = TextMuted, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(245.dp)) {
            DummyTripMap(Modifier.fillMaxSize())
            Row(Modifier.align(Alignment.TopEnd).background(Color(0xFF89919C), RoundedCornerShape(topEnd = 22.dp, bottomStart = 12.dp)).padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${formatKm(trip.kmDriven)} km  |  ${tripDuration(trip.engineStartTime, trip.engineEndTime)}", color = Color.White, fontSize = 17.sp)
            }
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp).background(Color(0xE83A3F49), RoundedCornerShape(18.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(13.dp).background(AccentBlue, CircleShape)); Spacer(Modifier.width(12.dp)); Text(trip.startAddress, color = Color.White, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(13.dp).background(Color(0xFFF05A3A), CircleShape)); Spacer(Modifier.width(12.dp)); Text(trip.endAddress, color = Color.White, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            }
        }
    }
}

@Composable private fun DummyTripMap(modifier: Modifier = Modifier) {
    Canvas(modifier.background(Color(0xFFE7EBEF))) {
        val w = size.width; val h = size.height
        for (i in 1..5) {
            val x = w * i / 6f; drawLine(Color.White, Offset(x, 0f), Offset(x - 18f, h), 9f)
            val y = h * i / 6f; drawLine(Color.White, Offset(0f, y), Offset(w, y + 10f), 9f)
        }
        drawLine(Color(0xFF9CCBF0), Offset(0f, h*.68f), Offset(w, h*.57f), 22f)
        val path = androidx.compose.ui.graphics.Path().apply { moveTo(w*.22f,h*.38f); lineTo(w*.37f,h*.38f); lineTo(w*.43f,h*.58f); lineTo(w*.70f,h*.58f); lineTo(w*.82f,h*.78f) }
        drawPath(path, Color(0xFF20499C), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 7f))
        drawCircle(AccentBlue, 10f, Offset(w*.22f,h*.38f)); drawCircle(Color(0xFFF05A3A), 10f, Offset(w*.82f,h*.78f))
    }
}

@Composable private fun DetailLine(label: String, value: String) { Column { Text(label, color = TextMuted, fontSize = 12.sp); Text(value, color = Color.White, fontSize = 15.sp) } }

private fun parseTime(value: String): OffsetDateTime? = runCatching { OffsetDateTime.parse(value) }.getOrNull()
private fun formatDate(value: String): String = parseTime(value)?.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)) ?: value
private fun formatTime(value: String): String = parseTime(value)?.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)) ?: value
private fun tripDuration(start: String, end: String): String {
    val a = parseTime(start); val b = parseTime(end); if (a == null || b == null) return "—"
    val mins = Duration.between(a, b).toMinutes().coerceAtLeast(0); return if (mins >= 60) "${mins/60}h ${mins%60}m" else "${mins}m"
}
private fun formatKm(km: Double) = String.format(Locale.US, "%.2f", km)
private object BuildVin { const val display = "MA1A..8901" }
