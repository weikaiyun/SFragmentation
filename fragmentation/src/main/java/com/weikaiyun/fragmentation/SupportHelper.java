package com.weikaiyun.fragmentation;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentationMagician;

import java.util.ArrayList;
import java.util.List;

public class SupportHelper {
    private static final long SHOW_SPACE = 200L;

    private SupportHelper() {
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInput(final View view) {
        if (view == null || view.getContext() == null) return;
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        }, SHOW_SPACE);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(View view) {
        if (view == null || view.getContext() == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 获得栈顶SupportFragment
     */
    public static ISupportFragment getTopFragment(FragmentManager fragmentManager) {
        return getTopFragment(fragmentManager, 0);
    }

    public static ISupportFragment getTopFragment(FragmentManager fragmentManager, int containerId) {
        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);
        for (int i = fragmentList.size() - 1; i >= 0; i--) {
            Fragment fragment = fragmentList.get(i);
            if (fragment instanceof ISupportFragment) {
                ISupportFragment iFragment = (ISupportFragment) fragment;
                if (containerId == 0) return iFragment;

                if (containerId == iFragment.getSupportDelegate().mContainerId) {
                    return iFragment;
                }
            }
        }
        return null;
    }

    /**
     * 获取目标Fragment的前一个SupportFragment
     *
     * @param fragment 目标Fragment
     */
    public static ISupportFragment getPreFragment(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getParentFragmentManager();

        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);
        int index = fragmentList.indexOf(fragment);
        for (int i = index - 1; i >= 0; i--) {
            Fragment preFragment = fragmentList.get(i);
            if (preFragment instanceof ISupportFragment) {
                return (ISupportFragment) preFragment;
            }
        }
        return null;
    }

    /**
     * Same as fragmentManager.findFragmentByTag(fragmentClass.getName());
     * find Fragment from FragmentStack
     */
    @SuppressWarnings("unchecked")
    public static <T extends ISupportFragment> T findFragment(FragmentManager fragmentManager, Class<T> fragmentClass) {
        return findStackFragment(fragmentClass, null, fragmentManager);
    }

    /**
     * Same as fragmentManager.findFragmentByTag(fragmentTag);
     * <p>
     * find Fragment from FragmentStack
     */
    @SuppressWarnings("unchecked")
    public static <T extends ISupportFragment> T findFragment(FragmentManager fragmentManager, String fragmentTag) {
        return findStackFragment(null, fragmentTag, fragmentManager);
    }

    /**
     * 从栈顶开始，寻找FragmentManager以及其所有子栈, 直到找到状态为show & userVisible的Fragment
     */
    public static ISupportFragment getActiveFragment(FragmentManager fragmentManager) {
        return getActiveFragment(fragmentManager, null);
    }

    @SuppressWarnings("unchecked")
    static <T extends ISupportFragment> T findStackFragment(Class<T> fragmentClass, String toFragmentTag, FragmentManager fragmentManager) {
        Fragment fragment = null;

        if (toFragmentTag == null) {
            List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);

            int sizeChildFrgList = fragmentList.size();

            for (int i = sizeChildFrgList - 1; i >= 0; i--) {
                Fragment brotherFragment = fragmentList.get(i);
                if (brotherFragment instanceof ISupportFragment && brotherFragment.getClass().getName().equals(fragmentClass.getName())) {
                    fragment = brotherFragment;
                    break;
                }
            }
        } else {
            fragment = fragmentManager.findFragmentByTag(toFragmentTag);
            if (fragment == null) return null;
        }
        return (T) fragment;
    }

    private static ISupportFragment getActiveFragment(FragmentManager fragmentManager, ISupportFragment parentFragment) {
        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fragmentManager);
        for (int i = fragmentList.size() - 1; i >= 0; i--) {
            Fragment fragment = fragmentList.get(i);
            if (fragment instanceof ISupportFragment) {
                if (fragment.isResumed() && !fragment.isHidden()) {
                    return getActiveFragment(fragment.getChildFragmentManager(), (ISupportFragment) fragment);
                }
            }
        }
        return parentFragment;
    }

    static List<Fragment> getWillPopFragments(FragmentManager fm, String targetTag, boolean includeTarget) {
        Fragment target = fm.findFragmentByTag(targetTag);
        List<Fragment> willPopFragments = new ArrayList<>();

        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fm);

        int size = fragmentList.size();

        int startIndex = -1;
        for (int i = size - 1; i >= 0; i--) {
            if (target == fragmentList.get(i)) {
                if (includeTarget) {
                    startIndex = i;
                } else if (i + 1 < size) {
                    startIndex = i + 1;
                }
                break;
            }
        }

        if (startIndex == -1) return willPopFragments;

        for (int i = size - 1; i >= startIndex; i--) {
            Fragment fragment = fragmentList.get(i);
            if (fragment != null && fragment.getView() != null) {
                willPopFragments.add(fragment);
            }
        }
        return willPopFragments;
    }
}
