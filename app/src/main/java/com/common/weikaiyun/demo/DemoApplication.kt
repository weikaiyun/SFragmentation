package com.common.weikaiyun.demo

import android.app.Application
import android.content.Context
import com.common.weikaiyun.demo.db.UserDatabase
import com.weikaiyun.fragmentation.Fragmentation

class DemoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        context = this
        UserDatabase.getInstance()

        Fragmentation.builder() // 设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
            .stackViewMode(Fragmentation.BUBBLE)
            .debug(true) // 实际场景建议.debug(BuildConfig.DEBUG)
            .install()
    }

    companion object {
        lateinit var context: Context
        lateinit var instance: DemoApplication
    }
}