package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class UserDetails(
    val id: String,
    val preferences: Map<String, List<String>>?,
    @SerializedName("watch_history")
    val watchHistory: List<Any>
)
