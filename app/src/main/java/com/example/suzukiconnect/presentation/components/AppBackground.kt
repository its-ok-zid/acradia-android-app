package com.example.suzukiconnect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.suzukiconnect.presentation.theme.NavyBottom
import com.example.suzukiconnect.presentation.theme.NavyMid
import com.example.suzukiconnect.presentation.theme.NavyTop

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyTop, NavyMid, NavyBottom))),
        content = content
    )
}
