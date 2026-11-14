package com.pypisan.sanchitra.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Entity(
    tableName = "channels",
    indices = [
        Index(value = ["stream_url"], unique = true),
        Index(value = ["category"])
    ]
)
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "iptv_channel_id")
    val iptvChannelId: String? = null,

    @ColumnInfo(name = "logo_id")
    val logoId: Long? = null,

    @ColumnInfo(name = "network")
    val network: String? = null,

    @ColumnInfo(name = "country")
    val country: String? = "IN",

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "language")
    val language: String? = "English",

    @ColumnInfo(name = "logo_url")
    val logoUrl: String? = null,

    @ColumnInfo(name = "banner_url")
    val bannerUrl: String? = null,

    @ColumnInfo(name = "stream_url")
    val streamUrl: String,

    @ColumnInfo(name = "epg_url")
    val epgUrl: String? = null,

    @ColumnInfo(name = "license_key")
    val licenseKey: String? = null,

    @ColumnInfo(name = "license_url")
    val licenseUrl: String? = null,

    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean = false,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "is_web")
    val isWeb: Boolean = true,

    @ColumnInfo(name = "is_app")
    val isApp: Boolean = true,

    @ColumnInfo(name = "is_drm")
    val isDrm: Boolean = false,

    @ColumnInfo(name = "is_premium")
    val isPremium: Boolean = true,

    @ColumnInfo(name = "is_internal")
    val isInternal: Boolean = true,

    @ColumnInfo(name = "is_geo_restricted")
    val isGeoRestricted: Boolean = true,

    @ColumnInfo(name = "inserted_on")
    val insertedOn: String = nowUtcIso(),

    @ColumnInfo(name = "updated_on")
    val updatedOn: String = nowUtcIso(),

    @ColumnInfo(name = "iptv_channel")
    val iptvChannel: String? = null,

    @ColumnInfo(name = "logo")
    val logo: String? = null
) {
    init {
        require(name.isNotBlank()) { "Channel name must not be blank" }
        require(category.isNotBlank()) { "Channel category must not be blank" }
        require(streamUrl.isNotBlank()) { "Channel stream_url must not be blank" }
    }

    companion object {
        fun nowUtcIso(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())
        }
    }
}