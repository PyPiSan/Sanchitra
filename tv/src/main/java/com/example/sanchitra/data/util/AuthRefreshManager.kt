package com.example.sanchitra.data.util

import android.content.Context
import com.example.sanchitra.api.RefreshTokenRequest
import com.example.sanchitra.data.models.LoginStatusResponse
import com.example.sanchitra.utils.APIClient
import com.example.sanchitra.utils.RefreshAPIClient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object AuthRefreshManager {

        private val mutex = Mutex()
        private var lastRefreshResult: LoginStatusResponse? = null

        suspend fun refreshToken(context: Context): LoginStatusResponse? {
            return mutex.withLock {

                // If another request already refreshed while we were waiting
                val currentToken = TokenManager.getAccessToken(context)
                if (!currentToken.isNullOrEmpty()) {
                    // Optional: you can validate if it's already updated
                    // For simplicity, we continue
                }

                try {
                    val refreshToken = TokenManager.getRefreshToken(context)
                        ?: return null

                    val response = RefreshAPIClient.refreshApi.refreshToken(
                        RefreshTokenRequest(refreshToken)
                    )

                    if (response.isSuccessful && response.body()?.success == true) {
                        val body = response.body()!!
                        TokenManager.saveTokens(
                            context,
                            body.data.access_token,
                            body.data.refresh_token
                        )
                        lastRefreshResult = body
                        body
                    } else {
                        TokenManager.clear(context)
                        null
                    }

                } catch (e: Exception) {
                    TokenManager.clear(context)
                    null
                }
            }
        }
}