package com.common.weikaiyun.demo.ui.demo

import android.os.Bundle
import android.view.View
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportFragment
import com.common.weikaiyun.util.trigger
import com.common.weikaiyun.fragmentargument.argument
import kotlinx.android.synthetic.main.fragment_demo2.*

class DemoFragment2: BaseSupportFragment() {
    private var param1: Int by argument()
    private var param2: String by argument()
    companion object {
        fun newInstance(param1: Int, param2: String): DemoFragment2 =
            DemoFragment2().apply {
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
//                    .start(DemoFragment3.newInstance(3, "start4"))
                start(DemoFragment3.newInstance(3, "start4"))
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_demo2
}