package com.common.weikaiyun.demo.model

import androidx.lifecycle.LiveData
import com.common.weikaiyun.demo.db.User
import com.common.weikaiyun.demo.db.UserDatabase
import com.common.weikaiyun.retrofit.safecall.SafeApiRequest

class UserModel: SafeApiRequest() {
    fun getAll(): LiveData<List<User>> = UserDatabase.getInstance().userDao().getAll()

    fun loadAllByIds(userIds: IntArray): LiveData<List<User>> = UserDatabase.getInstance().userDao().loadAllByIds(userIds)

    fun findByName(first: String, last: String): LiveData<User> = UserDatabase.getInstance().userDao().findByName(first, last)

    suspend fun insertAll(vararg users: User) = UserDatabase.getInstance().userDao().insertAll(*users)

    suspend fun delete(user: User) = UserDatabase.getInstance().userDao().delete(user)
}