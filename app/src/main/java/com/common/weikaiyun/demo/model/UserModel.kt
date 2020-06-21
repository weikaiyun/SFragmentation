package com.common.weikaiyun.demo.model

import androidx.lifecycle.LiveData
import com.common.weikaiyun.demo.db.User
import com.common.weikaiyun.demo.db.UserDatabase
import com.common.weikaiyun.retrofit.safecall.SafeApiRequest

class UserModel: SafeApiRequest() {
    fun getAll(): LiveData<List<User>> = UserDatabase.getInstance().userDao().getAll()

    suspend fun insertAll(vararg users: User) = UserDatabase.getInstance().userDao().insertAll(*users)

    suspend fun updateAll(vararg users: User) = UserDatabase.getInstance().userDao().updateAll(*users)
}