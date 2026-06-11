package com.pypisan.sanchitra.presentation.screens.videoPlayer


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.repositories.EPGManager
import com.pypisan.sanchitra.data.repositories.TVRepository
import com.pypisan.sanchitra.data.repositories.TVRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TVPlayerScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TVRepository,
    private val epgManager: EPGManager
) : ViewModel() {

    private val channelIdFlow = savedStateHandle
        .getStateFlow<String?>(
            TVPlayerScreen.TVIdBundleKey,
            null
        )
        .filterNotNull()

    val uiState = channelIdFlow
        .map { id ->

            when (val result = repository.getChannelData(id, "")) {

                is TVRepositoryImpl.ApiResult.Success -> {
                    TVPlayerScreenUiState.Done(result.data)
                }

                is TVRepositoryImpl.ApiResult.Error -> {
                    TVPlayerScreenUiState.Error
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
