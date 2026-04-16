package com.example.sanchitra.data.models

data class UserDetails(
    val id: String,
    val preferences: Map<String, Any>,
    val watch_history: List<Any>
)
