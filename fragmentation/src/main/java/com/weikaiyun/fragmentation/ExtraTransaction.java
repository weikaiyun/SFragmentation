package com.weikaiyun.fragmentation;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.weikaiyun.fragmentation.record.TransactionRecord;

public abstract class ExtraTransaction {

    /**
     * @param tag Optional tag name for the fragment, to later retrieve the
     *            fragment with {@link SupportHelper#findFragment(FragmentManager, String)}
     *            , pop(String)
     *            or FragmentManager.findFragmentByTag(String).
     */
    public abstract ExtraTransaction setTag(String tag);

    /**
     * Set specific animation resources to run for the fragments that are
     * entering and exiting in this transaction. The <code>currentFragmentPopEnter</code>
     * and <code>targetFragmentExit</code> animations will be played for targetFragmentEnter/currentFragmentPopExit
     * operations specifically when popping the back stack.
     */
    public abstract ExtraTransaction setCustomAnimations(@AnimatorRes @AnimRes int targetFragmentEnter,
                                                         @AnimatorRes @AnimRes int currentFragmentPopExit,
                                                         @AnimatorRes @AnimRes int currentFragmentPopEnter,
                                                         @AnimatorRes @AnimRes int targetFragmentExit);

    public abstract void loadRootFragment(int containerId, ISupportFragment toFragment);
    public abstract void loadChildRootFragment(int containerId, ISupportFragment toFragment);

    public abstract void start(ISupportFragment toFragment);
    public abstract void startChild(ISupportFragment toFragment);

    public abstract void startNotHideSelf(ISupportFragment toFragment);
    public abstract void startChildNotHideSelf(ISupportFragment toFragment);

    public abstract void startNotHideSelf(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode);
    public abstract void startChildNotHideSelf(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode);

