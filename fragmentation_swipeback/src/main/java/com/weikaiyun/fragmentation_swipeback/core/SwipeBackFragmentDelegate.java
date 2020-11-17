package com.weikaiyun.fragmentation_swipeback.core;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weikaiyun.fragmentation.ISupportFragment;
import com.weikaiyun.fragmentation.SwipeBackLayout;

public class SwipeBackFragmentDelegate {
    private final Fragment mFragment;
    private final ISupportFragment mSupport;
    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackFragmentDelegate(ISwipeBackFragment swipeBackFragment) {
        if (!(swipeBackFragment instanceof Fragment) || !(swipeBackFragment instanceof ISupportFragment))
            throw new RuntimeException("Must extends Fragment and implements ISupportFragment!");
        mFragment = (Fragment) swipeBackFragment;
        mSupport = (ISupportFragment) swipeBackFragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        onFragmentCreate();
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (view instanceof SwipeBackLayout) {
            View childView = ((SwipeBackLayout) view).getChildAt(0);
            mSupport.getSupportDelegate().setBackground(childView);
        } else {
            mSupport.getSupportDelegate().setBackground(view);
        }
    }

    public View attachToSwipeBack(View view) {
        mSwipeBackLayout.attachToFragment(mSupport, view);
        return mSwipeBackLayout;
    }

    public void setEdgeLevel(SwipeBackLayout.EdgeLevel edgeLevel) {
        mSwipeBackLayout.setEdgeLevel(edgeLevel);
    }

    public void setEdgeLevel(int widthPixel) {
        mSwipeBackLayout.setEdgeLevel(widthPixel);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setEnableGesture(enable);
    }

    /**
     * Set the offset of the parallax slip.
     */
    public void setParallaxOffset(@FloatRange(from = 0.0f, to = 1.0f) float offset) {
        mSwipeBackLayout.setParallaxOffset(offset);
    }

    private void onFragmentCreate() {
        if (mFragment.getContext() == null) return;

        mSwipeBackLayout = new SwipeBackLayout(mFragment.getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
        mSwipeBackLayout.setBackgroundColor(Color.TRANSPARENT);
    }
}
