package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class ChannelDto(
    val id: Int?,
    val name: String?,
    val category: String?,

    @SerializedName("logo_url")
    val logoUrl: String?,

    @SerializedName("banner_url")
    val bannerUrl: String?,

    @SerializedName("stream_url")
    val streamUrl: String?,

    val language: String?,

    @SerializedName("is_drm")
    val isDrm: Boolean?,

    @SerializedName("license_key")
    val licenseKey: String?
)

fun ChannelDto.toDomain(): Channel {
    return Channel(
        id = id ?: 0,
        name = name ?: "",
        category = category ?: "Others",
        logoUrl = logoUrl,
        bannerUrl = bannerUrl,
        streamUrl = streamUrl ?: "",
        language = language ?: "",
        isDrm = isDrm ?: false,
        licenseKey = licenseKey
    )
}
