package com.pypisan.sanchitra.presentation.screens.videoPlayer


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.Channel
import com.pypisan.sanchitra.data.entities.EPGResponse
import com.pypisan.sanchitra.data.repositories.ChannelRepository
import com.pypisan.sanchitra.data.repositories.EPGManager
import com.pypisan.sanchitra.data.repositories.TVRepository
import com.pypisan.sanchitra.data.repositories.TVRepositoryImpl
import com.pypisan.sanchitra.data.util.StringConstants
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
    private val channelRepository: ChannelRepository,
    private val epgManager: EPGManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<TVPlayerScreenUiState>(TVPlayerScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _epgState = MutableStateFlow(EPGResponse(emptyList()))
    val epgState = _epgState.asStateFlow()

    private var loadJob: Job? = null
    private var epgJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadChannel(id: String) {
        // 1. INSTANTLY wipe the screen and show Loading
        _uiState.value = TVPlayerScreenUiState.Loading
        loadJob?.cancel()
        epgJob?.cancel()

        if (StringConstants.Profile.isLoggedIN) {
            // Logged in -> fetch channel + EPG from API
            loadJob = viewModelScope.launch {
                when (val result = repository.getChannelData(id, "")) {
                    is TVRepositoryImpl.ApiResult.Success -> {
                        _uiState.value = TVPlayerScreenUiState.Done(result.data)
                    }
                    is TVRepositoryImpl.ApiResult.Error -> {
                        _uiState.value = TVPlayerScreenUiState.Error
                    }
                }
            }

            epgJob = viewModelScope.launch {
                val numericId = id.toIntOrNull()
                if (numericId != null) {
                    epgManager.observeEPG(numericId).collect {
                        _epgState.value = it
                    }
                } else {
                    _epgState.value = EPGResponse(emptyList())
                }
            }
        } else {
            // Not logged in -> fetch channel from Room DB, skip EPG
            _epgState.value = EPGResponse(emptyList())

            loadJob = viewModelScope.launch {
                val channelId = id.toLongOrNull()
                if (channelId == null) {
                    _uiState.value = TVPlayerScreenUiState.Error
                    return@launch
                }

                val entity = channelRepository.getChannelById(channelId)
                if (entity == null) {
                    _uiState.value = TVPlayerScreenUiState.Error
                    return@launch
                }

                _uiState.value = TVPlayerScreenUiState.Done(
                    Channel(
                        id = entity.id.toInt(),
                        name = entity.name,
                        category = entity.category,
                        logoUrl = entity.logoUrl ?: entity.logo,
                        description = null,
                        bannerUrl = entity.bannerUrl,
                        streamUrl = entity.streamUrl,
                        language = entity.language ?: "English",
                        licenseKey = entity.licenseKey,
                        licenseUrl = entity.licenseUrl,
                        isDrm = entity.isDrm,
                        isInternal = entity.isInternal,
                        isPremium = entity.isPremium
                    )
                )
            }
        }
    }

    fun reset() {
        // 2. Safely wipe memory when the back button is pressed
        loadJob?.cancel()
        epgJob?.cancel()
        _uiState.value = TVPlayerScreenUiState.Loading
        _epgState.value = EPGResponse(emptyList())
    }

    fun updateViewCount(channelId: Int) {
        if (!StringConstants.Profile.isLoggedIN) return // Skip API call for Room DB channels

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
