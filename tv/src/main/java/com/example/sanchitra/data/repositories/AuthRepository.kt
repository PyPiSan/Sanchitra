package com.example.sanchitra.data.repositories

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.example.sanchitra.R
import com.example.sanchitra.data.models.DeviceLoginInitResponse
import com.example.sanchitra.data.models.LoginStatusResponse
import com.example.sanchitra.data.models.UserDetailResponse
import com.example.sanchitra.data.models.UserProfileMap
import com.example.sanchitra.data.models.UserProfiles
import com.example.sanchitra.data.util.getToken
import com.example.sanchitra.utils.APIClient

class AuthRepository {

    @OptIn(UnstableApi::class)
    suspend fun initDeviceLogin(): DeviceLoginInitResponse? {
        return try {
            val response = APIClient.api.initDeviceLogin()
//            Log.d("API_DEBUG", "Init Response Code: ${response.code()}")
//            Log.d("API_DEBUG", "Init Response Body: ${response.body()}")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Init API Error: ${e.message}")
            null
        }
    }

    suspend fun checkLoginStatus(deviceCode: String): LoginStatusResponse? {
        return try {
            val response = APIClient.api.checkLoginStatus(deviceCode)

            Log.d("API_DEBUG", "Status Response: ${response.body()}")

            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Status API Error: ${e.message}")
            null
        }
    }

    suspend fun getUserDetail(context: Context): UserDetailResponse? {
        return try {
            val token = getToken(context)

            if (token.isNullOrEmpty()) {
                Log.e("USER_API", "Token is null or empty")
                return null
            }

            val response = APIClient.api.getUserDetail("Bearer $token")

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("USER_API", "Error Code: ${response.code()}")
                null
            }

        } catch (e: Exception) {
            Log.e("USER_API", "UserDetail API Error: ${e.message}")
            null
        }
    }
}
