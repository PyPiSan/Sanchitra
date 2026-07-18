package com.pypisan.sanchitra.presentation.screens.movies

import androidx.lifecycle.ViewModel
import com.pypisan.sanchitra.data.entities.Videos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class VideoSharedViewModel @Inject constructor() : ViewModel() {

    private val _selectedVideo = MutableStateFlow<Videos?>(null)
    val selectedVideo: StateFlow<Videos?> = _selectedVideo

    fun setVideo(video: Videos) {
        _selectedVideo.value = video
    }

    fun clearVideo() {
        _selectedVideo.value = null
    }
}