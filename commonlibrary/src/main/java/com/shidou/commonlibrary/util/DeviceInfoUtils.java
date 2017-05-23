package com.shidou.commonlibrary.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * 获取设备的各种编号
 */
public class DeviceInfoUtils {

    /**
     * 获取设备ID
     * Returns the unique device ID, for example, the IMEI for GSM and the MEID
     * or ESN for CDMA phones. Return null if device ID is not available.
     */
    public static String getDeviceId(Context context) {
        String deviceId;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
        } catch (Exception e) {
            deviceId = null;
        }

        if (TextUtils.isEmpty(deviceId) || deviceId.contains("*") || deviceId.equals("0")) {
            deviceId = null;
        }
        return deviceId;
    }

    /**
     * 获取SIM卡的序列号
     *
     * @param context
     * @return
     */
    public static String getSIMSerialNumber(Context context) {
        String number;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            number = telephonyManager.getSimSerialNumber();
        } catch (Exception e) {
            number = null;
        }
        return number;
    }

    /**
     * 获取IMSI
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        String number;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            number = telephonyManager.getSubscriberId();
        } catch (Exception e) {
            number = null;
        }
        return number;
    }

    public static String getDeviceSN() {
        String number;
        try {
            Class SystemProperties = Class.forName("android.os.SystemProperties");
            Method SystemPropertiesGet = SystemProperties.getMethod("get", String.class);
            number = (String) SystemPropertiesGet.invoke(null, "ro.serialno");
        } catch (Exception e) {
            number = null;
        }
        return number;
    }

    public static String getAndroidId(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }
}
