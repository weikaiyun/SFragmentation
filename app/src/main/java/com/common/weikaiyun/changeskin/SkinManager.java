package com.common.weikaiyun.changeskin;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import com.common.weikaiyun.changeskin.attr.SkinAttrSupport;
import com.common.weikaiyun.changeskin.attr.SkinView;
import com.common.weikaiyun.changeskin.callback.ISkinChangingCallback;
import com.common.weikaiyun.changeskin.utils.L;
import com.common.weikaiyun.changeskin.utils.PrefUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SkinManager {
    private Context mContext;
    private Resources mResources;
    private ResourceManager mResourceManager;
    private PrefUtils mPrefUtils;

    private boolean usePlugin;

    private String mSuffix;
    private String mCurPluginPath;
    private String mCurPluginPkg;

    private List<Activity> mActivities = new ArrayList<>();

    static volatile SkinManager INSTANCE;

    public static SkinManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SkinManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SkinManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mPrefUtils = new PrefUtils(mContext);

        String skinPluginPath = mPrefUtils.getPluginPath();
        String skinPluginPkg = mPrefUtils.getPluginPkgName();
        mSuffix = mPrefUtils.getSuffix();

        if (!validPluginParams(skinPluginPath, skinPluginPkg))
            return;

        try {
            loadPlugin(skinPluginPath, skinPluginPkg);
            mCurPluginPath = skinPluginPath;
            mCurPluginPkg = skinPluginPkg;
        } catch (Exception e) {
            mPrefUtils.clear();
            e.printStackTrace();
        }
    }

    private PackageInfo getPackageInfo(String skinPluginPath) {
        PackageManager pm = mContext.getPackageManager();
        return pm.getPackageArchiveInfo(skinPluginPath, PackageManager.GET_ACTIVITIES);
    }

    private void loadPlugin(String skinPath, String skinPkgName) throws Exception {
        AssetManager assetManager = AssetManager.class.newInstance();
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
        addAssetPath.invoke(assetManager, skinPath);

        Resources superRes = mContext.getResources();
        mResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mResourceManager = new ResourceManager(mResources, skinPkgName, null);
        usePlugin = true;
    }

    private boolean validPluginParams(String skinPath, String skinPkgName) {
        if (TextUtils.isEmpty(skinPath) || TextUtils.isEmpty(skinPkgName)) {
            return false;
        }

        File file = new File(skinPath);
        if (!file.exists())
            return false;

        PackageInfo info = getPackageInfo(skinPath);
        return info.packageName.equals(skinPkgName);
    }

    private void checkPluginParamsThrow(String skinPath, String skinPkgName) {
        if (!validPluginParams(skinPath, skinPkgName)) {
            throw new IllegalArgumentException("skinPluginPath or skinPkgName not valid ! ");
        }
    }

    public void removeAnySkin() {
        L.e("removeAnySkin");
        clearPluginInfo();
        notifyChangedListeners();
    }


    public boolean needChangeSkin() {
        return usePlugin || !TextUtils.isEmpty(mSuffix);
    }


    public ResourceManager getResourceManager() {
        if (!usePlugin) {
            mResourceManager = new ResourceManager(mContext.getResources(), mContext.getPackageName(), mSuffix);
        }
        return mResourceManager;
    }

    /**
     * 应用内换肤，传入区别资源的后缀
     */
    public void changeSkin(String suffix) {
        clearPluginInfo();
        mSuffix = suffix;
        mPrefUtils.putPluginSuffix(suffix);
        notifyChangedListeners();
    }

    private void clearPluginInfo() {
        mCurPluginPath = "";
        mCurPluginPkg = "";
        usePlugin = false;
        mSuffix = "";
        mPrefUtils.clear();
    }

    private void updatePluginInfo(String skinPluginPath, String pkgName) {
        mPrefUtils.putPluginPath(skinPluginPath);
        mPrefUtils.putPluginPkgName(pkgName);
        mPrefUtils.putPluginSuffix("");

        mCurPluginPkg = pkgName;
        mCurPluginPath = skinPluginPath;
        mSuffix = "";
    }

    /**
     * 根据suffix选择插件内某套皮肤，默认为""
     */
    public void changeSkin(final String skinPluginPath, final String skinPluginPkg, ISkinChangingCallback callback) {
        L.e("changeSkin = " + skinPluginPath + " , " + skinPluginPkg);
        if (callback == null)
            callback = ISkinChangingCallback.DEFAULT_SKIN_CHANGING_CALLBACK;
        final ISkinChangingCallback skinChangingCallback = callback;

        skinChangingCallback.onStart();

        try {
            checkPluginParamsThrow(skinPluginPath, skinPluginPkg);
        } catch (IllegalArgumentException e) {
            skinChangingCallback.onError(new RuntimeException("checkPlugin occur error"));
            return;
        }

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    loadPlugin(skinPluginPath, skinPluginPkg);
                    return 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }

            }

            @Override
            protected void onPostExecute(Integer res) {
                if (res == 0) {
                    skinChangingCallback.onError(new RuntimeException("loadPlugin occur error"));
                    return;
                }
                try {
                    updatePluginInfo(skinPluginPath, skinPluginPkg);
                    notifyChangedListeners();
                    skinChangingCallback.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    skinChangingCallback.onError(e);
                }
            }
        }.execute();
    }

    public void notifyChangedListeners() {
        for (Activity activity : mActivities) {
            apply(activity);
        }
    }


    public void apply(Activity activity) {
        List<SkinView> skinViews = SkinAttrSupport.getSkinViews(activity);
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }

    public void register(final Activity activity) {
        mActivities.add(activity);

        activity.findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                apply(activity);
            }
        });
    }

    public void unregister(Activity activity) {
        mActivities.remove(activity);
    }

    /**
     * apply for dynamic construct view
     */
    public void injectSkin(View view) {
        List<SkinView> skinViews = new ArrayList<>();
        SkinAttrSupport.addSkinViews(view, skinViews);
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }
}
