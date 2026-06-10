package com.pypisan.sanchitra.data.repositories


import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.pypisan.sanchitra.data.models.CommonResponse
import com.pypisan.sanchitra.data.models.DeviceLoginInitResponse
import com.pypisan.sanchitra.data.models.LanguageResponse
import com.pypisan.sanchitra.data.models.LoginStatusResponse
import com.pypisan.sanchitra.data.models.UserDetailResponse
import com.pypisan.sanchitra.utils.APIService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: APIService
) {

    @OptIn(UnstableApi::class)
    suspend fun initDeviceLogin(): DeviceLoginInitResponse? {
        return try {
            val response = api.initDeviceLogin()

            if (response.isSuccessful) response.body() else null

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

//            Log.d("TV", "checkLoginStatus Response: ${response.body()}")

            if (response.isSuccessful) response.body() else null

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

        } catch (e: Exception) {
            Log.e("TV", "UserDetail API Error: ${e.message}")
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

        } catch (e: Exception) {
            Log.e("TV", "deleteUserAccount API Error: ${e.message}")
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

        } catch (e: Exception) {
            Log.e("TV", "languagesList API Error: ${e.message}")
            null
        }
    }
}