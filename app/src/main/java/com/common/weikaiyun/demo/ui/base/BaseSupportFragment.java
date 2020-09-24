package com.common.weikaiyun.demo.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.weikaiyun.fragmentation.SupportFragment;

abstract public class BaseSupportFragment extends SupportFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container,false);
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
