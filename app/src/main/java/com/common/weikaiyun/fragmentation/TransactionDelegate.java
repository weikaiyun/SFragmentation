package com.common.weikaiyun.fragmentation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentationMagician;
import androidx.lifecycle.Lifecycle;

import com.common.weikaiyun.R;
import com.common.weikaiyun.fragmentation.queue.Action;
import com.common.weikaiyun.fragmentation.queue.ActionQueue;
import com.common.weikaiyun.fragmentation.record.ResultRecord;
import com.common.weikaiyun.fragmentation.record.TransactionRecord;

import java.util.List;

/**
 * Controller
 */
class TransactionDelegate {
    private static final String TAG = "Fragmentation";

    static final String FRAGMENTATION_ARG_RESULT_RECORD = "fragment_arg_result_record";
    static final String FRAGMENTATION_ARG_CONTAINER = "fragmentation_arg_container";

    private static final String FRAGMENTATION_STATE_SAVE_RESULT = "fragmentation_state_save_result";

    static final int TYPE_ADD = 0;
    static final int TYPE_ADD_RESULT = 1;
    static final int TYPE_ADD_WITHOUT_HIDE = 2;
    static final int TYPE_ADD_RESULT_WITHOUT_HIDE = 3;
    static final int TYPE_REPLACE = 4;
    static final int TYPE_REPLACE_NOT_BACK = 5;

    private Handler mHandler;

    ActionQueue mActionQueue;

    TransactionDelegate(ISupportActivity support) {
        mHandler = new Handler(Looper.getMainLooper());
        mActionQueue = new ActionQueue(mHandler);
    }

