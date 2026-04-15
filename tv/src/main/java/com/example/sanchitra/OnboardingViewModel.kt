package com.example.sanchitra

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sanchitra.data.repositories.AuthRepository
import com.example.sanchitra.data.util.getToken
import com.example.sanchitra.data.util.saveToken
import com.example.sanchitra.utils.AuthState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val repo = AuthRepository()

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    fun start(context: Context) {
        viewModelScope.launch {

            val token = getToken(context)

            if (!token.isNullOrEmpty()) {
                authState = AuthState.LoggedIn
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

                if (status?.status == "approved") {
                    saveToken(context, status.access_token!!)
                    authState = AuthState.LoggedIn
                    break
                }
            }
        }
    }
}