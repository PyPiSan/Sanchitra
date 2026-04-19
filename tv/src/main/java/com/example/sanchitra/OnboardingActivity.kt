package com.example.sanchitra

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.sanchitra.data.models.UserProfileMap
import com.example.sanchitra.data.models.UserProfiles
import com.example.sanchitra.presentation.screens.auth.ProfileSelectionScreen
import com.example.sanchitra.presentation.screens.auth.QRLoginScreen
import com.example.sanchitra.presentation.screens.auth.SplashScreen
import com.example.sanchitra.presentation.screens.common.ErrorScreen
import com.example.sanchitra.utils.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

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