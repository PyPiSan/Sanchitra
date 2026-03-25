package com.example.sanchitra

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import retrofit2.awaitResponse

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.sanchitra.api.UserInit
import com.example.sanchitra.utils.Constant
import com.example.sanchitra.utils.RequestModule
import com.example.sanchitra.utils.UnsafeOkHttpClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


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
        var showSplash by remember { mutableStateOf(true) }
        var isDataLoaded by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            // Run API and Timer in parallel
            val startTime = System.currentTimeMillis()

            // Wait for API result
//            val success = initUser(context)
            isDataLoaded = true

            // Calculate how much time is left for the 4s splash
            val elapsedTime = System.currentTimeMillis() - startTime
            val remainingTime = 2000L - elapsedTime

            if (remainingTime > 0) {
                delay(remainingTime)
            }

            // Hide splash only after data is ready AND 4 seconds passed
            showSplash = false
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF141414))
        ) {
            // Splash Layer
            AnimatedVisibility(
                visible = showSplash,
                exit = fadeOut(tween(1000))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_banner_foreground),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Profile Selection Layer
            if (!showSplash) {
                if (isDataLoaded) {
                    ProfileSelectionScreen(
                        onProfileSelected = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }
                    )
                } else {
                    // Show a simple retry or error message if API failed
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Connection Error. Please restart.", color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ProfileSelectionScreen(onProfileSelected: (Profile) -> Unit) {
    val profiles = listOf(
        Profile("Primary", R.drawable.baseline_account),
        Profile("Kids", R.drawable.baseline_account),
        Profile("Guest", R.drawable.baseline_account),
        Profile("Add Profile", R.drawable.baseline_account)
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val arcRadius = screenWidth * 0.22f
    val iconSize = screenHeight * 0.12f

    // STATE: Track which index is focused and the current progress
    var focusedIndex by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(1f) }

    // TIMER LOGIC: Restarts whenever focusedIndex changes
    LaunchedEffect(focusedIndex) {
        progress = 1f // Reset progress to full
        val durationMillis = 5000L // 5 seconds to auto-select
        val startTime = System.currentTimeMillis()

        while (progress > 0f) {
            val elapsed = System.currentTimeMillis() - startTime
            progress = (1f - (elapsed.toFloat() / durationMillis)).coerceAtLeast(0f)

            if (progress <= 0f) {
                onProfileSelected(profiles[focusedIndex])
            }
            delay(16) // Smooth update (~60fps)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0f172a), Color(0xFF020617)),
                    center = androidx.compose.ui.geometry.Offset(
                        3000f,
                        1000f
                    ), // Pushed further for big screens
                    radius = 2500f
                )
            )
    ) {
        // Left Side Text
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = screenWidth * 0.08f)
        ) {
            Text(
                text = "Who's Watching?",
                color = Color.White,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (screenWidth.value * 0.04f).sp
                )
            )
//                Text(
//                    text = "Auto-selecting in 5 seconds...",
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = Color.Gray
//                )
        }

        // Right Side Arc
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(screenWidth / 2)
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.CenterEnd
        ) {
            profiles.forEachIndexed { index, profile ->
                val angleRange = 100f // Tighter angle so icons stay on screen
                val currentAngle = (index * (angleRange / (profiles.size - 1))) - (angleRange / 2)
                val angleInRadians = Math.toRadians(currentAngle.toDouble())

                // xPos moves items inward from the right edge
                val xPos = -(arcRadius.value * cos(angleInRadians)).dp
                val yPos = (arcRadius.value * sin(angleInRadians)).dp

                ProfileArcItem(
                    profile = profile,
                    isFocusedByParent = (index == focusedIndex),
                    timerProgress = progress,
                    modifier = Modifier
                        .padding(end = 60.dp)
                        .offset(x = xPos, y = yPos),
                    iconSize = iconSize,
                    onFocusGained = { focusedIndex = index },
                    onSelected = { onProfileSelected(profile) }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ProfileArcItem(
    profile: Profile,
    isFocusedByParent: Boolean,
    timerProgress: Float,
    modifier: Modifier,
    iconSize: Dp,
    onFocusGained: () -> Unit,
    onSelected: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val ringSize = iconSize * 1.6f

    Row(
        modifier = modifier
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) onFocusGained()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        // TEXT: Appears to the LEFT of the icon when focused
        androidx.compose.animation.AnimatedVisibility(
            visible = isFocused,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Text(
                text = profile.name,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(end = 20.dp) // Gap between text and icon
            )
        }

        // ICON + Progress Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(ringSize)
        ) {
            if (isFocusedByParent) {
                CircularProgressIndicator(
                    progress = timerProgress,
                    modifier = Modifier.fillMaxSize(), // Fills the ringSize
                    color = Color(0xFF00bbff),
                    strokeWidth = 4.dp // Slightly thicker for 4K screens
                )
            }

            Surface(
                onClick = { onSelected() },
                shape = ClickableSurfaceDefaults.shape(CircleShape),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.3f),
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(BorderStroke(3.dp, Color.White))
                ),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = Color(0xFF1a1a1a),
                    focusedContainerColor = Color(0xFF1a1a1a) // Don't hide icon with white!
                ),
                modifier = Modifier.size(iconSize)
            ) {
                Image(
                    painter = painterResource(id = profile.iconRes),
                    contentDescription = profile.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(iconSize * 0.2f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00bbff),
    trackColor: Color = Color.White.copy(alpha = 0.1f),
    strokeWidth: Dp = 4.dp
) {
    Canvas(modifier = modifier) {
        // 1. Draw the background track (the full circle)
        drawCircle(
            color = trackColor,
            style = Stroke(width = strokeWidth.toPx()),
        )

        // 2. Draw the progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round //
            )
        )
    }
}

data class Profile(val name: String, val iconRes: Int)

// Refactor to suspend function
suspend fun initUser(context: Context): Boolean = withContext(Dispatchers.IO) {
    val deviceUser = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    val myVersion = "Android " + Build.VERSION.RELEASE
    Constant.uid = deviceUser
    val origin = context.resources.configuration.locale.country

    try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        Constant.versionName = pInfo.versionName
    } catch (ignored: PackageManager.NameNotFoundException) {
    }

    val client = if (BuildConfig.DEBUG) {
        UnsafeOkHttpClient.getUnsafeOkHttpClient()
    } else {
        OkHttpClient()
    }

    val retrofit = Retrofit.Builder()
        .baseUrl(Constant.userUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val getID = retrofit.create(RequestModule::class.java)
    val call = getID.getUser(
        UserInit(deviceUser, origin, myVersion, Constant.versionName, "tv")
    )

    return@withContext try {
        // Use awaitResponse() to wait for the network call
        val response = call.awaitResponse()
        val resource = response.body()

        if (response.isSuccessful && resource != null) {
            val flag = resource.userStatus ?: false
            Constant.key = resource.apikey

            if (flag) {
                if (resource.logged == true) {
                    Constant.loggedInStatus = true
                    Constant.logo = resource.icon ?: 0
                    Constant.userName = resource.userData ?: ""
                    if (resource.ads != null) {
                        Constant.isFree = resource.ads
                    }
                } else {
                    Constant.loggedInStatus = false
                }
                true // API Success
            } else false
        } else false
    } catch (t: Throwable) {
        false // API Failure
    }

}