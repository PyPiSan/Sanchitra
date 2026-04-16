package com.example.sanchitra.data.models

data class UserProfileMap(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val icon: Int
)