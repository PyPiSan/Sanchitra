package com.pypisan.sanchitra.data.models

data class IPTVChannel(
    val id: String,
    val name: String,
    val country: String,
    val categories: List<String>,
    val streams: List<Stream>,
    val logo: Logo
)

data class Stream(
    val id: Int,
    val url: String,
    val quality: String,
    val label: String?,
    val feed: String,
    val isActive: Boolean,
    val isWeb: Boolean,
    val isApp: Boolean
)

data class Logo(
    val url: String,
    val width: Int,
    val height: Int,
    val format: String
)