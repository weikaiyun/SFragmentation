package com.common.weikaiyun.demo.db

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insertAll(vararg users: User)

    @Update
    suspend fun updateAll(vararg users: User)
}