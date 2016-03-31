package com.xmd.technician;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsCallback;
import com.bugtags.library.BugtagsOptions;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.igexin.sdk.PushManager;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.msgctrl.ControllerRegister;

import java.util.List;

/**
 * Created by sdcm on 16-3-10.
 */
public class TechApplication extends Application{
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this, android.os.Process.myPid());
        Logger.v("Process Name : " + processName);
        if (!TextUtils.isEmpty(processName)) {
            if (processName.contains(":pushservice")) {
                // for getui push service, do nothing;
                Logger.v("getui process Start !");
            } else {
                Logger.v("manager initialize !");
                appContext = getApplicationContext();

                long start = System.currentTimeMillis();

                AppConfig.initialize();
                ThreadManager.initialize();
                ControllerRegister.initialize();
                SharedPreferenceHelper.initialize();

                PushManager.getInstance().initialize(this);

                BugtagsOptions options = new BugtagsOptions.Builder().
                        trackingCrashLog(true).//是否收集crash
                        trackingConsoleLog(true).//是否收集console log
                        trackingUserSteps(true).//是否收集用户操作步骤
                        build();
                Bugtags.start(AppConfig.BUGTAGS_APP_KEY, this, Bugtags.BTGInvocationEventBubble, options);

                EMOptions emOptions = new EMOptions();
                EMClient.getInstance().init(this, emOptions);

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
}
