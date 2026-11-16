package com.pypisan.sanchitra.presentation.screens.mediamanager

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pypisan.sanchitra.presentation.screens.common.TvGlassDialog
import com.pypisan.sanchitra.presentation.screens.common.TvTextField

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

    TvGlassDialog(
        showDialog = showDialog,
        title = "Import Media / Channels from URL",
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
            label = "Playlist / Source Name",
            placeholder = "My Channels",
            enabled = !isLoading,
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        Spacer(modifier = Modifier.height(14.dp))
        TvTextField(
            value = url,
            onValueChange = {
                url = it
                validationError = null
            },
            label = "Web URL (M3U / M3U8 / JSON)",
            placeholder = "https://example.com/playlist.m3u",
            enabled = !isLoading,
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        displayedError?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = it,
                color = Color(0xFFFF6B6B),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}