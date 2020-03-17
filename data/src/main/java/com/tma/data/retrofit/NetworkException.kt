package com.tma.data.retrofit

import java.io.IOException

class NetworkException (var errorCode: ErrorCode) : IOException() {

    enum class ErrorCode(var code: Int) {
        NO_INTERNET_CONNECTION(1000),
        SSL_EXCEPTION(1001),
        SESSION_TIMEOUT(1002);
    }

    override val message: String?
        get() = when(errorCode) {
            ErrorCode.NO_INTERNET_CONNECTION -> "NO_INTERNET_CONNECTION"
            ErrorCode.SSL_EXCEPTION -> "SSL_EXCEPTION"
            ErrorCode.SESSION_TIMEOUT -> "SESSION_TIMEOUT"
        }
}