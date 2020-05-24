package com.common.weikaiyun.windowinset

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout

@Suppress("DEPRECATION")
class MultiApplyWindowFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    FrameLayout(context, attrs, defStyleAttr) {

    private val mTempRect = Rect()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if(changed) requestApplyInsets()
    }
    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        requestApplyInsets()
        super.addView(child, index, params)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        var applied = super.onApplyWindowInsets(insets)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            applied = applied.consumeDisplayCutout()
        }
        if (applied.isConsumed) {
            // If the ViewPager consumed all insets, return now
            return applied
        }
        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight

        // Now we'll manually dispatch the insets to our children. Since ViewPager
        // children are always full-height, we do not want to use the standard
        // ViewGroup dispatchApplyWindowInsets since if child 0 consumes them,
        // the rest of the children will not receive any insets. To workaround this
        // we manually dispatch the applied insets, not allowing children to
        // consume them from each other. We do however keep track of any insets
        // which are consumed, returning the union of our children's consumption
        val res = mTempRect
        res.left = applied.systemWindowInsetLeft
        res.top = applied.systemWindowInsetTop
        res.right = applied.systemWindowInsetRight
        res.bottom = applied.systemWindowInsetBottom

        var i = 0
        val count = childCount
        var consume = false
        while (i < count) {
            val childAt = getChildAt(i)
            val left = (applied.systemWindowInsetLeft - childAt.left).coerceAtLeast(0)
            val right = (applied.systemWindowInsetRight - (measuredWidth - childAt.right)).coerceAtLeast(0)
            val top = (applied.systemWindowInsetTop - childAt.top).coerceAtLeast(0)
            val bottom = (applied.systemWindowInsetBottom - (measuredHeight - childAt.bottom)).coerceAtLeast(0)
            val replaceSystemWindowInsets = applied.replaceSystemWindowInsets(left, top, right, bottom)
            val childInsets = childAt.dispatchApplyWindowInsets(replaceSystemWindowInsets)
            // Now keep track of any consumed by tracking each dimension's min
            // value
            res.left = childInsets.systemWindowInsetLeft.coerceAtMost(res.left)
            res.top = childInsets.systemWindowInsetTop.coerceAtMost(res.top)
            res.right = childInsets.systemWindowInsetRight.coerceAtMost(res.right)
            res.bottom = childInsets.systemWindowInsetBottom.coerceAtMost(res.bottom)
            i++
            consume = consume || childInsets.isConsumed
        }

        // Now return a new WindowInsets, using the consumed window insets
        return applied.replaceSystemWindowInsets(
            res.left, res.top, res.right, res.bottom
        ).let {
            if (consume) it.consumeSystemWindowInsets() else it
        }
    }
}