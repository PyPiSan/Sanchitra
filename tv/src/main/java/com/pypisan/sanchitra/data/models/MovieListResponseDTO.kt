package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.entities.VideoMeta

data class MovieListResponseDTO(
    val videos: Map<String, List<VideoDTO>>
)

data class VideoDTO(
    val id: Int,
    val filename: String?,
    val title: String,
    val image: String,
    val logo: String?,
    val resolution: String?,
    val categories: List<String> = emptyList(),
    val duration: Int,

    @SerializedName("languages") val languages: List<String> = emptyList(),

    @SerializedName("streaming_url") val streamingUrl: String,

    @SerializedName("license_key") val licenseKey: String,

    @SerializedName("license_url") val licenseUrl: String,

    @SerializedName("download_url") val downloadUrl: String?,

    @SerializedName("is_active") val isActive: Boolean? = null,

    @SerializedName("is_web") val isWeb: Boolean? = null,

    @SerializedName("is_drm") val isDrm: Boolean? = null,

    @SerializedName("is_app") val isApp: Boolean? = null,

    val meta: MetaDto
)

data class MetaDto(
    val banner: String? = null,
    val description: String,
    val trailer: String? = null,

    @SerializedName("release_date") val releaseDate: String? = null,

    @SerializedName("imdb_rating") val imdbRating: String? = null,

    @SerializedName("rotten_tomatoes") val rottenTomatoes: String? = null,

    @SerializedName("budget") val budget: String? = null,

    @SerializedName("revenue") val revenue: String? = null,

    @SerializedName("subtitle_url") val subtitleUrl: String? = null,
)

fun VideoDTO.toDomain(): Videos {
    return Videos(
        id = id,
        url = streamingUrl,
        drm = isDrm ?: false,
        licenseKey = licenseKey,
        licenseUrl = licenseUrl,
        title = title,
        image = image,
        categories = categories,
        duration = duration,
        language = languages,
        logo = logo,
        meta = meta.toDomain(),
    )
}

fun MetaDto.toDomain(): VideoMeta {
    return VideoMeta(
        banner = banner,
        description = description,
        trailer = trailer,
        releaseDate = releaseDate,
        subtitleUrl = subtitleUrl,
        imdbRating = imdbRating,
        rottenTomatoes = rottenTomatoes,
        budget = budget,
        revenue = revenue,
    )
}