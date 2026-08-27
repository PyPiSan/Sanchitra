package com.pypisan.sanchitra

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.pypisan.sanchitra.data.util.isEmulator
import com.pypisan.sanchitra.presentation.App
import com.pypisan.sanchitra.presentation.theme.SanchitraTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalTvMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        // Set device_environment user property
        val deviceType = if (isEmulator()) "emulator" else "real_device"
        firebaseAnalytics.setUserProperty("device_environment", deviceType)

        setContent {
            SanchitraTheme{
                Box(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurface
                    ) {
                        App(
                            onBackPressed = onBackPressedDispatcher::onBackPressed,
                        )
                    }
                }
            }
        }
    }
}