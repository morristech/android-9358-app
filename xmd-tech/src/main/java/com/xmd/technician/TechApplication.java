package com.xmd.technician;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.view.WindowManager;

import com.shidou.commonlibrary.Callback;
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
import com.xmd.app.XmdApp;
import com.xmd.app.event.EventLogin;
import com.xmd.app.event.EventLogout;
import com.xmd.app.event.EventRestartApplication;
import com.xmd.app.user.User;
import com.xmd.appointment.XmdModuleAppointment;
import com.xmd.chat.ConversationManager;
import com.xmd.chat.MenuFactory;
import com.xmd.chat.XmdChat;
import com.xmd.chat.viewmodel.ConversationViewModel;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.m.notify.XmdPushModule;
import com.xmd.m.notify.display.FloatNotifyManager;
import com.xmd.permission.BusinessPermissionManager;
import com.xmd.permission.ContactPermissionInfo;
import com.xmd.permission.ContactPermissionManager;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.common.Utils;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.ControllerRegister;
import com.xmd.technician.window.AvailableCouponListActivity;
import com.xmd.technician.window.WelcomeActivity;

import org.greenrobot.eventbus.EventBus;
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

                WindowManager windowManager = (WindowManager) appContext.getSystemService(WINDOW_SERVICE);
                ScreenUtils.initScreenSize(windowManager);

                //SP初始化
                SharedPreferenceHelper.initialize();

                //解析APP版本和渠道信息
                parseAppVersion();

                //初始化日志
                XLogger.init(7, getFilesDir().getPath() + File.separator + "logs");
                XLogger.setGloableTag("9358");
                printMachineInfo();

                //初始化网络库
                XmdNetwork.getInstance().init(this, getUserAgent(), SharedPreferenceHelper.getServerHost());
                XmdNetwork.getInstance().setDebug(BuildConfig.DEBUG);
                XmdNetwork.getInstance().setToken(SharedPreferenceHelper.getUserToken()); //处理旧的toke数据

                //初始化错误拦截器
                CrashHandler.getInstance().init(getApplicationContext(), new CrashHandler.Callback() {
                    @Override
                    public void onExitApplication() {
                        ActivityHelper.getInstance().exitAndClearApplication();
                    }
                });

                //初始化界面辅助类
                EmojiManager.getInstance().init(this);
                XToast.init(this, -1);
                FloatNotifyManager.getInstance().init(this);

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

                //初始化线程池
                ThreadPoolManager.init(this);

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
                XmdChat.getInstance().init(this, BuildConfig.DEBUG, menuFactory);
                XmdChat.getInstance().setConversationFilter(conversationFilter);

//                DataRefreshService.start();
//                HelloReplyService.start();

                //初始化权限模块
                BusinessPermissionManager.getInstance().init();

                long end = System.currentTimeMillis();
                Logger.v("Start cost : " + (end - start) + " ms");

                if (LoginTechnician.getInstance().isLogin()) {
                    EventBus.getDefault().removeStickyEvent(EventLogout.class);
                    EventBus.getDefault().postSticky(new EventLogin(LoginTechnician.getInstance().getToken(), LoginTechnician.getInstance().getUserInfo()));
                }

                EventBusSafeRegister.register(this);
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

    @Subscribe
    public void restart(EventRestartApplication event) {
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


    //聊天菜单
    private MenuFactory menuFactory = new MenuFactory() {

        @Override
        public void onShowDeliverCouponView(Activity activity, User remoteUser) {
            Intent intent = new Intent(activity, AvailableCouponListActivity.class);
            intent.putExtra(AvailableCouponListActivity.EXTRA_CHAT_ID, remoteUser.getChatId());
            activity.startActivity(intent);
        }
    };

    //聊天会话列表过滤器
    private ConversationManager.ConversationFilter conversationFilter = new ConversationManager.ConversationFilter() {
        @Override
        public void filter(ConversationViewModel data, Callback<Boolean> listener) {
            ContactPermissionManager.getInstance().getPermission(data.getUser().getId(), new NetworkSubscriber<ContactPermissionInfo>() {
                @Override
                public void onCallbackSuccess(ContactPermissionInfo result) {
                    XLogger.i("load permission for " + data.getChatId() + "," + data.getName() + " result:" + result);
                    listener.onResponse(result.echat, null);
                }

                @Override
                public void onCallbackError(Throwable e) {
                    listener.onResponse(null, e);
                }
            });
        }
    };
}
