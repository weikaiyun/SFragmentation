package com.common.weikaiyun.retrofit.okhttp

import okhttp3.logging.HttpLoggingInterceptor

object OkHttpClientManager {
    var headerGenerator: HeaderGenerator? = null

    private val loggingInterceptor =  HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpHelper.createOkHttpClient(5, 20, 20,
        CustomInterceptor(), loggingInterceptor)
}