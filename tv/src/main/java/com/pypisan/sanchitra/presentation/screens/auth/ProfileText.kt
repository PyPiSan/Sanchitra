package com.pypisan.sanchitra.presentation.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun ProfileText(name: String, isFocused: Boolean) {

    val textAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0.6f,
        animationSpec = tween(150),
        label = "textAlpha"
    )

    val textScale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1f,
        animationSpec = tween(150),
        label = "textScale"
    )

    Text(
        text = name,
        color = Color.White.copy(alpha = textAlpha),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .padding(end = 20.dp)
            .graphicsLayer {
                scaleX = textScale
                scaleY = textScale
            }
    )
}