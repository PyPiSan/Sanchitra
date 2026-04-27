package com.pypisan.sanchitra.utils

import com.pypisan.sanchitra.data.util.StringConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RefreshAPIClient {
    val refreshApi: RefreshAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(StringConstants.API.TVURL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshAPIService::class.java)
    }
}