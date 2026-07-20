package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun ErrorOverlay(
    modifier: Modifier = Modifier,
    message: String = "Playback has encountered an issue. Please try again.",
    buttonText: String = "Retry",
    onRetry: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    // Demand focus immediately when the overlay appears for D-pad control
    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
            // Ignore if layout not ready
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            // Darken the background more dramatically for contrast (0.9 alpha)
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        // We use a Card to contain the error info, optimized for TV screen width.
        Card(
            modifier = Modifier
                .width(480.dp) // optimized width for TV
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp), // Modern, soft corners
            // Neutral dark background for the card
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp)
            ) {
                // A large icon adds visual context
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Error Indicator",
                    tint = Color(0xFFFFB74D), // Soft Amber warning color
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Upgraded title/headline text
                Text(
                    text = message,
                    color = Color.White,
                    // Use Material Headline styling
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    // Kept center alignment and multiline
                    textAlign = TextAlign.Center,
                    softWrap = true,
                    // Added lineHeight for better vertical readability
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Standard Button component handles D-pad focus effects natively
                // and feels much more polished than manual Box styling.
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth() // Button fills the card width
                        .height(56.dp), // Significant focus target height
                    colors = ButtonDefaults.buttonColors(
                        // High contrast: white button, black text/icon
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    // Combine icon and text inside the button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = buttonText.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp // Add slight letter spacing
                        )
                    }
                }
            }
        }
    }
}