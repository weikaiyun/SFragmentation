package com.common.weikaiyun.demo.model

import com.common.weikaiyun.demo.db.User
import com.common.weikaiyun.demo.db.UserDatabase

class UserModel {
    suspend fun insertAll(vararg users: User) = UserDatabase.getInstance().userDao().insertAll(*users)

    suspend fun updateAll(vararg users: User) = UserDatabase.getInstance().userDao().updateAll(*users)
}