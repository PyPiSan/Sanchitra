package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.VideoQuality

@Composable
fun MediaMetadataBadges(
    modifier: Modifier = Modifier,
    visible: Boolean,
    hasSubtitles: Boolean,
    audios: List<AudioTrack> = emptyList(),
    qualities: List<VideoQuality> = emptyList(),
    customBadges: List<String> = emptyList()
) {
    val isMultiAudio = remember(audios) {
        audios.mapNotNull { track ->
            track.language?.lowercase()?.trim()?.takeIf { it.isNotBlank() }
                ?: track.label?.lowercase()?.trim()?.takeIf { it.isNotBlank() }
        }
            .distinct()
            .size > 2 // > 1 means 2 or more distinct languages (e.g. English + Spanish)
    }

    // Automatically determine Video Quality Format (4K UHD, FHD 1080p, HD 720p)
    val qualityBadge = remember(qualities) {
        val selected = qualities.firstOrNull { it.isSelected && it.height > 0 }
        val targetHeight = selected?.height
            ?: qualities.filter { it.height > 0 }.maxOfOrNull { it.height }
            ?: -1

        when {
            targetHeight >= 2160 -> "4K" to "UHD"
            targetHeight >= 1080 -> "FHD" to "1080p"
            targetHeight >= 720 -> "HD" to "720p"
            targetHeight > 0 -> "SD" to "480p"
            else -> null
        }
    }

    AnimatedVisibility(
        visible = visible && (hasSubtitles || isMultiAudio || qualityBadge != null || customBadges.isNotEmpty()),
        enter = fadeIn(tween(250)) + slideInVertically(initialOffsetY = { -it / 2 }),
        exit = fadeOut(tween(250)) + slideOutVertically(targetOffsetY = { -it / 2 }),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Resolution Badge (4K UHD / FHD 1080p / HD 720p)
            qualityBadge?.let { (tag, label) ->
                DarkGlassBadgePill(
                    tag = tag,
                    label = label
                )
            }

            // 2. Subtitles Badge ("CC | SUBTITLES")
            if (hasSubtitles) {
                DarkGlassBadgePill(
                    tag = "CC",
                    label = "SUBTITLES"
                )
            }

            // 3. Multi-Audio Badge ("AUDIO | MULTI-LANG")
            if (isMultiAudio) {
                DarkGlassBadgePill(
                    tag = "AUDIO",
                    label = "MULTI-LANG"
                )
            }

            // 4. Custom Extra Badges (e.g. "HDR", "5.1")
            customBadges.forEach { extraTag ->
                DarkGlassBadgePill(tag = extraTag)
            }
        }
    }
}

@Composable
private fun DarkGlassBadgePill(
    modifier: Modifier = Modifier,
    tag: String,
    label: String? = null,
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Crisp Pure White Primary Tag
            Text(
                text = tag,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            // Divider & Soft Silver Secondary Label
            if (label != null) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(14.dp)
                        .background(Color.White.copy(alpha = 0.25f))
                )

                Text(
                    text = label,
                    color = Color(0xFFD1D1D8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}