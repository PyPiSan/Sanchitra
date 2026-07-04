package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class EPGResponseDto(
    @SerializedName("epg")
    val epg: List<EPGItemDto> = emptyList()
)

data class EPGItemDto(
    @SerializedName("channel_name")
    val channelName: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("image_url")
    val image: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("start")
    val start: String = "",

    @SerializedName("end")
    val end: String = ""
)

fun EPGResponseDto.toDomain(): EPGResponse {
    return EPGResponse(
        epg = epg.map { it.toDomain() }
    )
}

fun EPGItemDto.toDomain(): EPGItem {
    return EPGItem(
        channelName = channelName,
        name = name,
        image = image,
        description = description,
        start = start,
        end = end
    )
}
