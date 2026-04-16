package com.example.sanchitra.data.util

import android.content.Context
import androidx.core.content.edit

fun saveToken(context: Context, token: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit { putString("access_token", token) }
}
fun saveRefreshToken(context: Context, token: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit { putString("refresh_token", token) }
}

fun getToken(context: Context): String? {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("access_token", null)
}