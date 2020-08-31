package com.common.weikaiyun.retrofit.okhttp

import com.common.weikaiyun.demo.DemoApplication
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

object OkHttpHelper {
    fun createOkHttpClient(connectTimeout: Int, readTimeout: Int, writeTimeout: Int, vararg interceptors: Interceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
        var httpCache: File? = DemoApplication.instance.getExternalFilesDir("httpCache")
        if (httpCache == null) {
            val cacheDir: File = DemoApplication.instance.cacheDir
            httpCache = File(cacheDir, "httpCache")
        }
        builder.cache(Cache(httpCache, 100 * 1024 * 1024))
        builder.connectTimeout(connectTimeout.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(writeTimeout.toLong(), TimeUnit.SECONDS)
        for (interceptor in interceptors) {
            builder.addInterceptor(interceptor)
        }

        //此处需添加证书处理, cert_file放在asset目录下即可
        HttpsUtil.addCert(DemoApplication.instance, "cert_file", builder)

        return builder.build()
    }
}