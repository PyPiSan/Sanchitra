package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.entities.IPTVChannel


data class IPTVResponseDto(
    @SerializedName("channels") val channels: List<IPTVChannelDto>
)

data class IPTVChannelDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("categories") val categories: String,
    @SerializedName("is_app") val isApp: Boolean,
    @SerializedName("is_web") val isWeb: Boolean,
    @SerializedName("logo") val logo: String
)

fun IPTVChannelDto.toDomain(): IPTVChannel {
    return IPTVChannel(
        id = id,
        name = name,
        country = country,
        categories = categories,
        isApp = isApp,
        isWeb = isWeb,
        logo = logo
    )
}
