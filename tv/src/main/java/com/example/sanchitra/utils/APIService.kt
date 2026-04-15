package com.example.sanchitra.utils

import com.example.sanchitra.data.models.DeviceLoginInitResponse
import com.example.sanchitra.data.models.LoginStatusResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface APIService {

//   login init
    @POST("users/device/login/init/")
    suspend fun initDeviceLogin(): Response<DeviceLoginInitResponse>

//    login status check
    @POST("users/device/login/status/")
    suspend fun checkLoginStatus(
    @Query("device_code") deviceCode: String): Response<LoginStatusResponse>
}