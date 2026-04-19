package com.example.sanchitra.utils

sealed class AuthState {
    object Loading : AuthState()
    object ProfileSelection : AuthState()
    object ProfileSelected : AuthState()
    data class QRLogin(val loginURL: String, val deviceCode: String, val backgroundUrl: String) :
        AuthState()

    object Error : AuthState()
}