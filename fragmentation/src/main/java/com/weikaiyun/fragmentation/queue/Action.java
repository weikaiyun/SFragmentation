package com.weikaiyun.fragmentation.queue;

import androidx.fragment.app.FragmentManager;

public abstract class Action {
    public static final long DEFAULT_POP_TIME = 320L;

    public static final int ACTION_NORMAL = 0;
    public static final int ACTION_POP = 1;
    public static final int ACTION_BACK = 2;
    public static final int ACTION_LOAD = 3;

    public FragmentManager fragmentManager;
    public int action = ACTION_NORMAL;
    public long duration = 0;

    public Action() {
    }

    public Action(int action) {
        this.action = action;
    }

    public Action(int action, FragmentManager fragmentManager) {
        this(action);
        this.fragmentManager = fragmentManager;
    }

    public abstract void run();
}
