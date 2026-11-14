package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.entities.ChannelItem
import com.pypisan.sanchitra.data.entities.SearchResult
import com.pypisan.sanchitra.data.entities.VideoItem
import com.pypisan.sanchitra.data.entities.VideoItemMeta

data class SearchResultDTO(
    @SerializedName("channels") val channels: List<SearchResponseChannelsDTO>? = null,
    @SerializedName("videos") val videos: List<SearchResponseVideoDTO>? = null
)

data class SearchResponseChannelsDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String?,
    @SerializedName("categories") val categories: String?,
    @SerializedName("is_web") val isWeb: Boolean = false,
    @SerializedName("is_app") val isApp: Boolean = false,
    @SerializedName("logo") val logo: String?,
    @SerializedName("stream_url") val streamUrl: String?,
    @SerializedName("network") val network: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("quality") val quality: String?,
    @SerializedName("is_drm") val isDrm: Boolean = false,
    @SerializedName("license_key") val licenseKey: String?,
    @SerializedName("license_url") val licenseUrl: String?,
    @SerializedName("config") val config: ChannelConfigDTO?
)

data class ChannelConfigDTO(
    @SerializedName("epg_id") val epgId: String?
)

data class SearchResponseVideoDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int = 0,
    @SerializedName("is_app") val isApp: Boolean = false,
    @SerializedName("is_web") val isWeb: Boolean = false,
    @SerializedName("image") val image: String?,
    @SerializedName("logo") val logo: String?,
    @SerializedName("categories") val categories: List<String> = emptyList(),
    @SerializedName("languages") val languages: List<String> = emptyList(),
    @SerializedName("meta") val meta: SearchResponseVideoMetaDTO
)

data class SearchResponseVideoMetaDTO(
    @SerializedName("banner") val banner: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("trailer") val trailer: String?,
    @SerializedName("subtitle_url") val subtitleUrl: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("imdb_rating") val imdbRating: String?,
    @SerializedName("rotten_tomatoes") val rottenTomatoes: String?,
    @SerializedName("budget") val budget: String?,
    @SerializedName("revenue") val revenue: String?
)



fun SearchResultDTO.toDomain(): SearchResult {
    return SearchResult(
        channels = this.channels?.map { it.toDomain() } ?: emptyList(),
        videos = this.videos?.map { it.toDomain() } ?: emptyList()
    )
}

fun SearchResponseChannelsDTO.toDomain(): ChannelItem {
    return ChannelItem(
        id = this.id,
        name = this.name,
        country = this.country.orEmpty(),
        categories = this.categories.orEmpty(),
        isWeb = this.isWeb,
        isApp = this.isApp,
        logo = this.logo.orEmpty(),
        streamUrl = this.streamUrl.orEmpty(),
        network = this.network.orEmpty(),
        language = this.language.orEmpty(),
        quality = this.quality.orEmpty(),
        isDrm = this.isDrm,
        licenseKey = this.licenseKey,
        licenseUrl = this.licenseUrl,
        epgId = this.config?.epgId
    )
}

fun SearchResponseVideoDTO.toDomain(): VideoItem {
    return VideoItem(
        id = this.id,
        title = this.title,
        duration = this.duration,
        isApp = this.isApp,
        isWeb = this.isWeb,
        image = this.image.orEmpty(),
        logo = this.logo.orEmpty(),
        categories = this.categories,
        languages = this.languages,
        meta = this.meta.toDomain()
    )
}

fun SearchResponseVideoMetaDTO.toDomain(): VideoItemMeta {
    return VideoItemMeta(
        banner = this.banner.orEmpty(),
        description = this.description.orEmpty(),
        trailer = this.trailer.orEmpty(),
        subtitleUrl = this.subtitleUrl,
        releaseDate = this.releaseDate.orEmpty(),
        imdbRating = this.imdbRating,
        rottenTomatoes = this.rottenTomatoes,
        budget = this.budget,
        revenue = this.revenue
    )
}