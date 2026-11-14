package com.pypisan.sanchitra.presentation.screens.mediamanager

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.pypisan.sanchitra.tvmaterial.StandardDialog
import java.io.File

@Composable
fun ImportUrlDialog(
    showDialog: Boolean = true,
    isLoading: Boolean = false,
    serverError: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, url: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val displayedError = validationError ?: serverError

    TvStandardDialog(
        showDialog = showDialog,
        title = "Import Media/Channels from URL",
        confirmText = if (isLoading) "Importing..." else "Add / Import",
        isLoading = isLoading,
        onDismiss = { if (!isLoading) onDismiss() },
        onConfirm = {
            if (url.isBlank()) {
                validationError = "Please enter a playlist or media URL."
            } else {
                validationError = null
                onConfirm(name.trim(), url.trim())
            }
        }
    ) {
        TvTextField(
            value = name,
            onValueChange = { name = it },
            label = "Playlist / Source Name (Optional)",
            placeholder = "My Channels",
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TvTextField(
            value = url,
            onValueChange = {
                url = it
                validationError = null
            },
            label = "Web URL (M3U / M3U8 / JSON)",
            placeholder = "https://example.com/playlist.m3u",
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        displayedError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color(0xFFEF5350), fontSize = 13.sp)
        }
    }
}

@Composable
fun TestStreamDialog(
    showDialog: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (streamUrl: String) -> Unit
) {
    var streamUrl by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    TvStandardDialog(
        showDialog = showDialog,
        title = "Test Streaming Link",
        confirmText = "Play Test",
        onDismiss = onDismiss,
        onConfirm = {
            if (streamUrl.isBlank()) {
                errorText = "Please enter a stream URL."
            } else {
                onConfirm(streamUrl.trim())
            }
        }
    ) {
        TvTextField(
            value = streamUrl,
            onValueChange = {
                streamUrl = it
                errorText = null
            },
            label = "Stream URL (HLS / DASH / MP4)",
            placeholder = "https://example.com/live/stream.m3u8",
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        errorText?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color(0xFFEF5350), fontSize = 13.sp)
        }
    }
}

@Composable
fun TestDrmDialog(
    showDialog: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (streamUrl: String, licenseUrl: String, drmScheme: String) -> Unit
) {
    var streamUrl by remember { mutableStateOf("") }
    var licenseUrl by remember { mutableStateOf("") }
    var selectedScheme by remember { mutableStateOf("Widevine") }
    var errorText by remember { mutableStateOf<String?>(null) }
    val schemes = listOf("Widevine", "ClearKey", "PlayReady")
    val focusManager = LocalFocusManager.current

    TvStandardDialog(
        showDialog = showDialog,
        title = "Test DRM Link",
        confirmText = "Test DRM",
        onDismiss = onDismiss,
        onConfirm = {
            if (streamUrl.isBlank() || licenseUrl.isBlank()) {
                errorText = "Please enter both stream URL and license URL."
            } else {
                onConfirm(streamUrl.trim(), licenseUrl.trim(), selectedScheme)
            }
        }
    ) {
        TvTextField(
            value = streamUrl,
            onValueChange = {
                streamUrl = it
                errorText = null
            },
            label = "MPD / Stream URL",
            placeholder = "https://example.com/manifest.mpd",
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TvTextField(
            value = licenseUrl,
            onValueChange = {
                licenseUrl = it
                errorText = null
            },
            label = "License Server URL",
            placeholder = "https://license.example.com/widevine",
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("DRM Scheme", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            schemes.forEach { scheme ->
                FilterChip(
                    selected = selectedScheme == scheme,
                    onClick = { selectedScheme = scheme },
                    label = { Text(scheme) }
                )
            }
        }
        errorText?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color(0xFFEF5350), fontSize = 13.sp)
        }
    }
}

