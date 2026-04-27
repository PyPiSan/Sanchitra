package com.pypisan.sanchitra

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.pypisan.sanchitra.presentation.screens.auth.ProfileSelectionScreen
import com.pypisan.sanchitra.presentation.screens.auth.QRLoginScreen
import com.pypisan.sanchitra.presentation.screens.auth.SplashScreen
import com.pypisan.sanchitra.presentation.screens.common.ErrorScreen
import com.pypisan.sanchitra.utils.AuthState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Tell the window to ignore system bars and draw edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2. Hide the Status Bar and Navigation Bar (Immersive Mode)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())

        // Ensures bars don't pop back in when you touch the remote
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        setContent {
            MaterialTheme {
                OnboardingScreen()
            }
        }
    }

    @OptIn(ExperimentalTvMaterial3Api::class)
    @Composable
    fun OnboardingScreen() {
        val context = LocalContext.current
        val viewModel: OnboardingViewModel = viewModel()
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
                    ErrorScreen()
                }

            }
        }
    }
}