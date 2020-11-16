package com.weikaiyun.fragmentation_swipeback.core;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.weikaiyun.fragmentation.ISupportActivity;
import com.weikaiyun.fragmentation.ISupportFragment;
import com.weikaiyun.fragmentation.SupportHelper;
import com.weikaiyun.fragmentation.SwipeBackLayout;

import java.util.List;

public class SwipeBackActivityDelegate {
    private final FragmentActivity mActivity;
    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackActivityDelegate(ISwipeBackActivity swipeBackActivity) {
        if (!(swipeBackActivity instanceof FragmentActivity) || !(swipeBackActivity instanceof ISupportActivity))
            throw new RuntimeException("Must extends FragmentActivity/AppCompatActivity and implements ISupportActivity");
        mActivity = (FragmentActivity) swipeBackActivity;
    }

    public void onCreate(Bundle savedInstanceState) {
        onActivityCreate();
    }

    public void onPostCreate(Bundle savedInstanceState) {
        mSwipeBackLayout.attachToActivity(mActivity);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setEnableGesture(enable);
    }

    public void setEdgeLevel(SwipeBackLayout.EdgeLevel edgeLevel) {
        mSwipeBackLayout.setEdgeLevel(edgeLevel);
    }

    public void setEdgeLevel(int widthPixel) {
        mSwipeBackLayout.setEdgeLevel(widthPixel);
    }

    public boolean swipeBackPriority() {
        List<Fragment> list = SupportHelper.getActiveFragments(mActivity.getSupportFragmentManager());
        int fragmentNum = 0;
        for (Fragment f : list) {
            if (f instanceof ISupportFragment
                    && ((ISupportFragment) f).getSupportDelegate().isCanPop()
                    && ((ISupportFragment) f).getSupportDelegate().isStartByFragmentation()) {
                fragmentNum++;
            }
        }
        return fragmentNum <= 0;
    }

    private void onActivityCreate() {
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mActivity.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        mSwipeBackLayout = new SwipeBackLayout(mActivity);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
    }
}
