package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.models.IPTVChannelDetail
import com.pypisan.sanchitra.data.repositories.EPGManager
import com.pypisan.sanchitra.data.repositories.IPTVRepository
import com.pypisan.sanchitra.data.repositories.IPTVRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IPTVPlayerScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: IPTVRepository,
    private val epgManager: EPGManager
) : ViewModel() {

    private val channelIdFlow = savedStateHandle
        .getStateFlow<String?>(
            IPTVPlayerScreen.IPTVIdBundleKey,
            null
        )
        .filterNotNull()

    val uiState = channelIdFlow
        .map { id ->
            when (val result = repository.getIPTVChannelDetail(id)) {

                is IPTVRepositoryImpl.ApiResult.Success -> {
                    Log.d("IPTV", "IPTV Channel is: ${result.data}")
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
            initialValue = TVPlayerScreenUiState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val epgState = channelIdFlow
        .flatMapLatest { id ->
            epgManager.observeEPG(id.toInt())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EPGResponse(emptyList())
        )
}

@Immutable
sealed class IPTVPlayerScreenUiState {
    data object Loading : IPTVPlayerScreenUiState()
    data object Error : IPTVPlayerScreenUiState()
    data class Done(val iptvChannel: IPTVChannelDetail) : IPTVPlayerScreenUiState()
}