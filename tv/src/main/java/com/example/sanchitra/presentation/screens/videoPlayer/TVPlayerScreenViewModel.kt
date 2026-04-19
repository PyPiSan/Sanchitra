package com.example.sanchitra.presentation.screens.videoPlayer


import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanchitra.data.models.Channel
import com.example.sanchitra.data.repositories.TVRepository
import com.example.sanchitra.data.repositories.TVRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TVPlayerScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: TVRepository,
) : ViewModel() {

    val uiState = savedStateHandle
        .getStateFlow<String?>(TVPlayerScreen.TVIdBundleKey, null)
        .map { id ->
//            Log.d("VIDEO_DEBUG", "id: $id")
            if (id == null) {
                TVPlayerScreenUiState.Error
            } else {

                when (val result = repository.getChannelData(id, "hd")) {

                    is TVRepositoryImpl.ApiResult.Success -> {
                        TVPlayerScreenUiState.Done(result.data)
                    }

                    is TVRepositoryImpl.ApiResult.Error -> {
                        TVPlayerScreenUiState.Error
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TVPlayerScreenUiState.Loading
        )
}

@Immutable
sealed class TVPlayerScreenUiState {
    data object Loading : TVPlayerScreenUiState()
    data object Error : TVPlayerScreenUiState()
    data class Done(val channel: Channel) : TVPlayerScreenUiState()
}