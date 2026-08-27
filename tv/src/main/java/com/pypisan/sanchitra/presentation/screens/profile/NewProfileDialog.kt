package com.pypisan.sanchitra.presentation.screens.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.pypisan.sanchitra.presentation.theme.SanchitraCardShape
import com.pypisan.sanchitra.tvmaterial.StandardDialog
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun NewProfileDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    message: String? = null,
    onDismissRequest: () -> Unit,
    onConfirmAddProfile: (profileName: String) -> Unit,
) {
    var profileName by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val textFieldFocusRequester = remember { FocusRequester() }
    val createButtonFocusRequester = remember { FocusRequester() }

    // Reset input and request focus on the text field when dialog opens
    LaunchedEffect(showDialog) {
        if (showDialog) {
            profileName = ""
            localError = null
            delay(150)
            textFieldFocusRequester.requestFocus()
        }
    }

    StandardDialog(
        showDialog = showDialog,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (message == null) {
                AccountsSectionDialogButton(
                    modifier = Modifier.padding(start = 8.dp)
                        .focusRequester(createButtonFocusRequester)
                        .focusProperties {
                            up = textFieldFocusRequester
                        }
                    ,
                    text = "Create",
                    shouldRequestFocus = false,
                    onClick = {
                        if (profileName.trim().isBlank()) {
                            localError = "Please enter a profile name"
                        } else {
                            onConfirmAddProfile(profileName.trim())
                        }
                    }
                )
            }
        },
        dismissButton = {
            AccountsSectionDialogButton(
                modifier = Modifier.padding(end = 8.dp),
                text = if (message == null) "Cancel" else "Close",
                shouldRequestFocus = message != null,
                onClick = onDismissRequest
            )
        },
        title = {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = if (message != null) "Action Failed" else "Add New Profile",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (message != null) {
                    // Failure / Error View
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    // Profile Name Input Field
                    OutlinedTextField(
                        value = profileName,
                        onValueChange = {
                            profileName = it
                            localError = null
                        },
                        label = { Text("Profile Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusProperties {
                                down = createButtonFocusRequester
                            }
                            .focusRequester(textFieldFocusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )

                    localError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = SanchitraCardShape
    )
}