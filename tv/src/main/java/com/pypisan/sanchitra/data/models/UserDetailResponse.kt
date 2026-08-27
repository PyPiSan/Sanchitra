package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName
import com.pypisan.sanchitra.data.models.UserDetails
import com.pypisan.sanchitra.data.models.UserProfiles

data class UserDetailResponse(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("country") val country: String?,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_admin") val isAdmin: Boolean,
    @SerializedName("is_logged_in") val isLoggedIn: Boolean,
    @SerializedName("last_login") val lastLogin: String,
    @SerializedName("last_logout") val lastLogout: String,
    @SerializedName("inserted_on") val insertedOn: String,
    @SerializedName("updated_on") val updatedOn: String,
    @SerializedName("profiles") val profiles: List<UserProfiles>,
    @SerializedName("details") val details: UserDetails
)
