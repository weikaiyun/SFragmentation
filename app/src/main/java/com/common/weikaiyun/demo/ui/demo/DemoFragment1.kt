package com.common.weikaiyun.demo.ui.demo

import android.os.Bundle
import android.view.View
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSwipeBackFragment
import com.common.weikaiyun.fragmentargument.argument
import com.common.weikaiyun.util.trigger
import kotlinx.android.synthetic.main.fragment_demo.*

class DemoFragment1 : BaseSwipeBackFragment() {
    private var param1: Int by argument()
    private var param2: String by argument()
    companion object {
        const val REQUEST_CODE = 100

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
//            自定义动画
//            it.trigger(400) {
//                extraTransaction()
//                    .setCustomAnimations(R.anim.h_fragment_enter, R.anim.h_fragment_pop_exit,
//                        R.anim.h_fragment_pop_enter, R.anim.h_fragment_exit)
//                    .start(DemoFragment2.newInstance(2, "start3"))
//            }

//            正常start
//            it.trigger(400) {
//                start(DemoFragment2.newInstance(2, "start3"))
//            }

//          带返回结果的start
            it.trigger(400) {
                startForResult(DemoFragment2.newInstance(2, "start3"), REQUEST_CODE)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_demo

    //可见性判断
    override fun onInvisible() {

    }

    override fun onVisible() {

    }

    //懒加载
    override fun lazyInit() {

    }

    //返回处理结果
    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        if (requestCode == REQUEST_CODE && resultCode == DemoFragment2.RESULT_CODE) {
            //处理返回结果
        }
    }
}