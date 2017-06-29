package com.xmd.technician;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.m.notify.push.XmdPushMessageListener;
import com.xmd.technician.bean.GetuiPayload;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mo on 17-6-29.
 * 处理消息推送
 */

public class PushMessageListener implements XmdPushMessageListener {
    @Override
    public void onMessage(XmdPushMessage message) {

    }

    @Override
    public void onRawMessage(String message) {
        GetuiPayload wrapperMsg = new Gson().fromJson(message, GetuiPayload.class);
        if (TextUtils.isEmpty(wrapperMsg.businessType)) {
            return;
        }
        switch (wrapperMsg.businessType) {
            case XmdPushMessage.BUSINESS_TYPE_FAST_PAY: {
                XmdDisplay xmdDisplay = new XmdDisplay();
                xmdDisplay.setBusinessType(XmdPushMessage.BUSINESS_TYPE_FAST_PAY);
                xmdDisplay.setScene(XmdDisplay.SCENE_BG | XmdDisplay.SCENE_FG);
                xmdDisplay.setAction(XmdDisplay.ACTION_VIEW_FAST_PAY);
                xmdDisplay.setTitle("买单");
                xmdDisplay.setMessage("您有新的买单");
                EventBus.getDefault().post(xmdDisplay);
            }
            break;
        }
    }
}
