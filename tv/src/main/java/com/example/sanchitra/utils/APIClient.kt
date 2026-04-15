package com.example.sanchitra.utils

import com.example.sanchitra.data.util.StringConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIClient {
    val api: APIService by lazy {
        Retrofit.Builder()
            .baseUrl(StringConstants.API.TVURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}