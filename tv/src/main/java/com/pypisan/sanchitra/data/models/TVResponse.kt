package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class TVResponse(
    @SerializedName("channels")
    val channels: Map<String, List<ChannelDto>>
)