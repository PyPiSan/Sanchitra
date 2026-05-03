package com.pypisan.sanchitra.data.util


import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import androidx.compose.ui.graphics.*
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners


@Composable
fun GenerateQR(
    text: String,
    logoRes: Int? = null
) {
    val logoPainter = logoRes?.let { painterResource(id = it) }
    val painter = rememberQrCodePainter(text) {

        // Colors (gradient like your example)
        colors {
            dark = QrBrush.brush {
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFE082), // soft gold highlight
                        Color(0xFFFFC107), // rich gold
                        Color(0xFFFF5722), // warm red-orange
                        Color(0xFF8B0000)  // deep red
                    )
                )
            }
        }

        // Shapes (clean + slightly customized)
        shapes {
            ball = MyCircleBallShape() // custom shape
            darkPixel = QrPixelShape.roundCorners()
            frame = QrFrameShape.roundCorners(0.45f)
        }

        // Logo (better styling)
        if (logoPainter != null) {
            logo {
                painter = logoPainter
                size = 0.4f
                shape = QrLogoShape.circle()
            }
        }
    }

    Image(
        painter = painter,
        contentDescription = "QR",
        modifier = Modifier.size(220.dp)
    )
}