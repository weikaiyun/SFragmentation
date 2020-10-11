package com.common.weikaiyun.demo.ui.main

import android.os.Bundle
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportActivity
import com.weikaiyun.fragmentation.SupportHelper

class DemoMainActivity : BaseSupportActivity() {

    override fun getContentViewID(): Int = R.layout.activity_demo_main

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(savedInstanceState: Bundle?) {
        var mainFragment = SupportHelper.findFragment(supportFragmentManager, DemoMainFragment::class.java)
        if (mainFragment == null) {
            mainFragment = DemoMainFragment.newInstance()
            loadRootFragment(R.id.container, mainFragment)
        }
    }
}
