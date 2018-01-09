package com.xmd.cashier.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.utils.DateUtil;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.net.UploadRetrofit;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.service.CustomService;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-12-26.
 * 网络状态监控
 */

public class MonitorManager {
    private static final String TAG = "MonitorManager";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;

    private MonitorManager() {
    }

    private static MonitorManager mInstance = new MonitorManager();

    public static MonitorManager getInstance() {
        return mInstance;
    }

    public void setManager(WifiManager wifiManager, ConnectivityManager connectivityManager) {
        mWifiManager = wifiManager;
        mConnectivityManager = connectivityManager;
        monitorNetwork();
    }

    public String getWifiStatus() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo.getBSSID() != null) {
            String ssid = wifiInfo.getSSID();
            int speed = wifiInfo.getLinkSpeed();
            int rssi = wifiInfo.getRssi();
            return "=== [" + ssid + "]" +
                    " [信号强度:" + rssi + "]" +
                    " [" + speed + WifiInfo.LINK_SPEED_UNITS + "] ===";
        } else {
            return "=== Not WiFi  or WiFi is not available ===";
        }
    }

    public void monitorNetwork() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "=== POS网络状态变更 ===");
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "=== Network: [" + mNetworkInfo.getTypeName() + "] ===");
            if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "=== [SSID:" + wifiInfo.getSSID() + "]" + "[BSSID:" + wifiInfo.getBSSID() + "] ===");
            }
        } else {
            XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "=== Network is not available ===");
        }
    }

    public void startPollingWifiStatus(long triggerTime) {
        Context context = MainApplication.getInstance().getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CustomService.class);
        intent.setAction(CustomService.ACTION);
        intent.putExtra(AppConstants.EXTRA_CMD, CustomService.CMD_POLLING_WIFI_STATUS);
        PendingIntent pi = PendingIntent.getService(context, CustomService.CMD_POLLING_WIFI_STATUS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Utils.isAboveKitkat()) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        }
    }

    public void stopPollingWifiStatus() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "结束WiFi状态轮询");
        Context context = MainApplication.getInstance().getApplicationContext();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CustomService.class);
        intent.setAction(CustomService.ACTION);
        intent.putExtra(AppConstants.EXTRA_CMD, CustomService.CMD_POLLING_WIFI_STATUS);
        PendingIntent pi = PendingIntent.getService(context, CustomService.CMD_POLLING_WIFI_STATUS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pi);
    }

    private MultipartBody.Part prepareFilePart(String partName) {
        File sendFile = null;
        try {
            sendFile = new File(Environment.getExternalStorageDirectory() + File.separator + "cashier.log");    // /mnt/sdcard/cashier.log
            if (sendFile.exists()) {
                sendFile.delete();
            }
            sendFile.createNewFile();
            XLogger.copyLogsToFile(sendFile);
        } catch (Exception ignore) {
            XLogger.e(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "copyLogsToFile exception");
        }
        // 为file建立RequestBody实例
        RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), sendFile);
        // MultipartBody.Part借助文件名完成最终的上传
        return MultipartBody.Part.createFormData(partName, sendFile.getName(), requestFile);
    }

    // 上传日志
    public Subscription uploadLogFile(final Callback<BaseBean> callback) {
        return UploadRetrofit.getService().uploadLog(prepareFilePart("9358cashier"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        SPManager.getInstance().setLastUploadTime(DateUtil.getCurrentTime() + " (success) ");
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        SPManager.getInstance().setLastUploadTime(DateUtil.getCurrentTime() + " (" + e.getLocalizedMessage() + ") ");
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }
}
