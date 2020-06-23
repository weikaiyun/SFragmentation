package com.common.weikaiyun.fragmentation.adapter

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

@SuppressLint("WrongConstant")
abstract class CustomFragmentStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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