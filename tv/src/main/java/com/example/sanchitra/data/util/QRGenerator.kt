package com.example.sanchitra.data.util

import android.graphics.*
import android.graphics.Color.WHITE
import androidx.core.graphics.toColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale

// fun generateTelegramStyleQR(
//     text: String,
//     size: Int = 512,
//     logo: Bitmap? = null
// ): Bitmap {
//     val hints = mapOf(
//         EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
//         EncodeHintType.MARGIN to 1 // Smaller margin for a tighter look
//     )

//     val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
//     val bitmap = createBitmap(size, size)
//     val canvas = Canvas(bitmap)

//     // Colors: Telegram-style gradient
//     val startColor = "#0088CC".toColorInt()
//     val endColor = "#39A9ED".toColorInt()

//     val paint = Paint(Paint.ANTI_ALIAS_FLAG)
//     val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = WHITE }

//     // Create Gradient Shader
//     paint.shader = LinearGradient(
//         0f, 0f, size.toFloat(), size.toFloat(),
//         startColor, endColor, Shader.TileMode.CLAMP
//     )

//     // Fill background
//     canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), bgPaint)

//     val cell = size / matrix.width.toFloat()
//     val mWidth = matrix.width

//     // Helper: Identify regions
//     fun isFinder(x: Int, y: Int): Boolean {
//         return (x < 7 && y < 7) || (x > mWidth - 8 && y < 7) || (x < 7 && y > mWidth - 8)
//     }

//     // Helper: Identify center logo area (to clear space)
//     fun isCenter(x: Int, y: Int): Boolean {
//         val centerStart = mWidth / 2 - 3
//         val centerEnd = mWidth / 2 + 3
//         return x in centerStart..centerEnd && y in centerStart..centerEnd
//     }

//     // 1. Draw Data Modules (Circles)
//     for (x in 0 until mWidth) {
//         for (y in 0 until mWidth) {
//             if (matrix[x, y] && !isFinder(x, y) && !isCenter(x, y)) {
//                 val cx = x * cell + cell / 2f
//                 val cy = y * cell + cell / 2f
//                 // Telegram uses circles for data modules
//                 canvas.drawCircle(cx, cy, cell * 0.38f, paint)
//             }
//         }
//     }

//     // 2. Draw Stylish Finder Patterns
//     fun drawFinder(startX: Int, startY: Int) {
//         val left = startX * cell
//         val top = startY * cell

//         // Outer Squircle-like Frame
//         val outerRect = RectF(
//             left + cell * 0.5f,
//             top + cell * 0.5f,
//             left + cell * 6.5f,
//             top + cell * 6.5f
//         )

//         val innerRect = RectF(
//             left + cell * 1.8f,
//             top + cell * 1.8f,
//             left + cell * 5.2f,
//             top + cell * 5.2f
//         )

//         // Draw outer frame with thick stroke
//         val path = Path().apply {
//             addRoundRect(outerRect, cell * 2f, cell * 2f, Path.Direction.CW)
//             // Cut the inner hole
//             addRoundRect(innerRect, cell * 0.8f, cell * 0.8f, Path.Direction.CCW)
//         }
//         canvas.drawPath(path, paint)

//         // Draw the inner "pupil" (Circle/Rounded Square)
//         val pupilRect = RectF(
//             left + cell * 2.5f,
//             top + cell * 2.5f,
//             left + cell * 4.5f,
//             top + cell * 4.5f
//         )
//         canvas.drawRoundRect(pupilRect, cell * 0.8f, cell * 0.8f, paint)
//     }

//     drawFinder(0, 0) // Top Left
//     drawFinder(mWidth - 7, 0) // Top Right
//     drawFinder(0, mWidth - 7) // Bottom Left

//     // 3. Draw Stylish Logo with White Padding
//     logo?.let {
//         val logoSize = (size * 0.22f).toInt()
//         val left = (size - logoSize) / 2f
//         val top = (size - logoSize) / 2f

//         // Draw white circle/squircle behind logo
//         val logoBg = RectF(left - 10, top - 10, left + logoSize + 10, top + logoSize + 10)
//         canvas.drawRoundRect(logoBg, logoSize * 0.4f, logoSize * 0.4f, bgPaint)

//         // Draw the logo itself
//         val scaledLogo = it.scale(logoSize, logoSize)

//         // Optional: Mask logo into a circle for extra style
//         val logoPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//         canvas.drawBitmap(scaledLogo, left, top, logoPaint)
//     }

//     return bitmap
// }


