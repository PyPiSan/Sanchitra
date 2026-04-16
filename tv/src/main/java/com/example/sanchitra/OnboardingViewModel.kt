package com.example.sanchitra

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanchitra.data.models.UserProfileMap
import com.example.sanchitra.data.repositories.AuthRepository
import com.example.sanchitra.data.util.getToken
import com.example.sanchitra.data.util.saveRefreshToken
import com.example.sanchitra.data.util.saveToken
import com.example.sanchitra.utils.AuthState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val repo = AuthRepository()
    var profiles by mutableStateOf<List<UserProfileMap>>(emptyList())
        private set

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    fun start(context: Context) {
        viewModelScope.launch {

            val token = getToken(context)

            if (!token.isNullOrEmpty()) {
                loadProfiles(context)
                return@launch
            }

            val result = repo.initDeviceLogin()

            if (result != null && result.success) {
                authState = AuthState.QRLogin(
                    qrData = result.qr_url,
                    deviceCode = result.device_code
                )
//                Log.d("API_DEBUG", "Auth State is: $authState")

                startPolling(result.device_code, context)
            } else {
                authState = AuthState.Error
            }
        }
    }

    private fun startPolling(deviceCode: String, context: Context) {
        viewModelScope.launch {
            while (true) {
                delay(3000)

                val status = repo.checkLoginStatus(deviceCode)

                if (status?.success == true) {
                    saveToken(context, status.data.access_token)
                    saveRefreshToken(context, status.data.refresh_token)
                    loadProfiles(context)
//                    authState = AuthState.ProfileSelection
                    break
                }
            }
        }
    }

    fun loadProfiles(context: Context) {
        viewModelScope.launch {
            try {
                val result = repo.getUserDetail(context)

                if (result != null) {
                    profiles = result.profiles.map {
                        UserProfileMap(
                            id = it.id,
                            name = it.profile_name,
                            imageUrl = it.profile_picture,
                            icon = R.drawable.baseline_account // fallback
                        )
                    }

                    authState = AuthState.ProfileSelection
                } else {
                    authState = AuthState.Error
                }

            } catch (e: Exception) {
                authState = AuthState.Error
            }
        }
    }

    fun onProfileSelected(profile: UserProfileMap) {
        authState = AuthState.ProfileSelected
    }
}