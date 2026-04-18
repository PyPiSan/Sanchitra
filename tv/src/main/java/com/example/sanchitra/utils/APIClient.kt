package com.example.sanchitra.utils

import android.content.Context
import com.example.sanchitra.data.util.AuthInterceptor
import com.example.sanchitra.data.util.StringConstants
import com.example.sanchitra.data.util.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIClient {

    fun create(context: Context): APIService {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .authenticator(TokenAuthenticator(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(StringConstants.API.TVURL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}