package com.pypisan.sanchitra.presentation.screens.profile

import androidx.lifecycle.ViewModel
import com.pypisan.sanchitra.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    suspend fun userPrefUpdate(preferenceType: String, preferences: List<String>): Boolean {
        val result = repo.updateUserPreferences(preferenceType, preferences)
        return result != null && result.success
    }

}