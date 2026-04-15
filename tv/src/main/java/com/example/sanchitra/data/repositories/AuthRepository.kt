package com.example.sanchitra.data.repositories

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.example.sanchitra.data.models.DeviceLoginInitResponse
import com.example.sanchitra.data.models.LoginStatusResponse
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
}