package com.common.weikaiyun.demo.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.weikaiyun.fragmentation_swipeback.SwipeBackActivity;

abstract public class BaseSwipeBackActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewID());
    }

    abstract public int getContentViewID();

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initView(savedInstanceState);
        initData(savedInstanceState);
    }

    public void initView(@Nullable Bundle savedInstanceState) {

    }

    public void initData(@Nullable Bundle savedInstanceState) {

    }
}
