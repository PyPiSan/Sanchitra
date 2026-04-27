package com.pypisan.sanchitra.data.models

import androidx.media3.common.Format

data class AudioTrackInfo(
    val language: String,
    val bitrate: Int,
    val format: Format
)
