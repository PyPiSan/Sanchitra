package com.pypisan.sanchitra.presentation.screens.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun TvGlassRadioOption(
    label: String,
    selected: Boolean,
    accentColor: Color,
    focusBorderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1f,
        label = "radioScale"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            selected && isFocused -> accentColor.copy(alpha = 0.36f)
            selected -> accentColor.copy(alpha = 0.22f)
            isFocused -> Color(0xFF343442).copy(alpha = 0.92f)
            else -> Color(0xFF1F1F28).copy(alpha = 0.85f)
        },
        label = "radioBg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isFocused -> focusBorderColor
            selected -> accentColor.copy(alpha = 0.80f)
            else -> Color.White.copy(alpha = 0.20f)
        },
        label = "radioBorder"
    )

    val shape = RoundedCornerShape(12.dp)

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .zIndex(if (isFocused) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = shape,
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(if (isFocused || selected) 1.8.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = if (isFocused) 0.16f else 0.07f),
                        0.5f to Color.Transparent,
                        1.0f to Color.White.copy(alpha = 0.03f)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Custom Radio Circle
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (selected) accentColor else Color.White.copy(alpha = 0.55f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = label,
                color = if (selected || isFocused) Color.White else Color.White.copy(alpha = 0.80f),
                fontSize = 14.sp,
                fontWeight = if (selected || isFocused) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}