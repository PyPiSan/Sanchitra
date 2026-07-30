package com.pypisan.sanchitra.presentation.screens.videoPlayer.components


import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text

@Composable
fun SubtitleOverlay(
    subtitleText: String?,
    subtitleBitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    if (subtitleText.isNullOrBlank() && subtitleBitmap == null) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp)
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        when {
            subtitleBitmap != null -> {
                Image(
                    bitmap = subtitleBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            !subtitleText.isNullOrBlank() -> {
                Text(
                    text = subtitleText,
                    color = Color.White,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            blurRadius = 8f
                        )
                    ),
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 10.dp
                    )
                )
            }
        }
    }
}