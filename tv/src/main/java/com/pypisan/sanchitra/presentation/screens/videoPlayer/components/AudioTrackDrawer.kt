package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pypisan.sanchitra.data.entities.AudioTrack
import kotlinx.coroutines.delay

@Composable
fun AudioTrackDrawer(
    visible: Boolean,
    audioTracks: List<AudioTrack>,
    onDismiss: () -> Unit,
    onTrackSelected: (AudioTrack) -> Unit
) {

    if (visible) {
        BackHandler {
            onDismiss()
        }
    }

    val firstFocusRequester = remember {
        FocusRequester()
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it }
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { it }
        ) + fadeOut()
    ) {

        LaunchedEffect(Unit) {
            delay(250)
            firstFocusRequester.requestFocus()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(400.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFF1A1A1A),
                                Color(0xFF101010)
                            )
                        ),
                        shape = RoundedCornerShape(
                            topStart = 32.dp,
                            bottomStart = 32.dp
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(
                            topStart = 32.dp,
                            bottomStart = 32.dp
                        )
                    )
                    .padding(
                        horizontal = 28.dp,
                        vertical = 36.dp
                    )
            ) {

                // Updated Titles
                Text(
                    text = "Audio Tracks",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Choose Audio Language",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    itemsIndexed(audioTracks) { index, item ->

                        var focused by remember {
                            mutableStateOf(false)
                        }

                        val scale by animateFloatAsState(
                            targetValue = if (focused) 1.03f else 1f,
                            label = ""
                        )

                        val bgColor by animateColorAsState(
                            targetValue = when {
                                focused -> Color.White
                                item.isSelected -> Color.White.copy(alpha = 0.12f)
                                else -> Color.Transparent
                            },
                            label = ""
                        )

                        val borderColor by animateColorAsState(
                            targetValue = when {
                                focused -> Color.White
                                item.isSelected -> Color.White.copy(alpha = 0.15f)
                                else -> Color.Transparent
                            },
                            label = ""
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scale)
                                .then(
                                    if (index == 0) {
                                        Modifier.focusRequester(
                                            firstFocusRequester
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .onFocusChanged {
                                    focused = it.isFocused
                                }
                                .focusable()
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    width = 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .background(bgColor)
                                .onKeyEvent {
                                    if (
                                        it.type == KeyEventType.KeyDown &&
                                        (
                                                it.key == Key.Enter ||
                                                        it.key == Key.DirectionCenter
                                                )
                                    ) {
                                        onTrackSelected(item)
                                        true
                                    } else {
                                        false
                                    }
                                }
                                .padding(
                                    horizontal = 22.dp,
                                    vertical = 18.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                item.label?.let {
                                    Text(
                                        text = it,
                                        color = if (focused) {
                                            Color.Black
                                        } else {
                                            Color.White
                                        },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )
                            }

                            AnimatedVisibility(
                                visible = item.isSelected
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected Track",
                                    tint = if (focused) {
                                        Color.Black
                                    } else {
                                        Color.White
                                    }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(80.dp)
                        )
                    }
                }
            }
        }
    }
}