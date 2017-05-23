package com.xmd.manager.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.igexin.sdk.PushConsts;
import com.xmd.manager.AppConfig;
import com.xmd.manager.Constant;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.GetuiPayload;
import com.xmd.manager.common.GsonUtils;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;

/**
 * Created by sdcm on 15-11-30.
 * only use this receiver when need to handle transmitting messages
 */
public class GetuiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        Logger.v("Getuireceiver: onReceive() action = " + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                //第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                if (Utils.isNotEmpty(cid)) {
                    AppConfig.sClientId = cid;
                    SharedPreferenceHelper.setClientId(AppConfig.sClientId);
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID);
                }
                break;
            case PushConsts.GET_MSG_DATA:
                /*/*//**
             * {"msgType":"notice_msg","businessType":"new_order",
             *  "msgContent":"您有新的订单，预约人22，15813724945，请尽快处理。",
             *  "msgTargetId":"刘德华","msgDate":"2015-12-07 10:17:01","appType":"android","pushTemplateType":"transmission_template,notification_template",
             *  "noticeTitle":"新订单","logo":"push.png","noticeUrl":"http://www.baidu.com",
             *  "logoUrl":null,"transmissionType":2,"ring":true,"1vibrate":true,"clearable":true}
             /*/
                // 获取透传数据
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    GetuiPayload wrapperMsg = GsonUtils.toBean(data, GetuiPayload.class);

                    if (Constant.BUSINESS_CHAT_ORDER.equals(wrapperMsg.businessType)) {
                        WrapperNotificationManager.getInstance().postOrderNotification(wrapperMsg, "新订单");
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
                    } else if (Constant.BUSINESS_PROCESS_ORDER.equals(wrapperMsg.businessType)) {
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
                    }
                }
                break;
        }
    }
}
