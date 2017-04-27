package com.xmd.technician;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.igexin.sdk.PushManager;
import com.shidou.commonlibrary.helper.CrashHandler;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.network.OkHttpUtil;
import com.shidou.commonlibrary.widget.XToast;
import com.umeng.analytics.MobclickAgent;
import com.xmd.technician.chat.EmchatManager;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.TechNotifier;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.ThreadPoolManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.model.HelloReplyService;
import com.xmd.technician.msgctrl.ControllerRegister;
import com.xmd.technician.push.GetuiPushService;
import com.xmd.technician.push.GetuiReceiveService;
import com.xmd.technician.window.WelcomeActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by sdcm on 16-3-10.
 */
public class TechApplication extends MultiDexApplication {
    private static final String TAG = "TechApplication";
    private static Context appContext;
    private static TechNotifier mNotifier;

    private String mAppVersionName;
    private int mAppVersionCode;
    private String mUserAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this, android.os.Process.myPid());
        Logger.v("Process Name : " + processName);
        if (Utils.isNotEmpty(processName)) {
            if (processName.contains(":pushservice")) {
                Logger.v("getui process Start !");
            } else {
                Logger.v("Technician initialize !");
                appContext = getApplicationContext();

                //SP初始化
                SharedPreferenceHelper.initialize();

                //解析APP版本和渠道信息
                parseAppVersion();

                //初始化日志
                XLogger.init(7, getFilesDir().getPath() + File.separator + "logs");
                XLogger.setGloableTag("9358");
                printMachineInfo();

                //初始化网络库
                OkHttpUtil.init(getFilesDir() + File.separator + "networkCache", 10 * 1024 * 1024, 10000, 10000, 10000);
                OkHttpUtil.getInstance().setLog(BuildConfig.DEBUG);
                OkHttpUtil.getInstance().setCommonHeader("User-Agent", getUserAgent());

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

                //初始化通知中心
                NotificationCenter.init(this);

                //初始化线程池
                ThreadPoolManager.init(this);

                long start = System.currentTimeMillis();

                AppConfig.initialize();
                //初始化升级服务器
                initUpdateServer();

                ThreadManager.initialize();
                ControllerRegister.initialize();


                //初始化消息推送
                initGeiTuiPush();

                //初始化环信
                if (SharedPreferenceHelper.isDevelopMode()) {
                    EmchatManager.getInstance().init(this, Constant.EMCHAT_APP_KEY_DEBUG, BuildConfig.DEBUG);
                } else {
                    EmchatManager.getInstance().init(this, Constant.EMCHAT_APP_KEY_RELEASE, BuildConfig.DEBUG);
                }

                initNotifier();
                DataRefreshService.start();
                HelloReplyService.start();

                long end = System.currentTimeMillis();
                Logger.v("Start cost : " + (end - start) + " ms");
            }
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    private String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    void initNotifier() {
        mNotifier = new TechNotifier();
        mNotifier.init(appContext);
    }

    public static TechNotifier getNotifier() {
        return mNotifier;
    }

    public static void restart() {
        XLogger.i("----------restart application----------");
        ActivityHelper.getInstance().removeAllActivities();
        Intent intent = new Intent(appContext, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
    }

    public String getUserAgent() {
        if (mUserAgent == null) {
            mUserAgent = "tech-android-" + mAppVersionName + "-" + mAppVersionCode;
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

    private void initGeiTuiPush() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            AppConfig.sGetuiAppId = applicationInfo.metaData.getString("GETUI_APP_ID", "");
            AppConfig.sGetuiAppKey = applicationInfo.metaData.getString("GETUI_APP_KEY", "");
            AppConfig.sGetuiAppSecret = applicationInfo.metaData.getString("GETUI_APP_SECRET", "");
            AppConfig.sGetuiMasterSecret = applicationInfo.metaData.getString("GETUI_MASTER_SECRET", "");
            //注意METADATA是没有办法运行时修改的，所以需要推送的测试，还是只能编译一个版本出来
            PushManager.getInstance().initialize(this, GetuiPushService.class);
            PushManager.getInstance().registerPushIntentService(this, GetuiReceiveService.class);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("can not get meta data!");
        }
    }

    private void initUpdateServer() {
        if (SharedPreferenceHelper.isDevelopMode()) {
            AppConfig.sDefUpdateServer = "http://192.168.1.100:9883";
        } else {
            AppConfig.sDefUpdateServer = "http://service.xiaomodo.com";
        }
    }

    private void printMachineInfo() {
        XLogger.i(TAG, "=========================================");
        XLogger.i(TAG, "Android SDK:" + Build.VERSION.RELEASE);
        XLogger.i(TAG, "MODEL:" + Build.MODEL);
        XLogger.i(TAG, "BOARD:" + Build.BOARD);
        XLogger.i(TAG, "DEVICE:" + Build.DEVICE);
        XLogger.i(TAG, "MANUFACTURER:" + Build.MANUFACTURER);
        XLogger.i(TAG, "APP PRODUCT FLAVORS:" + BuildConfig.FLAVOR);
        XLogger.i(TAG, "APP PRODUCT DEV MODE :" + SharedPreferenceHelper.isDevelopMode());
        XLogger.i(TAG, "APP VERSION CODE:" + mAppVersionCode);
        XLogger.i(TAG, "APP VERSION NAME:" + mAppVersionName);
        XLogger.i(TAG, "=========================================");
    }
}
