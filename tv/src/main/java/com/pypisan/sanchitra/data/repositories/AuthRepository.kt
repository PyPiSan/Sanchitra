package com.pypisan.sanchitra.data.repositories


import android.util.Log
import com.pypisan.sanchitra.data.entities.AddProfileRequest
import com.pypisan.sanchitra.data.entities.ChangePasswordRequest
import com.pypisan.sanchitra.data.entities.UpdatePreferencesRequest
import com.pypisan.sanchitra.data.models.CommonResponse
import com.pypisan.sanchitra.data.models.DeviceLoginInitResponse
import com.pypisan.sanchitra.data.models.LanguageResponse
import com.pypisan.sanchitra.data.models.LoginStatusResponse
import com.pypisan.sanchitra.data.models.UserDetailResponse
import com.pypisan.sanchitra.data.models.UserExtended
import com.pypisan.sanchitra.utils.APIService
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepository @Inject constructor(
    private val api: APIService
) {

    suspend fun initDeviceLogin(): DeviceLoginInitResponse? {
        return try {
            val response = api.initDeviceLogin()

            if (response.isSuccessful) response.body() else null

        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "initDeviceLogin API Error: ${e.message}")
            null
        }
    }

    suspend fun checkLoginStatus(
        deviceCode: String
    ): LoginStatusResponse? {
        return try {
            val response = api.checkLoginStatus(deviceCode)

            if (response.isSuccessful) response.body() else null

        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "checkLoginStatus API Error: ${e.message}")
            null
        }
    }

    suspend fun getUserDetail(): UserDetailResponse? {
        return try {

            val response = api.getUserDetail()

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TV", "UserDetail error Code: ${response.code()}")
                null
            }

        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "UserDetail API Error: ${e.message}")
            null
        }
    }

    suspend fun getUserExtended(): UserExtended? {
        return try {

            val response = api.getUserExtended()

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TV", "UserExtended error Code: ${response.code()}")
                null
            }

        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "UserExtended API Error: ${e.message}")
            null
        }
    }


    suspend fun userProfileLogout(): CommonResponse? {
        return try {

            val response = api.userLogout()

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TV", "userProfileLogout error Code: ${response.code()}")
                null
            }

        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "userLogout API Error: ${e.message}")
            null
        }
    }

    suspend fun userAccountDelete(): CommonResponse? {
        return try {

            val response = api.deleteUserAccount()

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TV", "deleteUserAccount error Code: ${response.code()}")
                null
            }

        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "deleteUserAccount API Error: ${e.message}")
            null
        }
    }

    suspend fun userPasswordChange(
        currentPassword: String, newPassword: String
    ): CommonResponse? {
        return try {
            val requestBody = ChangePasswordRequest(
                currentPassword = currentPassword, newPassword = newPassword
            )
            val response = api.updatePassword(requestBody)

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TV", "userPasswordChange error Code: ${response.code()}")
                null
            }
        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "userPasswordChange API Error: ${e.message}")
            null
        }
    }

    suspend fun userAddProfile(
        profileName: String, profilePicture: String?
    ): CommonResponse? {
        return try {
            val requestBody = AddProfileRequest(
                profileName = profileName, profilePicture = profilePicture
            )
            val response = api.addProfile(requestBody)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TV", "userAddProfile error Code: ${response.code()}")
                null
            }

        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "userAddProfile API Error: ${e.message}")
            null
        }
    }

    suspend fun languagesList(): LanguageResponse? {
        return try {

            val response = api.getLanguages()

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TV", "languagesList error Code: ${response.code()}")
                null
            }

        } catch (e: CancellationException) {
            // Re-throw coroutine cancellation so the coroutine cancels properly
            throw e
        } catch (e: Exception) {
            Log.e("TV", "languagesList API Error: ${e.message}")
            null
        }
    }

    suspend fun updateUserPreferences(
        preferenceType: String, preferences: List<String>
    ): CommonResponse? {
        return try {
            val requestBody = UpdatePreferencesRequest(
                preferenceType = preferenceType, preferences = preferences
            )
            val response = api.updatePreferences(requestBody)

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("TV", "updateUserPreferences error Code: ${response.code()}")
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("TV", "updateUserPreferences API Error: ${e.message}")
            null
        }
    }
}