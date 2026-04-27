package com.pypisan.sanchitra.data.util

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        // Skip auth if No-Auth header present
        if (request.header("No-Auth") != null) {
            return chain.proceed(request)
        }

        val token = getToken(context)

        val newRequest = request.newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                header("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(newRequest)
    }
}