package com.pypisan.sanchitra.presentation.screens.profile

import androidx.lifecycle.ViewModel
import com.pypisan.sanchitra.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountSectionViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    suspend fun deleteAccount(): Boolean {

        val result = repo.userAccountDelete()

        return result != null && result.success
    }

    suspend fun userProfileLogout(): Boolean {

        val result = repo.userProfileLogout()

        return result != null && result.success
    }
}