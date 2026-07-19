package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.entities.IPTVChannel


data class IPTVResponseDto(
    val channels: List<IPTVChannelDto>
)

data class IPTVChannelDto(
    val id: String,
    val name: String,
    val country: String,
    val categories: String,
    @SerializedName("is_app") val isApp: Boolean,
    @SerializedName("is_web") val isWeb: Boolean,
    val logo: String
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
