package com.weikaiyun.fragmentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.weikaiyun.fragmentation.record.ResultRecord;
import com.weikaiyun.fragmentation.record.TransactionRecord;

public class SupportFragmentDelegate {
    int mContainerId;

    private TransactionDelegate mTransactionDelegate;
    TransactionRecord mTransactionRecord;
    Bundle mNewBundle;

    private ISupportFragment mSupportF;
    private Fragment mFragment;
    protected FragmentActivity _mActivity;
    private ISupportActivity mSupport;

    private boolean isVisible;

    private boolean canPop = true;

    public void setCanPop(boolean canPop) {
        this.canPop = canPop;
    }

    public boolean isCanPop() {
        return canPop;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public SupportFragmentDelegate(ISupportFragment support) {
        if (!(support instanceof Fragment))
            throw new RuntimeException("Must extends Fragment");
        this.mSupportF = support;
        this.mFragment = (Fragment) support;
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    public ExtraTransaction extraTransaction() {
        if (mTransactionDelegate == null)
            throw new RuntimeException(mFragment.getClass().getSimpleName() + " not attach!");

        return new ExtraTransaction.ExtraTransactionImpl<>((FragmentActivity) mSupport,
                mSupportF, mTransactionDelegate, false);
    }

    public void onAttach(Context context) {
        if (context instanceof ISupportActivity) {
            this.mSupport = (ISupportActivity) context;
            this._mActivity = (FragmentActivity) context;
            mTransactionDelegate = mSupport.getSupportDelegate().getTransactionDelegate();
        } else {
            throw new RuntimeException(context.getClass().getSimpleName() + " must impl ISupportActivity!");
        }
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = mFragment.getArguments();
        if (bundle != null) {
            mContainerId = bundle.getInt(TransactionDelegate.FRAGMENTATION_ARG_CONTAINER);
        }
    }

    public void onViewCreated(@Nullable Bundle savedInstanceState) {
        View view = mFragment.getView();
        if (view != null) {
            setBackground(view);
        }
    }

    public void setBackground(View view) {
        if (view.getBackground() != null) {
            return;
        }

        int defaultBg = mSupport.getSupportDelegate().getDefaultFragmentBackground();
        if (defaultBg == 0) {
            int background = getWindowBackground();
            view.setBackgroundResource(background);
        } else {
            view.setBackgroundResource(defaultBg);
        }
    }

    private int getWindowBackground() {
        TypedArray a = _mActivity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();
        return background;
    }

    public void onDestroy() {
        mTransactionDelegate.handleResultRecord(mFragment);
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
     * 类似 {@link Activity#setResult(int, Intent)}
     * <p>
     * Similar to {@link Activity#setResult(int, Intent)}
     *
     * @see #startForResult(ISupportFragment, int)
     */
    public void setFragmentResult(int resultCode, Bundle bundle) {
        Bundle args = mFragment.getArguments();
        if (args == null || !args.containsKey(TransactionDelegate.FRAGMENTATION_ARG_RESULT_RECORD)) {
            return;
        }

        ResultRecord resultRecord = args.getParcelable(TransactionDelegate.FRAGMENTATION_ARG_RESULT_RECORD);
        if (resultRecord != null) {
            resultRecord.resultCode = resultCode;
            resultRecord.resultBundle = bundle;
        }
    }

    /**
     * 添加NewBundle,用于启动模式为SingleTask/SingleTop时
     *
     * @see #start(ISupportFragment, int)
     */
    public void putNewBundle(Bundle newBundle) {
        this.mNewBundle = newBundle;
    }

    /**
     * Back Event
     *
     * @return false则继续向上传递, true则消费掉该事件
     */
    public boolean onBackPressedSupport() {
        return false;
    }

    /**********************************************************************************************/

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        Activity activity = mFragment.getActivity();
        if (activity == null) return;
        View view = activity.getWindow().getDecorView();
        SupportHelper.hideSoftInput(view);
    }

    /**
     * 显示软键盘
     */
    public void showSoftInput(View view) {
        SupportHelper.showSoftInput(view);
    }


    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     */

    public void loadRootFragment(int containerId, ISupportFragment toFragment) {

        mTransactionDelegate.loadRootTransaction(getChildFragmentManager(),
                containerId, toFragment);
    }

    /**
     * 加载多个同级根Fragment
     */
    public void loadMultipleRootFragment(int containerId, int showPosition,
                                         ISupportFragment... toFragments) {

        mTransactionDelegate.loadMultipleRootTransaction(getChildFragmentManager(),
                containerId, showPosition, toFragments);
    }

    /**
     * show一个Fragment,hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment(只有通过loadMultipleRootFragment()载入的Fragment)
     * <p>
     * 建议使用更明确的{@link #showHideFragment(ISupportFragment, ISupportFragment)}
     */
    public void showHideFragment(ISupportFragment showFragment) {
        showHideFragment(showFragment, null);
    }

    /**
     * show一个Fragment,hide一个Fragment
     */
    public void showHideFragment(ISupportFragment showFragment, ISupportFragment hideFragment) {
        mTransactionDelegate.showHideFragment(getChildFragmentManager(), showFragment, hideFragment);
    }

    public void start(ISupportFragment toFragment) {
        start(toFragment, ISupportFragment.STANDARD);
    }

    /**
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(final ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {

        mTransactionDelegate.dispatchStartTransaction(mFragment.getParentFragmentManager(), mSupportF,
                toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
    }

    /**
     * Launch an fragment for which you would like a result when it poped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {

        mTransactionDelegate.dispatchStartTransaction(mFragment.getParentFragmentManager(), mSupportF,
                toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
    }

    /**
     * Start the target Fragment and pop itself
     */
    public void startWithPop(ISupportFragment toFragment) {
        mTransactionDelegate.dispatchStartWithPopTransaction(mFragment.getParentFragmentManager(), mSupportF, toFragment);
    }

    public void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass,
                               boolean includeTargetFragment) {

        mTransactionDelegate.dispatchStartWithPopToTransaction(mFragment.getParentFragmentManager(), mSupportF,
                toFragment, targetFragmentClass.getName(), includeTargetFragment);
    }

    public void replaceFragment(ISupportFragment toFragment) {

        mTransactionDelegate.dispatchStartTransaction(mFragment.getParentFragmentManager(), mSupportF,
                toFragment, 0, ISupportFragment.STANDARD,
                TransactionDelegate.TYPE_REPLACE);
    }

    public void startChild(ISupportFragment toFragment) {
        startChild(toFragment, ISupportFragment.STANDARD);
    }

    public void startChild(final ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {

        mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), getChildTopFragment(),
                toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
    }

    public void startChildForResult(ISupportFragment toFragment, int requestCode) {

        mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), getChildTopFragment(),
                toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
    }

    public void startChildWithPop(ISupportFragment toFragment) {
        mTransactionDelegate.dispatchStartWithPopTransaction(getChildFragmentManager(), getChildTopFragment(), toFragment);
    }

    public void replaceChildFragment(ISupportFragment toFragment) {
        mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), getChildTopFragment(),
                toFragment, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_REPLACE);
    }

    public void pop() {
        mTransactionDelegate.pop(mFragment.getParentFragmentManager());
    }

    /**
     * Pop the child fragment.
     */
    public void popChild() {
        mTransactionDelegate.pop(getChildFragmentManager());
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
                afterPopTransactionRunnable, mFragment.getParentFragmentManager());
    }

    public void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        popToChild(targetFragmentClass, includeTargetFragment, null);
    }

    public void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment,
                           Runnable afterPopTransactionRunnable) {

        mTransactionDelegate.popTo(targetFragmentClass.getName(), includeTargetFragment,
                afterPopTransactionRunnable, getChildFragmentManager());
    }

    private FragmentManager getChildFragmentManager() {
        return mFragment.getChildFragmentManager();
    }

    private ISupportFragment getChildTopFragment() {
        return SupportHelper.getTopFragment(getChildFragmentManager());
    }

    public FragmentActivity getActivity() {
        return _mActivity;
    }
}