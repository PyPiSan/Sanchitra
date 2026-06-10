package com.pypisan.sanchitra.data.models

data class UserDetails(
    val id: String,
    val preferences: Map<String, List<String>>,
    val watch_history: List<Any>
)