@Composable
fun ImportLocalDiskDialog(
    showDialog: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (Uri) -> Unit
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var manualPath by remember { mutableStateOf("") }
    var pickerUnavailable by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            manualPath = ""
            pickerUnavailable = false
            errorText = null
        }
    }

    val resolvedUri: Uri? = selectedUri ?: manualPath.trim()
        .takeIf { it.isNotEmpty() }
        ?.let { Uri.fromFile(File(it)) }

    TvStandardDialog(
        showDialog = showDialog,
        title = "Import from Local Disk",
        confirmText = "Import File",
        onDismiss = onDismiss,
        onConfirm = {
            if (resolvedUri == null) {
                errorText = "Please browse and select a file first."
            } else {
                onConfirm(resolvedUri)
            }
        }
    ) {
        Text(
            text = "Choose an M3U, JSON, or media file stored on your TV or USB drive.",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                try {
                    filePicker.launch(arrayOf("*/*"))
                } catch (_: ActivityNotFoundException) {
                    pickerUnavailable = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color(0xFFFFE082)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFE082))
        ) {
            Text(if (selectedUri == null) "Browse Local Disk" else "Change Selected File")
        }

        if (selectedUri != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Selected: ${selectedUri?.lastPathSegment ?: selectedUri.toString()}",
                color = Color(0xFF81C784),
                fontSize = 13.sp
            )
        }

        if (pickerUnavailable) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No system file picker found on this TV. Enter the file path manually below:",
                color = Color(0xFFEF5350),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            TvTextField(
                value = manualPath,
                onValueChange = {
                    manualPath = it
                    selectedUri = null
                    errorText = null
                },
                label = "Local / USB File Path",
                placeholder = "/storage/emulated/0/Download/playlist.m3u",
                imeAction = ImeAction.Done,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )
        }

        errorText?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color(0xFFEF5350), fontSize = 13.sp)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
private fun TvStandardDialog(
    showDialog: Boolean,
    title: String,
    confirmText: String,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A20),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        },
        confirmButton = {
            var isConfirmFocused by remember { mutableStateOf(false) }
            Button(
                onClick = { if (!isLoading) onConfirm() },
                modifier = Modifier.onFocusChanged { isConfirmFocused = it.isFocused },
                border = if (isConfirmFocused) BorderStroke(2.dp, Color(0xFFFFE082)) else null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White
                )
            ) {
                Text(confirmText, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            var isCancelFocused by remember { mutableStateOf(false) }
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.onFocusChanged { isCancelFocused = it.isFocused },
                border = BorderStroke(
                    width = if (isCancelFocused) 2.dp else 1.dp,
                    color = if (isCancelFocused) Color(0xFFFFE082) else Color.White.copy(alpha = 0.35f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun TvTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    var isFocused by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) } // Prevents keyboard from auto-opening

    // Open keyboard only after user explicitly clicks the box
    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // Touch / mouse click enters edit mode
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                isEditing = true
            }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = !isEditing, // Stays focusable for D-pad without popping up keyboard!
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.35f)) },
        singleLine = true,
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = {
                isEditing = false
                onImeAction()
            },
            onDone = {
                isEditing = false
                keyboardController?.hide()
                onImeAction()
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { state ->
                isFocused = state.isFocused
                if (!state.isFocused && isEditing) {
                    isEditing = false
                    keyboardController?.hide()
                }
            }
            .onPreviewKeyEvent { keyEvent ->
                val isSelectKey = keyEvent.key == Key.DirectionCenter ||
                        keyEvent.key == Key.Enter ||
                        keyEvent.key == Key.NumPadEnter

                if (!isEditing && isSelectKey) {
                    if (keyEvent.type == KeyEventType.KeyUp) {
                        isEditing = true
                    }
                    true // Consume both KeyDown & KeyUp to open keyboard cleanly
                } else {
                    false
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFFFFE082),
            unfocusedBorderColor = if (isFocused) Color(0xFFFFE082) else Color.White.copy(alpha = 0.25f),
            focusedLabelColor = Color(0xFFFFE082),
            unfocusedLabelColor = Color.White.copy(alpha = 0.65f),
            cursorColor = Color(0xFFFFE082)
        )
    )
}