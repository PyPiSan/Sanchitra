package com.pypisan.sanchitra.data.entities

import com.google.gson.annotations.SerializedName

data class UpdatePreferencesRequest(
    @SerializedName("preference_type")
    val preferenceType: String,

    @SerializedName("preferences")
    val preferences: List<String>
)