package com.tma.data.retrofit

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.security.GeneralSecurityException
import javax.net.ssl.SSLPeerUnverifiedException

class CommonInterceptor (private var networkMonitor: NetworkMonitor) : Interceptor {
    companion object {
        var staticAuthorization: String? = null
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (networkMonitor.isConnected()) {
            try {
                return chain.proceed(processRequest(chain.request()))
            } catch (slle: SSLPeerUnverifiedException) {
                throw NetworkException(NetworkException.ErrorCode.SSL_EXCEPTION)
            } catch (e: Exception) {
                throw e
            }
        } else {
            throw NetworkException(NetworkException.ErrorCode.NO_INTERNET_CONNECTION)
        }
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    fun processRequest(request: Request): Request {
        return request.newBuilder()
            .apply {
                header("Content-Type", "application/json")
                staticAuthorization?.let {
                    header("Authorization", it)
                }

                method(request.method, request.body)
            }.build()
    }
}