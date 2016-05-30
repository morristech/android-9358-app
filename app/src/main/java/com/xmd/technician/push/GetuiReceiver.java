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
import com.xmd.technician.TechApplication;
import com.xmd.technician.bean.GetuiPayload;
import com.xmd.technician.bean.SystemNotice;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.TechNotifier;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Util;
import com.xmd.technician.common.Utils;
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
                    GetuiPayload wrapperMsg = new Gson().fromJson(data, GetuiPayload.class);
                    if(!TextUtils.isEmpty(wrapperMsg.msgTargetId) && !wrapperMsg.msgTargetId.equals(SharedPreferenceHelper.getUserId())){
                        Logger.d("Message is not pushed to the current Technician");
                        return;
                    }

                    if(!TextUtils.isEmpty(wrapperMsg.appType)&& !wrapperMsg.appType.contains("android")){
                        Logger.d("Message is not pushed to android app");
                        return;
                    }

                    if(ChatConstant.MESSAGE_SYSTEM_NOTICE.equals(wrapperMsg.businessType)){
                        SystemNotice notice = new Gson().fromJson(wrapperMsg.msgContent, SystemNotice.class);
                        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                        msg.setChatType(EMMessage.ChatType.Chat);
                        msg.setTo(SharedPreferenceHelper.getEmchatId());
                        msg.setFrom(ChatConstant.MESSAGE_SYSTEM_NOTICE);
                        msg.addBody(new EMTextMessageBody(wrapperMsg.msgContent));
                        msg.setAttribute(ChatConstant.KEY_CUSTOM_TYPE, wrapperMsg.msgType);
                        msg.setAttribute(ChatConstant.KEY_TITLE, notice.title);
                        msg.setAttribute(ChatConstant.KEY_SUMMARY, notice.content);
                        msg.setAttribute(ChatConstant.KEY_IMAGE_URL, notice.imageUrl);
                        msg.setAttribute(ChatConstant.KEY_LINK_URL, notice.linkUrl);

                        EMClient.getInstance().chatManager().saveMessage(msg);

                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CONVERSATION_LIST);
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SYSTEM_NOTICE_NOTIFY);

                    }else if(ChatConstant.MESSAGE_CHAT_TEXT.equals(wrapperMsg.businessType)){
                        ChatUser user = new Gson().fromJson(wrapperMsg.msgContent, ChatUser.class);
                        UserUtils.saveUser(user);
                        TechApplication.getNotifier().showNotification(TechNotifier.CHAT_TEXT, user.getUsername());

                    }else if(ChatConstant.MESSAGE_CHAT_ORDER.equals(wrapperMsg.businessType)){
                        ChatUser user = new Gson().fromJson(wrapperMsg.msgContent, ChatUser.class);
                        UserUtils.saveUser(user);
                        TechApplication.getNotifier().showNotification(TechNotifier.CHAT_ORDER, user.getUsername());

                    }else if(ChatConstant.MESSAGE_CHAT_REWARD.equals(wrapperMsg.businessType)){
                        ChatUser user = new Gson().fromJson(wrapperMsg.msgContent, ChatUser.class);
                        UserUtils.saveUser(user);
                        TechApplication.getNotifier().showNotification(TechNotifier.CHAT_REWARD, user.getUsername());

                    }else if(ChatConstant.MESSAGE_CHAT_PAID_COUPON.equals(wrapperMsg.businessType)){
                        ChatUser user = new Gson().fromJson(wrapperMsg.msgContent, ChatUser.class);
                        UserUtils.saveUser(user);
                        TechApplication.getNotifier().showNotification(TechNotifier.CHAT_PAID_COUPON, user.getUsername());

                    }
                }
                break;
        }

    }

}
