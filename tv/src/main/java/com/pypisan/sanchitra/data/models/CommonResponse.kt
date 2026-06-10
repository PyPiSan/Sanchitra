package com.pypisan.sanchitra.data.models

data class CommonResponse(
    val success: Boolean,
    val message: String,
)


data class LanguageResponse(
    val success: Boolean,
    val data: List<String>,
)