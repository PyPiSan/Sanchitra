package com.example.sanchitra.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Channel(
    val id: Int,
    val name: String,
    val category: String,

    @SerializedName("logo_url")
    val logoUrl: String,

    @SerializedName("stream_url")
    val streamUrl: String,

    val language: String,

    @SerializedName("is_drm")
    val isDrm: Boolean,

    @SerializedName("license_key")
    val licenseKey: String?
): Parcelable