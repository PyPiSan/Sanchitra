package com.pypisan.sanchitra.utils

import android.content.Context
import com.pypisan.sanchitra.data.util.AuthInterceptor
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.data.util.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {

    private var okHttpClient: OkHttpClient? = null

    private fun getClient(context: Context): OkHttpClient {
        return okHttpClient ?: OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .authenticator(TokenAuthenticator(context))
            .build()
            .also { okHttpClient = it }
    }

    fun createApiService(context: Context): APIService {
        return Retrofit.Builder()
            .baseUrl(StringConstants.API.TVURL)
            .client(getClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }

    fun createMediaApiService(context: Context): MediaAPIService {
        return Retrofit.Builder()
            .baseUrl(StringConstants.API.VIDEOURL)
            .client(getClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MediaAPIService::class.java)
    }
}