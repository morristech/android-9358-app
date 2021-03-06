package com.xmd.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;
import android.view.WindowManager;

import com.shidou.commonlibrary.helper.CrashHandler;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DeviceInfoUtils;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.umeng.analytics.MobclickAgent;
import com.xmd.app.EmojiManager;
import com.xmd.app.XmdActivityManager;
import com.xmd.app.XmdApp;
import com.xmd.app.user.User;
import com.xmd.appointment.XmdModuleAppointment;
import com.xmd.chat.MenuFactory;
import com.xmd.chat.XmdChat;
import com.xmd.chat.xmdchat.constant.XmdChatConstant;
import com.xmd.inner.NativeManager;
import com.xmd.m.comment.XmdComment;
import com.xmd.m.network.XmdNetwork;
import com.xmd.m.notify.XmdPushModule;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.window.DeliveryCouponActivity;
import com.xmd.permission.BusinessPermissionManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sdcm on 15-10-22.
 */
public class ManagerApplication extends MultiDexApplication {
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
        if (getApplicationContext().getPackageName().equals(processName)) {
            appContext = getApplicationContext();

            boolean debug = BuildConfig.DEBUG || BuildConfig.FLAVOR.equals("dev");

            //解析APP版本和渠道信息
            parseAppVersion();

            //初始化日志
            String logFileDirPath = Environment.getExternalStorageDirectory() + "/spa-logs";
            XLogger.init(7, logFileDirPath);
            XLogger.setGloableTag("9358");
            XLogger.setLogcat(debug);
            printMachineInfo();

            //初始化磁盘缓存模块
            try {
                DiskCacheManager.init(new File(getFilesDir() + File.separator + "diskCache"), (20 * 1024 * 1024));
            } catch (IOException e) {
                XLogger.e("初始化磁盘缓存系统失败：" + e.getMessage());
            }

            //初始化线程池
            ThreadPoolManager.init(this);
            //初始化权限模块
            BusinessPermissionManager.getInstance().init();
            SharedPreferenceHelper.initialize();

            //初始化网络库
            XmdNetwork.getInstance().init(this, getUserAgent(), SharedPreferenceHelper.getServerHost());
            XmdNetwork.getInstance().setDebug(debug);
            XmdNetwork.getInstance().setToken(SharedPreferenceHelper.getUserToken()); //处理旧的toke数据

            //初始化错误拦截器
            CrashHandler.getInstance().init(getApplicationContext(), new CrashHandler.Callback() {
                @Override
                public void onExitApplication() {
                    XmdActivityManager.getInstance().exitApplication();
                }
            });

            //界面辅助类
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            ScreenUtils.initScreenSize(windowManager);
            EmojiManager.getInstance().init(this);
            XToast.init(this, -1);


            //打开友盟错误统计,可以和全局错误拦截器共存
            MobclickAgent.setCatchUncaughtExceptions(true);
            // 应用入口，禁止默认的页面统计方式
            MobclickAgent.openActivityDurationTrack(true);
            //设置日志加密
            MobclickAgent.enableEncrypt(true);

            ToastUtils.init(this);

            long start = System.currentTimeMillis();

            Manager.getInstance().initialize(appContext);

            //模块功能初始化
            Set<String> functions = new HashSet<>();
            functions.add(XmdApp.FUNCTION_ALIVE_REPORT);
            functions.add(XmdApp.FUNCTION_USER_INFO);
            XmdApp.getInstance().init(this, SharedPreferenceHelper.getServerHost(), functions);
            XmdModuleAppointment.getInstance().init(this);

            //初始化聊天模块
            String chatAppKey = String.valueOf(BuildConfig.FLAVOR.equals("prd") ? XmdChatConstant.SDK_APP_ID_PRODUCTION : XmdChatConstant.SDK_APP_ID_DEV);
            XmdChat.getInstance().init(this, chatAppKey, debug, menuFactory);

            XmdPushModule.getInstance().init(this, "manager", UINavigation.xmdActionFactory, null);

            long end = System.currentTimeMillis();
            Logger.v("Start cost : " + (end - start) + " ms");


            ManagerAccountManager.getInstance().init(this);

            XmdComment.getInstance().init();

            NativeManager.getInstance().init();
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

    private MenuFactory menuFactory = new MenuFactory() {

        @Override
        public void onShowDeliverCouponView(Activity activity, User remoteUser) {
            Intent intent = new Intent(activity, DeliveryCouponActivity.class);
            intent.putExtra(DeliveryCouponActivity.EXTRA_CHAT_ID, remoteUser.getChatId());
            activity.startActivity(intent);
        }
    };
}
