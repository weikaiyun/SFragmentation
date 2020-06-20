package com.common.weikaiyun.retrofit.safecall

import com.common.weikaiyun.retrofit.exception.ExceptionHandler
import com.common.weikaiyun.retrofit.netbean.NetBean
import retrofit2.Response

abstract class SafeApiRequest {

    suspend fun<T: Any> apiRequest(call: suspend () -> Response<NetBean<T>>) : Result<T> {
        try {
            val response = call.invoke()

            if (response.body()!!.isOk) {
                return Result.Success(response.body()!!.data)
            }

            val customException = ExceptionHandler.CustomException(response.body()!!.code, response.body()!!.message)
            return Result.Error(ExceptionHandler.handleException(customException))

        } catch (e: Exception) {
            return Result.Error(ExceptionHandler.handleException(e))
        }
    }

}