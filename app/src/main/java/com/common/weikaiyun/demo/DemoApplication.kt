package com.common.weikaiyun.demo

import android.app.Application
import android.content.Context
import com.common.weikaiyun.demo.db.UserDatabase

class DemoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        context = this
        UserDatabase.getInstance()
    }

    companion object {
        lateinit var context: Context
        lateinit var instance: DemoApplication
    }
}