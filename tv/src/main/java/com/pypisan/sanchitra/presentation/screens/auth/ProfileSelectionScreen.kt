package com.pypisan.sanchitra.presentation.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.data.models.UserProfileMap
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ProfileSelectionScreen(
    profiles: List<UserProfileMap>,
    onProfileSelected: (UserProfileMap) -> Unit,
    onGuestSelected: () -> Unit = {},
    onAddProfile: () -> Unit = {}
) {

    if (profiles.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val arcRadius = screenWidth * 0.22f
    val iconSize = screenHeight * 0.12f

    var focusedIndex by remember { mutableIntStateOf(0) }

    // ✅ FIX 1: single clean selection lock
    var isSelecting by remember { mutableStateOf(false) }

    val progressAnim = remember { Animatable(1f) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val primaryIndex = remember(profiles) {
        profiles.indexOfFirst { it.id != "guest" && it.id != "add" }
    }

    // Reset selection state when focus changes
    LaunchedEffect(focusedIndex) {
        isSelecting = false
    }

    // TIMER LOGIC (clean)
    LaunchedEffect(focusedIndex) {

        val isPrimary = focusedIndex == primaryIndex

        if (!isPrimary || isSelecting) {
            progressAnim.snapTo(1f)
            return@LaunchedEffect
        }

        progressAnim.snapTo(1f)
        delay(300)

        if (!isPrimary || isSelecting) return@LaunchedEffect

        progressAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(5000, easing = LinearEasing)
        )

        // ✅ FIX 2: proper auto-select trigger
        if (!isSelecting && focusedIndex == primaryIndex) {
            isSelecting = true
            onProfileSelected(profiles[focusedIndex])
        }
    }

    val density = LocalDensity.current

    val adjustedRadius = remember(profiles.size, arcRadius) {
        arcRadius * (1f + (profiles.size - 3) * 0.08f)
    }

    val arcOffsets = remember(profiles.size) {
        val angleRange = 160f
        val itemCount = profiles.size

        List(itemCount) { index ->
            val fraction = if (itemCount == 1) 0.5f
            else index.toFloat() / (itemCount - 1)

            val angle = (-angleRange / 2f) + (fraction * angleRange)
            val rad = Math.toRadians(angle.toDouble())

            Pair(
                -(adjustedRadius.value * cos(rad)).toFloat(),
                (arcRadius.value * sin(rad)).toFloat()
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0f172a), Color(0xFF020617)),
                    center = Offset(3000f, 1000f),
                    radius = 2500f
                )
            )
    ) {

        Text(
            text = "Who's Watching?",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = screenWidth * 0.08f),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = (screenWidth.value * 0.04f).sp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(screenWidth / 2)
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.CenterEnd
        ) {

            profiles.forEachIndexed { index, profile ->

                key(profile.id) {

                    val offset = arcOffsets[index]

                    ProfileArcItem(
                        profile = profile,
                        isFocusedByParent = (index == focusedIndex),
                        timerProgress = if (index == focusedIndex) progressAnim.value else 1f,
                        modifier = Modifier
                            .padding(end = 60.dp)
                            .offset(
                                x = with(density) { offset.first.toDp() },
                                y = with(density) { offset.second.toDp() }
                            ),
                        iconSize = iconSize,
                        focusRequester = if (index == 0) focusRequester else null,

                        onFocusGained = {
                            focusedIndex = index
                        },

                        onSelected = {
                            if (isSelecting) return@ProfileArcItem
                            isSelecting = true

                            when (profile.id) {
                                "guest" -> onGuestSelected()
                                "add" -> onAddProfile()
                                else -> onProfileSelected(profile)
                            }
                        }
                    )
                }
            }
        }
    }
}