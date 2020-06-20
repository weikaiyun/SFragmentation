package com.common.weikaiyun.retrofit.netbean

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetBean<T>(val code: Int, val message: String, val data: T) {
    val isOk: Boolean
        get() = code == 200
}