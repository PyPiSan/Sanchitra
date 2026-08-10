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
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.screens.auth.ProfileSelectionScreen
import com.pypisan.sanchitra.presentation.screens.auth.QRLoginScreen
import com.pypisan.sanchitra.presentation.screens.auth.SplashScreen
import com.pypisan.sanchitra.utils.AuthState

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun OnboardingScreen() {
    val context = LocalContext.current
    val viewModel: OnboardingViewModel = hiltViewModel()
    val state = viewModel.authState

    LaunchedEffect(Unit) {
        viewModel.start(context)
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
                Error(messageId = R.string.login_error)
            }

        }
    }
}