package com.example.sanchitra.data.util

import android.content.Context

fun saveToken(context: Context, token: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("access_token", token).apply()
}

fun getToken(context: Context): String? {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("access_token", null)
}