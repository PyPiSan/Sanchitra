package com.example.sanchitra.data.models

data class UserDetailResponse(
    val id: String,
    val username: String,
    val email: String,
    val mobile: String,
    val country: String?,
    val first_name: String,
    val last_name: String,
    val is_active: Boolean,
    val is_admin: Boolean,
    val is_logged_in: Boolean,
    val last_login: String,
    val last_logout: String,
    val inserted_on: String,
    val updated_on: String,
    val profiles: List<UserProfiles>,
    val details: UserDetails
)
