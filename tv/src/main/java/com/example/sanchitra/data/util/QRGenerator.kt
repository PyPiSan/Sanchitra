package com.example.sanchitra.data.util


import android.content.Context
import android.graphics.*
import androidx.core.graphics.drawable.toBitmap
import com.example.sanchitra.presentation.screens.auth.getBitmapFromDrawable
import com.github.alexzhirkevich.customqrgenerator.*
import com.github.alexzhirkevich.customqrgenerator.vector.*
import com.github.alexzhirkevich.customqrgenerator.style.Neighbors
import com.github.alexzhirkevich.customqrgenerator.style.QrBallShape
import com.github.alexzhirkevich.customqrgenerator.style.QrColor
import com.github.alexzhirkevich.customqrgenerator.style.QrFrameShape
import com.github.alexzhirkevich.customqrgenerator.style.QrPixelShape


fun generateTelegramQR(
    context: Context,
    text: String,
    logoRes: Int? = null
): Bitmap {

    val options = createQrOptions(1024, 1024, .1f) {

        colors {
            dark = QrColor.LinearGradient(
                startColor = 0xffbc077a.toInt(),
                endColor = 0xff9a10a4.toInt(),
                orientation = QrColor.LinearGradient.Orientation.LeftDiagonal
            )
            light = QrColor.Solid(0xFFFFFFFF.toInt())
        }

        shapes {
            frame = QrFrameShape.RoundCorners(.3f)
            ball = QrBallShape.Circle()
            darkPixel = CircleRandomRadius()
        }
    }

    // ✅ Use drawable WITHOUT passing options (your version limitation)
    val drawable = QrCodeDrawable(QrData.Url(text))

    val bitmap = drawable.toBitmap(1024, 1024)

    // ✅ Apply logo manually (reliable across versions)
    if (logoRes != null) {
        val canvas = Canvas(bitmap)
        val logo = context.getBitmapFromDrawable(logoRes)

        val size = (bitmap.width * 0.3f).toInt()
        val left = (bitmap.width - size) / 2f
        val top = (bitmap.height - size) / 2f

        val scaled = Bitmap.createScaledBitmap(logo, size, size, true)

        canvas.drawRoundRect(
            RectF(
                left - 10,
                top - 10,
                left + size + 10,
                top + size + 10
            ),
            30f,
            30f,
            Paint().apply { color = Color.WHITE }
        )

        canvas.drawBitmap(scaled, left, top, null)
    }

    return bitmap
}

class CircleRandomRadius : QrPixelShape {

    private val shapes = listOf(
        QrPixelShape.Circle(.6f),
        QrPixelShape.Circle(.75f),
        QrPixelShape.Circle(.9f),
    )

    private var lastNeighbors = Neighbors.Empty
    private var lastShape = shapes[0]

    override fun invoke(i: Int, j: Int, elementSize: Int, neighbors: Neighbors): Boolean {
        if (lastNeighbors != neighbors) {
            lastNeighbors = neighbors
            // ✅ stable (no flicker)
            lastShape = shapes[(i + j) % shapes.size]
        }
        return lastShape.invoke(i, j, elementSize, neighbors)
    }
}