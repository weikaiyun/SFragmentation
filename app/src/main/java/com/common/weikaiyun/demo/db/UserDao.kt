package com.common.weikaiyun.demo.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<User>>

    @Insert
    suspend fun insertAll(vararg users: User)

    @Update
    suspend fun updateAll(vararg users: User)
}