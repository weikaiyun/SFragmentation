package com.common.weikaiyun.fragmentation.adapter

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * This class is FragmentPagerAdapter where all the fragments are in an array and you can access to them later.
 * @property mFragments the list of fragments
 * 这个类可以配合 Fragmentation框架使用，在ViewPager中使用supportFragment，详情可以查看下面两个链接，可能需要翻墙才能打开
 *
 * https://medium.com/@pjonceski/fragmentpageradapter-with-fragments-that-restore-their-state-properly-a427ecfd792e
 *
 * https://inthecheesefactory.com/blog/fragment-state-saving-best-practices/en
 */

abstract class CustomFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragments = SparseArray<Fragment>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        mFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        mFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    /**
     * Returns the instance of the fragment if it is created, null otherwise.
     */
    fun getFragment(position: Int): Fragment? {
        return if (mFragments.size() == 0) {
            null
        } else mFragments.get(position)
    }
}