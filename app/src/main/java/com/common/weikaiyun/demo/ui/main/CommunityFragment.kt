package com.common.weikaiyun.demo.ui.main

import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportFragment

class CommunityFragment: BaseSupportFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_community

    companion object {
        fun newInstance(): CommunityFragment = CommunityFragment()
    }
}