package com.pypisan.sanchitra.data.models


data class IPTVChannel(
    val id: String,
    val name: String,
    val country: String,
    val categories: String,
    val isApp: Boolean,
    val isWeb: Boolean,
    val logo: String
)