    void post(final Runnable runnable) {
        mActionQueue.enqueue(new Action() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    void loadRootTransaction(final FragmentManager fm, final int containerId,
                             final ISupportFragment to, final boolean addToBackStack) {

        enqueue(fm, new Action(Action.ACTION_LOAD) {
            @Override
            public void run() {
                bindContainerId(containerId, to);

                String toFragmentTag = to.getClass().getName();
                TransactionRecord transactionRecord = to.getSupportDelegate().mTransactionRecord;
                if (transactionRecord != null) {
                    if (transactionRecord.tag != null) {
                        toFragmentTag = transactionRecord.tag;
                    }
                }

                start(fm, null, to, toFragmentTag, !addToBackStack, TYPE_REPLACE);
            }
        });
    }

    void loadMultipleRootTransaction(final FragmentManager fm, final int containerId, final int showPosition,
                                     final ISupportFragment... tos) {

        enqueue(fm, new Action(Action.ACTION_LOAD) {
            @Override
            public void run() {
                FragmentTransaction ft = fm.beginTransaction();
                for (int i = 0; i < tos.length; i++) {
                    Fragment to = (Fragment) tos[i];

                    bindContainerId(containerId, tos[i]);

                    String toName = to.getClass().getName();
                    ft.add(containerId, to, toName);
                    ft.setMaxLifecycle(to, Lifecycle.State.RESUMED);

                    if (i != showPosition) {
                        ft.hide(to);
                        ft.setMaxLifecycle(to, Lifecycle.State.STARTED);
                    }
                }

                supportCommit(fm, ft);
            }
        });
    }

    /**
     * Dispatch the start transaction.
     */
    void dispatchStartTransaction(final FragmentManager fm, final ISupportFragment from,
                                  final ISupportFragment to, final int requestCode, final int launchMode, final int type) {

        enqueue(fm, new Action(launchMode == ISupportFragment.SINGLETASK ? Action.ACTION_POP : Action.ACTION_NORMAL) {
            @Override
            public void run() {
                doDispatchStartTransaction(fm, from, to, requestCode, launchMode, type);
            }
        });
    }

    /**
     * Show showFragment then hide hideFragment
     */
    void showHideFragment(final FragmentManager fm, final ISupportFragment showFragment,
                          final ISupportFragment hideFragment) {

        enqueue(fm, new Action() {
            @Override
            public void run() {
                doShowHideFragment(fm, showFragment, hideFragment);
            }
        });
    }

    /**
     * Start the target Fragment and pop itself
     */
    void startWithPop(final FragmentManager fm, final ISupportFragment from, final ISupportFragment to) {
        enqueue(fm, new Action(Action.ACTION_POP) {
            @Override
            public void run() {
                if (FragmentationMagician.isStateSaved(fm)) return;
                ISupportFragment top = getTopFragmentForStart(from, fm);
                if (top == null)
                    throw new NullPointerException("There is no Fragment in the FragmentManager, " +
                            "maybe you need to call loadRootFragment() first!");

                int containerId = top.getSupportDelegate().mContainerId;
                bindContainerId(containerId, to);

                FragmentationMagician.executePendingTransactions(fm);
                FragmentationMagician.popBackStack(fm);
                FragmentationMagician.executePendingTransactions(fm);
            }
        });

        dispatchStartTransaction(fm, from, to, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD);
    }

    void startWithPopTo(final FragmentManager fm, final ISupportFragment from,
                        final ISupportFragment to, final String fragmentTag, final boolean includeTargetFragment) {

        enqueue(fm, new Action(Action.ACTION_POP) {
            @Override
            public void run() {
                if (FragmentationMagician.isStateSaved(fm)) return;

                int flag = 0;
                if (includeTargetFragment) {
                    flag = FragmentManager.POP_BACK_STACK_INCLUSIVE;
                }

                List<Fragment> willPopFragments = SupportHelper.getWillPopFragments(fm, fragmentTag, includeTargetFragment);

                final ISupportFragment top = getTopFragmentForStart(from, fm);
                if (top == null)
                    throw new NullPointerException("There is no Fragment in the FragmentManager, " +
                            "maybe you need to call loadRootFragment() first!");

                int containerId = top.getSupportDelegate().mContainerId;
                bindContainerId(containerId, to);

                if (willPopFragments.size() <= 0) return;

                FragmentationMagician.executePendingTransactions(fm);

                safePopTo(fragmentTag, fm, flag, willPopFragments);
            }

        });

        dispatchStartTransaction(fm, from, to, 0, ISupportFragment.STANDARD, TransactionDelegate.TYPE_ADD);
    }

    /**
     * Remove
     */
    void remove(final FragmentManager fm, final Fragment fragment, final boolean showPreFragment) {
        enqueue(fm, new Action(Action.ACTION_POP, fm) {
            @Override
            public void run() {
                FragmentTransaction ft = fm.beginTransaction()
                        .remove(fragment);
                if (showPreFragment) {
                    ISupportFragment preFragment = SupportHelper.getPreFragment(fragment);
                    if (preFragment instanceof Fragment) {
                        ft.show((Fragment) preFragment);
                        ft.setMaxLifecycle((Fragment) preFragment, Lifecycle.State.RESUMED);
                    }
                }
                supportCommit(fm, ft);
            }
        });
    }

    /**
     * Pop
     */
    void pop(final FragmentManager fm) {
        enqueue(fm, new Action(Action.ACTION_POP, fm) {
            @Override
            public void run() {
                if (FragmentationMagician.isStateSaved(fm)) return;
                FragmentationMagician.popBackStack(fm);
            }
        });
    }

    /**
     * Pop the last fragment transition from the manager's fragment pop stack.
     *
     * @param targetFragmentTag     Tag
     * @param includeTargetFragment Whether it includes targetFragment
     */
    void popTo(final String targetFragmentTag, final boolean includeTargetFragment,
               final Runnable afterPopTransactionRunnable, final FragmentManager fm) {

        enqueue(fm, new Action(Action.ACTION_POP) {
            @Override
            public void run() {
                if (FragmentationMagician.isStateSaved(fm)) return;

                doPopTo(targetFragmentTag, includeTargetFragment, fm);

                if (afterPopTransactionRunnable != null) {
                    afterPopTransactionRunnable.run();
                }
            }
        });
    }

    /**
     * Dispatch the pop-event. Priority of the top of the stack of Fragment
     */
    boolean dispatchBackPressedEvent(ISupportFragment activeFragment) {
        if (activeFragment != null) {
            boolean result = activeFragment.onBackPressedSupport();
            if (result) {
                return true;
            }

            Fragment parentFragment = ((Fragment) activeFragment).getParentFragment();
            return dispatchBackPressedEvent((ISupportFragment) parentFragment);
        }
        return false;
    }

    void handleResultRecord(Fragment from) {
        Bundle args = from.getArguments();
        if (args == null) return;
        final ResultRecord resultRecord = args.getParcelable(FRAGMENTATION_ARG_RESULT_RECORD);
        if (resultRecord == null) return;

        ISupportFragment targetFragment = (ISupportFragment) from
                .getParentFragmentManager()
                .getFragment(from.getArguments(), FRAGMENTATION_STATE_SAVE_RESULT);

        if (targetFragment == null) return;
        targetFragment.onFragmentResult(resultRecord.requestCode, resultRecord.resultCode, resultRecord.resultBundle);
    }

    private void enqueue(FragmentManager fm, Action action) {
        if (fm == null) {
            Log.w(TAG, "FragmentManager is null, skip the action!");
            return;
        }
        mActionQueue.enqueue(action);
    }

    private void doDispatchStartTransaction(FragmentManager fm, ISupportFragment from,
                                            ISupportFragment to, int requestCode, int launchMode, int type) {

        checkNotNull(to);

        if ((type == TYPE_ADD_RESULT || type == TYPE_ADD_RESULT_WITHOUT_HIDE) && from != null) {
            if (!((Fragment) from).isAdded()) {
                Log.w(TAG, ((Fragment) from).getClass().getSimpleName() + " has not been attached yet! " +
                        "startForResult() converted to start()");
            } else {
                saveRequestCode(fm, (Fragment) from, (Fragment) to, requestCode);
            }
        }

        from = getTopFragmentForStart(from, fm);

        int containerId = getArguments((Fragment) to).getInt(FRAGMENTATION_ARG_CONTAINER, 0);
        if (from == null && containerId == 0) {
            Log.e(TAG, "There is no Fragment in the FragmentManager, maybe you need to call loadRootFragment()!");
            return;
        }

        if (from != null && containerId == 0) {
            bindContainerId(from.getSupportDelegate().mContainerId, to);
        }

        // process ExtraTransaction
        String toFragmentTag = to.getClass().getName();
        boolean notAddToBackStack = false;
        TransactionRecord transactionRecord = to.getSupportDelegate().mTransactionRecord;
        if (transactionRecord != null) {
            if (transactionRecord.tag != null) {
                toFragmentTag = transactionRecord.tag;
            }
            notAddToBackStack = transactionRecord.notAddToBackStack;
        }

        if (handleLaunchMode(fm, from, to, toFragmentTag, launchMode)) return;

        start(fm, from, to, toFragmentTag, notAddToBackStack, type);
    }

    private ISupportFragment getTopFragmentForStart(ISupportFragment from, FragmentManager fm) {
        ISupportFragment top;
        if (from == null) {
            top = SupportHelper.getTopFragment(fm);
        } else {
            top = SupportHelper.getTopFragment(fm, from.getSupportDelegate().mContainerId);
        }
        return top;
    }

    private void start(FragmentManager fm, final ISupportFragment from, ISupportFragment to, String toFragmentTag,
                       boolean notAddToBackStack, int type) {
        FragmentTransaction ft = fm.beginTransaction();
        boolean addMode = (type == TYPE_ADD
                || type == TYPE_ADD_RESULT
                || type == TYPE_ADD_WITHOUT_HIDE
                || type == TYPE_ADD_RESULT_WITHOUT_HIDE);

        Fragment fromF = (Fragment) from;
        Fragment toF = (Fragment) to;
        Bundle args = getArguments(toF);

        if (from == null) {
            ft.replace(args.getInt(FRAGMENTATION_ARG_CONTAINER), toF, toFragmentTag);
            ft.setMaxLifecycle(toF, Lifecycle.State.RESUMED);
        } else {
            if (addMode) {
                TransactionRecord record = to.getSupportDelegate().mTransactionRecord;
                if (record != null) {
                    if (record.targetFragmentEnter != Integer.MIN_VALUE) {
                        ft.setCustomAnimations(record.targetFragmentEnter, record.currentFragmentPopExit,
                                record.currentFragmentPopEnter, record.targetFragmentExit);
                    }
                } else {
                    ft.setCustomAnimations(R.anim.v_fragment_enter, R.anim.v_fragment_pop_exit,
                            R.anim.v_fragment_pop_enter, R.anim.v_fragment_exit);
                }
                ft.add(from.getSupportDelegate().mContainerId, toF, toFragmentTag);
                ft.setMaxLifecycle(toF, Lifecycle.State.RESUMED);
                if (type != TYPE_ADD_WITHOUT_HIDE && type != TYPE_ADD_RESULT_WITHOUT_HIDE) {
                    ft.hide(fromF);
                    ft.setMaxLifecycle(fromF, Lifecycle.State.STARTED);
                }
            } else {
                ft.replace(from.getSupportDelegate().mContainerId, toF, toFragmentTag);
                ft.setMaxLifecycle(toF, Lifecycle.State.RESUMED);
            }
        }

        if (!notAddToBackStack && type != TYPE_REPLACE_NOT_BACK) {
            ft.addToBackStack(toFragmentTag);
        }
        supportCommit(fm, ft);
    }

    private void doShowHideFragment(FragmentManager fm, ISupportFragment showFragment, ISupportFragment hideFragment) {
        if (showFragment == hideFragment) return;

        FragmentTransaction ft = fm.beginTransaction().show((Fragment) showFragment);
        ft.setMaxLifecycle((Fragment) showFragment, Lifecycle.State.RESUMED);

        if (hideFragment == null) {
            List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(fm);
            for (Fragment fragment : fragmentList) {
                if (fragment != null && fragment != showFragment) {
                    ft.hide(fragment);
                    ft.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
                }
            }
        } else {
            ft.hide((Fragment) hideFragment);
            ft.setMaxLifecycle((Fragment) hideFragment, Lifecycle.State.STARTED);
        }
        supportCommit(fm, ft);
    }

    private void bindContainerId(int containerId, ISupportFragment to) {
        Bundle args = getArguments((Fragment) to);
        args.putInt(FRAGMENTATION_ARG_CONTAINER, containerId);
    }

    private Bundle getArguments(Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        return bundle;
    }

    private void supportCommit(FragmentManager fm, FragmentTransaction transaction) {
        transaction.commitAllowingStateLoss();
    }

    private boolean handleLaunchMode(FragmentManager fm, ISupportFragment topFragment,
                                     final ISupportFragment to, String toFragmentTag, int launchMode) {

        if (topFragment == null) return false;
        final ISupportFragment stackToFragment = SupportHelper.findBackStackFragment(to.getClass(), toFragmentTag, fm);
        if (stackToFragment == null) return false;

        if (launchMode == ISupportFragment.SINGLETOP) {
            if (to == topFragment || to.getClass().getName().equals(topFragment.getClass().getName())) {
                handleNewBundle(to, stackToFragment);
                return true;
            }
        } else if (launchMode == ISupportFragment.SINGLETASK) {
            doPopTo(toFragmentTag, false, fm);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    handleNewBundle(to, stackToFragment);
                }
            });
            return true;
        }

