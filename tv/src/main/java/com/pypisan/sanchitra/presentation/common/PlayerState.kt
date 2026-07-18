package com.pypisan.sanchitra.presentation.common

// Determines which player is currently open in the overlay
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class PlayerState : Parcelable {
    @Parcelize
    object Idle : PlayerState()

    @Parcelize
    data class TV(val channelId: String) : PlayerState()

    @Parcelize
    data class Video(val metaId: String) : PlayerState()

    @Parcelize
    data class IPTV(val channelId: String) : PlayerState()
}