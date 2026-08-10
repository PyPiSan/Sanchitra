package com.pypisan.sanchitra.data.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.core.net.toUri

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Int.toHrMinFormat(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours > 0 && minutes > 0 -> "$hours hr $minutes min"
        hours > 0 -> "$hours hr"
        else -> "$minutes min"
    }
}

fun extractCookie(linkUrl: String): String? {
    return try {
        linkUrl.toUri()
            .getQueryParameter("__hdnea__")
    } catch (e: Exception) {
        Log.e("DRMSessionManager", "Error extracting cookie: ${e.message}")
    } as String?
}

data class MpdResponse(
    val mpdUrl: String,
    val licenseUrl: String
)