        return false;
    }

    private void handleNewBundle(ISupportFragment toFragment, ISupportFragment stackToFragment) {
        Bundle argsNewBundle = toFragment.getSupportDelegate().mNewBundle;

        Bundle args = getArguments((Fragment) toFragment);
        if (args.containsKey(FRAGMENTATION_ARG_CONTAINER)) {
            args.remove(FRAGMENTATION_ARG_CONTAINER);
        }

        if (argsNewBundle != null) {
            args.putAll(argsNewBundle);
        }

        stackToFragment.onNewBundle(args);
    }

    /**
     * save requestCode
     */
    private void saveRequestCode(FragmentManager fm, Fragment from, Fragment to, int requestCode) {
        Bundle bundle = getArguments(to);
        ResultRecord resultRecord = new ResultRecord();
        resultRecord.requestCode = requestCode;
        bundle.putParcelable(FRAGMENTATION_ARG_RESULT_RECORD, resultRecord);
        fm.putFragment(bundle, FRAGMENTATION_STATE_SAVE_RESULT, from);
    }

    private void doPopTo(final String targetFragmentTag, boolean includeTargetFragment, FragmentManager fm) {
        Fragment targetFragment = fm.findFragmentByTag(targetFragmentTag);
        if (targetFragment == null) {
            Log.e(TAG, "Pop failure! Can't find FragmentTag:" + targetFragmentTag + " in the FragmentManager's Stack.");
            return;
        }

        int flag = 0;
        if (includeTargetFragment) {
            flag = FragmentManager.POP_BACK_STACK_INCLUSIVE;
        }

        List<Fragment> willPopFragments = SupportHelper.getWillPopFragments(fm, targetFragmentTag, includeTargetFragment);
        if (willPopFragments.size() <= 0) return;
        safePopTo(targetFragmentTag, fm, flag, willPopFragments);
    }

    private void safePopTo(String fragmentTag, final FragmentManager fm, int flag, List<Fragment> willPopFragments) {
        FragmentationMagician.popBackStack(fm, fragmentTag, flag);
        FragmentationMagician.executePendingTransactions(fm);
    }

    private static <T> void checkNotNull(T value) {
        if (value == null) {
            throw new NullPointerException("toFragment == null");
        }
    }
}
