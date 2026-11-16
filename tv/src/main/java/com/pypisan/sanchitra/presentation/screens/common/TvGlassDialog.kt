package com.pypisan.sanchitra.presentation.screens.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex

@Composable
fun TvGlassDialog(
    showDialog: Boolean,
    title: String,
    confirmText: String,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!showDialog) return

    val dialogShape = RoundedCornerShape(20.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 520.dp)
                .clip(dialogShape)
                // Dark smoked glass dialog background
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF262632).copy(alpha = 0.98f),
                            Color(0xFF14141B).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f),
                            Color(0xFFFF5722).copy(alpha = 0.28f)
                        )
                    ),
                    shape = dialogShape
                )
                .drawWithCache {
                    val sheen = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width * 0.65f, size.height * 0.45f)
                    )
                    onDrawBehind { drawRect(brush = sheen) }
                }
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header (explicit white + subtle accent underline)
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF5722).copy(alpha = 0.65f),
                                    Color.White.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Body (TextFields + error)
                content()

                Spacer(modifier = Modifier.height(24.dp))

                // Rounded-Rectangle Glossy Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlossyDialogButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        accentColor = Color.White,
                        isPrimary = false
                    )
                    GlossyDialogButton(
                        text = confirmText,
                        onClick = { if (!isLoading) onConfirm() },
                        accentColor = Color(0xFFFF5722),
                        isPrimary = true,
                        enabled = !isLoading
                    )
                }
            }
        }
    }
}

@Composable
private fun GlossyDialogButton(
    text: String,
    onClick: () -> Unit,
    accentColor: Color,
    isPrimary: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1f,
        label = "dialogBtnScale"
    )
    val baseBgColor by animateColorAsState(
        targetValue = when {
            isPrimary && isFocused -> accentColor.copy(alpha = 0.92f)
            isPrimary -> accentColor.copy(alpha = 0.26f)
            isFocused -> Color.White.copy(alpha = 0.24f)
            else -> Color.White.copy(alpha = 0.08f)
        },
        label = "dialogBtnBg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isFocused -> Color(0xFFFFE082) // Clear gold TV focus highlight
            isPrimary -> accentColor.copy(alpha = 0.55f)
            else -> Color.White.copy(alpha = 0.22f)
        },
        label = "dialogBtnBorder"
    )

    val buttonShape = RoundedCornerShape(12.dp) // Rounded rectangle (not pill)

    Surface(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .zIndex(if (isFocused) 1f else 0f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = buttonShape,
        color = Color.Transparent,
        contentColor = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = if (isFocused) 2.dp else 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    if (isFocused) Color.White else borderColor,
                    borderColor,
                    borderColor.copy(alpha = 0.5f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .background(baseBgColor)
                // Glossy top-to-bottom glass reflection
                .background(
                    brush = Brush.verticalGradient(
                        0.00f to Color.White.copy(alpha = if (isFocused) 0.34f else 0.16f),
                        0.48f to Color.White.copy(alpha = if (isFocused) 0.12f else 0.05f),
                        0.50f to Color.Transparent,
                        1.00f to Color.White.copy(alpha = if (isFocused) 0.10f else 0.03f)
                    )
                )
                .padding(horizontal = 22.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isFocused || isPrimary) Color.White else Color.White.copy(alpha = 0.80f),
                fontSize = 15.sp,
                fontWeight = if (isFocused || isPrimary) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
