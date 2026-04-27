package com.pypisan.sanchitra.utils

import android.content.Context
import com.pypisan.sanchitra.data.util.AuthInterceptor
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.data.util.TokenAuthenticator
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