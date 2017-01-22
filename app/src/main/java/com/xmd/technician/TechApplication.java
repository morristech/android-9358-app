package com.xmd.technician;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.TechNotifier;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.ThreadPoolManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.ControllerRegister;

import java.util.List;

/**
 * Created by sdcm on 16-3-10.
 */
public class TechApplication extends Application {
    private static Context appContext;
    private static TechNotifier mNotifier;
    public static Boolean isTest = false;

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
//                Thread.setDefaultUncaughtExceptionHandler(new AppUncaughtExceptionHandler(appContext));

                NotificationCenter.init(this);

                ThreadPoolManager.init(this);

                long start = System.currentTimeMillis();

                AppConfig.initialize();
                ThreadManager.initialize();
                ControllerRegister.initialize();
                SharedPreferenceHelper.initialize();

                MobclickAgent.setCatchUncaughtExceptions(true);
                // 应用入口，禁止默认的页面统计方式
                MobclickAgent.openActivityDurationTrack(false);

                PushManager.getInstance().initialize(this);
                /*
                BugtagsOptions options = new BugtagsOptions.Builder().
                        trackingCrashLog(true).//是否收集crash
                        trackingConsoleLog(true).//是否收集console log
                        trackingUserSteps(true).//是否收集用户操作步骤
                        build();
                Bugtags.start(AppConfig.BUGTAGS_APP_KEY, this, Bugtags.BTGInvocationEventNone, options);
                */
                EMOptions emOptions = new EMOptions();
                EMClient.getInstance().init(this, emOptions);
                //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
                EMClient.getInstance().setDebugMode(true);

                initNotifier();

                DataRefreshService.start();

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
}
