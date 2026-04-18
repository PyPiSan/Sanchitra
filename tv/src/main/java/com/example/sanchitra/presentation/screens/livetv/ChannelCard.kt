package com.example.sanchitra.presentation.screens.livetv

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.sanchitra.data.models.Channel

@Composable
fun ChannelCard(
    channel: Channel,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .width(180.dp)
            .padding(16.dp)
            .clickable { onClick() }
    ) {

        AsyncImage(
            model = channel.logoUrl,
            contentDescription = channel.name,
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        )

        Text(
            text = channel.name,
            color = Color.White,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}