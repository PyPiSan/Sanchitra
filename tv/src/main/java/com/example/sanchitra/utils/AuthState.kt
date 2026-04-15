package com.example.sanchitra.utils

sealed class AuthState {
    object Loading : AuthState()
    object LoggedIn : AuthState()
    data class QRLogin(val qrData: String, val deviceCode: String) : AuthState()
    object Error : AuthState()
}