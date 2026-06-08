package com.pypisan.sanchitra.presentation.screens.profile

import com.pypisan.sanchitra.presentation.theme.JetStreamCardShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.tvmaterial.StandardDialog

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun AccountsSectionDeleteDialog(
    showDialog: Boolean,
    isLoading: Boolean,
    message: String?,
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
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
                    text = stringResource(R.string.yes_delete_account),
                    shouldRequestFocus = true,
                    onClick = onConfirmDelete
                )
            }
        },
        dismissButton = {
            if (!isLoading) {
                AccountsSectionDialogButton(
                    modifier = Modifier.padding(end = 8.dp),
                    text = stringResource(R.string.no_keep_it),
                    shouldRequestFocus = false,
                    onClick = onDismissRequest
                )
            }
        },
        title = {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = when {
                    isLoading -> "Deleting..."
                    message != null -> message
                    else -> stringResource(R.string.delete_account_dialog_title)
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
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = stringResource(R.string.delete_account_dialog_text),
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
