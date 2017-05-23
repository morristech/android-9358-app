package com.xmd.manager;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.xmd.manager.beans.Entry;
import com.xmd.manager.common.FileUtils;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdcm on 15-10-23.
 */
public class AppConfig {

    private static final String APP_FOLDER = "sdspa";
    private static final String AVATAR_FOLDER = "avatar";
    private static final String DOWNLOAD_FOLDER = "download";
    private static final String SERVER_HOSTS = "serverhosts";

    //public static final String GETUI_MASTER_SECRET = "JEZC14IDw86NZIgxAn0et5";
    public static List<String> sServerHosts;
    public static String sGetuiAppId = "";
    public static String sGetuiAppKey = "";
    public static String sGetuiAppSecret = "";
    public static String sGetuiMasterSecret = "";
    public static String sPkgName = "";
    public static String sClientId = "";
    public static boolean sGetuiClientIdBound = false;
    public static String sBindClientIdStatus = "";

    public static int sScreenWidth = -1;
    public static int sScreenHeight = -1;

    private static String sAppVersionName = "";
    private static int sAppVersionCode = -1;
    private static String sDeviceImei = "";
    private static String sDeviceImsi = "";

    private static String sSDCardPath;

    public static void initialize() {
        sPkgName = ManagerApplication.getAppContext().getPackageName();

        try {
            ApplicationInfo appInfo = ManagerApplication.getAppContext().getPackageManager().getApplicationInfo(sPkgName, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                sGetuiAppId = appInfo.metaData.getString("PUSH_APPID");
                sGetuiAppSecret = appInfo.metaData.getString("PUSH_APPSECRET");
                sGetuiMasterSecret = appInfo.metaData.getString("PUSH_MASTERSECRET");
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

    public static String getDownloadFolder() {
        return getAppFolder() + File.separator + DOWNLOAD_FOLDER;
    }

    /**
     * @return sdcard/sdspa/avatar
     */
    public static String getAvatarFolder() {
        return getAppFolder() + File.separator + AVATAR_FOLDER;
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

    public static String getAppVersionNameAndCode() {
        return getAppVersionName() + "." + getAppVersionCode();
    }

    public static String getAppVersionName() {
        if (Utils.isEmpty(sAppVersionName)) {
            sAppVersionName = Utils.getVersionName();
        }
        return sAppVersionName;
    }

    public static int getAppVersionCode() {
        if (sAppVersionCode == -1) {
            sAppVersionCode = Utils.getVersionCode();
        }
        return sAppVersionCode;
    }

    public static String getDeviceImei() {
        if (Utils.isEmpty(sDeviceImei)) {
            sDeviceImei = Utils.getDeviceImei(ManagerApplication.getAppContext());
        }

        return sDeviceImei;
    }

    public static String getDeviceImsi() {
        if (Utils.isEmpty(sDeviceImsi)) {
            sDeviceImsi = Utils.getDeviceImsi(ManagerApplication.getAppContext());
        }

        return sDeviceImsi;
    }

    public static List<Entry> generateEntryList() {
        List<Entry> list = new ArrayList<>();

        list.add(new Entry("client id", sClientId));
        list.add(new Entry("bind client", sBindClientIdStatus));
        list.add(new Entry("server host", SharedPreferenceHelper.getServerHost()));
        list.add(new Entry("emchat id", SharedPreferenceHelper.getEmchatId()));
        list.add(new Entry("avatar", SharedPreferenceHelper.getUserAvatar()));
        list.add(new Entry("easemob", Utils.getStringMetaData("EASEMOB_APPKEY")));

        return list;
    }

    public static String getUserAgent() {
        return Constant.APP_BROWSER_USER_AGENT;
    }

}
