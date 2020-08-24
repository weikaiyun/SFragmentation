package com.common.weikaiyun.demo

import com.common.weikaiyun.fragmentation.SupportFragment
import com.common.weikaiyun.fragmentation.fragmentargument.argument

class DemoFragment : SupportFragment() {
    private var param1: Int by argument()
    private var param2: String by argument()
    companion object {
        fun newInstance(param1: Int, param2: String): DemoFragment =
            DemoFragment().apply {
                this.param1 = param1
                this.param2 = param2
            }
    }

    override fun getLayoutId(): Int = 0
}