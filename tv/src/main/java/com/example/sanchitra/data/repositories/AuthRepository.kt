package com.example.sanchitra.data.repositories

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.example.sanchitra.data.models.DeviceLoginInitResponse
import com.example.sanchitra.data.models.LoginStatusResponse
import com.example.sanchitra.data.models.UserDetailResponse
import com.example.sanchitra.utils.APIClient

class AuthRepository {

    @OptIn(UnstableApi::class)
    suspend fun initDeviceLogin(context: Context): DeviceLoginInitResponse? {
        return try {
            val api = APIClient.create(context)
            val response = api.initDeviceLogin()

            if (response.isSuccessful) response.body() else null

        } catch (e: Exception) {
            Log.e("API_DEBUG", "Init API Error: ${e.message}")
            null
        }
    }

    suspend fun checkLoginStatus(
        deviceCode: String,
        context: Context
    ): LoginStatusResponse? {
        return try {
            val api = APIClient.create(context)
            val response = api.checkLoginStatus(deviceCode)

            Log.d("API_DEBUG", "Status Response: ${response.body()}")

            if (response.isSuccessful) response.body() else null

        } catch (e: Exception) {
            Log.e("API_DEBUG", "Status API Error: ${e.message}")
            null
        }
    }

    suspend fun getUserDetail(context: Context): UserDetailResponse? {
        return try {
            val api = APIClient.create(context)

            val response = api.getUserDetail()

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("API_DEBUG", "Error Code: ${response.code()}")
                null
            }

        } catch (e: Exception) {
            Log.e("API_DEBUG", "UserDetail API Error: ${e.message}")
            null
        }
    }
}