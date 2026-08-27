package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class IPTVCategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
)