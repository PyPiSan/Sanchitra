package com.pypisan.sanchitra.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Text
import com.pypisan.sanchitra.R

@Composable
fun Error(
    modifier: Modifier = Modifier,
    messageId: Int? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(
                id = messageId ?: R.string.message_error
            ),
            color = Color.White
        )
    }
}