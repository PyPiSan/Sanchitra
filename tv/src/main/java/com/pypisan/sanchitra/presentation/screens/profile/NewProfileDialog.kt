package com.pypisan.sanchitra.presentation.screens.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.pypisan.sanchitra.presentation.theme.SanchitraCardShape
import kotlinx.coroutines.delay

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun NewProfileDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    avatarUrls: List<String> = emptyList(),
    message: String? = null,
    onDismissRequest: () -> Unit,
    onConfirmAddProfile: (profileName: String, selectedAvatarUrl: String) -> Unit,
) {
    if (!showDialog) return

    var profileName by remember { mutableStateOf("") }
    var selectedAvatarUrl by remember { mutableStateOf(avatarUrls.firstOrNull().orEmpty()) }
    var localError by remember { mutableStateOf<String?>(null) }

    val textFieldFocusRequester = remember { FocusRequester() }
    val firstAvatarFocusRequester = remember { FocusRequester() }
    val createButtonFocusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize
    val screenWidth = with(density) { containerSize.width.toDp() }
    val screenHeight = with(density) { containerSize.height.toDp() }
    val avatarSize = (screenHeight * 0.18f).coerceIn(84.dp, 130.dp)

    LaunchedEffect(showDialog) {
        profileName = ""
        selectedAvatarUrl = avatarUrls.firstOrNull().orEmpty()
        localError = null
        if (message == null) {
            delay(150)
            textFieldFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(avatarUrls) {
        if (selectedAvatarUrl.isBlank() && avatarUrls.isNotEmpty()) {
            selectedAvatarUrl = avatarUrls.first()
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Keep error dialog compact; make Add Profile dialog nearly full screen
        if (message != null) {
            val isFailure = message == "Failed to add new profile. Please try again."

            Box(
                modifier = modifier
                    .widthIn(min = 360.dp, max = 480.dp)
                    .clip(SanchitraCardShape)
                    .background(Color(0xFF0F172A))
                    .border(
                        width = 1.dp,
                        color = if (isFailure) {
                            Color(0xFFE50914).copy(alpha = 0.5f)
                        } else {
                            Color(0xFF22C55E).copy(alpha = 0.5f)
                        },
                        shape = SanchitraCardShape
                    )
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = if (isFailure) "Action Failed" else "Action Success",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = message,
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        AccountsSectionDialogButton(
                            text = "Close",
                            shouldRequestFocus = true,
                            onClick = onDismissRequest
                        )
                    }
                }
            }
            return@Dialog
        }

        // Nearly full-screen container matching ProfileSelectionScreen theme
        Box(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(SanchitraCardShape)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = SanchitraCardShape
                )
                .drawBehind {
                    // 1. Deep Slate -> Black gradient matching ProfileSelectionScreen
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF020617),
                                Color(0xFF000000)
                            )
                        )
                    )

                    // 2. Soft Red Spotlight behind title
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x25E50914),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, size.height * 0.14f),
                            radius = size.width * 0.45f
                        )
                    )

                    // 3. Subtle Broadcast Arc behind avatar row
                    val wavePath = Path().apply {
                        moveTo(-size.width * 0.1f, size.height * 0.55f)
                        quadraticTo(
                            x1 = size.width / 2f,
                            y1 = size.height * 0.85f,
                            x2 = size.width * 1.1f,
                            y2 = size.height * 0.55f
                        )
                    }
                    drawPath(
                        path = wavePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x33E50914),
                                Color.Transparent
                            )
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
                .padding(horizontal = 36.dp, vertical = 28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Title
                Text(
                    text = "Add New Profile",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (screenWidth.value * 0.032f).coerceIn(24f, 38f).sp
                    )
                )

                // 2. Text Edit Box ON TOP of the scrollable images
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(0.48f)
                ) {
                    OutlinedTextField(
                        value = profileName,
                        onValueChange = {
                            profileName = it
                            localError = null
                        },
                        label = { Text("Profile Name", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("Enter name", color = Color.White.copy(alpha = 0.35f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (avatarUrls.isNotEmpty()) {
                                    firstAvatarFocusRequester.requestFocus()
                                } else {
                                    createButtonFocusRequester.requestFocus()
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(textFieldFocusRequester)
                            .focusProperties {
                                down = if (avatarUrls.isNotEmpty()) {
                                    firstAvatarFocusRequester
                                } else {
                                    createButtonFocusRequester
                                }
                            },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.Green,
                            focusedBorderColor = Color.Green,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
                            focusedContainerColor = Color.White.copy(alpha = 0.06f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                        )
                    )

                    localError?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // 3. Scrollable Horizontal List of Circular Avatar Images
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Choose an Avatar",
                        color = Color.White.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.titleMedium
                    )

                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(
                            items = avatarUrls,
                            key = { index, url -> "$index-$url" }
                        ) { index, url ->
                            SelectableAvatarCircle(
                                imageUrl = url,
                                size = avatarSize,
                                isSelected = url == selectedAvatarUrl,
                                modifier = (if (index == 0) {
                                    Modifier.focusRequester(firstAvatarFocusRequester)
                                } else {
                                    Modifier
                                }).focusProperties {
                                    up = textFieldFocusRequester
                                    down = createButtonFocusRequester
                                },
                                onSelect = {
                                    selectedAvatarUrl = url
                                    localError = null
                                }
                            )
                        }
                    }
                }

                // 4. Bottom Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AccountsSectionDialogButton(
                        modifier = Modifier.focusProperties {
                            up = if (avatarUrls.isNotEmpty()) {
                                firstAvatarFocusRequester
                            } else {
                                textFieldFocusRequester
                            }
                        },
                        text = "Cancel",
                        shouldRequestFocus = false,
                        onClick = onDismissRequest
                    )

                    AccountsSectionDialogButton(
                        modifier = Modifier
                            .focusRequester(createButtonFocusRequester)
                            .focusProperties {
                                up = if (avatarUrls.isNotEmpty()) {
                                    firstAvatarFocusRequester
                                } else {
                                    textFieldFocusRequester
                                }
                            },
                        text = "Create",
                        shouldRequestFocus = false,
                        onClick = {
                            when {
                                profileName.trim().isBlank() -> {
                                    localError = "Please enter a profile name"
                                    textFieldFocusRequester.requestFocus()
                                }
                                avatarUrls.isNotEmpty() && selectedAvatarUrl.isBlank() -> {
                                    localError = "Please select an avatar"
                                }
                                else -> {
                                    onConfirmAddProfile(profileName.trim(), selectedAvatarUrl)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SelectableAvatarCircle(
    imageUrl: String,
    size: Dp,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.18f else if (isSelected) 1.06f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "AvatarScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged { isFocused = it.isFocused }
            .clickable(onClick = onSelect)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(
                    width = when {
                        isFocused -> 3.5.dp
                        isSelected -> 3.dp
                        else -> 1.5.dp
                    },
                    color = when {
                        isFocused -> Color.White
                        isSelected -> Color.Green // Matches your Netflix-red accent
                        else -> Color.White.copy(alpha = 0.25f)
                    },
                    shape = CircleShape
                )
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF020617))
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color.Green,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}