package com.example.sanchitra.data.models

data class DeviceLoginInitResponse(
    val qr_url: String,
    val device_code: String,
    val success: Boolean,
    val message: String

)