package com.pypisan.sanchitra.presentation.screens.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.pypisan.sanchitra.presentation.theme.SanchitraCardShape
import com.pypisan.sanchitra.tvmaterial.StandardDialog


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun AccountSectionActionDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    title: String,
    description: String,
    message: String? = null,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    closeButtonText: String = "Close",
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    StandardDialog(
        showDialog = showDialog,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            // Confirmation mode: Show Confirm button
            if (message == null) {
                AccountsSectionDialogButton(
                    modifier = Modifier.padding(start = 8.dp),
                    text = confirmButtonText,
                    shouldRequestFocus = true,
                    onClick = onConfirm
                )
            }
        },
        dismissButton = {
            // Confirmation mode: "Cancel" | Failure mode: "Close"
            AccountsSectionDialogButton(
                modifier = Modifier.padding(end = 8.dp),
                text = if (message == null) dismissButtonText else closeButtonText,
                shouldRequestFocus = message != null,
                onClick = onDismissRequest
            )
        },
        title = {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = if (message != null) "Action Failed" else title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = message ?: description,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = SanchitraCardShape
    )
}