package com.common.weikaiyun.demo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "distance") val distance: Int,
    @ColumnInfo(name = "gender") val gender: Int,
    @ColumnInfo(name = "last_name") val lastName: String
)