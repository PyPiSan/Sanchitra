package com.pypisan.sanchitra.presentation.screens.videoPlayer


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.repositories.EPGManager
import com.pypisan.sanchitra.data.repositories.TVRepository
import com.pypisan.sanchitra.data.repositories.TVRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TVPlayerScreenViewModel @Inject constructor(
    private val repository: TVRepository,
    private val epgManager: EPGManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<TVPlayerScreenUiState>(TVPlayerScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _epgState = MutableStateFlow(EPGResponse(emptyList()))
    val epgState = _epgState.asStateFlow()

    private var epgJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadChannel(id: String) {
        // 1. INSTANTLY wipe the screen and show Loading
        _uiState.value = TVPlayerScreenUiState.Loading

        viewModelScope.launch {
            when (val result = repository.getChannelData(id, "")) {
                is TVRepositoryImpl.ApiResult.Success -> {
                    _uiState.value = TVPlayerScreenUiState.Done(result.data)
                }
                is TVRepositoryImpl.ApiResult.Error -> {
                    _uiState.value = TVPlayerScreenUiState.Error
                }
            }
        }

        epgJob?.cancel()
        epgJob = viewModelScope.launch {
            epgManager.observeEPG(id.toInt()).collect {
                _epgState.value = it
            }
        }
    }

    fun reset() {
        // 2. Safely wipe memory when the back button is pressed
        _uiState.value = TVPlayerScreenUiState.Loading
        _epgState.value = EPGResponse(emptyList())
        epgJob?.cancel()
    }

    fun updateViewCount(channelId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateViewCount(channelId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Immutable
sealed class TVPlayerScreenUiState {
    data object Loading : TVPlayerScreenUiState()
    data object Error : TVPlayerScreenUiState()
    data class Done(val channel: Channel) : TVPlayerScreenUiState()
}
