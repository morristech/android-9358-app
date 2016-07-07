package com.xmd.technician;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.xmd.technician.common.FileUtils;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sdcm on 16-3-11.
 */
public class AppConfig {


    private static final String APP_FOLDER = "sdspa";
    private static final String AVATAR_FOLDER = "avatar";
    private static final String SERVER_HOSTS = "serverhosts";

    public static final String BUGTAGS_APP_KEY = "32ae23df06dfde970b3b8affdd3abd30";
    public static final String BUGTAGS_APP_SERECT = "b1264e122723187c9925c8799a8d90a6";

    public static final String GETUI_MASTER_SECRET = "JEZC14IDw86NZIgxAn0et5";

    public static String sClientId = "";
    public static String sBindClientIdStatus = "";
    public static String sGetuiAppId = "";
    public static String sGetuiAppKey = "";
    public static String sGetuiAppSecret = "";
    public static List<String> sServerHosts;

    private static String sAppVersionName = "";
    private static int sAppVersionCode = -1;
    private static String sSDCardPath;

    private static String sShareType;
    private static String sCouponId;

    public static void initialize() {
        String pkgName = TechApplication.getAppContext().getPackageName();
        try {
            ApplicationInfo appInfo = TechApplication.getAppContext().getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                sGetuiAppId = appInfo.metaData.getString("PUSH_APPID");
                sGetuiAppSecret = appInfo.metaData.getString("PUSH_APPSECRET");
                sGetuiAppKey = (appInfo.metaData.get("PUSH_APPKEY") != null) ? appInfo.metaData.get("PUSH_APPKEY").toString() : null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sServerHosts = new ArrayList<>();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sSDCardPath = Environment.getExternalStorageDirectory().getPath();
            if (FileUtils.checkFolderExists(getAppFolder(), true)) {
                //Read the config Server Hosts from the /sdcard/sdspa/serverhosts
                readServerHostFile();
            }

        }

    }

    private static void readServerHostFile() {
        File file = new File(getAppFolder() + File.separator + SERVER_HOSTS);
        if (file.exists()) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    sServerHosts.add(line);
                }
            } catch (FileNotFoundException e) {
                Logger.e(e.getLocalizedMessage());
            } catch (IOException e) {
                Logger.e(e.getLocalizedMessage());
            }
        }
        if (sServerHosts.isEmpty()) {
            sServerHosts.add(Constant.DEFAULT_SERVER_HOST);
        }
    }

    /**
     * @return sdcard
     */
    public static String getSDCardPath() {
        return sSDCardPath != null ? sSDCardPath : "";
    }

    /**
     * @return sdcard/sdspa
     */
    public static String getAppFolder() {
        return getSDCardPath() + File.separator + APP_FOLDER;
    }

    public static void reportShareEvent(Map<String, Object> params) {
        sShareType = (String) params.get(Constant.PARAM_SHARE_TYPE);
        sCouponId = (String) params.get(Constant.PARAM_ACT_ID);
        MobclickAgent.onEvent(TechApplication.getAppContext(), (String) params.get(Constant.PARAM_SHARE_TYPE));
    }

    public static void reportCouponShareEvent() {
        if (Constant.SHARE_COUPON.equals(sShareType)) {
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
                @Override
                public void run() {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_SHARE_EVENT_COUNT, sCouponId);
                }
            });
        }
    }

    public static String getAppVersionNameAndCode() {
        return getAppVersionName() + "." + getAppVersionCode();
    }

    public static String getAppVersionName() {
        if (TextUtils.isEmpty(sAppVersionName)) {
            sAppVersionName = getVersionName();
        }
        return sAppVersionName;
    }

    public static int getAppVersionCode() {
        if (sAppVersionCode == -1) {
            sAppVersionCode = getVersionCode();
        }
        return sAppVersionCode;
    }

    /**
     * @return VersionName
     */
    private static String getVersionName() {
        PackageManager m = TechApplication.getAppContext().getPackageManager();
        String appVersion = "";
        try {
            appVersion = m.getPackageInfo(TechApplication.getAppContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Exception won't be thrown as the current package name is
            // safe to exist on the system.
            throw new AssertionError();
        }

        return appVersion;
    }

    /**
     * @return
     */
    private static int getVersionCode() {
        PackageManager m = TechApplication.getAppContext().getPackageManager();
        int versionCode = -1;
        try {
            versionCode = m.getPackageInfo(TechApplication.getAppContext().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // Exception won't be thrown as the current package name is
            // safe to exist on the system.
            throw new AssertionError();
        }

        return versionCode;
    }
}
