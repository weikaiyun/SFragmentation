package com.common.weikaiyun.demo

import android.app.Application
import android.content.Context
import com.common.weikaiyun.R
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
            .animation(R.anim.v_fragment_enter, R.anim.v_fragment_pop_exit, R.anim.v_fragment_pop_enter, R.anim.v_fragment_exit)
            .install()
    }

    companion object {
        @JvmStatic
        lateinit var context: Context

        @JvmStatic
        lateinit var instance: DemoApplication
    }
}