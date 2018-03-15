package com.xmd.cashier.manager;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.event.EventPrintResult;
import com.xmd.cashier.dal.event.EventPushReact;
import com.xmd.m.notify.event.EventPushReceive;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

/**
 * Created by zr on 18-3-14.
 * 友盟统计相关
 */

public class UmengManager {
    private static UmengManager mInstance;
    private Context mContext;

    private UmengManager() {

    }

    public static UmengManager getInstance() {
        if (mInstance == null) {
            mInstance = new UmengManager();
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
        EventBusSafeRegister.register(this);
    }

    @Subscribe
    public void onEvent(EventPushReceive eventPushReceive) {
        MobclickAgent.onEvent(mContext, AppConstants.UMENG_EVENT_PUSH_RECEIVE);
    }

    @Subscribe
    public void onEvent(EventPushReact eventPushReact) {
        HashMap<String, String> map = new HashMap<>();
        map.put("businessType", eventPushReact.getBusinessType());
        MobclickAgent.onEvent(mContext, AppConstants.UMENG_EVENT_PUSH_REACT, map);
    }

    @Subscribe
    public void onEvent(EventPrintResult eventPrintResult) {
        HashMap<String, String> map = new HashMap<>();
        map.put("result", eventPrintResult.getMessage());
        MobclickAgent.onEvent(mContext, AppConstants.UMENT_EVENT_PRINT_RESULT, map);
    }
}
