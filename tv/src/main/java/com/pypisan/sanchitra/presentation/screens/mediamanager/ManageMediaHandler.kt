package com.pypisan.sanchitra.presentation.screens.mediamanager

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class MediaActionType {
    IMPORT_URL,
    TEST_STREAM,
    MODIFY_MEDIA,
    IMPORT_LOCAL
}

data class MediaActionCardData(
    val type: MediaActionType,
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val icon: ImageVector,
    val accentColor: Color
)
