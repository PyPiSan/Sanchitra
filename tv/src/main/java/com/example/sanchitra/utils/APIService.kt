package com.example.sanchitra.utils

import com.example.sanchitra.api.RefreshTokenRequest
import com.example.sanchitra.data.models.DeviceLoginInitResponse
import com.example.sanchitra.data.models.LoginStatusResponse
import com.example.sanchitra.data.models.UserDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface APIService {

    //   login init
    @POST("users/device/login/init/")
    suspend fun initDeviceLogin(
        @Header("No-Auth") noAuth: Boolean = true
    ): Response<DeviceLoginInitResponse>

    //    login status check
    @GET("users/device/login/status/")
    suspend fun checkLoginStatus(
        @Query("device_code") deviceCode: String,
        @Header("No-Auth") noAuth: Boolean = true
    ): Response<LoginStatusResponse>

    @GET("users/detail/")
    suspend fun getUserDetail(): Response<UserDetailResponse>
}

interface RefreshAPIService{
    @POST("users/refresh/auth/")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<LoginStatusResponse>
}