package com.pypisan.sanchitra.data.entities

data class SearchResult(
    val channels: List<ChannelItem> = emptyList(),
    val videos: List<VideoItem> = emptyList()
)

data class ChannelItem(
    val id: Int,
    val name: String,
    val country: String,
    val categories: String,
    val isWeb: Boolean,
    val isApp: Boolean,
    val logo: String,
    val streamUrl: String,
    val network: String,
    val language: String,
    val quality: String,
    val isDrm: Boolean,
    val licenseKey: String?,
    val licenseUrl: String?,
    val epgId: String?
)

data class VideoItem(
    val id: Int,
    val title: String,
    val duration: Int,
    val isApp: Boolean,
    val isWeb: Boolean,
    val image: String,
    val logo: String,
    val categories: List<String>,
    val languages: List<String>,
    val meta: VideoItemMeta
)

data class VideoItemMeta(
    val banner: String,
    val description: String,
    val trailer: String?,
    val releaseDate: String?,
    val subtitleUrl: String?,
    val imdbRating: String?,
    val rottenTomatoes: String?,
    val budget: String?,
    val revenue: String?,
)
