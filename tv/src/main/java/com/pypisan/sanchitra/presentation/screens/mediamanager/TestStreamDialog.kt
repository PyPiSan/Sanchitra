package com.pypisan.sanchitra.presentation.screens.mediamanager

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.pypisan.sanchitra.presentation.screens.common.TvGlassRadioOption
import com.pypisan.sanchitra.presentation.screens.common.TvTextField

enum class StreamType { HLS, DRM }
enum class DrmType { LICENSE_URL, CLEAR_KEY }

data class CustomHeaderEntry(
    val id: Long,
    val key: String = "",
    val value: String = ""
)

data class TestStreamData(
    val streamName: String = "",
    val streamUrl: String = "",
    val streamType: StreamType = StreamType.HLS,
    val subtitleUrl: String = "",
    val logoUrl: String = "",
    val drmType: DrmType = DrmType.LICENSE_URL,
    val licenseUrl: String = "",
    val clearKeyKid: String = "",
    val clearKeyHex: String = "",
    val headers: Map<String, String> = emptyMap()
)

data class TestStreamDialogConfig(
    val title: String = "Test Streaming Link",
    val confirmText: String = "Play Test",
    val cancelText: String = "Cancel",
    val accentColor: Color = Color(0xFFFF5722),
    val focusBorderColor: Color = Color(0xFFFFE082),
    val defaultHlsHeaders: List<String> = listOf("User-Agent", "Referer", "Origin", "Cookie"),
    val defaultDrmHeaders: List<String> = listOf("User-Agent", "Authorization", "Referer"),
    val allowExtraCustomHeaders: Boolean = true,
    val maxWidth: Dp = 620.dp,
    val maxHeight: Dp = 490.dp
)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TestStreamDialog(
    showDialog: Boolean = true,
    initialData: TestStreamData = TestStreamData(),
    config: TestStreamDialogConfig = TestStreamDialogConfig(),
    onDismiss: () -> Unit,
    onConfirm: (TestStreamData) -> Unit
) {
    if (!showDialog) return

    var streamName by remember(showDialog) { mutableStateOf(initialData.streamName) }
    var streamUrl by remember(showDialog) { mutableStateOf(initialData.streamUrl) }
    var streamType by remember(showDialog) { mutableStateOf(initialData.streamType) }

    // HLS state
    var useHlsHeaders by remember(showDialog) {
        mutableStateOf(initialData.streamType == StreamType.HLS && initialData.headers.isNotEmpty())
    }
    val hlsDefaultValues = remember(true, config.defaultHlsHeaders) {
        mutableStateMapOf<String, String>().apply {
            config.defaultHlsHeaders.forEach { key ->
                put(key, initialData.headers[key] ?: "")
            }
        }
    }
    val hlsCustomHeaders = remember(showDialog) {
        mutableStateListOf<CustomHeaderEntry>().apply {
            var nextId = 1L
            initialData.headers.forEach { (k, v) ->
                if (k !in config.defaultHlsHeaders) {
                    add(CustomHeaderEntry(id = nextId++, key = k, value = v))
                }
            }
        }
    }
    var subtitleUrl by remember(showDialog) { mutableStateOf(initialData.subtitleUrl) }
    var logoUrl by remember(showDialog) { mutableStateOf(initialData.logoUrl) }

    // DRM state
    var drmType by remember(showDialog) { mutableStateOf(initialData.drmType) }
    var licenseUrl by remember(showDialog) { mutableStateOf(initialData.licenseUrl) }
    var useDrmHeaders by remember(showDialog) {
        mutableStateOf(initialData.streamType == StreamType.DRM && initialData.headers.isNotEmpty())
    }
    val drmDefaultValues = remember(showDialog, config.defaultDrmHeaders) {
        mutableStateMapOf<String, String>().apply {
            config.defaultDrmHeaders.forEach { key ->
                put(key, initialData.headers[key] ?: "")
            }
        }
    }
    val drmCustomHeaders = remember(showDialog) {
        mutableStateListOf<CustomHeaderEntry>().apply {
            var nextId = 100L
            initialData.headers.forEach { (k, v) ->
                if (k !in config.defaultDrmHeaders) {
                    add(CustomHeaderEntry(id = nextId++, key = k, value = v))
                }
            }
        }
    }
    var clearKeyKid by remember(showDialog) { mutableStateOf(initialData.clearKeyKid) }
    var clearKeyHex by remember(showDialog) { mutableStateOf(initialData.clearKeyHex) }

    var errorText by remember(showDialog) { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val dialogShape = RoundedCornerShape(20.dp)

    fun buildHeadersMap(
        enabled: Boolean,
        defaultKeys: List<String>,
        defaultValues: Map<String, String>,
        customList: List<CustomHeaderEntry>
    ): Map<String, String> {
        if (!enabled) return emptyMap()
        return buildMap {
            defaultKeys.forEach { key ->
                val value = defaultValues[key]?.trim().orEmpty()
                if (value.isNotEmpty()) put(key, value)
            }
            customList.forEach { entry ->
                val k = entry.key.trim()
                val v = entry.value.trim()
                if (k.isNotEmpty() && v.isNotEmpty()) put(k, v)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Silence OS Autofill popup/log on TV dialog
        val dialogView = LocalView.current
        LaunchedEffect(dialogView) {
            dialogView.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = config.maxWidth)
                .heightIn(max = config.maxHeight)
                .clip(dialogShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF23232E).copy(alpha = 0.96f),
                            Color(0xFF13131A).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f),
                            config.accentColor.copy(alpha = 0.30f)
                        )
                    ),
                    shape = dialogShape
                )
                .drawWithCache {
                    val sheen = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.09f),
                            Color.Transparent
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width * 0.65f, size.height * 0.40f)
                    )
                    onDrawBehind { drawRect(brush = sheen) }
                }
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Pinned Header
                Text(
                    text = config.title,
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
                                    config.accentColor.copy(alpha = 0.70f),
                                    Color.White.copy(alpha = 0.14f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form Body (keeps buttons always visible on 540dp TVs)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Streaming Name & URL
                    TvTextField(
                        value = streamName,
                        onValueChange = { streamName = it },
                        label = "Streaming Name",
                        placeholder = "Sports HD / Test Channel",
                        imeAction = ImeAction.Next,
                        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    TvTextField(
                        value = streamUrl,
                        onValueChange = {
                            streamUrl = it
                            errorText = null
                        },
                        label = "Streaming URL",
                        placeholder = if (streamType == StreamType.HLS) {
                            "https://example.com/live/stream.m3u8"
                        } else {
                            "https://example.com/live/manifest.mpd"
                        },
                        imeAction = ImeAction.Next,
                        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    // Stream Type Radio Selector (HLS vs DRM)
                    Text(
                        text = "Stream Type",
                        color = Color.White.copy(alpha = 0.80f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TvGlassRadioOption(
                            label = "HLS",
                            selected = streamType == StreamType.HLS,
                            accentColor = config.accentColor,
                            focusBorderColor = config.focusBorderColor,
                            onClick = {
                                streamType = StreamType.HLS
                                errorText = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TvGlassRadioOption(
                            label = "DRM",
                            selected = streamType == StreamType.DRM,
                            accentColor = config.accentColor,
                            focusBorderColor = config.focusBorderColor,
                            onClick = {
                                streamType = StreamType.DRM
                                errorText = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Conditional Section: HLS vs DRM
                    if (streamType == StreamType.HLS) {
                        TvHeadersSection(
                            title = "HLS Headers",
                            enabled = useHlsHeaders,
                            onToggle = { useHlsHeaders = !useHlsHeaders },
                            defaultKeys = config.defaultHlsHeaders,
                            defaultValues = hlsDefaultValues,
                            customHeaders = hlsCustomHeaders,
                            allowExtraCustomHeaders = config.allowExtraCustomHeaders,
                            accentColor = config.accentColor,
                            focusBorderColor = config.focusBorderColor
                        )

                        TvTextField(
                            value = subtitleUrl,
                            onValueChange = { subtitleUrl = it },
                            label = "Subtitle URL (Optional)",
                            placeholder = "https://example.com/subtitles.vtt",
                            imeAction = ImeAction.Next,
                            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        TvTextField(
                            value = logoUrl,
                            onValueChange = { logoUrl = it },
                            label = "Logo URL (Optional)",
                            placeholder = "https://example.com/logo.png",
                            imeAction = ImeAction.Done,
                            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    } else {
                        // DRM Type Radio Selector (License URL vs ClearKey)
                        Text(
                            text = "DRM Method",
                            color = Color.White.copy(alpha = 0.80f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TvGlassRadioOption(
                                label = "License URL",
                                selected = drmType == DrmType.LICENSE_URL,
                                accentColor = config.accentColor,
                                focusBorderColor = config.focusBorderColor,
                                onClick = {
                                    drmType = DrmType.LICENSE_URL
                                    errorText = null
                                },
                                modifier = Modifier.weight(1f)
                            )
                            TvGlassRadioOption(
                                label = "ClearKey",
                                selected = drmType == DrmType.CLEAR_KEY,
                                accentColor = config.accentColor,
                                focusBorderColor = config.focusBorderColor,
                                onClick = {
                                    drmType = DrmType.CLEAR_KEY
                                    errorText = null
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (drmType == DrmType.LICENSE_URL) {
                            TvTextField(
                                value = licenseUrl,
                                onValueChange = {
                                    licenseUrl = it
                                    errorText = null
                                },
                                label = "DRM License URL",
                                placeholder = "https://license.example.com/widevine",
                                imeAction = ImeAction.Next,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                            )

                            TvHeadersSection(
                                title = "License Headers",
                                enabled = useDrmHeaders,
                                onToggle = { useDrmHeaders = !useDrmHeaders },
                                defaultKeys = config.defaultDrmHeaders,
                                defaultValues = drmDefaultValues,
                                customHeaders = drmCustomHeaders,
                                allowExtraCustomHeaders = config.allowExtraCustomHeaders,
                                accentColor = config.accentColor,
                                focusBorderColor = config.focusBorderColor
                            )
                        } else {
                            // ClearKey: Only KID and Hex
                            TvTextField(
                                value = clearKeyKid,
                                onValueChange = {
                                    clearKeyKid = it
                                    errorText = null
                                },
                                label = "ClearKey KID (Hex)",
                                placeholder = "e.g. 1234567890abcdef1234567890abcdef",
                                imeAction = ImeAction.Next,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                            )

                            TvTextField(
                                value = clearKeyHex,
                                onValueChange = {
                                    clearKeyHex = it
                                    errorText = null
                                },
                                label = "ClearKey Hex Key",
                                placeholder = "e.g. abcdef1234567890abcdef1234567890",
                                imeAction = ImeAction.Done,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        }
                    }

                    errorText?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pinned Bottom Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlossyDialogButton(
                        text = config.cancelText,
                        onClick = onDismiss,
                        accentColor = Color.White,
                        focusBorderColor = config.focusBorderColor,
                        isPrimary = false
                    )
                    GlossyDialogButton(
                        text = config.confirmText,
                        onClick = {
                            when {
                                streamUrl.isBlank() -> {
                                    errorText = "Please enter a streaming URL."
                                }
                                streamType == StreamType.DRM &&
                                        drmType == DrmType.LICENSE_URL &&
                                        licenseUrl.isBlank() -> {
                                    errorText = "Please enter a DRM license URL."
                                }
                                streamType == StreamType.DRM &&
                                        drmType == DrmType.CLEAR_KEY &&
                                        (clearKeyKid.isBlank() || clearKeyHex.isBlank()) -> {
                                    errorText = "Please enter both ClearKey KID and Hex Key."
                                }
                                else -> {
                                    errorText = null
                                    val resolvedHeaders = when {
                                        streamType == StreamType.HLS -> buildHeadersMap(
                                            enabled = useHlsHeaders,
                                            defaultKeys = config.defaultHlsHeaders,
                                            defaultValues = hlsDefaultValues,
                                            customList = hlsCustomHeaders
                                        )
                                        drmType == DrmType.LICENSE_URL -> buildHeadersMap(
                                            enabled = useDrmHeaders,
                                            defaultKeys = config.defaultDrmHeaders,
                                            defaultValues = drmDefaultValues,
                                            customList = drmCustomHeaders
                                        )
                                        else -> emptyMap()
                                    }

                                    onConfirm(
                                        TestStreamData(
                                            streamName = streamName.trim(),
                                            streamUrl = streamUrl.trim(),
                                            streamType = streamType,
                                            subtitleUrl = if (streamType == StreamType.HLS) subtitleUrl.trim() else "",
                                            logoUrl = if (streamType == StreamType.HLS) logoUrl.trim() else "",
                                            drmType = drmType,
                                            licenseUrl = if (streamType == StreamType.DRM && drmType == DrmType.LICENSE_URL) {
                                                licenseUrl.trim()
                                            } else "",
                                            clearKeyKid = if (streamType == StreamType.DRM && drmType == DrmType.CLEAR_KEY) {
                                                clearKeyKid.trim()
                                            } else "",
                                            clearKeyHex = if (streamType == StreamType.DRM && drmType == DrmType.CLEAR_KEY) {
                                                clearKeyHex.trim()
                                            } else "",
                                            headers = resolvedHeaders
                                        )
                                    )
                                }
                            }
                        },
                        accentColor = config.accentColor,
                        focusBorderColor = config.focusBorderColor,
                        isPrimary = true
                    )
                }
            }
        }
    }
}


@Composable
private fun TvHeadersSection(
    title: String,
    enabled: Boolean,
    onToggle: () -> Unit,
    defaultKeys: List<String>,
    defaultValues: MutableMap<String, String>,
    customHeaders: MutableList<CustomHeaderEntry>,
    allowExtraCustomHeaders: Boolean,
    accentColor: Color,
    focusBorderColor: Color
) {
    val toggleInteraction = remember { MutableInteractionSource() }
    val isToggleFocused by toggleInteraction.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Glass Toggle Row
        Surface(
            onClick = onToggle,
            interactionSource = toggleInteraction,
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(
                width = if (isToggleFocused || enabled) 1.5.dp else 1.dp,
                color = when {
                    isToggleFocused -> focusBorderColor
                    enabled -> accentColor.copy(alpha = 0.70f)
                    else -> Color.White.copy(alpha = 0.20f)
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        when {
                            enabled -> accentColor.copy(alpha = 0.16f)
                            isToggleFocused -> Color(0xFF323240).copy(alpha = 0.90f)
                            else -> Color(0xFF1F1F28).copy(alpha = 0.85f)
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (enabled) accentColor else Color.Transparent)
                        .border(
                            width = 1.5.dp,
                            color = if (enabled) accentColor else Color.White.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (enabled) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (enabled) "Enter values for default headers" else "Select to add custom HTTP headers",
                        color = Color.White.copy(alpha = 0.60f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Expanded Default Header Name -> Value Fields
        AnimatedVisibility(visible = enabled) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF181820).copy(alpha = 0.90f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                defaultKeys.forEach { headerKey ->
                    TvTextField(
                        value = defaultValues[headerKey] ?: "",
                        onValueChange = { defaultValues[headerKey] = it },
                        label = headerKey,
                        placeholder = "Enter $headerKey value",
                        imeAction = ImeAction.Next,
                        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                }

                customHeaders.forEachIndexed { index, entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(0.42f)) {
                            TvTextField(
                                value = entry.key,
                                onValueChange = { newKey ->
                                    customHeaders[index] = entry.copy(key = newKey)
                                },
                                label = "Header Name",
                                placeholder = "X-Custom",
                                imeAction = ImeAction.Next,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Right) }
                            )
                        }
                        Box(modifier = Modifier.weight(0.58f)) {
                            TvTextField(
                                value = entry.value,
                                onValueChange = { newVal ->
                                    customHeaders[index] = entry.copy(value = newVal)
                                },
                                label = "Value",
                                placeholder = "Header value",
                                imeAction = ImeAction.Next,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        }
                        IconButton(
                            onClick = { customHeaders.removeAt(index) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove header",
                                tint = Color(0xFFFF6B6B)
                            )
                        }
                    }
                }

                if (allowExtraCustomHeaders) {
                    GlossyDialogButton(
                        text = "+ Add Custom Header",
                        onClick = {
                            val nextId = (customHeaders.maxOfOrNull { it.id } ?: 0L) + 1L
                            customHeaders.add(CustomHeaderEntry(id = nextId))
                        },
                        accentColor = Color.White,
                        focusBorderColor = focusBorderColor,
                        isPrimary = false
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
    focusBorderColor: Color,
    isPrimary: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        label = "dialogBtnScale"
    )
    val baseBgColor by animateColorAsState(
        targetValue = when {
            isPrimary && isFocused -> accentColor.copy(alpha = 0.96f)
            isPrimary -> accentColor.copy(alpha = 0.55f)
            isFocused -> Color(0xFF3E3E4E).copy(alpha = 0.95f)
            else -> Color(0xFF252530).copy(alpha = 0.85f)
        },
        label = "dialogBtnBg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isFocused -> focusBorderColor
            isPrimary -> accentColor.copy(alpha = 0.60f)
            else -> Color.White.copy(alpha = 0.24f)
        },
        label = "dialogBtnBorder"
    )

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
        shape = RoundedCornerShape(12.dp),
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
                .background(
                    brush = Brush.verticalGradient(
                        0.00f to Color.White.copy(alpha = if (isFocused) 0.34f else 0.16f),
                        0.48f to Color.White.copy(alpha = if (isFocused) 0.12f else 0.05f),
                        0.50f to Color.Transparent,
                        1.00f to Color.White.copy(alpha = if (isFocused) 0.10f else 0.03f)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isFocused || isPrimary) Color.White else Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                fontWeight = if (isFocused || isPrimary) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}