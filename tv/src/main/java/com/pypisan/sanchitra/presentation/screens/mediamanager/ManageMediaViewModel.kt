package com.pypisan.sanchitra.presentation.screens.mediamanager

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.repositories.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageMediaViewModel @Inject constructor(
    val repository: ChannelRepository
) : ViewModel() {

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _importError = MutableStateFlow<String?>(null)
    val importError: StateFlow<String?> = _importError.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    fun clearError() {
        _importError.value = null
    }

    fun importFromUrl(
        name: String,
        url: String,
        onSuccess: () -> Unit
    ) {
        if (_isImporting.value) return
        viewModelScope.launch {
            _isImporting.value = true
            _importError.value = null

            val category = name.ifBlank { "Web Playlist" }
            repository.importFromUrl(url, defaultCategory = category)
                .onSuccess { count ->
                    _isImporting.value = false
                    _events.emit("Imported $count channels")
                    onSuccess()
                }
                .onFailure { err ->
                    _isImporting.value = false
                    _importError.value = err.message ?: "Failed to import M3U URL"
                }
        }
    }

    fun importFromLocal(
        uri: Uri,
        onSuccess: () -> Unit
    ) {
        if (_isImporting.value) return
        viewModelScope.launch {
            _isImporting.value = true
            _importError.value = null

            repository.importFromUri(uri, defaultCategory = "Local Playlist")
                .onSuccess { count ->
                    _isImporting.value = false
                    _events.emit("Imported $count channels")
                    onSuccess()
                }
                .onFailure { err ->
                    _isImporting.value = false
                    _importError.value = err.message ?: "Failed to import local file"
                }
        }
    }
}