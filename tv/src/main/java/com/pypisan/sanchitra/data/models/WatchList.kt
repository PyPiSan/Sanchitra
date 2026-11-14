package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class WatchList(
    @SerializedName("id") val id: String,
    @SerializedName("items") val items: List<Any>
)