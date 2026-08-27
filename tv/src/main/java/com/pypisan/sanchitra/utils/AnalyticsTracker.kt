package com.pypisan.sanchitra.utils

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

object AnalyticsTracker {
    private val analytics: FirebaseAnalytics get() = Firebase.analytics

    fun logScreenView(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }

    fun logTabSelected(tabName: String) {
        analytics.logEvent("profile_tab_changed") {
            param("tab_name", tabName)
        }
    }

    fun logLanguageToggled(language: String, isSelected: Boolean) {
        analytics.logEvent("language_toggled") {
            param("language", language)
            param("action", if (isSelected) "selected" else "deselected")
        }
    }
}