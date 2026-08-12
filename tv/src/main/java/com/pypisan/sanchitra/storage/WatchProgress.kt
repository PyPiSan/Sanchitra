package com.pypisan.sanchitra.storage

data class WatchProgress(
    val id: String,
    val timeMillis: Long,
    val durationMillis: Long = 0L
) {
    // Helper to calculate progress fraction (0.0 to 1.0)
    val progressFraction: Float
        get() = if (durationMillis > 0) {
            (timeMillis.toFloat() / durationMillis.toFloat()).coerceIn(0f, 1f)
        } else 0f
}