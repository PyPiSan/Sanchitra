package com.pypisan.sanchitra.presentation.screens.videoPlayer.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text

@Composable
fun SubtitleTextOverlay(
    subtitleText: String,
    modifier: Modifier = Modifier
) {

    if (subtitleText.isBlank()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {

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
            modifier = Modifier
//                .background(
//                    Color.Black.copy(alpha = 0.82f),
//                    RoundedCornerShape(8.dp)
//                )
                .padding(
                    horizontal = 18.dp,
                    vertical = 10.dp
                )
        )
    }
}