package com.xmd.manager;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.shidou.commonlibrary.helper.CrashHandler;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.umeng.analytics.MobclickAgent;
import com.xmd.app.XmdApp;
import com.xmd.manager.common.ActivityHelper;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sdcm on 15-10-22.
 */
public class ManagerApplication extends Application {
    private static final String TAG = "ManagerApplication";

    private static Context appContext;
    private String mAppVersionName;
    private int mAppVersionCode;
    private String mUserAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = Utils.getProcessName(this, android.os.Process.myPid());
        Logger.v("Process Name : " + processName);
        if (Utils.isNotEmpty(processName)) {
            if (processName.contains(":pushservice")) {
                // for getui push service, do nothing;

            } else {
                appContext = getApplicationContext();

                //解析APP版本和渠道信息
                parseAppVersion();

                //初始化日志
                XLogger.init(7, getFilesDir().getPath() + File.separator + "logs");
                XLogger.setGloableTag("9358");
                printMachineInfo();

                //初始化网络库
                com.shidou.commonlibrary.network.OkHttpUtil.init(getFilesDir() + File.separator + "networkCache", 10 * 1024 * 1024, 10000, 10000, 10000);
                com.shidou.commonlibrary.network.OkHttpUtil.getInstance().setLog(BuildConfig.DEBUG);
                com.shidou.commonlibrary.network.OkHttpUtil.getInstance().setCommonHeader("User-Agent", getUserAgent());

                //初始化错误拦截器
                CrashHandler.getInstance().init(getApplicationContext(), new CrashHandler.Callback() {
                    @Override
                    public void onExitApplication() {
                        ActivityHelper.getInstance().exitAndClearApplication();
                    }
                });

                XToast.init(this, -1);

                //初始化磁盘缓存模块
                try {
                    DiskCacheManager.init(new File(getFilesDir() + File.separator + "diskCache"), (1024 * 1024));
                } catch (IOException e) {
                    XLogger.e("初始化磁盘缓存系统失败：" + e.getMessage());
                }

                //打开友盟错误统计,可以和全局错误拦截器共存
                MobclickAgent.setCatchUncaughtExceptions(true);
                // 应用入口，禁止默认的页面统计方式
                MobclickAgent.openActivityDurationTrack(false);
                //设置日志加密
                MobclickAgent.enableEncrypt(true);

                ToastUtils.init(this);

                long start = System.currentTimeMillis();

                Manager.getInstance().initialize(appContext);

                //模块功能初始化
                Set<String> functions = new HashSet<>();
                functions.add(XmdApp.FUNCTION_ALIVE_REPORT);
                XmdApp.getInstance().init(this, SharedPreferenceHelper.getServerHost(), functions);

                long end = System.currentTimeMillis();
                Logger.v("Start cost : " + (end - start) + " ms");
            }
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    public String getUserAgent() {
        if (mUserAgent == null) {
            mUserAgent = "mgr-android-" + mAppVersionName + "-" + mAppVersionCode;
        }
        return mUserAgent;
    }

    private void parseAppVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            mAppVersionName = packageInfo.versionName;
            mAppVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printMachineInfo() {
        XLogger.i(TAG, "=========================================");
        XLogger.i(TAG, "Android SDK:" + Build.VERSION.RELEASE);
        XLogger.i(TAG, "MODEL:" + Build.MODEL);
        XLogger.i(TAG, "BOARD:" + Build.BOARD);
        XLogger.i(TAG, "DEVICE:" + Build.DEVICE);
        XLogger.i(TAG, "IMEI:" + DeviceInfoUtils.getDeviceId(this));
        XLogger.i(TAG, "MANUFACTURER:" + Build.MANUFACTURER);
        XLogger.i(TAG, "APP PRODUCT FLAVORS:" + BuildConfig.FLAVOR);
        XLogger.i(TAG, "APP VERSION CODE:" + mAppVersionCode);
        XLogger.i(TAG, "APP VERSION NAME:" + mAppVersionName);
        XLogger.i(TAG, "=========================================");
    }
}