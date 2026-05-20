package com.example.fotapp.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class RateLimitInterceptor(private val maxRetries: Int = 3) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var attempt = 0

        while (response.code == 429 && attempt < maxRetries) {
            val retryAfterHeader = response.header("Retry-After")
            val retryAfterSeconds = retryAfterHeader?.toLongOrNull() ?: (attempt + 1) * 5L
            
            Log.w("RateLimit", "429 Too Many Requests for ${request.url}. Retrying in $retryAfterSeconds seconds... (Attempt ${attempt + 1}/$maxRetries)")
            
            try {
                Thread.sleep(retryAfterSeconds * 1000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }

            attempt++
            response.close()
            response = chain.proceed(request)
        }

        return response
    }
}
