package com.common.weikaiyun.fragmentation;

import android.os.Bundle;
import android.view.MotionEvent;

public interface ISupportActivity {
    SupportActivityDelegate getSupportDelegate();

    ExtraTransaction extraTransaction();

    void post(Runnable runnable);

    void onBackPressed();

    void onBackPressedSupport();

    boolean dispatchTouchEvent(MotionEvent ev);

    int getContentViewID();

    void initData(Bundle savedInstanceState);

    void initView(Bundle savedInstanceState);
}
