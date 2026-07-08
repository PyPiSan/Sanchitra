package com.pypisan.sanchitra.data.models

data class Channel(
    val id: Int,
    val name: String,
    val category: String,
    val logoUrl: String?,
    val bannerUrl: String?,
    val streamUrl: String,
    val language: String,
    val licenseKey: String?,
    val licenseUrl: String?,
    val isDrm: Boolean,
    val isInternal: Boolean,
    val isPremium: Boolean,
)