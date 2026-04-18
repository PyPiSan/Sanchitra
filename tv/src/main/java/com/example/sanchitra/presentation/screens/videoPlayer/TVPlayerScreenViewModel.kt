package com.example.sanchitra.presentation.screens.videoPlayer

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanchitra.data.models.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class TVPlayerScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val channel = savedStateHandle.get<Channel>("channel")

    val uiState = MutableStateFlow<TVPlayerScreenUiState>(
        if (channel != null) {
            TVPlayerScreenUiState.Done(channel)
        } else {
            TVPlayerScreenUiState.Error
        }
    )

    init {
        Log.d("TVPlayerVM", "channel = $channel")
    }
}

@Immutable
sealed class TVPlayerScreenUiState {
    data object Loading : TVPlayerScreenUiState()
    data object Error : TVPlayerScreenUiState()
    data class Done(val channel: Channel) : TVPlayerScreenUiState()
}