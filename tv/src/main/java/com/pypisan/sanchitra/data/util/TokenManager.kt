package com.pypisan.sanchitra.data.util

import android.content.Context

object TokenManager {

    fun getAccessToken(context: Context): String? {
        return getToken(context) // your existing function
    }

    fun getRefreshToken(context: Context): String? {
        return getRefreshTokenFromPrefs(context)
    }

    fun saveTokens(context: Context, access: String, refresh: String) {
        saveToken(context, access)
        saveRefreshToken(context, refresh)
    }

    fun clear(context: Context) {
        saveToken(context, "")
        saveRefreshToken(context, "")
    }
}