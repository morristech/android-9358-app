package com.xmd.technician;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by sdcm on 16-3-11.
 */
public class AppConfig {
    public static final String BUGTAGS_APP_KEY = "32ae23df06dfde970b3b8affdd3abd30";
    public static final String BUGTAGS_APP_SERECT = "b1264e122723187c9925c8799a8d90a6";

    public static final String GETUI_MASTER_SECRET = "7SynzmDHd99zOmlO4BIBV5";

    public static String sClientId = "";
    public static String sBindClientIdStatus = "";
    public static String sGetuiAppId = "";
    public static String sGetuiAppKey = "";
    public static String sGetuiAppSecret = "";

    private static String sAppVersionName = "";
    private static int sAppVersionCode = -1;

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
     *
     * @return VersionName
     */
    private static String getVersionName() {
        PackageManager m = TechApplication.getAppContext().getPackageManager();
        String appVersion = "";
        try {
            appVersion = m.getPackageInfo(TechApplication.getAppContext().getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            // Exception won't be thrown as the current package name is
            // safe to exist on the system.
            throw new AssertionError();
        }

        return appVersion;
    }

    /**
     *
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
