package com.example.sanchitra.data.models

data class LoginStatusResponse(
    val success: Boolean,
    val message: String,
    val data: TokenData
)

data class TokenData(
    val access_token: String,
    val refresh_token: String,
    val token_type: String
)