package com.pypisan.sanchitra.data.util

import android.content.Context
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val context: Context
) : Authenticator {

    val newAccessToken = TokenManager.getAccessToken(context)
    override fun authenticate(route: Route?, response: Response): Request? {

        // If token already updated, just retry with it
        if (response.request.header("Authorization") != "Bearer $newAccessToken") {
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }

        // Prevent infinite loops
        if (responseCount(response) >= 2) {
            TokenManager.clear(context)
            return null
        }

        val newTokens = runBlocking {
            AuthRefreshManager.refreshToken(context)
        }

        return if (newTokens != null) {
            val newAccessToken = newTokens.data.access_token

            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()

        } else {
            // Refresh failed → logout
            TokenManager.clear(context)
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var res = response.priorResponse
        while (res != null) {
            count++
            res = res.priorResponse
        }
        return count
    }
}