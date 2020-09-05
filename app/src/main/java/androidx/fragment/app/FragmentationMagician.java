package androidx.fragment.app;

import java.util.List;

public class FragmentationMagician {

    public static boolean isStateSaved(FragmentManager fragmentManager) {
        if (!(fragmentManager instanceof FragmentManagerImpl))
            return false;
        try {
            FragmentManagerImpl fragmentManagerImpl = (FragmentManagerImpl) fragmentManager;
            return fragmentManagerImpl.isStateSaved();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void popBackStack(final FragmentManager fragmentManager) {
        fragmentManager.popBackStack();
    }

    public static void popBackStackImmediate(final FragmentManager fragmentManager) {
        fragmentManager.popBackStackImmediate();
    }

    public static void popBackStack(final FragmentManager fragmentManager, final String name, final int flags) {
        fragmentManager.popBackStack(name, flags);
    }

    public static void popBackStackImmediate(final FragmentManager fragmentManager, final String name, final int flags) {
        fragmentManager.popBackStackImmediate(name, flags);
    }


    public static void executePendingTransactions(final FragmentManager fragmentManager) {
        fragmentManager.executePendingTransactions();
    }

    public static List<Fragment> getActiveFragments(FragmentManager fragmentManager) {
        return fragmentManager.getFragments();
    }
}