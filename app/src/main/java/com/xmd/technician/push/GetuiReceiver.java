package com.xmd.technician.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.igexin.sdk.PushConsts;
import com.xmd.technician.AppConfig;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
                if (!TextUtils.isEmpty(cid)) {
                    AppConfig.sClientId = cid;
                    SharedPreferenceHelper.setClientId(AppConfig.sClientId);
                    ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND,
                                () -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID));
                }
                break;
            case PushConsts.GET_MSG_DATA:
               /* *
                 * {"msgType":"notice_msg","businessType":"new_order",
                 *  "msgContent":"您有新的订单，预约人22，15813724945，请尽快处理。",
                 *  "msgTargetId":"刘德华","msgDate":"2015-12-07 10:17:01","appType":"android","pushTemplateType":"transmission_template,notification_template",
                 *  "noticeTitle":"新订单","logo":"push.png","noticeUrl":"http://www.baidu.com",
                 *  "logoUrl":null,"transmissionType":2,"ring":true,"1vibrate":true,"clearable":true}*/

                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    WrapperMsg wrapperMsg = new Gson().fromJson(data, WrapperMsg.class);

                    EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                    msg.setChatType(EMMessage.ChatType.Chat);
                    msg.setTo(SharedPreferenceHelper.getEmchatId());
                    msg.setFrom(ChatConstant.MESSAGE_SYSTEM_NOTICE);
                    msg.addBody(new EMTextMessageBody(wrapperMsg.msgContent));
                    msg.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, wrapperMsg.msgType);
                    msg.setAttribute(ChatConstant.KEY_TITLE, wrapperMsg.noticeTitle);
                    msg.setAttribute(ChatConstant.KEY_SUMMARY, wrapperMsg.msgContent);
                    msg.setAttribute(ChatConstant.KEY_IMAGE_URL, wrapperMsg.logoUrl);
                    msg.setAttribute(ChatConstant.KEY_LINK_URL, wrapperMsg.noticeUrl);

                    EMClient.getInstance().chatManager().saveMessage(msg);

                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SYSTEM_NOTICE_NOTIFY);
                }
                break;
        }

    }

    public class WrapperMsg {
        public String msgType;
        public String businessType;
        public String msgContent;
        public String msgTargetId;
        public String msgDate;
        public String appType;
        public String pushTemplateType;
        public String noticeTitle;
        public String logo;
        public String logoUrl;
        public String noticeUrl;
        public String transmissionType;
        public boolean ring;
        public boolean clearable;
        public boolean vibrate;

    }
}
