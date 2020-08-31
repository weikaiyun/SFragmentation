package com.common.weikaiyun.retrofit.okhttp

import com.common.weikaiyun.demo.DemoApplication
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

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
        if (OkHttpClientManager.canTrustAll) {
            try {
                val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(null as KeyStore?)
                val trustManagers = trustManagerFactory.trustManagers
                if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
                    throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
                }
                val trustManager = trustManagers[0] as X509TrustManager
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, Array(1) { trustManager }, null)
                val sslSocketFactory = sslContext.socketFactory
                builder.sslSocketFactory(sslSocketFactory, trustManager)
                builder.hostnameVerifier { _, _ -> true }
            } catch (e: Exception) {

            }
        }
        return builder.build()
    }
}