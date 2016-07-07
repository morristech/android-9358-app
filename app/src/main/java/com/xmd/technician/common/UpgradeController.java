package com.xmd.technician.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import com.shidou.update.UpdateConstants;
import com.shidou.update.UpdaterController;
import com.xmd.technician.Constant;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AppUpdateConfigResult;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sdcm on 15-10-22.
 */
public class UpgradeController extends AbstractController {
    private UpdaterController mUpdaterController;
    private Context mContext;
    private boolean mIsForeUpdate;
    private boolean mIsBackgroundUpdate;

    public void init(Context context) {
        mContext = context;
        mUpdaterController = new UpdaterController(mContext);
        mUpdaterController.setDownloadFilePath("9358-tech", "tech-app.apk");
        mUpdaterController.registerUpdateEventReceiver(mBroadcastReceiver);
        RxBus.getInstance().toObservable(AppUpdateConfigResult.class).subscribe(
                configResult -> {
                    if (!configResult.update) {
                        if (!mIsBackgroundUpdate) {
                            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN,
                                    () -> Toast.makeText(context, "当前已是最新版本", Toast.LENGTH_LONG).show());
                        }
                    } else {
                        mIsForeUpdate = configResult.config.type == UpdateConstants.UPDATE_TYPE_FORCE;
                        configResult.config.allowMobile = !mIsBackgroundUpdate;
                        configResult.config.showDownloadNotification = !mIsForeUpdate;
                        mUpdaterController.startUpdate(configResult.config);
                    }
                });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE:
                autoCheckUpdate();
                break;
            case MsgDef.MSG_DEF_MANUALLY_CHECK_UPGRADE:
                manuallyCheckUpdate();
                break;
        }

        return true;
    }

    private void autoCheckUpdate() {
        mIsBackgroundUpdate = true;
        getConfig();
    }

    private void manuallyCheckUpdate() {
        mIsBackgroundUpdate = false;
        getConfig();
    }

    private void getConfig() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_CLUB_CODE, "unknown");
        params.put(RequestConstant.KEY_VERSION, String.valueOf(Utils.getVersionCode()));
        params.put(RequestConstant.KEY_APP_ID, String.valueOf(Constant.APP_ID));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_APP_UPDATE_CONFIG, params);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UpdaterController.ACTION_UPDATE_EVENT)) {
                int event = intent.getIntExtra(UpdaterController.EXTRA_UPDATE_EVENT, -1);
                Logger.d("event:" + event);
                if (event == UpdateConstants.EVENT_DOWNLOAD_START) {
                    if (!mIsForeUpdate) {
                        Toast.makeText(context, "开始进行更新", Toast.LENGTH_LONG).show();
                    }
                } else if (event == UpdateConstants.EVENT_UPDATE_ERROR) {
                    int code = intent.getIntExtra(UpdaterController.EXTRA_ERROR_CODE, -1);
                    String msg = intent.getStringExtra(UpdaterController.EXTRA_ERROR_MSG);
                    Logger.d("\terror:" + code + "," + msg);
                    if (!mIsForeUpdate) {
                        Toast.makeText(context, "升级失败，错误代码：" + code, Toast.LENGTH_LONG).show();
                    }
                } else if (event == UpdateConstants.EVENT_DOWNLOAD_PROGRESS) {
                    long size = intent.getLongExtra(UpdaterController.EXTRA_DOWNLOAD_SIZE, 0);
                    long totalSize = intent.getLongExtra(UpdaterController.EXTRA_DOWNLOAD_TOTAL_SIZE, 0);
                    Logger.d(size + "/" + totalSize + "===" + (100 * size) / totalSize + "%");
                } else if (event == UpdateConstants.EVENT_HIDE_UPDATE_UI) {
                    if (mIsForeUpdate) {
                        ActivityHelper.getInstance().exitAndClearApplication();
                    }
                }
            }
        }
    };


}
