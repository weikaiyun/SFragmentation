package com.common.weikaiyun.retrofit.okhttp

import android.text.TextUtils
import android.webkit.CookieManager
import okhttp3.Interceptor
import okhttp3.Response

class CustomInterceptor: Interceptor {
    private val cookieManager = CookieManager.getInstance()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val curUrl = request.url
        val url = curUrl.toString()
        val cookie = cookieManager.getCookie(url)
        val builder = request.newBuilder()
        if (!TextUtils.isEmpty(cookie)) {
            builder.header("Cookie", cookie)
        }

        OkHttpClientManager.headerGenerator?.apply {
            val headers: List<CustomHeader> = generateHeaders()
            for ((name, value) in headers) {
                builder.addHeader(name, value)
            }
        }

        val realRequest = builder.build()
        val originalResponse = chain.proceed(realRequest)

        /*
         * -----------------↓↓↓ RESPONSE ↓↓↓-----------------
         */
        val headers = originalResponse.headers("Set-Cookie")
        for (header in headers) {
            cookieManager.setCookie(originalResponse.request.url.toString(), header)
        }

        return originalResponse
    }
}