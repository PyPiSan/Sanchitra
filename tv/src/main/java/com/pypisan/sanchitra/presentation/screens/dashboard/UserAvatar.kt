package com.pypisan.sanchitra.presentation.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.SelectableSurfaceDefaults
import androidx.tv.material3.Surface
import coil.compose.SubcomposeAsyncImage

@Composable
fun UserAvatar(
    selected: Boolean,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    onClick: () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = SelectableSurfaceDefaults.shape(shape = CircleShape),
        scale = SelectableSurfaceDefaults.scale(
            focusedScale = 1.25f,        // Clearly zooms in when focused
            selectedScale = 1.05f,
            focusedSelectedScale = 1.25f
        ),
        // --- 100% VISIBLE BORDERS ON ANY TV ---
        border = SelectableSurfaceDefaults.border(
            // When Focused with D-Pad: Bright Pure White 3.5dp ring
            focusedBorder = Border(
                border = BorderStroke(width = 3.5.dp, color = Color.White),
                shape = CircleShape
            ),
            // When Selected (active Profile tab): Vibrant Primary 3dp ring
            selectedBorder = Border(
                border = BorderStroke(width = 3.dp, color = MaterialTheme.colorScheme.primary),
                shape = CircleShape
            ),
            // When BOTH Selected and currently Focused: Thick White ring
            focusedSelectedBorder = Border(
                border = BorderStroke(width = 3.5.dp, color = Color.White),
                shape = CircleShape
            )
        ),
        colors = SelectableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            selectedContainerColor = Color.Transparent
        ),
        modifier = modifier
    ) {
        // Inner Island Gap: 4dp padding leaves clean black space for the ring to glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "User Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = {
                        DefaultAvatarIcon(selected = selected)
                    }
                )
            } else {
                DefaultAvatarIcon(selected = selected)
            }
        }
    }
}

@Composable
private fun DefaultAvatarIcon(selected: Boolean) {
    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "User Avatar",
        modifier = Modifier.fillMaxSize(),
        tint = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray
    )
}