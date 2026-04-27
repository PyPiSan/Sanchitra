package com.pypisan.sanchitra.presentation.screens.videoPlayer

import androidx.lifecycle.ViewModel
import com.pypisan.sanchitra.data.models.IPTVChannel
import com.pypisan.sanchitra.data.models.Stream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerSharedViewModel @Inject constructor() : ViewModel() {

    private val _selectedChannel = MutableStateFlow<IPTVChannel?>(null)
    val selectedChannel: StateFlow<IPTVChannel?> = _selectedChannel

    private val _selectedStream = MutableStateFlow<Stream?>(null)
    val selectedStream: StateFlow<Stream?> = _selectedStream

    fun setChannel(channel: IPTVChannel) {
        _selectedChannel.value = channel

        // auto-select best stream (optional)
        _selectedStream.value = channel.streams.maxByOrNull { it.quality }
    }

    fun selectStream(stream: Stream) {
        _selectedStream.value = stream
    }
}