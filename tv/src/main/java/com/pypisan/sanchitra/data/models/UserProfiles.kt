package com.pypisan.sanchitra.data.models

import com.google.gson.annotations.SerializedName

data class UserProfiles(
    @SerializedName("id") val id: String,
    @SerializedName("profile_name") val profileName: String,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("watchlist") val watchlist: List<WatchList>,
    @SerializedName("favorites") val favorites: List<Favorites>
)