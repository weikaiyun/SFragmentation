package com.common.weikaiyun.demo.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.weikaiyun.fragmentation_swipeback.SwipeBackFragment;

abstract public class BaseSwipeBackFragment extends SwipeBackFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container,false);
        return attachToSwipeBack(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view, savedInstanceState);
        initData(view, savedInstanceState);
    }

    abstract public int getLayoutId();

    public void initView(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void initData(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }
}
