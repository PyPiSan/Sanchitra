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
    private val repository: TVRepository
) : ViewModel() {

    val uiState: StateFlow<TVUiState> =
        repository.getChannels()
            .map { channels ->
//                Log.d("VM_DEBUG", "Channels received: ${channels.size}")
                val grouped = channels
                    .filter { it.isValid() }
                    .groupBy { it.category.ifBlank { "Others" } }

                TVUiState.Ready(grouped) as TVUiState   // 👈 force type
            }
            .onStart {
                emit(TVUiState.Loading)
            }
            .catch { e ->
                emit(TVUiState.Error(e.message ?: "Something went wrong"))
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                TVUiState.Loading
            )

    private fun Channel.isValid(): Boolean {
        return name.isNotBlank() && streamUrl.isNotBlank()
    }

    sealed interface TVUiState {
        object Loading : TVUiState

        data class Ready(
            val categories: Map<String, List<Channel>>
        ) : TVUiState

        data class Error(
            val message: String
        ) : TVUiState
    }
}