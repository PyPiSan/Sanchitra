package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.utils.PrimaryAccentColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun VideoQualityDrawer(
    visible: Boolean,
    qualities: List<VideoQuality>,
    onDismiss: () -> Unit,
    onQualitySelected: (VideoQuality) -> Unit,
    onShowControls: () -> Unit = {}
) {
    if (visible) {
        BackHandler {
            onDismiss()
        }
    }

    // Autofocus the currently selected quality (or fallback to index 0)
    val selectedIndex = remember(qualities) {
        val idx = qualities.indexOfFirst { it.isSelected }
        if (idx >= 0) idx else 0
    }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // Heartbeat loop: Keeps parent player controls alive while drawer is open
    LaunchedEffect(visible) {
        if (visible) {
            if (selectedIndex in qualities.indices) {
                lazyListState.scrollToItem(selectedIndex)
            }
            delay(100)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}

            while (true) {
                onShowControls()
                delay(2000)
            }
        }
    }

    AnimatedVisibility(
        visible = visible, enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(300)), exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(300))
    ) {
        // Background Scrim (Click outside to close)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            // RIGHT PANEL
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(420.dp)
                    .focusGroup() // Traps focus inside drawer
                    .onFocusChanged { focusState ->
                        // Re-claims focus if lost during recomposition
                        if (!focusState.hasFocus && visible) {
                            focusRequester.requestFocus()
                        }
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Prevents clicking inside drawer from closing it
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFF1A1A1E), Color(0xFF111114)
                            )
                        ), shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp)
                    )
                    .padding(horizontal = 28.dp, vertical = 32.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Video Quality",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Count Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${qualities.size}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Choose streaming resolution",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 32.dp,
                        start = 4.dp,
                        end = 4.dp
                    )
                ) {
                    itemsIndexed(
                        items = qualities,
                        key = { index, item -> "${item.height}_${item.bitrate}_${item.trackIndex}_$index" }) { index, item ->

                        var isFocused by remember { mutableStateOf(false) }

                        // Snappy TV focus spring animation
                        val scale by animateFloatAsState(
                            targetValue = if (isFocused) 1.04f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "ItemScale"
                        )

                        val containerColor by animateColorAsState(
                            targetValue = when {
                                isFocused -> Color.White                                   // Focused: Glossy White
                                item.isSelected -> PrimaryAccentColor.copy(alpha = 0.18f)  // Selected: Frosted Accent Glass
                                else -> Color.White.copy(alpha = 0.06f)                    // Unselected: Translucent Glass View
                            }, animationSpec = tween(150), label = "ItemBgColor"
                        )

                        // 2. Glass Outline Ring Border
                        val borderColor by animateColorAsState(
                            targetValue = when {
                                isFocused -> Color.White                                   // Focused: Solid White Ring
                                item.isSelected -> PrimaryAccentColor.copy(alpha = 0.50f)  // Selected: Glowing Accent Ring
                                else -> Color.White.copy(alpha = 0.12f)                    // Unselected: Thin Glass Ring
                            }, animationSpec = tween(150), label = "ItemBorderColor"
                        )

                        val textColor = if (isFocused) Color.Black else Color.White

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(scale)
                                .then(
                                    if (index == selectedIndex) {
                                        Modifier.focusRequester(focusRequester)
                                    } else Modifier
                                )
                                .onFocusChanged {
                                    isFocused = it.isFocused
                                    if (it.isFocused) onShowControls()
                                }
                                .clip(RoundedCornerShape(14.dp))
                                .background(containerColor)
                                .border(
                                    width = 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    coroutineScope.launch {
                                        delay(150) // Brief visual feedback before closing
                                        onQualitySelected(item)
                                    }
                                }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            // Active selection indicator bar on left
                            if (item.isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 4.dp, height = 18.dp)
                                        .background(
                                            color = if (isFocused) Color.Black else PrimaryAccentColor,
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }

                            Text(
                                text = item.label ?: "Auto",
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = if (item.isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )

                            if (item.isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected Quality",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
