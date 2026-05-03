package com.pypisan.sanchitra.data.repositories

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.pypisan.sanchitra.data.models.CommonResponse
import com.pypisan.sanchitra.data.models.DeviceLoginInitResponse
import com.pypisan.sanchitra.data.models.LoginStatusResponse
import com.pypisan.sanchitra.data.models.UserDetailResponse
import com.pypisan.sanchitra.utils.APIClient
import retrofit2.Response

class AuthRepository {

    @OptIn(UnstableApi::class)
    suspend fun initDeviceLogin(context: Context): DeviceLoginInitResponse? {
        return try {
            val api = APIClient.create(context)
            val response = api.initDeviceLogin()

            if (response.isSuccessful) response.body() else null

        } catch (e: Exception) {
            Log.e("TV", "initDeviceLogin API Error: ${e.message}")
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

//            Log.d("TV", "checkLoginStatus Response: ${response.body()}")

            if (response.isSuccessful) response.body() else null

        } catch (e: Exception) {
            Log.e("TV", "checkLoginStatus API Error: ${e.message}")
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
                Log.e("TV", "UserDetail error Code: ${response.code()}")
                null
            }

        } catch (e: Exception) {
            Log.e("TV", "UserDetail API Error: ${e.message}")
            null
        }
    }


    suspend fun userProfileLogout(context: Context): CommonResponse? {
        return try {
            val api = APIClient.create(context)

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

    suspend fun userAccountDelete(context: Context): CommonResponse? {
        return try {
            val api = APIClient.create(context)

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
}