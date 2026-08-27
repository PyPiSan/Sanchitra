package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class Favorites(
    @SerializedName("id") val id: String,
    @SerializedName("item") val items: List<Any>
)