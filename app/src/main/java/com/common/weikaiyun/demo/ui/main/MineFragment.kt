package com.common.weikaiyun.demo.ui.main

import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportFragment

class MineFragment: BaseSupportFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_mine

    companion object {
        fun newInstance(): MineFragment = MineFragment()
    }
}