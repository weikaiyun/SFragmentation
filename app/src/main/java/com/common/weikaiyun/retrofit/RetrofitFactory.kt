package com.common.weikaiyun.retrofit

import com.common.weikaiyun.retrofit.okhttp.OkHttpClientManager
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitFactory {
    private val client = OkHttpClientManager.okHttpClient

    //此处可以使用多url，只要每次baseUrl不同即可
    fun retrofit(baseUrl : String) : Retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
}