package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class UserDetailResponse(
    val id: String,
    val username: String,
    val email: String,
    val mobile: String,
    val country: String?,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_admin") val isAdmin: Boolean,
    @SerializedName("is_logged_in") val isLoggedIn: Boolean,
    @SerializedName("last_login") val lastLogin: String,
    @SerializedName("last_logout") val lastLogout: String,
    @SerializedName("inserted_on") val insertedOn: String,
    @SerializedName("updated_on") val updatedOn: String,
    val profiles: List<UserProfiles>,
    val details: UserDetails
)
