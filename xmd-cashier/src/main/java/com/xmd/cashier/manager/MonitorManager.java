package com.xmd.cashier.manager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.utils.DateUtil;
import com.xmd.cashier.dal.net.UploadRetrofit;
import com.xmd.cashier.dal.sp.SPManager;
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

    public void monitorNetwork() {
        XLogger.i(TAG, "===== POS网络状态监控 =====");
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
            XLogger.i(TAG, "=== Network: [" + mNetworkInfo.getTypeName() + "] [" + mNetworkInfo.getType() + "]");
            if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                XLogger.i(TAG, "=== Wifi: [SSID:" + wifiInfo.getSSID() + "]" + "[BSSID:" + wifiInfo.getBSSID() + "]");
            }
        } else {
            XLogger.i(TAG, "=== Network not available");
        }
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
            XLogger.i(TAG, "copyLogsToFile exception");
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
