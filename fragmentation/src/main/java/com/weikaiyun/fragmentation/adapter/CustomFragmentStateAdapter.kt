package com.weikaiyun.fragmentation.adapter

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.weikaiyun.fragmentation.BaseActivity
import com.weikaiyun.fragmentation.BaseFragment


class CustomFragmentStateAdapter(
    activity: BaseActivity,
    private val fragments: MutableList<BaseFragment>
) :
    FragmentStateAdapter(activity) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}