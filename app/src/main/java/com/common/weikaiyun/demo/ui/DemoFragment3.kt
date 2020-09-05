package com.common.weikaiyun.demo.ui

import android.os.Bundle
import android.view.View
import com.common.weikaiyun.R
import com.common.weikaiyun.fragmentation.SupportFragment
import com.common.weikaiyun.fragmentation.fragmentargument.argument
import kotlinx.android.synthetic.main.fragment_demo.*

class DemoFragment3: SupportFragment() {
    private var param1: Int by argument()
    private var param2: String by argument()
    companion object {
        fun newInstance(param1: Int, param2: String): DemoFragment3 =
            DemoFragment3().apply {
                this.param1 = param1
                this.param2 = param2
            }
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        title.text = "DemoFragment$param1"
        button.text = param2
        button.setOnClickListener {
            start(DemoFragment4.newInstance(4, "popTo1", "start5"))
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_demo
}