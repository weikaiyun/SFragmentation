package com.common.weikaiyun.fragmentation.adapter

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.common.weikaiyun.fragmentation.SupportActivity
import com.common.weikaiyun.fragmentation.SupportFragment

class CustomFragmentStateAdapter(
    activity: SupportActivity,
    private val fragments: MutableList<SupportFragment>
) :
    FragmentStateAdapter(activity) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}