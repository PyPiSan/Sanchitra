package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.entities.EPGResponse
import com.pypisan.sanchitra.data.entities.IPTVChannelDetail
import com.pypisan.sanchitra.data.repositories.ChannelRepository
import com.pypisan.sanchitra.data.repositories.EPGManager
import com.pypisan.sanchitra.data.repositories.IPTVRepository
import com.pypisan.sanchitra.data.repositories.IPTVRepositoryImpl
import com.pypisan.sanchitra.data.util.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IPTVPlayerScreenViewModel @Inject constructor(
    private val repository: IPTVRepository,
    private val channelRepository: ChannelRepository,
    private val epgManager: EPGManager
) : ViewModel() {

    private val _channelIdFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<IPTVPlayerScreenUiState> = _channelIdFlow
        .map { id ->
            if (id == null) return@map IPTVPlayerScreenUiState.Loading // Instant reset!

            if (StringConstants.Profile.isLoggedIN) {
                // Logged in -> fetch from API
                when (val result = repository.getIPTVChannelDetail(id)) {
                    is IPTVRepositoryImpl.ApiResult.Success -> {
                        IPTVPlayerScreenUiState.Done(result.data)
                    }
                    is IPTVRepositoryImpl.ApiResult.Error -> {
                        IPTVPlayerScreenUiState.Error
                    }
                }
            } else {
                // Not logged in -> fetch from Room DB
                val channelId = id.toLongOrNull()
                    ?: return@map IPTVPlayerScreenUiState.Error

                val entity = channelRepository.getChannelById(channelId)
                    ?: return@map IPTVPlayerScreenUiState.Error

                IPTVPlayerScreenUiState.Done(
                    IPTVChannelDetail(
                        id = entity.id.toInt(),
                        name = entity.name,
                        country = entity.country ?: "IN",
                        categories = entity.category,
                        isWeb = entity.isWeb,
                        isApp = entity.isApp,
                        logo = entity.logoUrl ?: entity.logo.orEmpty(),
                        streamUrl = entity.streamUrl,
                        network = entity.network.orEmpty(),
                        language = entity.language ?: "English",
                        quality = "",
                        isDrm = entity.isDrm,
                        licenseKey = entity.licenseKey,
                        licenseUrl = entity.licenseUrl,
                        epgId = null
                    )
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = IPTVPlayerScreenUiState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val epgState: StateFlow<EPGResponse> = _channelIdFlow
        .flatMapLatest { id ->
            if (id == null || !StringConstants.Profile.isLoggedIN) {
                // Skip EPG when reset or when playing from Room DB
                flowOf(EPGResponse(emptyList()))
            } else {
                val numericId = id.toIntOrNull()
                if (numericId == null) {
                    flowOf(EPGResponse(emptyList()))
                } else {
                    epgManager.observeEPG(numericId)
                }
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