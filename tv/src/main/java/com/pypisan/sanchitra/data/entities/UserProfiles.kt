package com.pypisan.sanchitra.data.entities

import com.google.gson.annotations.SerializedName

data class UserProfiles(
    val id: String,
    @SerializedName("profile_name") val profileName: String,
    @SerializedName("profile_picture") val profilePicture: String?,
    val watchlist: List<WatchList>,
    val favorites: List<Favorites>
)