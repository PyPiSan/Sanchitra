package com.example.sanchitra.presentation.screens.livetv

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sanchitra.data.models.Channel

@Composable
fun ChannelRow(
    title: String,
    channels: List<Channel>,
    onChannelClick: (Channel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = title,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(channels) { channel ->

                ChannelCard(
                    channel = channel,
                    onClick = { onChannelClick(channel) }
                )
            }
        }
    }
}