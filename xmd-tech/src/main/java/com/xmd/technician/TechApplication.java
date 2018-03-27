package com.xmd.technician;

import android.app.Activity;
import android.app.ActivityManager;
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
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.XmdActivityManager;
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventRestartApplication;
import com.xmd.app.user.User;
import com.xmd.appointment.XmdModuleAppointment;
import com.xmd.chat.MenuFactory;
import com.xmd.chat.XmdChat;
import com.xmd.chat.xmdchat.constant.XmdChatConstant;
import com.xmd.m.comment.XmdComment;
import com.xmd.m.network.XmdNetwork;
import com.xmd.m.notify.XmdPushModule;
import com.xmd.m.notify.display.FloatNotifyManager;
import com.xmd.permission.BusinessPermissionManager;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.ControllerRegister;
import com.xmd.technician.umengstatistics.UmengStatisticsManager;
import com.xmd.technician.window.AvailableCouponListActivity;
import com.xmd.technician.window.WelcomeActivity;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sdcm on 16-3-10.
 */
public class TechApplication extends MultiDexApplication {
    private static final String TAG = "TechApplication";
    private static Context appContext;

    private String mAppVersionName;
    private int mAppVersionCode;
    private String mUserAgent;
    private boolean debug;
    //聊天菜单
    private MenuFactory menuFactory = new MenuFactory() {

        @Override
        public void onShowDeliverCouponView(Activity activity, User remoteUser) {
            Intent intent = new Intent(activity, AvailableCouponListActivity.class);
            intent.putExtra(AvailableCouponListActivity.EXTRA_CHAT_ID, remoteUser.getChatId());
            activity.startActivity(intent);
        }
    };

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this, android.os.Process.myPid());
        Logger.v("Process Name : " + processName);
        if (getApplicationContext().getPackageName().equals(processName)) {
            Logger.v("CurrentUser initialize !");

            appContext = getApplicationContext();
            debug = BuildConfig.DEBUG || BuildConfig.FLAVOR.equals("dev");

            WindowManager windowManager = (WindowManager) appContext.getSystemService(WINDOW_SERVICE);
            ScreenUtils.initScreenSize(windowManager);

            //SP初始化
            SharedPreferenceHelper.initialize();
            //解析APP版本和渠道信息
            parseAppVersion();

            //初始化日志
            String logFileDirPath = Environment.getExternalStorageDirectory() + "/spa-logs";
            XLogger.init(7, logFileDirPath);
            XLogger.setGloableTag("9358");
            printMachineInfo();

            //初始化磁盘缓存模块
            try {
                DiskCacheManager.init(new File(getFilesDir() + File.separator + "diskCache"), (20 * 1024 * 1024));
            } catch (IOException e) {
                XLogger.e("初始化磁盘缓存系统失败：" + e.getMessage());
            }

            //初始化网络库
            XmdNetwork.getInstance().init(this, getUserAgent(), SharedPreferenceHelper.getServerHost());
            XmdNetwork.getInstance().setDebug(true);
            XmdNetwork.getInstance().setToken(SharedPreferenceHelper.getUserToken()); //处理旧的toke数据

            //初始化错误拦截器
            CrashHandler.getInstance().init(getApplicationContext(), new CrashHandler.Callback() {
                @Override
                public void onExitApplication() {
                    XmdActivityManager.getInstance().exitApplication();
                }
            });

            //初始化界面辅助类
            EmojiManager.getInstance().init(this);
            XToast.init(this, -1);
            FloatNotifyManager.getInstance().init(this);

            //打开友盟错误统计,可以和全局错误拦截器共存
            MobclickAgent.setCatchUncaughtExceptions(true);

            // 应用入口，禁止默认的页面统计方式
            MobclickAgent.openActivityDurationTrack(true);
            //定义后台回到前台统计时间间隔
            MobclickAgent.setSessionContinueMillis(3000);
            MobclickAgent.setDebugMode(false);

            //初始化线程池
            ThreadPoolManager.init(this);
            UmengStatisticsManager.getStatisticsManagerInstance().init(appContext);
            long start = System.currentTimeMillis();

            //模块功能初始化
            Set<String> functions = new HashSet<>();
            functions.add(XmdApp.FUNCTION_ALIVE_REPORT);
            functions.add(XmdApp.FUNCTION_USER_INFO);
            XmdApp.getInstance().init(this, SharedPreferenceHelper.getServerHost(), functions);
            XmdModuleAppointment.getInstance().init(this);

            AppConfig.initialize();
            //初始化升级服务器
            initUpdateServer();

            ThreadManager.initialize();
            ControllerRegister.initialize();

            //初始化消息推送
            XmdPushModule.getInstance().init(this, "tech", UINavigation.xmdActionFactory, new PushMessageListener());

            //初始化聊天模块
            String chatAppKey = String.valueOf(BuildConfig.FLAVOR.equals("prd") ? XmdChatConstant.SDK_APP_ID_PRODUCTION : XmdChatConstant.SDK_APP_ID_DEV);
            XmdChat.getInstance().init(this, chatAppKey, debug, menuFactory);

//                DataRefreshService.start();
//                HelloReplyService.start();

            //初始化权限模块
            BusinessPermissionManager.getInstance().init();

            long end = System.currentTimeMillis();
            Logger.v("Start cost : " + (end - start) + " ms");

            EventBusSafeRegister.register(this);

            XmdComment.getInstance().init();
            LoginTechnician.getInstance().checkAndLogin();
        }
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

    @Subscribe
    public void restart(EventRestartApplication event) {
        XLogger.i("----------restart application----------");
        XmdActivityManager.getInstance().finishAll();
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
        XLogger.i(TAG, "IMEI:" + DeviceInfoUtils.getDeviceId(this));
        XLogger.i(TAG, "MANUFACTURER:" + Build.MANUFACTURER);
        XLogger.i(TAG, "APP PRODUCT FLAVORS:" + BuildConfig.FLAVOR);
        XLogger.i(TAG, "APP PRODUCT DEV MODE :" + SharedPreferenceHelper.isDevelopMode());
        XLogger.i(TAG, "APP VERSION CODE:" + mAppVersionCode);
        XLogger.i(TAG, "APP VERSION NAME:" + mAppVersionName);
        XLogger.i(TAG, "=========================================");
    }
}
