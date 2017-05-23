package com.xmd.manager;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by sdcm on 16-1-22.
 */
public class SettingFlags {

    private static final String INIT_FLAG = "EEDA4BB1C1D173E2B9AB8F550ED1F034"; //INIT_FLAG

    public static final String ORDER_NOTIFIATION_ON = "0FF6209F3C25CD1289E9D8E294F240FD"; //ORDER_NOTIFIATION_ON

    private static final String SETTING_FLAG_PREFERENCE = "CDB14B159DD5D04B0BD62F0FDD17910F";

    private static SharedPreferences mSettingFlagPreference;

    public static void initialize() {
        mSettingFlagPreference = ManagerApplication.getAppContext().getSharedPreferences(SETTING_FLAG_PREFERENCE, Activity.MODE_PRIVATE);
        initValues();
    }

    private static void initValues() {
        if (!getBoolean(INIT_FLAG)) {
            setBoolean(INIT_FLAG, true);
            setBoolean(ORDER_NOTIFIATION_ON, true);
        }
    }

    public static void setString(String key, String value) {
        mSettingFlagPreference.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return mSettingFlagPreference.getString(key, "");
    }

    public static void setBoolean(String key, boolean value) {
        mSettingFlagPreference.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key) {
        return mSettingFlagPreference.getBoolean(key, false);
    }
}
