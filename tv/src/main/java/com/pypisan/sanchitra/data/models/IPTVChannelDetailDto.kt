package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.entities.IPTVChannelDetail


data class IPTVChannelDetailDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("categories")
    val categories: String,

    @SerializedName("is_web")
    val isWeb: Boolean,

    @SerializedName("is_app")
    val isApp: Boolean,

    @SerializedName("logo")
    val logo: String,

    @SerializedName("stream_url")
    val streamUrl: String,

    @SerializedName("network")
    val network: String,

    @SerializedName("language")
    val language: String,

    @SerializedName("quality")
    val quality: String,

    @SerializedName("is_drm")
    val isDrm: Boolean,

    @SerializedName("license_key")
    val licenseKey: String? = null,

    @SerializedName("license_url")
    val licenseUrl: String? = null,

    @SerializedName("config")
    val config: IPTVChannelConfigDTO? = null
)

data class IPTVChannelConfigDTO(
    @SerializedName("epg_id")
    val epgId: String? = null
)

fun IPTVChannelDetailDto.toDomain(): IPTVChannelDetail {
    return IPTVChannelDetail(
        id = id,
        name = name,
        country = country,
        categories = categories,
        isWeb = isWeb,
        isApp = isApp,
        logo = logo,
        streamUrl = streamUrl,
        network = network,
        language = language,
        quality = quality,
        isDrm = isDrm,
        licenseKey = licenseKey,
        licenseUrl = licenseUrl,
        epgId = config?.epgId
    )
}