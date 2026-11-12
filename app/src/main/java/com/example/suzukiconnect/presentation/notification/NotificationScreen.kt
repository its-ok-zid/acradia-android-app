package com.example.suzukiconnect.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.suzukiconnect.domain.model.NotificationItem
import com.example.suzukiconnect.domain.model.NotificationType
import com.example.suzukiconnect.presentation.components.AppBackground
import com.example.suzukiconnect.presentation.components.BottomTab
import com.example.suzukiconnect.presentation.components.SuzukiBottomBar
import com.example.suzukiconnect.presentation.theme.AccentBlue
import com.example.suzukiconnect.presentation.theme.TextMuted

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
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
                val data = state.data
                NotificationTopBar(
                    title = data?.category.orEmpty(),
                    subtitle = data?.vehicleName.orEmpty(),
                    onBackClick = onBackClick
                )

                if (data != null && data.notifications.isEmpty()) {
                    Text(
                        text = "No notifications yet",
                        color = TextMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 52.dp)
                    )
                } else if (data != null) {
                    val lastIndex = (data.notifications.size - 1).coerceAtLeast(1)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 22.dp, end = 22.dp, top = 52.dp, bottom = 16.dp
                        )
                    ) {
                        val indexed = data.notifications.withIndex().toList()
                        items(indexed, key = { it.value.id }) { (index, item) ->
                            NotificationCard(
                                item = item,
                                cardColor = lerp(
                                    Color(0xFF182B45),
                                    Color(0xFF1A3A62),
                                    index / lastIndex.toFloat()
                                )
                            )
                        }
                    }
                }
            }

            SuzukiBottomBar(
                selectedTab = BottomTab.NOTIFICATIONS,
                onTabClick = onBottomTabClick
            )
        }
    }
}

@Composable
private fun NotificationTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
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
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White, fontSize = 11.sp)
        }
        Icon(
            Icons.Filled.Tune,
            contentDescription = "Filter",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun NotificationCard(item: NotificationItem, cardColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColor, RoundedCornerShape(12.dp))
                .padding(start = 16.dp, end = 20.dp, top = 18.dp, bottom = 27.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                when (item.type) {
                    NotificationType.DOOR_UNLOCKED -> Icons.Filled.LockOpen
                    NotificationType.GENERAL -> Icons.Filled.Notifications
                    else -> Icons.Filled.Lock
                },
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier
                    .padding(top = 7.dp)
                    .size(30.dp)
            )
            Spacer(Modifier.width(27.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = AccentBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = item.message,
                    color = TextMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        Text(
            text = item.timestamp,
            color = TextMuted,
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 29.dp)
        )
    }
}