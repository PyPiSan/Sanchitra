package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("success")val success: Boolean,
    @SerializedName("message")val message: String,
)


data class LanguageResponse(
    @SerializedName("success")val success: Boolean,
    @SerializedName("data")val data: List<String>,
)