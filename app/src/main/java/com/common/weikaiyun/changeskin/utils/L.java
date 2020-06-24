package com.common.weikaiyun.changeskin.utils;

import android.util.Log;

public class L {
    private static final String TAG = "Skin";
    private static boolean debug = true;

    public static void e(String msg) {
        if (debug)
            Log.e(TAG, msg);
    }
}
