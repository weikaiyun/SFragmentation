package com.common.weikaiyun.demo.ui.main

import android.os.Bundle
import android.view.View
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportFragment
import com.common.weikaiyun.demo.ui.demo.DemoFragment1
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: BaseSupportFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView(view: View, savedInstanceState: Bundle?) {
        jump.setOnClickListener {
            //此处使用_mActivity.start, 则DemoFragment1与DemoMainFragment平级
            _mActivity.start(DemoFragment1.newInstance(1, "start2"))
        }
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
    }
}