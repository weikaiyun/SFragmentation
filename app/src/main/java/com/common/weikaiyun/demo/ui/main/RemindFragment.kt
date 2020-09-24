package com.common.weikaiyun.demo.ui.main

import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportFragment

class RemindFragment: BaseSupportFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_remind

    companion object {
        fun newInstance(): RemindFragment = RemindFragment()
    }
}