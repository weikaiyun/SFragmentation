package com.common.weikaiyun.demo.ui.demo

import android.os.Bundle
import android.view.View
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportFragment
import com.common.weikaiyun.fragmentargument.argument
import com.common.weikaiyun.util.trigger
import kotlinx.android.synthetic.main.fragment_demo.*

class DemoFragment1 : BaseSupportFragment() {
    private var param1: Int by argument()
    private var param2: String by argument()
    companion object {
        fun newInstance(param1: Int, param2: String): DemoFragment1 =
            DemoFragment1().apply {
                this.param1 = param1
                this.param2 = param2
            }
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        title.text = "DemoFragment$param1"
        button.text = param2
        button.setOnClickListener {
            it.trigger(400) {
//                extraTransaction()
//                    .setCustomAnimations(R.anim.h_fragment_enter, R.anim.h_fragment_pop_exit,
//                        R.anim.h_fragment_pop_enter, R.anim.h_fragment_exit)
//                    .start(DemoFragment2.newInstance(2, "start3"))
                start(DemoFragment2.newInstance(2, "start3"))
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_demo
}