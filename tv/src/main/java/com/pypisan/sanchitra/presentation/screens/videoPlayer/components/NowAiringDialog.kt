package com.pypisan.sanchitra.presentation.screens.videoPlayer.components


import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.pypisan.sanchitra.data.models.ProgramDisplayModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NowAiringDialog(
    visible: Boolean,
    programs: List<ProgramDisplayModel>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    if (!visible || programs.isEmpty()) return

    BackHandler { onDismiss() }

    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    val currentProgram = programs[currentIndex]

    val focusRequesterPrev = remember { FocusRequester() }
    val focusRequesterNext = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            if (currentIndex < programs.lastIndex) {
                focusRequesterNext.requestFocus()
            } else if (currentIndex > 0) {
                focusRequesterPrev.requestFocus()
            }
        } catch (_: Exception) {}
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        ElevatedCard(
            modifier = Modifier
                .width(720.dp)
                .height(280.dp)
                .border(2.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF212124)
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize() // Fill the card's fixed dimensions
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {

                AnimatedContent(
                    targetState = currentProgram,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        (slideInVertically(animationSpec = tween(300)) { h -> -h } + fadeIn(
                            animationSpec = tween(300)
                        ))
                            .togetherWith(slideOutVertically(animationSpec = tween(300)) { h -> h } + fadeOut(
                                animationSpec = tween(300)
                            ))
                    },
                    label = "ProgramTransition"
                ) { targetProgram ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProgramPreview(targetProgram.imageUrl)

                        Spacer(Modifier.width(24.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = targetProgram.status,
                                color = Color(0xFF64B5F6),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = targetProgram.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = targetProgram.description,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis,
                                color = Color(0xFFE0E0E0),
                                lineHeight = 18.sp,
                                fontSize = 14.sp
                            )

                            Spacer(Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(targetProgram.startTime, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
                                Spacer(Modifier.width(10.dp))

                                LinearProgressIndicator(
                                    progress = { targetProgram.progress },
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFF64B5F6),
                                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                                )
                                Spacer(Modifier.width(10.dp))

                                Text(targetProgram.endTime, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Previous Button
                if (currentIndex > 0) {
                    DpadKeyNavigationButton(
                        icon = Icons.Default.ArrowBackIosNew,
                        focused = false,
                        onClick = {
                            if (currentIndex > 1) {
                                currentIndex--
                                try { focusRequesterPrev.requestFocus() } catch (_: Exception) {}
                            } else {
                                currentIndex--
                                try { focusRequesterNext.requestFocus() } catch (_: Exception) {}
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .focusRequester(focusRequesterPrev)
                    )
                }

                // Next Button
                if (currentIndex < programs.lastIndex) {
                    DpadKeyNavigationButton(
                        icon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        focused = true,
                        onClick = {
                            // *** EXPLICIT FOCUS MANAGEMENT ON CLICK ***
                            if (currentIndex < programs.lastIndex - 1) {
                                currentIndex++
                                try { focusRequesterNext.requestFocus() } catch (_: Exception) {}
                            } else {
                                // Going to last index, so the next button is about to hide.
                                // Move focus to Previous before this button disappears.
                                currentIndex++
                                try { focusRequesterPrev.requestFocus() } catch (_: Exception) {}
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .focusRequester(focusRequesterNext)
                    )
                }
            }
        }
    }
}

@Composable
fun DpadKeyNavigationButton(
    icon: ImageVector,
    focused: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Styling constants
    val buttonWidth = 28.dp
    val buttonHeight = 100.dp // High aesthetic height
    var hasFocus by remember { mutableStateOf(false) }


    val containerColor = when {
        hasFocus -> Color.White
        focused -> Color.White.copy(alpha = 0.6f)
        else -> Color(0xFF64B5F6).copy(alpha = 0.3f)
    }

    val tintColor = when {
        hasFocus -> Color.Black
        focused -> Color.Black
        else -> Color.White
    }

    Box(
        modifier = modifier
            .width(buttonWidth)
            .height(buttonHeight)
            // --- FOCUS STATE HANDLING ---
            .onFocusChanged { hasFocus = it.isFocused }
            .focusable()

            .onKeyEvent { keyEvent ->

                if (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER &&
                    keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_UP // Trigger on release
                ) {
                    onClick() // Execute callback
                    true // Consume event
                } else {
                    false // Don't consume other keys (like traversal keys)
                }
            }
            // Add aesthetic border when focused
            .border(
                width = if (hasFocus) 2.dp else 0.dp,
                color = if (hasFocus) Color.White else Color.Transparent,
                shape = CircleShape
            )
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .clip(CircleShape) // circular shape area
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(16.dp) // Icon sized appropriately
        )
    }
}

@Composable
private fun ProgramPreview(
    imageUrl: String?
) {

    if (!imageUrl.isNullOrBlank()) {

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(width = 280.dp, height = 170.dp)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
        )

    } else {

        Box(
            modifier = Modifier
                .size(width = 280.dp, height = 170.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    Icons.Default.Image,
                    null,
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "No Preview",
                    color = Color.Gray
                )
            }
        }
    }
}