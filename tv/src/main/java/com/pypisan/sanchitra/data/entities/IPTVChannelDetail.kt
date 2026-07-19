package com.pypisan.sanchitra.data.entities

data class IPTVChannelDetail(
    val id: Int,
    val name: String,
    val country: String,
    val categories: String,
    val isWeb: Boolean,
    val isApp: Boolean,
    val logo: String,
    val streamUrl: String,
    val network: String,
    val language: String,
    val quality: String,
    val isDrm: Boolean,
    val licenseKey: String?,
    val licenseUrl: String?,
    val epgId: String?
)