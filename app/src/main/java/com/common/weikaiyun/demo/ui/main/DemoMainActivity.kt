package com.common.weikaiyun.demo.ui.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.db.User
import com.common.weikaiyun.demo.ui.base.BaseSupportActivity
import com.common.weikaiyun.demo.viewmodel.UserViewModel
import com.weikaiyun.fragmentation.SupportHelper

class DemoMainActivity : BaseSupportActivity() {

    override fun getContentViewID(): Int = R.layout.activity_demo_main

    override fun initData(savedInstanceState: Bundle?) {
        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val user1 = User(1, "1", 1, 1, 1, "1")
        val user2 = User(2, "2", 2, 2, 2, "2")
        val user3 = User(3, "3", 3, 3, 3, "3")
        val user4 = User(4, "4", 4, 4, 4, "4")
        val user5 = User(5, "5", 5, 5, 5, "5")

        viewModel.updateAll(user1, user2, user3, user4, user5)
    }

    override fun initView(savedInstanceState: Bundle?) {
        var mainFragment = SupportHelper.findFragment(supportFragmentManager, DemoMainFragment::class.java)
        if (mainFragment == null) {
            mainFragment = DemoMainFragment.newInstance()
            loadRootFragment(R.id.container, mainFragment)
        }
    }
}
