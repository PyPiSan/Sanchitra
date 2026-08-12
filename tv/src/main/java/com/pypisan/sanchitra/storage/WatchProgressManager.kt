package com.pypisan.sanchitra.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "watch_progress_prefs")

class WatchProgressManager(private val context: Context) {

    suspend fun saveProgress(id: String, timeMillis: Long, durationMillis: Long = 0L) {
        val timeKey = longPreferencesKey("time_$id")
        val durationKey = longPreferencesKey("duration_$id")

        context.dataStore.edit { prefs ->
            // If watched > 95%, clear progress (movie finished)
            if (durationMillis > 0 && (timeMillis.toFloat() / durationMillis) >= 0.95f) {
                prefs.remove(timeKey)
                prefs.remove(durationKey)
            } else {
                prefs[timeKey] = timeMillis
                prefs[durationKey] = durationMillis
            }
        }
    }

    fun getProgress(id: String): Flow<WatchProgress?> {
        val timeKey = longPreferencesKey("time_$id")
        val durationKey = longPreferencesKey("duration_$id")

        return context.dataStore.data.map { prefs ->
            val savedTime = prefs[timeKey] ?: return@map null
            val savedDuration = prefs[durationKey] ?: 0L
            WatchProgress(id = id, timeMillis = savedTime, durationMillis = savedDuration)
        }
    }

    suspend fun clearProgress(id: String) {
        val timeKey = longPreferencesKey("time_$id")
        val durationKey = longPreferencesKey("duration_$id")

        context.dataStore.edit { prefs ->
            prefs.remove(timeKey)
            prefs.remove(durationKey)
        }
    }
}