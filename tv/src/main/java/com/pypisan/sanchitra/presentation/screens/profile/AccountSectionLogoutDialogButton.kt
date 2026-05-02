package com.pypisan.sanchitra.presentation.screens.profile

import JetStreamCardShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.tvmaterial.StandardDialog

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun AccountLogoutDialog(
    showDialog: Boolean,
    isLoading: Boolean,
    message: String?,
    onDismissRequest: () -> Unit,
    onConfirmLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    StandardDialog(
        showDialog = showDialog,
        modifier = modifier,
        onDismissRequest = {
            if (!isLoading) onDismissRequest()
        },
        confirmButton = {
            if (!isLoading && message == null) {
                AccountsSectionDialogButton(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Logout",
                    shouldRequestFocus = true,
                    onClick = onConfirmLogout
                )
            }
        },
        dismissButton = {
            if (!isLoading) {
                AccountsSectionDialogButton(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "Cancel",
                    shouldRequestFocus = false,
                    onClick = onDismissRequest
                )
            }
        },
        title = {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = when {
                    isLoading -> "Logging out..."
                    message != null -> message
                    else -> "Logout"
                },
                color = MaterialTheme.colorScheme.surface,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Please wait...",
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    message != null -> {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        Text(
                            text = "Are you sure you want to logout from your account?",
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.onSurface,
        shape = JetStreamCardShape
    )
}