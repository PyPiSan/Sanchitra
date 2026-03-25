package com.example.sanchitra.presentation.common
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sanchitra.R


@Composable
fun Loading(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayMedium
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(id = R.string.message_loading),
            style = style
        )
    }
}
