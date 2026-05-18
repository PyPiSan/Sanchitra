package com.pypisan.sanchitra.data.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class UserProfileMap(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val icon: ImageVector? = null
)