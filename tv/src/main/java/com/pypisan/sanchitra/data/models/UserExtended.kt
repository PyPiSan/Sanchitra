package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class UserExtended(
    @SerializedName("user_id") val userId: String,
    @SerializedName("auth_token") val authToken: String,
    @SerializedName("j_token") val jToken: String,
    @SerializedName("sso_token") val sSOToken: String,
    @SerializedName("session_attributes") val sessionAtr: SessionAtr,
)


data class SessionAtr(
    @SerializedName("unique") val unique: String,
    @SerializedName("subscriberId") val subscriberId: String,
)