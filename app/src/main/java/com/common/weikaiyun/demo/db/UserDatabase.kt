package com.common.weikaiyun.demo.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.common.weikaiyun.demo.DemoApplication
import com.common.weikaiyun.room.*

@Database(entities = [User::class], version = 5)
abstract class UserDatabase : RoomDatabase() {
    companion object {
        fun getInstance() = Helper.instance

        val migration_1_2 = DatabaseMigration(DemoApplication.context,
            UserDatabase::class.java, 1, 2)
        val migration_2_3 = DatabaseMigration(DemoApplication.context,
            UserDatabase::class.java, 2, 3)
        val migration_3_4 = DatabaseMigration(DemoApplication.context,
            UserDatabase::class.java, 3, 4)
        val migration_4_5 = DatabaseMigration(DemoApplication.context,
            UserDatabase::class.java, 4, 5)
    }
    private object Helper {
        val instance = Room.databaseBuilder(DemoApplication.context, UserDatabase::class.java, "user-db")
            .fallbackToDestructiveMigration()
            .addMigrations(migration_1_2, migration_2_3, migration_3_4, migration_4_5)
            .build()
    }

    abstract fun userDao(): UserDao
}