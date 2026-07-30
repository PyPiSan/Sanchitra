package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.drawWithCache
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
import com.pypisan.sanchitra.utils.BrightRed
import com.pypisan.sanchitra.utils.GradientColors
import com.pypisan.sanchitra.utils.TrackBackgroundColor


@Composable
fun LiveEpgProgramSeeker(
    program: ProgramDisplayModel,
    modifier: Modifier = Modifier
) {
    val safeProgress = program.progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = safeProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "ProgressAnim"
    )

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

        // 2. Replaced Canvas with Spacer + drawWithCache
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .drawWithCache {
                    // This block ONLY executes when the size changes or when
                    // state read inside this block (animatedProgress) changes.

                    val trackHeight = 4.dp.toPx()
                    val cornerRadius = CornerRadius(trackHeight / 2)
                    val yCenter = size.height / 2
                    val yOffset = yCenter - (trackHeight / 2)
                    val dotRadius = 5.dp.toPx()

                    val progressWidth = size.width * animatedProgress

                    // The Brush allocation is now CACHED! It stops re-allocating
                    // once the 1-second progress animation finishes.
                    val progressBrush = if (progressWidth > 0) {
                        Brush.horizontalGradient(
                            colors = GradientColors,
                            startX = 0f,
                            endX = progressWidth
                        )
                    } else null

                    onDrawBehind {
                        // This block executes on EVERY frame because it reads `pulseAlpha`.
                        // However, NO objects are allocated here, ensuring perfectly smooth 60/120fps.

                        // A. Draw the inactive background track
                        drawRoundRect(
                            color = TrackBackgroundColor,
                            topLeft = Offset(0f, yOffset),
                            size = Size(size.width, trackHeight),
                            cornerRadius = cornerRadius
                        )

                        if (progressWidth > 0 && progressBrush != null) {
                            // B. Draw the Red Gradient Fill using the cached brush
                            drawRoundRect(
                                brush = progressBrush,
                                topLeft = Offset(0f, yOffset),
                                size = Size(progressWidth, trackHeight),
                                cornerRadius = cornerRadius
                            )

                            // C. Draw the Pulsing "Live" Glow at the leading edge
                            drawCircle(
                                color = BrightRed.copy(alpha = pulseAlpha), // pulseAlpha read here
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
                }
        )

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
                        colors = GradientColors
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .alignByBaseline()
        )
    }
}