fun generateTelegramStyleQR(
    text: String,
    size: Int = 512,
    logo: Bitmap? = null,
    startColor: Int = "#0088CC".toColorInt(),
    endColor: Int = "#39A9ED".toColorInt(),
    backgroundColor: Int = WHITE,
    moduleRadius: Float = 0.38f,
): Bitmap {
    // --- Encode ---
    val matrix = QRCodeWriter().encode(
        text, BarcodeFormat.QR_CODE, size, size,
        mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.MARGIN to 1
        )
    )

    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    val mWidth = matrix.width
    val cell = size / mWidth.toFloat()

    // --- Paints ---
    val gradientShader = LinearGradient(
        0f, 0f, size.toFloat(), size.toFloat(),
        startColor, endColor, Shader.TileMode.CLAMP
    )
    val qrPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = gradientShader }
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = backgroundColor }

    // --- Background with rounded corners ---
    val cornerRadius = size * 0.06f
    canvas.drawRoundRect(
        RectF(0f, 0f, size.toFloat(), size.toFloat()),
        cornerRadius, cornerRadius, bgPaint
    )

    // --- Region helpers ---
    fun isFinder(x: Int, y: Int) =
        (x < 7 && y < 7) || (x > mWidth - 8 && y < 7) || (x < 7 && y > mWidth - 8)

    fun isLogoZone(x: Int, y: Int): Boolean {
        val half = mWidth / 2
        val pad = if (logo != null) 4 else 0
        return x in (half - pad)..(half + pad) && y in (half - pad)..(half + pad)
    }

    // --- Data modules ---
    for (x in 0 until mWidth) {
        for (y in 0 until mWidth) {
            if (matrix[x, y] && !isFinder(x, y) && !isLogoZone(x, y)) {
                val cx = x * cell + cell / 2f
                val cy = y * cell + cell / 2f
                canvas.drawCircle(cx, cy, cell * moduleRadius, qrPaint)
            }
        }
    }

    // --- Finder patterns ---
    fun drawFinder(startX: Int, startY: Int) {
        val left = startX * cell
        val top = startY * cell

        val outerRect = RectF(left + cell * 0.5f, top + cell * 0.5f, left + cell * 6.5f, top + cell * 6.5f)
        val innerRect = RectF(left + cell * 1.8f, top + cell * 1.8f, left + cell * 5.2f, top + cell * 5.2f)
        val pupilRect = RectF(left + cell * 2.5f, top + cell * 2.5f, left + cell * 4.5f, top + cell * 4.5f)

        // Outer ring (frame with hole)
        canvas.drawPath(Path().apply {
            fillType = Path.FillType.EVEN_ODD
            addRoundRect(outerRect, cell * 2f, cell * 2f, Path.Direction.CW)
            addRoundRect(innerRect, cell * 0.8f, cell * 0.8f, Path.Direction.CW)
        }, qrPaint)

        // Inner pupil
        canvas.drawRoundRect(pupilRect, cell * 0.8f, cell * 0.8f, qrPaint)
    }

    drawFinder(0, 0)
    drawFinder(mWidth - 7, 0)
    drawFinder(0, mWidth - 7)

    // --- Logo ---
    logo?.let { src ->
        val logoSize = (size * 0.22f).toInt()
        val cx = (size - logoSize) / 2f
        val cy = (size - logoSize) / 2f
        val padding = logoSize * 0.18f

        // Shadow-like soft glow behind logo
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = backgroundColor
            maskFilter = BlurMaskFilter(padding * 1.5f, BlurMaskFilter.Blur.NORMAL)
        }
        val bgRect = RectF(cx - padding, cy - padding, cx + logoSize + padding, cy + logoSize + padding)
        canvas.drawRoundRect(bgRect, logoSize * 0.35f, logoSize * 0.35f, shadowPaint)

        // Solid white card behind logo
        canvas.drawRoundRect(bgRect, logoSize * 0.35f, logoSize * 0.35f, bgPaint)

        // Circular-masked logo
        val scaledLogo = src.scale(logoSize, logoSize)
        val logoMask = createBitmap(logoSize, logoSize).also { mask ->
            Canvas(mask).apply {
                drawRoundRect(
                    RectF(0f, 0f, logoSize.toFloat(), logoSize.toFloat()),
                    logoSize * 0.3f, logoSize * 0.3f,
                    Paint(Paint.ANTI_ALIAS_FLAG)
                )
                drawBitmap(scaledLogo, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                })
            }
        }
        canvas.drawBitmap(logoMask, cx, cy, Paint(Paint.ANTI_ALIAS_FLAG))
    }

    return bitmap
}
