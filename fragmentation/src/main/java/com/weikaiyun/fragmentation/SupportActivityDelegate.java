package com.weikaiyun.fragmentation;

import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentationMagician;

import com.weikaiyun.fragmentation.debug.DebugStackDelegate;
import com.weikaiyun.fragmentation.queue.Action;

import java.util.List;

public class SupportActivityDelegate {
    private ISupportActivity mSupport;
    private FragmentActivity mActivity;

    private TransactionDelegate mTransactionDelegate;

    private DebugStackDelegate mDebugStackDelegate;

    private int mDefaultFragmentBackground = 0;

    public SupportActivityDelegate(ISupportActivity support) {
        if (!(support instanceof FragmentActivity))
            throw new RuntimeException("Must extends FragmentActivity/AppCompatActivity");
        this.mSupport = support;
        this.mActivity = (FragmentActivity) support;
        this.mDebugStackDelegate = new DebugStackDelegate(this.mActivity);
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，操作非回退栈Fragment
     */
    public ExtraTransaction extraTransaction() {

        return new ExtraTransaction.ExtraTransactionImpl<>((FragmentActivity) mSupport,
                getTopFragment(), getTransactionDelegate(), true);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        mTransactionDelegate = getTransactionDelegate();
        mDebugStackDelegate.onCreate(Fragmentation.getDefault().getMode());
    }

    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        mDebugStackDelegate.onPostCreate(Fragmentation.getDefault().getMode());
    }

    public void onDestroy() {
        mDebugStackDelegate.onDestroy();
    }

    public TransactionDelegate getTransactionDelegate() {
        if (mTransactionDelegate == null) {
            mTransactionDelegate = new TransactionDelegate(mSupport);
        }
        return mTransactionDelegate;
    }

    /**
     * Causes the Runnable r to be added to the action queue.
     * <p>
     * The runnable will be run after all the previous action has been run.
     * <p>
     * 前面的事务全部执行后 执行该Action
     */
    public void post(final Runnable runnable) {
        mTransactionDelegate.post(runnable);
    }

    /**
     * 不建议复写该方法,请使用 {@link #onBackPressedSupport} 代替
     */
    public void onBackPressed() {
        mTransactionDelegate.mActionQueue.enqueue(new Action(Action.ACTION_BACK) {
            @Override
            public void run() {
                // 获取activeFragment:即从栈顶开始 状态为show的那个Fragment
                ISupportFragment activeFragment =
                        SupportHelper.getActiveFragment(getSupportFragmentManager());

                if (mTransactionDelegate.dispatchBackPressedEvent(activeFragment)) return;

                mSupport.onBackPressedSupport();
            }
        });
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    public void onBackPressedSupport() {
        List<Fragment> list = FragmentationMagician.getActiveFragments(getSupportFragmentManager());
        int fragmentNum = 0;
        for (Fragment f : list) {
            if (f instanceof ISupportFragment && ((ISupportFragment) f).getSupportDelegate().isCanPop()) {
                fragmentNum++;
            }
        }
        if (fragmentNum > 0) {
            pop();
        } else {
            ActivityCompat.finishAfterTransition(mActivity);
        }
    }

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     */

    public void loadRootFragment(int containerId, ISupportFragment toFragment) {

        mTransactionDelegate.loadRootTransaction(getSupportFragmentManager(),
                containerId, toFragment);
    }

    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    public void loadMultipleRootFragment(int containerId, int showPosition, ISupportFragment... toFragments) {

        mTransactionDelegate.loadMultipleRootTransaction(getSupportFragmentManager(),
                containerId, showPosition, toFragments);
    }

    /**
     * 这个的使用必须要注意
     * show一个Fragment,hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment,(只有通过loadMultipleRootFragment()载入的Fragment)
     * <p>
     *
     * 建议使用更明确的{@link #showHideFragment(ISupportFragment, ISupportFragment)}
     *
     * @param showFragment 需要show的Fragment
     */
    public void showHideFragment(ISupportFragment showFragment) {
        showHideFragment(showFragment, null);
    }

    /**
     * show一个Fragment,hide一个Fragment
     *
     * @param showFragment 需要show的Fragment
     * @param hideFragment 需要hide的Fragment
     */
    public void showHideFragment(ISupportFragment showFragment, ISupportFragment hideFragment) {

        mTransactionDelegate.showHideFragment(getSupportFragmentManager(), showFragment, hideFragment);
    }

    public void start(ISupportFragment toFragment) {
        start(toFragment, ISupportFragment.STANDARD);
    }

    /**
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {

        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(),
                getTopFragment(), toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
    }

    /**
     * Launch an fragment for which you would like a result when it popped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {

        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(),
                getTopFragment(), toFragment, requestCode, ISupportFragment.STANDARD,
                TransactionDelegate.TYPE_ADD_RESULT);
    }

    /**
     * Start the target Fragment and pop itself
     */
    public void startWithPop(ISupportFragment toFragment) {
        mTransactionDelegate.dispatchStartWithPopTransaction(getSupportFragmentManager(), getTopFragment(), toFragment);
    }

    public void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass,
                               boolean includeTargetFragment) {

        mTransactionDelegate.dispatchStartWithPopToTransaction(getSupportFragmentManager(),
                getTopFragment(), toFragment, targetFragmentClass.getName(), includeTargetFragment);
    }

    public void replaceFragment(ISupportFragment toFragment) {

        mTransactionDelegate.dispatchStartTransaction(getSupportFragmentManager(),
                getTopFragment(), toFragment, 0, ISupportFragment.STANDARD,
                TransactionDelegate.TYPE_REPLACE);
    }

    /**
     * Pop the child fragment.
     */
    public void pop() {
        mTransactionDelegate.pop(getSupportFragmentManager());
    }

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     * <p>
     * 出栈到目标fragment
     *
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        popTo(targetFragmentClass, includeTargetFragment, null);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment,
                      Runnable afterPopTransactionRunnable) {

        mTransactionDelegate.popTo(targetFragmentClass.getName(), includeTargetFragment,
                afterPopTransactionRunnable, getSupportFragmentManager());
    }

    private FragmentManager getSupportFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

    private ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    /**
     * 当Fragment根布局 没有设定background属性时,
     * Fragmentation默认使用Theme的android:windowbackground作为Fragment的背景,
     * 可以通过该方法改变Fragment背景。
     */
    public void setDefaultFragmentBackground(@DrawableRes int backgroundRes) {
        mDefaultFragmentBackground = backgroundRes;
    }

    public int getDefaultFragmentBackground() {
        return mDefaultFragmentBackground;
    }
}
