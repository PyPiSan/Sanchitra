package com.pypisan.sanchitra

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pypisan.sanchitra.data.models.UserProfileMap
import com.pypisan.sanchitra.data.repositories.AuthRepository
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.data.util.getToken
import com.pypisan.sanchitra.data.util.saveRefreshToken
import com.pypisan.sanchitra.data.util.saveToken
import com.pypisan.sanchitra.utils.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    var profiles by mutableStateOf<List<UserProfileMap>>(emptyList())
        private set

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    fun start(context: Context) {
        viewModelScope.launch {

            val token = getToken(context)

            if (!token.isNullOrEmpty()) {
                loadProfiles(context)
                loadLanguages()
                return@launch
            }

            val result = repo.initDeviceLogin()

            if (result != null && result.success) {

                authState = AuthState.QRLogin(
                    loginURL = result.loginURL,
                    deviceCode = result.deviceCode,
                    backgroundUrl = result.banner
                )

                startPolling(result.deviceCode, context)

            } else {

                authState = AuthState.Error

            }
        }
    }

    private fun startPolling(
        deviceCode: String, context: Context
    ) {

        viewModelScope.launch {

            val result = withTimeoutOrNull(5 * 60 * 1000) {

                while (true) {

                    delay(3000)

                    val status = repo.checkLoginStatus(deviceCode)

                    if (status?.success == true) {

                        saveToken(
                            context, status.data.access_token
                        )

                        saveRefreshToken(
                            context, status.data.refresh_token
                        )

                        loadProfiles(context)
                        loadLanguages()

                        return@withTimeoutOrNull true
                    }
                }
            }

            if (result == null) {
                handlePollingTimeout()
            }
        }
    }

    fun loadProfiles(context: Context) {

        viewModelScope.launch {

            val result = repo.getUserDetail()

            if (result != null) {

                StringConstants.Profile.accountsEmail = result.email
                StringConstants.Profile.userSelectedLanguage =
                    result.details.preferences?.get("languages") ?: listOf("English", "Hindi")

                val apiProfiles = result.profiles.map {

                    UserProfileMap(
                        id = it.id,
                        name = it.profile_name,
                        imageUrl = it.profile_picture,
                        icon = Icons.Default.AccountCircle
                    )
                }

                val extraProfiles = listOf(

//                    UserProfileMap(
//                        id = "guest",
//                        name = "Guest",
//                        imageUrl = null,
//                        icon = Icons.Default.NoAccounts
//                    ),

                    UserProfileMap(
                        id = "add", name = "Add Profile", imageUrl = null, icon = Icons.Default.Add
                    )
                )

                profiles = if (apiProfiles.size < 4) {
                    apiProfiles + extraProfiles
                } else {
                    apiProfiles
                }

                authState = AuthState.ProfileSelection

            } else {

                val token = getToken(context)

                if (token.isNullOrEmpty()) {
                    start(context)
                } else {
                    authState = AuthState.Error
                }
            }
        }
    }

    fun loadLanguages() {
        viewModelScope.launch {
            val result = repo.languagesList()
            if (result != null) {
                StringConstants.Utils.LanguageSectionItems = result.data
            } else {
                StringConstants.Utils.LanguageSectionItems = listOf(
                    "Hindi",
                    "English",
                    "Tamil",
                    "Telugu",
                    "Bengali",
                    "Gujarati",
                    "Punjabi",
                )
            }
        }
    }

    fun onProfileSelected(
        profile: UserProfileMap
    ) {
        authState = AuthState.ProfileSelected
        StringConstants.Profile.userProfileName = profile.name
        StringConstants.Profile.userProfilePicture = profile.imageUrl
    }

    fun handlePollingTimeout() {
        authState = AuthState.Error
    }
}