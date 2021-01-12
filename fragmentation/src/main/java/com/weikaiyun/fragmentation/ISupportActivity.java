package com.weikaiyun.fragmentation;

import android.view.MotionEvent;

import com.weikaiyun.fragmentation.animation.FragmentAnimator;

public interface ISupportActivity {

    SupportActivityDelegate getSupportDelegate();

    ExtraTransaction extraTransaction();

    void post(Runnable runnable);

    void onBackPressed();

    void onBackPressedSupport();

    boolean dispatchTouchEvent(MotionEvent ev);

    FragmentAnimator getFragmentAnimator();

    void setFragmentAnimator(FragmentAnimator fragmentAnimator);
}
