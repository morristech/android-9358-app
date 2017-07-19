package com.xmd.technician;

import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.m.notify.push.XmdPushMessageListener;
import com.xmd.technician.model.LoginTechnician;

/**
 * Created by mo on 17-6-29.
 * 处理消息推送
 */

public class PushMessageListener implements XmdPushMessageListener {
    @Override
    public void onMessage(XmdPushMessage message) {
        switch (message.getBusinessType()) {
            case XmdPushMessage.BUSINESS_TYPE_JOIN_CLUB:
                LoginTechnician.getInstance().loadTechInfo();
                break;
        }
    }

    @Override
    public void onRawMessage(String message) {

    }
}
