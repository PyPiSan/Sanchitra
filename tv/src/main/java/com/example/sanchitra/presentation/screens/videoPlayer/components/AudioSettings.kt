package com.example.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Surface
import androidx.media3.common.Player
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.SelectableSurfaceDefaults
import com.example.sanchitra.presentation.screens.videoPlayer.getAudioTracks

@Composable
fun AudioSettings(
    player: Player,
    onLanguageSelected: (String) -> Unit
) {
    val tracks = remember { getAudioTracks(player) }

    val languages = remember(tracks) {
        tracks.map { it.language }.distinct()
    }

    var selected by remember { mutableStateOf(languages.firstOrNull()) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        languages.forEach { lang ->

            AudioLanguageIcon(
                language = lang,
                selected = selected == lang,
                onClick = {
                    selected = lang
                    onLanguageSelected(lang)
                }
            )
        }
    }
}

@Composable
fun AudioLanguageIcon(
    language: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.size(40.dp),

        shape = SelectableSurfaceDefaults.shape(shape = CircleShape),

        colors = SelectableSurfaceDefaults.colors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),

            contentColor = MaterialTheme.colorScheme.onSurface
        ),

        scale = SelectableSurfaceDefaults.scale(
            selectedScale = 1.05f,
            focusedScale = 1.1f
        ),

        interactionSource = interactionSource
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = language.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}