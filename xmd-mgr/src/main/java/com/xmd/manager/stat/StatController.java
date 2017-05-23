package com.xmd.manager.stat;

import android.os.Build;
import android.os.Message;
import android.text.format.DateUtils;

import com.xmd.manager.AppConfig;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.msgctrl.AbstractController;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.service.RequestConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdcm on 15-12-17.
 */
public class StatController extends AbstractController {

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MsgDef.MSG_DEF_STAT_APP_START:
                statAppStart();
                break;
        }

        return true;
    }

    public void statAppStart() {
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
            @Override
            public void run() {
                upload();
            }
        });
    }

    private void upload() {
        long lastDate = SharedPreferenceHelper.getLastUploadStatDate();
        if (lastDate != 0 && DateUtils.isToday(lastDate)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_STAT_CATEGORY, StatConstant.STAT_CATEGORY_APP);
        params.put(RequestConstant.KEY_APP_VERSION, AppConfig.getAppVersionNameAndCode());
        params.put(RequestConstant.KEY_APP_BRAND, Build.BRAND);
        params.put(RequestConstant.KEY_APP_MODEL, Build.MODEL);
        params.put(RequestConstant.KEY_ACTIVE_DATE, DateUtil.getCurrentDate());
        params.put(RequestConstant.KEY_APP_IMEI, AppConfig.getDeviceImei());

        SharedPreferenceHelper.setLastUploadStatDate(System.currentTimeMillis());

    }
}
