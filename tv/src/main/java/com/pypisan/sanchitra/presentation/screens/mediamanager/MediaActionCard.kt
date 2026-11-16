package com.pypisan.sanchitra.presentation.screens.mediamanager

//Card Layout for Settings Card

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun MediaActionCard(
    data: MediaActionCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1f,
        label = "tvCardScale"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.24f else 0.10f,
        label = "tvCardGlow"
    )
    val bgTopColor by animateColorAsState(
        targetValue = if (isFocused) {
            Color(0xFF2D2D38).copy(alpha = 0.72f)
        } else {
            Color(0xFF22222B).copy(alpha = 0.52f)
        },
        label = "tvCardBgTop"
    )
    val bgBottomColor by animateColorAsState(
        targetValue = if (isFocused) {
            Color(0xFF1A1A22).copy(alpha = 0.85f)
        } else {
            Color(0xFF131318).copy(alpha = 0.72f)
        },
        label = "tvCardBgBottom"
    )
    val borderStartColor by animateColorAsState(
        targetValue = if (isFocused) data.accentColor else Color.White.copy(alpha = 0.32f),
        label = "tvCardBorderStart"
    )
    val borderEndColor by animateColorAsState(
        targetValue = if (isFocused) {
            data.accentColor.copy(alpha = 0.45f)
        } else {
            Color.White.copy(alpha = 0.06f)
        },
        label = "tvCardBorderEnd"
    )
    val pillBgColor by animateColorAsState(
        targetValue = if (isFocused) data.accentColor else Color.White.copy(alpha = 0.10f),
        label = "tvCardPillBg"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 155.dp) // Keeps 155.dp look, won't clip on TV Large Text
            .zIndex(if (isFocused) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged { isFocused = it.isFocused },
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = if (isFocused) 2.5.dp else 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    borderStartColor,
                    Color.White.copy(alpha = if (isFocused) 0.35f else 0.10f),
                    borderEndColor
                )
            )
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            focusedElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 155.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(bgTopColor, bgBottomColor)
                    )
                )
                .drawWithCache {
                    val sheenBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isFocused) 0.12f else 0.06f),
                            Color.Transparent
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width * 0.65f, size.height * 0.75f)
                    )
                    val glowCenter = Offset(x = 50.dp.toPx(), y = size.height / 2f)
                    val glowRadius = size.height * 0.95f
                    val glowBrush = Brush.radialGradient(
                        colors = listOf(
                            data.accentColor.copy(alpha = glowAlpha),
                            Color.Transparent
                        ),
                        center = glowCenter,
                        radius = glowRadius
                    )

                    onDrawBehind {
                        drawRect(brush = sheenBrush)
                        drawCircle(
                            brush = glowBrush,
                            center = glowCenter,
                            radius = glowRadius
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Icon Badge
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    data.accentColor.copy(alpha = if (isFocused) 0.30f else 0.18f),
                                    data.accentColor.copy(alpha = if (isFocused) 0.12f else 0.06f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (isFocused) 0.35f else 0.20f),
                                    data.accentColor.copy(alpha = 0.15f)
                                )
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = data.title,
                        tint = data.accentColor,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                // Middle Title & Subtitle
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = data.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = data.subtitle,
                        color = Color.White.copy(alpha = if (isFocused) 0.88f else 0.65f),
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right Action Button Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = pillBgColor,
                    border = if (!isFocused) {
                        BorderStroke(0.8.dp, Color.White.copy(alpha = 0.18f))
                    } else null
                ) {
                    Text(
                        text = data.buttonText,
                        color = if (isFocused) Color.Black else Color.White.copy(alpha = 0.90f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}