package com.common.weikaiyun.retrofit.exception

import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import javax.net.ssl.SSLHandshakeException

object ExceptionHandler {
    private const val UNAUTHORIZED = 401
    private const val FORBIDDEN = 403
    private const val NOT_FOUND = 404
    private const val REQUEST_TIMEOUT = 408
    private const val INTERNAL_SERVER_ERROR = 500
    private const val BAD_GATEWAY = 502
    private const val SERVICE_UNAVAILABLE = 503
    private const val GATEWAY_TIMEOUT = 504
    fun handleException(e: Throwable): ResponseThrowable {
        val ex: ResponseThrowable
        when (e) {
            is HttpException -> {
                ex = when (e.code()) {
                    UNAUTHORIZED -> {
                        ResponseThrowable(e, "未授权")
                    }
                    FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, GATEWAY_TIMEOUT, INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE -> {
                        ResponseThrowable(e, "网络请求错误")
                    }
                    else -> {
                        ResponseThrowable(e, "网络请求错误")
                    }
                }
            }

            is CustomException -> {
                val customException = CustomException(e.code, e.message)
                ex = ResponseThrowable(customException, e.message)
            }

            is ConnectException, is InterruptedIOException -> {
                ex = ResponseThrowable(e, "can not connect")
            }

            is KotlinNullPointerException -> {
                ex = ResponseThrowable(e, "Retrofit Response Body is Null")
            }

            is SSLHandshakeException -> {
                ex = ResponseThrowable(e, "证书验证失败")
            }

            else -> {
                ex = ResponseThrowable(e, "未知错误")
            }
        }
        return ex
    }

    class ResponseThrowable(throwable: Throwable, val responseMessage: String) : Exception(throwable)

    class CustomException(val code: Int, override val message: String) : RuntimeException()
}