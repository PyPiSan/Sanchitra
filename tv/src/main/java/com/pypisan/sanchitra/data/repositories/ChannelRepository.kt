package com.pypisan.sanchitra.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.withTransaction
import com.pypisan.sanchitra.data.entities.ChannelEntity
import com.pypisan.sanchitra.data.util.M3uParser
import com.pypisan.sanchitra.storage.AppDatabase
import com.pypisan.sanchitra.storage.ChannelDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val db: AppDatabase,
    private val dao: ChannelDao
) {
    fun observeAllChannels(): Flow<List<ChannelEntity>> = dao.observeAll()
    fun observeCategories(): Flow<List<String>> = dao.observeCategories()
    fun observeByCategory(category: String): Flow<List<ChannelEntity>> = dao.observeByCategory(category)

    suspend fun importFromUrl(
        url: String,
        defaultCategory: String = "Web Playlist"
    ): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            Log.d("ChannelImport", "Fetching URL: $url")
            val raw = downloadText(url).removePrefix("\uFEFF")
            Log.d("ChannelImport", "Downloaded ${raw.length} chars")

            val parsed = parseAuto(raw, defaultCategory)
            Log.d("ChannelImport", "Parsed ${parsed.size} channels")
            parsed.take(3).forEachIndexed { i, ch ->
                Log.d("ChannelImport", "Sample[$i]: name=${ch.name}, cat=${ch.category}, url=${ch.streamUrl}")
            }

            require(parsed.isNotEmpty()) { "No valid channels found in URL." }
            saveInTransaction(parsed)

            val totalInDb = dao.getAllIdentities().size
            Log.d("ChannelImport", "Saved! Total rows in Room DB: $totalInDb")
            parsed.size
        }.onFailure { e ->
            Log.e("ChannelImport", "Import failed for $url", e)
        }
    }

    suspend fun importFromUri(
        uri: Uri,
        defaultCategory: String = "Local Playlist"
    ): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val raw = context.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader().readText()
            } ?: error("Unable to read selected file.")

            val parsed = parseAuto(raw, defaultCategory)
            require(parsed.isNotEmpty()) { "No valid channels found in file." }
            saveInTransaction(parsed)
            parsed.size
        }
    }

    suspend fun updateChannel(channel: ChannelEntity) = withContext(Dispatchers.IO) {
        dao.update(channel.copy(updatedOn = ChannelEntity.nowUtcIso()))
    }

    suspend fun deleteChannel(channelId: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(channelId)
    }

    suspend fun deleteCategory(category: String) = withContext(Dispatchers.IO) {
        dao.deleteByCategory(category)
    }

    // In ChannelRepository.kt:
    suspend fun getChannelById(channelId: Long): ChannelEntity? = withContext(Dispatchers.IO) {
        dao.getById(channelId)
    }

    private suspend fun saveInTransaction(incoming: List<ChannelEntity>) {
        db.withTransaction {
            val existingByUrl = dao.getAllIdentities().associateBy { it.streamUrl }
            val now = ChannelEntity.nowUtcIso()

            // Deduplicate within the incoming file and preserve existing id + inserted_on
            val prepared = incoming
                .associateBy { it.streamUrl }
                .values
                .map { item ->
                    val existing = existingByUrl[item.streamUrl]
                    if (existing != null) {
                        item.copy(
                            id = existing.id,
                            insertedOn = existing.insertedOn,
                            updatedOn = now
                        )
                    } else {
                        item.copy(insertedOn = now, updatedOn = now)
                    }
                }

            dao.upsertAll(prepared)
        }
    }

    private fun parseAuto(content: String, defaultCategory: String): List<ChannelEntity> {
        val trimmed = content.trim()
        return if (trimmed.startsWith("[") || trimmed.startsWith("{")) {
            parseJsonChannels(trimmed)
        } else {
            M3uParser.parse(trimmed, defaultCategory)
        }
    }

    private fun downloadText(urlString: String): String {
        val conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 20_000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", "SanchitraTV/1.0")
        }
        try {
            val code = conn.responseCode
            if (code !in 200..299) error("HTTP $code while fetching playlist")
            return conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    private fun parseJsonChannels(raw: String): List<ChannelEntity> {
        val array = if (raw.startsWith("[")) {
            JSONArray(raw)
        } else {
            val obj = JSONObject(raw)
            obj.optJSONArray("channels") ?: JSONArray().put(obj)
        }

        val result = mutableListOf<ChannelEntity>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val name = obj.optNonBlankString("name") ?: continue
            val category = obj.optNonBlankString("category") ?: continue
            val streamUrl = obj.optNonBlankString("stream_url") ?: continue

            val now = ChannelEntity.nowUtcIso()
            result += ChannelEntity(
                id = obj.optLong("id", 0L),
                name = name,
                category = category,
                streamUrl = streamUrl,
                iptvChannelId = obj.optNullableString("iptv_channel_id"),
                logoId = obj.optNullableLong("logo_id"),
                network = obj.optNullableString("network"),
                country = obj.optNullableString("country") ?: "IN",
                language = obj.optNullableString("language") ?: "English",
                logoUrl = obj.optNullableString("logo_url"),
                bannerUrl = obj.optNullableString("banner_url"),
                epgUrl = obj.optNullableString("epg_url"),
                licenseKey = obj.optNullableString("license_key"),
                licenseUrl = obj.optNullableString("license_url"),
                isFeatured = obj.optBoolean("is_featured", false),
                isActive = obj.optBoolean("is_active", true),
                isWeb = obj.optBoolean("is_web", true),
                isApp = obj.optBoolean("is_app", true),
                isDrm = obj.optBoolean("is_drm", false),
                isPremium = obj.optBoolean("is_premium", true),
                isInternal = obj.optBoolean("is_internal", true),
                isGeoRestricted = obj.optBoolean("is_geo_restricted", true),
                insertedOn = obj.optNonBlankString("inserted_on") ?: now,
                updatedOn = obj.optNonBlankString("updated_on") ?: now,
                iptvChannel = obj.optNullableString("iptv_channel"),
                logo = obj.optNullableString("logo")
            )
        }
        return result
    }

    private fun JSONObject.optNonBlankString(key: String): String? =
        if (isNull(key)) null else optString(key).trim().takeIf { it.isNotEmpty() }

    private fun JSONObject.optNullableString(key: String): String? =
        if (isNull(key)) null else optString(key).takeIf { it.isNotEmpty() }

    private fun JSONObject.optNullableLong(key: String): Long? =
        if (isNull(key)) null else optLong(key)
}