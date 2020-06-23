package com.common.weikaiyun.util

abstract class Continuation<in T> : kotlin.coroutines.Continuation<T> {
    abstract fun resume(value: T)
    abstract fun resumeWithException(exception: Throwable)
    override fun resumeWith(result: Result<T>) = result.fold(::resume, ::resumeWithException)
}