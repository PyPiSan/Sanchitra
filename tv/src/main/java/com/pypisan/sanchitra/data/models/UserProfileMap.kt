package com.pypisan.sanchitra.data.models

import androidx.compose.runtime.Immutable

@Immutable
data class UserProfileMap(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val icon: Int
)