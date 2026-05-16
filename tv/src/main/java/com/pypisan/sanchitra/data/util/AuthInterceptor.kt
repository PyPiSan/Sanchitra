package com.pypisan.sanchitra.data.util

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = getToken(context)
        val request = chain.request().newBuilder().apply {

            if (!token.isNullOrEmpty()) {
                addHeader(
                    "Authorization", "Bearer $token"
                )
            }
        }.build()

        return chain.proceed(request)
    }
}