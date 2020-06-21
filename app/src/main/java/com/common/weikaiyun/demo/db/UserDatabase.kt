package com.common.weikaiyun.demo.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.common.weikaiyun.demo.DemoApplication
import com.common.weikaiyun.room.*

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    companion object {
        fun getInstance() = Helper.instance

        val migration_1_2 = DatabaseMigration(DemoApplication.context, UserDatabase::class.java, 1, 2)
        val migration_2_3 = DatabaseMigration(DemoApplication.context, UserDatabase::class.java, 2, 3)
    }
    private object Helper {
        val instance = Room.databaseBuilder(DemoApplication.context, UserDatabase::class.java, "user-db")
            .fallbackToDestructiveMigration()
            .addMigrations(migration_1_2, migration_2_3)
            .build()
    }

    abstract fun userDao(): UserDao
}