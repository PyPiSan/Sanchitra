package com.pypisan.sanchitra.data.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import io.github.alexzhirkevich.qrose.options.Neighbors
import io.github.alexzhirkevich.qrose.options.QrBallShape

class MyCircleBallShape : QrBallShape {

    override fun Path.path(
        size: Float,
        neighbors: Neighbors
    ): Path = apply {

        val radius = size * 0.55f

        val center = Offset(size / 2f, size / 2f)

        addOval(
            Rect(
                left = center.x - radius,
                top = center.y - radius,
                right = center.x + radius,
                bottom = center.y + radius
            )
        )
    }
}