package com.pypisan.sanchitra.presentation.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProfileProgressRing(
    progress: Float,
    ringSize: Dp,
    visible: Boolean
) {

    Canvas(modifier = Modifier.size(ringSize)) {
        if (visible) {
            drawArc(
                color = Color(0xFF00bbff),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}