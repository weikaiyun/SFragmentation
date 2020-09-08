package com.common.weikaiyun.util

import android.view.View
import com.common.weikaiyun.R

/**
 * get set
 * 给view添加一个上次触发时间的属性（用来屏蔽连续触发操作）
 */
private var <T : View>T.triggerLastTime: Long
    get() = if (getTag(R.id.triggerLastTimeKey) != null) getTag(R.id.triggerLastTimeKey) as Long else 0
    set(value) {
        setTag(R.id.triggerLastTimeKey, value)
    }

/**
 * get set
 * 给view添加一个延迟的属性（用来屏蔽连续触发操作）
 */
private var <T : View> T.triggerDelay: Long
    get() = if (getTag(R.id.triggerDelayKey) != null) getTag(R.id.triggerDelayKey) as Long else -1
    set(value) {
        setTag(R.id.triggerDelayKey, value)
    }

/**
 * 判断时间是否满足再次触发的要求
 */
private fun <T : View> T.triggerEnable(): Boolean {
    var triggerable = false
    val currentTriggerTime = System.currentTimeMillis()
    if (currentTriggerTime - triggerLastTime >= triggerDelay) {
        triggerable = true
    }
    triggerLastTime = currentTriggerTime
    return triggerable
}

/***
 * 带延迟过滤触发事件的 View 扩展
 * @param delay Long 延迟时间，默认500毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.trigger(delay: Long = 500, block: (T) -> Unit) {
    triggerDelay = delay
    if (triggerEnable()) {
        block(this)
    }
}