package com.example.sanchitra.data.models

data class IPTVResponseDto(
    val channels: List<IPTVChannelDto>
)

data class IPTVChannelDto(
    val id: String,
    val name: String,
    val country: String,
    val categories: List<String>,
    val streams: List<StreamDto>,
    val logo: LogoDto
)

data class StreamDto(
    val id: Int,
    val url: String,
    val quality: String,
    val label: String?,
    val feed: String,
    val is_active: Boolean,
    val is_web: Boolean,
    val is_app: Boolean
)

data class LogoDto(
    val url: String,
    val width: Int,
    val height: Int,
    val format: String
)

fun IPTVChannelDto.toDomain(): IPTVChannel {
    return IPTVChannel(
        id = id,
        name = name,
        country = country,
        categories = categories,
        streams = streams.map { it.toDomain() },
        logo = logo.toDomain()
    )
}

fun StreamDto.toDomain(): Stream {
    return Stream(
        id = id,
        url = url,
        quality = quality,
        label = label,
        feed = feed,
        isActive = is_active,
        isWeb = is_web,
        isApp = is_app
    )
}

fun LogoDto.toDomain(): Logo {
    return Logo(
        url = url,
        width = width,
        height = height,
        format = format
    )
}
