package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.entities.IPTVChannelDetail
import com.pypisan.sanchitra.data.repositories.EPGManager
import com.pypisan.sanchitra.data.repositories.IPTVRepository
import com.pypisan.sanchitra.data.repositories.IPTVRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IPTVPlayerScreenViewModel @Inject constructor(
    private val repository: IPTVRepository,
    private val epgManager: EPGManager
) : ViewModel() {

    private val _channelIdFlow = MutableStateFlow<String?>(null)

    val uiState = _channelIdFlow
        .map { id ->
            if (id == null) return@map IPTVPlayerScreenUiState.Loading // Instant reset!

            when (val result = repository.getIPTVChannelDetail(id)) {
                is IPTVRepositoryImpl.ApiResult.Success -> {
                    IPTVPlayerScreenUiState.Done(result.data)
                }
                is IPTVRepositoryImpl.ApiResult.Error -> {
                    IPTVPlayerScreenUiState.Error
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = IPTVPlayerScreenUiState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val epgState = _channelIdFlow
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(EPGResponse(emptyList())) // Instant EPG reset!
            } else {
                epgManager.observeEPG(id.toInt())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EPGResponse(emptyList())
        )

    fun loadChannel(id: String) {
        _channelIdFlow.value = id
    }

    fun reset() {
        _channelIdFlow.value = null
    }
}


@Immutable
sealed class IPTVPlayerScreenUiState {
    data object Loading : IPTVPlayerScreenUiState()
    data object Error : IPTVPlayerScreenUiState()
    data class Done(val iptvChannel: IPTVChannelDetail) : IPTVPlayerScreenUiState()
}