package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class DeviceLoginInitResponse(

    @SerializedName("qr_url") val loginURL: String,
    @SerializedName("device_code") val deviceCode: String,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("banner") val banner: String

)