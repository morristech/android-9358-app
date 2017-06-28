package com.xmd.m.notify.push;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.m.notify.XmdPushModule;

/**
 * Created by heyangya on 17-4-24.
 * 个推消息接收服务
 */

public class GetuiReceiveService extends GTIntentService {
    private Gson gson = new Gson();

    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String cid) {
        XLogger.i(XmdPushModule.TAG, "onReceiveClientId:" + cid);
        XmdPushManager.getInstance().setClientId(cid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        byte[] payload = gtTransmitMessage.getPayload();
        if (payload != null) {
            String data = new String(payload);
            XLogger.d(XmdPushModule.TAG, "onReceiveMessageData:" + data);
            if (TextUtils.isEmpty(data)) {
                return;
            }
            try {
                XmdPushMessage message = gson.fromJson(data, XmdPushMessage.class);
                //显示
                message.show();
            } catch (Exception e) {
                XLogger.e(XmdPushModule.TAG, "parse message error:" + e.getMessage() + ",data:" + data);
                return;
            }
            //传递给外界处理
            if (XmdPushManager.getInstance().getListener() != null) {
                XmdPushManager.getInstance().getListener().onMessage(data);
            }
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {

    }
}
