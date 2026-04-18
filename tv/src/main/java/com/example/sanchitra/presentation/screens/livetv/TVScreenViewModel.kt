package com.example.sanchitra.presentation.screens.livetv

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanchitra.data.models.Channel
import com.example.sanchitra.data.repositories.TVRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TVScreenViewModel @Inject constructor(
    tvRepository: TVRepository
) : ViewModel() {

    val uiState: StateFlow<TVScreenUiState> =
        tvRepository.getChannels()
            .map { channels ->
//                Log.d("VM_DEBUG", "Channels received: ${channels.size}")
                val grouped = channels
                    .filter { it.isValid() }
                    .groupBy { it.category.ifBlank { "Others" } }

                TVScreenUiState.Ready(grouped) as TVScreenUiState
            }
            .onStart {
                emit(TVScreenUiState.Loading)
            }
            .catch { e ->
                emit(TVScreenUiState.Error(e.message ?: "Something went wrong"))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                TVScreenUiState.Loading
            )

    private fun Channel.isValid(): Boolean {
        return name.isNotBlank() && streamUrl.isNotBlank()
    }

    sealed interface TVScreenUiState {
        data object Loading : TVScreenUiState

        data class Ready(
            val categories: Map<String, List<Channel>>
        ) : TVScreenUiState

        data class Error(
            val message: String
        ) : TVScreenUiState
    }
}