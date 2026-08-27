package com.pypisan.sanchitra.data.entities

import com.google.gson.annotations.SerializedName

data class AddProfileRequest(
    @SerializedName("name") val profileName: String,
    @SerializedName("picture") val profilePicture: String?
)
