package com.pypisan.sanchitra

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.screens.auth.ProfileSelectionScreen
import com.pypisan.sanchitra.presentation.screens.auth.QRLoginScreen
import com.pypisan.sanchitra.presentation.screens.auth.SplashScreen
import com.pypisan.sanchitra.utils.AnalyticsTracker
import com.pypisan.sanchitra.utils.AuthState


@Composable
fun OnboardingScreen(
    isSwitchProfile: Boolean = (LocalContext.current as? Activity)
        ?.intent
        ?.getBooleanExtra("IS_SWITCH_PROFILE", false) ?: false
) {
    val context = LocalContext.current
    val viewModel: OnboardingViewModel = hiltViewModel()
    val state = viewModel.authState

    // 1. Single startup effect
    LaunchedEffect(isSwitchProfile) {
        viewModel.start(context, isSwitchProfile = isSwitchProfile)
    }

    // 2. Efficient state-driven analytics tracking
    LaunchedEffect(state) {
        val screenName = when (state) {
            is AuthState.Loading -> "SplashScreen"
            is AuthState.ProfileSelection -> "ProfileSelectionScreen"
            is AuthState.QRLogin -> "QRLoginScreen"
            is AuthState.Error -> "LoginErrorScreen"
            is AuthState.ProfileSelected -> null
        }
        screenName?.let { AnalyticsTracker.logScreenView(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141414))
    ) {
        when (state) {

            is AuthState.Loading -> {
                SplashScreen()
            }

            is AuthState.ProfileSelection -> {
                ProfileSelectionScreen(
                    profiles = viewModel.profiles,
                    onProfileSelected = { profile ->
                        viewModel.onProfileSelected(profile)
                    }
                )
            }

            is AuthState.ProfileSelected -> {
                LaunchedEffect(Unit) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as Activity).finish()
                }
            }

            is AuthState.QRLogin -> {
                QRLoginScreen(
                    state.loginURL,
                    state.deviceCode,
                    state.backgroundUrl
                )
            }

            is AuthState.Error -> {
                Error(messageId = StringConstants.Composable.Placeholders.LoginError)
            }

        }
    }
}