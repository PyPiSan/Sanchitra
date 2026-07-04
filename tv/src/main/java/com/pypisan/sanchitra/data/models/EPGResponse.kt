package com.pypisan.sanchitra.data.models

data class EPGResponse(
    val epg: List<EPGItem> = emptyList()
)

data class EPGItem(
    val channelName: String,
    val name: String,
    val image: String,
    val description: String,
    val start: String,
    val end: String
)
