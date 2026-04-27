package com.pypisan.sanchitra.data.models

data class UserProfiles(
    val id: String,
    val profile_name: String,
    val profile_picture: String?,
    val watchlist: List<WatchList>,
    val favorites: List<Favorites>
)
