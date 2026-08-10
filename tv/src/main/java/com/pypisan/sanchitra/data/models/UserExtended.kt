package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class UserExtended(
    @SerializedName("auth_token") val authToken: String,
    @SerializedName("j_token") val jToken: String,
    @SerializedName("sso_token") val sSOToken: String,
    @SerializedName("uniqueId") val unique: String,
    @SerializedName("subscriberId") val subscriberId: String,
    @SerializedName("drmLink") val drmLink: String
)