package com.pypisan.sanchitra.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

object AnalyticsTracker {

    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var crashlytics: FirebaseCrashlytics? = null

    fun initDeviceProperties(context: Context) {
        val appContext = context.applicationContext
        firebaseAnalytics = FirebaseAnalytics.getInstance(appContext)

        // Run on background thread so it doesn't slow down TV app launch
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val profile = DeviceProfileHelper.getDeviceProfileMap(appContext)

                val model = profile[DeviceProfileHelper.KEY_MODEL]?.toString() ?: "Unknown"
                val drmLevel = profile[DeviceProfileHelper.KEY_DRM_SECURITY_LEVEL]?.toString() ?: "Unknown"
                val osVer = profile[DeviceProfileHelper.KEY_OS_VERSION]?.toString() ?: "Unknown"
                val ramMb = profile[DeviceProfileHelper.KEY_RAM_TOTAL_MB]?.toString() ?: "0"

                // 1. Set Firebase User Properties (Shows up in Firebase Audiences/Filters)
                // Note: Firebase requires user property names <= 24 chars and values <= 36 chars
                setUserPropertySafe("tv_model", model)
                setUserPropertySafe("tv_drm_level", drmLevel)
                setUserPropertySafe("tv_os_ver", osVer)
                setUserPropertySafe("tv_ram_mb", ramMb)

                // 2. Set Default Event Parameters (Automatically attached to EVERY event logged)
                val defaultParams = Bundle().apply {
                    putString("device_model", model)
                    putString("drm_level", drmLevel)
                    putString("os_version", osVer)
                }
                firebaseAnalytics?.setDefaultEventParameters(defaultParams)

                // 3. Attach to Firebase Crashlytics (Crucial for debugging TV crashes)
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.setCustomKey("tv_model", model)
                crashlytics.setCustomKey("tv_drm_level", drmLevel)
                crashlytics.setCustomKey("tv_ram_mb", (profile[DeviceProfileHelper.KEY_RAM_TOTAL_MB] as? Long) ?: 0L)
                crashlytics.setCustomKey("tv_os_version", osVer)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUserPropertySafe(key: String, value: String) {
        // Firebase enforces a strict 36-character limit on user property values
        val trimmedValue = if (value.length > 36) value.take(36) else value
        firebaseAnalytics?.setUserProperty(key, trimmedValue)
    }

    fun logScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    // Add this for non-fatal errors (API failures, timeouts, parsing errors, etc.)
    fun recordNonFatal(throwable: Throwable, message: String? = null) {
        if (throwable is CancellationException) return // Don't pollute Crashlytics with normal coroutine cancellations

        val instance = crashlytics ?: FirebaseCrashlytics.getInstance()
        if (!message.isNullOrBlank()) {
            instance.log(message) // Attached as breadcrumb log to this report
        }
        instance.recordException(throwable)
    }

    // Optional: call this on key steps (e.g., player errors, playback start) for Crashlytics breadcrumbs
    fun logBreadcrumb(message: String) {
        (crashlytics ?: FirebaseCrashlytics.getInstance()).log(message)
    }

    fun logEvent(eventName: String, params: Map<String, Any>? = null) {
        val bundle = Bundle().apply {
            params?.forEach { (key, value) ->
                putString(key, value.toString())
            }
        }
        firebaseAnalytics?.logEvent(eventName, bundle)
    }
}