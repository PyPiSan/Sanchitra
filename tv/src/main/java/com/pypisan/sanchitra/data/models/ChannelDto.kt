package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.entities.Channel

data class ChannelDto(
    val id: Int?, val name: String?, val category: String?, val description: String?,

    @SerializedName("logo_url") val logoUrl: String?,

    @SerializedName("banner_url") val bannerUrl: String?,

    @SerializedName("stream_url") val streamUrl: String?,

    val language: String?,

    @SerializedName("is_drm") val isDrm: Boolean?,

    @SerializedName("is_premium") val isPremium: Boolean?,

    @SerializedName("is_internal") val isInternal: Boolean?,

    @SerializedName("license_key") val licenseKey: String?,

    @SerializedName("license_url") val licenseUrl: String?
)

fun ChannelDto.toDomain(): Channel {
    return Channel(
        id = id ?: 0,
        name = name ?: "",
        description = description ?: "",
        category = category ?: "Others",
        logoUrl = logoUrl,
        bannerUrl = bannerUrl,
        streamUrl = streamUrl ?: "",
        language = language ?: "",
        isDrm = isDrm ?: false,
        licenseKey = licenseKey,
        licenseUrl = licenseUrl,
        isPremium = isPremium ?: false,
        isInternal = isInternal ?: false
    )
}
