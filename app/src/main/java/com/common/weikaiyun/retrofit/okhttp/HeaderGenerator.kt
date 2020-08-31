package com.common.weikaiyun.retrofit.okhttp

interface HeaderGenerator {
    fun generateHeaders(): List<CustomHeader>
}