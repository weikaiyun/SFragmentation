package com.weikaiyun.fragmentation;

import android.view.MotionEvent;

public interface ISupportActivity {
    SupportActivityDelegate getSupportDelegate();

    ExtraTransaction extraTransaction();

    void post(Runnable runnable);

    void onBackPressed();

    void onBackPressedSupport();

    boolean dispatchTouchEvent(MotionEvent ev);
}
