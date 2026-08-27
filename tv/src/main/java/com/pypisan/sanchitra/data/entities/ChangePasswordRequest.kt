package com.pypisan.sanchitra.data.entities

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)
