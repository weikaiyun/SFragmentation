package com.common.weikaiyun.util

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.common.weikaiyun.demo.DemoApplication

object ResUtils {
    fun getDrawable(@DrawableRes resId: Int): Drawable {
        return AppCompatResources.getDrawable(DemoApplication.context, resId)!!
    }

    fun getColor(@ColorRes resId: Int): Int {
        return DemoApplication.context.getColor(resId)
    }
}