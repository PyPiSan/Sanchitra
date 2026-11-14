package com.pypisan.sanchitra.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "sanchitra_search_history_pref"
        private const val KEY_SEARCH_HISTORY = "key_search_history_list"
        private const val MAX_HISTORY_SIZE = 10

        @Volatile
        private var INSTANCE: SearchHistoryManager? = null

        fun getInstance(context: Context): SearchHistoryManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SearchHistoryManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * Get all search history items (ordered from newest to oldest)
     */
    fun getSearchHistory(): List<String> {
        val json = prefs.getString(KEY_SEARCH_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Append a new search term to the top.
     * Moves duplicates to the top and caps history size to MAX_HISTORY_SIZE.
     */
    fun appendSearchHistory(query: String): List<String> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return getSearchHistory()

        val currentList = getSearchHistory().toMutableList()

        // Remove if it already exists so it gets moved to the top
        currentList.remove(trimmed)
        currentList.add(0, trimmed)

        // Trim to max capacity
        val updatedList = if (currentList.size > MAX_HISTORY_SIZE) {
            currentList.take(MAX_HISTORY_SIZE)
        } else {
            currentList
        }

        saveList(updatedList)
        return updatedList
    }

    /**
     * Clear all search history
     */
    fun clearSearchHistory() {
        prefs.edit { remove(KEY_SEARCH_HISTORY) }
    }

    /**
     * Remove a single item
     */
    fun deleteSearchItem(query: String): List<String> {
        val currentList = getSearchHistory().toMutableList()
        currentList.remove(query)
        saveList(currentList)
        return currentList
    }

    private fun saveList(list: List<String>) {
        val json = gson.toJson(list)
        prefs.edit { putString(KEY_SEARCH_HISTORY, json) }
    }
}