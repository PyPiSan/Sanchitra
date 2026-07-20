package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pypisan.sanchitra.data.models.ProgramDisplayModel
import com.pypisan.sanchitra.R

@Composable
fun LiveEpgProgramSeeker(
    program: ProgramDisplayModel,
    modifier: Modifier = Modifier
) {
    // 1. Progress state (Smoothly animates when data arrives)
    val safeProgress = program.progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = safeProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "ProgressAnim"
    )

    // 2. Infinite pulsing animation for the "Live Edge" dot
    val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Start Time
        Text(
            text = program.startTime,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(Modifier.width(12.dp))

        // Custom Canvas Progress Bar
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(16.dp) // Total height bounds (includes the glowing dot)
        ) {
            val trackHeight = 4.dp.toPx()
            val cornerRadius = CornerRadius(trackHeight / 2)
            val yCenter = size.height / 2
            val yOffset = yCenter - (trackHeight / 2)

            // A. Draw the inactive background track
            drawRoundRect(
                color = Color.White.copy(alpha = 0.2f),
                topLeft = Offset(0f, yOffset),
                size = Size(size.width, trackHeight),
                cornerRadius = cornerRadius
            )

            val progressWidth = size.width * animatedProgress

            if (progressWidth > 0) {
                // B. Draw the Red Gradient Fill
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF330000), // Very dark maroon/black-red at the start
                            Color(0xFFFF0000)  // Pure bright red at the live edge
                        ),
                        startX = 0f,
                        endX = progressWidth
                    ),
                    topLeft = Offset(0f, yOffset),
                    size = Size(progressWidth, trackHeight),
                    cornerRadius = cornerRadius
                )

                // C. Draw the Pulsing "Live" Glow at the leading edge
                val dotRadius = 5.dp.toPx()

                // The outer pulsing red halo (tinted red to match the gradient)
                drawCircle(
                    color = Color(0xFFFF0000).copy(alpha = pulseAlpha),
                    radius = dotRadius * 1.8f,
                    center = Offset(progressWidth, yCenter)
                )

                drawCircle(
                    color = Color.White,
                    radius = dotRadius,
                    center = Offset(progressWidth, yCenter)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // End Time
        Text(
            text = program.endTime,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun LiveAlwaysFullSeeker() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // The track (The semi-transparent background of the seekbar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0x00FF0000), // Transparent Red at the start
                                Color(0x88FF0000), // Semi-transparent Red in the middle
                                Color(0xFFFF0000)  // Solid Bright Red at the live edge
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun LiveBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, shape = CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        androidx.tv.material3.Text(
            text = stringResource(R.string.live),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFCC0000), Color(0xFFFF0000))
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .alignByBaseline()
        )
    }
}