    public abstract void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode);
    public abstract void startChild(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode);

    public abstract void startForResult(ISupportFragment toFragment, int requestCode);
    public abstract void startChildForResult(ISupportFragment toFragment, int requestCode);

    public abstract void startForResultNotHideSelf(ISupportFragment toFragment, int requestCode);
    public abstract void startChildForResultNotHideSelf(ISupportFragment toFragment, int requestCode);

    public abstract void startWithPop(ISupportFragment toFragment);
    public abstract void startChildWithPop(ISupportFragment toFragment);

    public abstract void startWithPopTo(ISupportFragment toFragment, String targetFragmentTag, boolean includeTargetFragment);
    public abstract void startChildWithPopTo(ISupportFragment toFragment, String targetFragmentTag, boolean includeTargetFragment);

    public abstract void replace(ISupportFragment toFragment);
    public abstract void replaceChild(ISupportFragment toFragment);

    public abstract void remove(ISupportFragment toFragment);
    public abstract void removeWithAnimation(ISupportFragment toFragment, int targetFragmentExit);
    public abstract void removeChild(ISupportFragment toFragment);
    public abstract void removeChildWithAnimation(ISupportFragment toFragment, int targetFragmentExit);

    /**
     * 使用setTag()自定义Tag时，使用下面popTo()／popToChild()出栈
     *
     * @param targetFragmentTag     通过setTag()设置的tag
     * @param includeTargetFragment 是否包含目标(Tag为targetFragmentTag)Fragment
     */
    public abstract void popTo(String targetFragmentTag, boolean includeTargetFragment);

    public abstract void popToChild(String targetFragmentTag, boolean includeTargetFragment);

    public abstract void popTo(String targetFragmentTag, boolean includeTargetFragment, Runnable afterPopTransactionRunnable);

    public abstract void popToChild(String targetFragmentTag, boolean includeTargetFragment, Runnable afterPopTransactionRunnable);


    /**
     * Impl
     */
    final static class ExtraTransactionImpl<T extends ISupportFragment>
            extends ExtraTransaction {

        private final FragmentActivity mActivity;
        private final T mSupportF;
        private final Fragment mFragment;
        private final TransactionDelegate mTransactionDelegate;
        private final boolean mFromActivity;
        private final TransactionRecord mRecord;

        ExtraTransactionImpl(FragmentActivity activity, T supportF,
                             TransactionDelegate transactionDelegate, boolean fromActivity) {

            this.mActivity = activity;
            this.mSupportF = supportF;
            this.mFragment = (Fragment) supportF;
            this.mTransactionDelegate = transactionDelegate;
            this.mFromActivity = fromActivity;
            mRecord = new TransactionRecord();
        }

        @Override
        public void start(ISupportFragment toFragment) {
            start(toFragment, ISupportFragment.STANDARD);
        }

        @Override
        public void startChild(ISupportFragment toFragment) {
            startChild(toFragment, ISupportFragment.STANDARD);
        }

        @Override
        public void replace(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), mSupportF,
                    toFragment, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_REPLACE);
        }

        @Override
        public void replaceChild(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), mSupportF,
                    toFragment, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_REPLACE);
        }

        @Override
        public ExtraTransaction setTag(String tag) {
            mRecord.tag = tag;
            return this;
        }

        @Override
        public ExtraTransaction setCustomAnimations(int targetFragmentEnter, int currentFragmentPopExit, int currentFragmentPopEnter, int targetFragmentExit) {
            mRecord.targetFragmentEnter = targetFragmentEnter;
            mRecord.currentFragmentPopExit = currentFragmentPopExit;
            mRecord.currentFragmentPopEnter = currentFragmentPopEnter;
            mRecord.targetFragmentExit = targetFragmentExit;
            return this;
        }

        @Override
        public void loadRootFragment(int containerId, ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.loadRootTransaction(getFragmentManager(), containerId, toFragment);
        }

        @Override
        public void loadChildRootFragment(int containerId, ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.loadRootTransaction(getChildFragmentManager(), containerId, toFragment);
        }

        @Override
        public void remove(ISupportFragment toFragment) {
            mTransactionDelegate.remove(getFragmentManager(), toFragment);
        }

        @Override
        public void removeWithAnimation(ISupportFragment toFragment, int targetFragmentExit) {
            mTransactionDelegate.remove(getFragmentManager(), toFragment, targetFragmentExit);
        }

        @Override
        public void removeChild(ISupportFragment toFragment) {
            mTransactionDelegate.remove(getChildFragmentManager(), toFragment);
        }

        @Override
        public void removeChildWithAnimation(ISupportFragment toFragment, int targetFragmentExit) {
            mTransactionDelegate.remove(getChildFragmentManager(), toFragment, targetFragmentExit);
        }

        @Override
        public void popTo(String targetFragmentTag, boolean includeTargetFragment) {
            popTo(targetFragmentTag, includeTargetFragment, null);
        }

        @Override
        public void popTo(String targetFragmentTag, boolean includeTargetFragment,
                          Runnable afterPopTransactionRunnable) {
            mTransactionDelegate.popTo(targetFragmentTag, includeTargetFragment,
                    afterPopTransactionRunnable, getFragmentManager());
        }

        @Override
        public void popToChild(String targetFragmentTag, boolean includeTargetFragment) {
            popToChild(targetFragmentTag, includeTargetFragment, null);
        }

        @Override
        public void popToChild(String targetFragmentTag, boolean includeTargetFragment,
                               Runnable afterPopTransactionRunnable) {
            mTransactionDelegate.popTo(targetFragmentTag, includeTargetFragment,
                    afterPopTransactionRunnable, getChildFragmentManager());
        }

        @Override
        public void startNotHideSelf(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), mSupportF,
                    toFragment, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_WITHOUT_HIDE);
        }

        @Override
        public void startChildNotHideSelf(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), mSupportF,
                    toFragment, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_WITHOUT_HIDE);
        }

        @Override
        public void startNotHideSelf(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), mSupportF,
                    toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD_WITHOUT_HIDE);
        }

        @Override
        public void startChildNotHideSelf(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), mSupportF,
                    toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD_WITHOUT_HIDE);
        }

        @Override
        public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), mSupportF,
                    toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
        }

        @Override
        public void startChild(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), mSupportF,
                    toFragment, 0, launchMode, TransactionDelegate.TYPE_ADD);
        }

        @Override
        public void startForResult(ISupportFragment toFragment, int requestCode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), mSupportF,
                    toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
        }

        @Override
        public void startChildForResult(ISupportFragment toFragment, int requestCode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), mSupportF,
                    toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT);
        }

        @Override
        public void startForResultNotHideSelf(ISupportFragment toFragment, int requestCode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getFragmentManager(), mSupportF,
                    toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT_WITHOUT_HIDE);
        }

        @Override
        public void startChildForResultNotHideSelf(ISupportFragment toFragment, int requestCode) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartTransaction(getChildFragmentManager(), mSupportF,
                    toFragment, requestCode, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD_RESULT_WITHOUT_HIDE);
        }

        @Override
        public void startWithPop(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartWithPopTransaction(getFragmentManager(), mSupportF, toFragment);
        }

        @Override
        public void startChildWithPop(ISupportFragment toFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartWithPopTransaction(getChildFragmentManager(), mSupportF, toFragment);
        }

        @Override
        public void startWithPopTo(ISupportFragment toFragment, String targetFragmentTag, boolean includeTargetFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartWithPopToTransaction(getFragmentManager(), mSupportF,
                    toFragment, targetFragmentTag, includeTargetFragment);
        }

        @Override
        public void startChildWithPopTo(ISupportFragment toFragment, String targetFragmentTag, boolean includeTargetFragment) {
            toFragment.getSupportDelegate().mTransactionRecord = mRecord;
            mTransactionDelegate.dispatchStartWithPopToTransaction(getChildFragmentManager(), mSupportF,
                    toFragment, targetFragmentTag, includeTargetFragment);
        }

        private FragmentManager getFragmentManager() {
            if (mFragment == null) {
                return mActivity.getSupportFragmentManager();
            }
            return mFragment.getParentFragmentManager();
        }

        private FragmentManager getChildFragmentManager() {
            return mFragment.getChildFragmentManager();
        }
    }
}
