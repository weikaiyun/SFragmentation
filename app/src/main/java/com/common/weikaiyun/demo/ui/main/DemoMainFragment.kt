package com.common.weikaiyun.demo.ui.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.common.weikaiyun.R
import com.common.weikaiyun.demo.ui.base.BaseSupportFragment
import com.common.weikaiyun.util.ResUtils
import com.weikaiyun.fragmentation.SupportHelper
import kotlinx.android.synthetic.main.fragment_demo_main.*

class DemoMainFragment: BaseSupportFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_demo_main

    private lateinit var homeFragment: HomeFragment
    private lateinit var remindFragment: RemindFragment
    private lateinit var communityFragment: CommunityFragment
    private lateinit var mineFragment: MineFragment

    private var currentTab = HOME

    private val iconArr = arrayListOf<Drawable>(
        ResUtils.getDrawable(R.drawable.icon_bottom_homepage),
        ResUtils.getDrawable(R.drawable.icon_bottom_remind),
        ResUtils.getDrawable(R.drawable.icon_bottom_community),
        ResUtils.getDrawable(R.drawable.icon_bottom_mine)
    )
    private val selectedIconArr = arrayListOf<Drawable>(
        ResUtils.getDrawable(R.drawable.icon_bottom_homepage_selected),
        ResUtils.getDrawable(R.drawable.icon_bottom_remind_selected),
        ResUtils.getDrawable(R.drawable.icon_bottom_community_selected),
        ResUtils.getDrawable(R.drawable.icon_bottom_mine_selected)
    )

    private val textColor = ResUtils.getColor(R.color.color_normal)

    private val selectedTextColor = ResUtils.getColor(R.color.color_selected)

    override fun initView(view: View, savedInstanceState: Bundle?) {
        val homeFragmentInStack: HomeFragment? = SupportHelper.findFragment(childFragmentManager, HomeFragment::class.java)
        if (homeFragmentInStack != null) {
            homeFragment = homeFragmentInStack
            remindFragment = SupportHelper.findFragment(childFragmentManager, RemindFragment::class.java)
            communityFragment = SupportHelper.findFragment(childFragmentManager, CommunityFragment::class.java)
            mineFragment = SupportHelper.findFragment(childFragmentManager, MineFragment::class.java)
        } else {
            homeFragment = HomeFragment()
            remindFragment = RemindFragment()
            communityFragment = CommunityFragment()
            mineFragment = MineFragment()
            loadMultipleRootFragment(R.id.fl_container, currentTab, homeFragment, remindFragment, communityFragment, mineFragment)
        }

        cl_home.setOnClickListener {
            if (currentTab != HOME) {
                showHideFragment(homeFragment)
                checkTab(HOME)
            }
        }

        cl_remind.setOnClickListener {
            if (currentTab != REMIND) {
                showHideFragment(remindFragment)
                checkTab(REMIND)
            }
        }

        cl_community.setOnClickListener {
            if (currentTab != COMMUNITY) {
                showHideFragment(communityFragment)
                checkTab(COMMUNITY)
            }
        }

        cl_mine.setOnClickListener {
            if (currentTab != MINE) {
                showHideFragment(mineFragment)
                checkTab(MINE)
            }
        }

        checkTab(HOME)
    }

    private fun checkTab(tab: Int) {
        currentTab = tab
        when(currentTab) {
            HOME -> {
                img_home.setImageDrawable(selectedIconArr[HOME])
                tv_home.setTextColor(selectedTextColor)
                img_remind.setImageDrawable(iconArr[REMIND])
                tv_remind.setTextColor(textColor)
                img_community.setImageDrawable(iconArr[COMMUNITY])
                tv_community.setTextColor(textColor)
                img_mine.setImageDrawable(iconArr[MINE])
                tv_mine.setTextColor(textColor)
            }
            REMIND -> {
                img_home.setImageDrawable(iconArr[HOME])
                tv_home.setTextColor(textColor)
                img_remind.setImageDrawable(selectedIconArr[REMIND])
                tv_remind.setTextColor(selectedTextColor)
                img_community.setImageDrawable(iconArr[COMMUNITY])
                tv_community.setTextColor(textColor)
                img_mine.setImageDrawable(iconArr[MINE])
                tv_mine.setTextColor(textColor)
            }
            COMMUNITY -> {
                img_home.setImageDrawable(iconArr[HOME])
                tv_home.setTextColor(textColor)
                img_remind.setImageDrawable(iconArr[REMIND])
                tv_remind.setTextColor(textColor)
                img_community.setImageDrawable(selectedIconArr[COMMUNITY])
                tv_community.setTextColor(selectedTextColor)
                img_mine.setImageDrawable(iconArr[MINE])
                tv_mine.setTextColor(textColor)
            }
            MINE -> {
                img_home.setImageDrawable(iconArr[HOME])
                tv_home.setTextColor(textColor)
                img_remind.setImageDrawable(iconArr[REMIND])
                tv_remind.setTextColor(textColor)
                img_community.setImageDrawable(iconArr[COMMUNITY])
                tv_community.setTextColor(textColor)
                img_mine.setImageDrawable(selectedIconArr[MINE])
                tv_mine.setTextColor(selectedTextColor)
            }
        }
    }

    companion object {
        const val HOME = 0
        const val REMIND = 1
        const val COMMUNITY = 2
        const val MINE = 3

        @JvmStatic
        fun newInstance(): DemoMainFragment {
            return DemoMainFragment()
        }
    }
}