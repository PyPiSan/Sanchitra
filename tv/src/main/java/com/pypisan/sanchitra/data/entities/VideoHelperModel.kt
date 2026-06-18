package com.pypisan.sanchitra.data.entities

import androidx.media3.common.Tracks

data class SubtitleTrack(
    val trackIndex: Int,
    val language: String?,
    val label: String?,
    val group: Tracks.Group?,
    val isSelected: Boolean = false
)

data class AudioTrack(
    val group: Tracks.Group?,
    val trackIndex: Int,
    val language: String?,
    val label: String?,
    val isSelected: Boolean = false
)

data class VideoQuality(
    val label: String?,
    val group: Tracks.Group?,
    val trackIndex: Int,
    val width: Int,
    val height: Int,
    val bitrate: Int,
    val isSelected: Boolean = false
)