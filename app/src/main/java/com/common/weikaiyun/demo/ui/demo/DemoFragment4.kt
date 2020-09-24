package com.common.weikaiyun.demo.ui.demo

import android.os.Bundle
import android.view.View
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportFragment
import com.common.weikaiyun.util.trigger
import com.common.weikaiyun.fragmentargument.argument
import kotlinx.android.synthetic.main.fragment_demo4.*

class DemoFragment4: BaseSupportFragment() {
    private var param1: Int by argument()
    private var param2: String by argument()
    private var param3: String by argument()
    private var param4: String by argument()
    companion object {
        fun newInstance(param1: Int, param2: String, param3: String, param4: String): DemoFragment4 =
            DemoFragment4().apply {
                this.param1 = param1
                this.param2 = param2
                this.param3 = param3
                this.param4 = param4
            }
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        title.text = "DemoFragment$param1"
        button1.text = param2
        button1.setOnClickListener {
            it.trigger(400) {
//                extraTransaction()
//                    .setCustomAnimations(R.anim.h_fragment_enter, R.anim.h_fragment_pop_exit,
//                        R.anim.h_fragment_pop_enter, R.anim.h_fragment_exit)
//                    .popTo(DemoFragment1::class.java.name, false)
                popTo(DemoFragment1::class.java, false)
            }
        }

        button2.text = param3

        button2.setOnClickListener {
            it.trigger(1000) {
//                extraTransaction()
//                    .setCustomAnimations(R.anim.h_fragment_enter, R.anim.h_fragment_pop_exit,
//                        R.anim.h_fragment_pop_enter, R.anim.h_fragment_exit)
//                    .startWithPopTo(DemoFragment5.newInstance(5, "testStartWithPopTo"),
//                    DemoFragment1::class.java.name, false)
                startWithPopTo(
                    DemoFragment5.newInstance(5, "testStartWithPopTo"),
                    DemoFragment1::class.java, false)
            }
        }

        button3.text = param4
        button3.setOnClickListener {
            it.trigger(1000) {
//                extraTransaction()
//                    .setCustomAnimations(R.anim.h_fragment_enter, R.anim.h_fragment_pop_exit,
//                        R.anim.h_fragment_pop_enter, R.anim.h_fragment_exit)
//                    .startWithPop(DemoFragment5.newInstance(5, "testStartWithPop"))
                startWithPop(DemoFragment5.newInstance(5, "testStartWithPop"))
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_demo4
}