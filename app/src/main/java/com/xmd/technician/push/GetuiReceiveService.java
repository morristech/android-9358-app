package com.xmd.technician.push;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.technician.AppConfig;
import com.xmd.technician.Constant;
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
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.onlinepaynotify.model.PayNotifyInfoManager;

/**
 * Created by heyangya on 17-4-24.
 */

public class GetuiReceiveService extends GTIntentService {
    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String cid) {
        XLogger.i("onReceiveClientId:" + cid);
        if (!TextUtils.isEmpty(cid)) {
            AppConfig.sClientId = cid;
            SharedPreferenceHelper.setClientId(AppConfig.sClientId);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND,
                    () -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID));
        }
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        byte[] payload = gtTransmitMessage.getPayload();
        if (payload != null) {
            String data = new String(payload);
            GetuiPayload wrapperMsg = new Gson().fromJson(data, GetuiPayload.class);
            if (!TextUtils.isEmpty(wrapperMsg.msgTargetId) && !wrapperMsg.msgTargetId.equals(SharedPreferenceHelper.getUserId())) {
                Logger.d("Message is not pushed to the current Technician");
                return;
            }

            if (!TextUtils.isEmpty(wrapperMsg.appType) && !wrapperMsg.appType.contains("android")) {
                Logger.d("Message is not pushed to android app");
                return;
            }

            if (ChatConstant.MESSAGE_SYSTEM_NOTICE.equals(wrapperMsg.businessType)) {
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

            } else if (ChatConstant.MESSAGE_CHAT_TEXT.equals(wrapperMsg.businessType)) {
                ChatUser user = new Gson().fromJson(wrapperMsg.msgContent, ChatUser.class);
                UserUtils.saveUser(user);

                if (EMClient.getInstance().isConnected())
                    TechApplication.getNotifier().showNotification(TechNotifier.CHAT_TEXT, user.getUsername(), user.getNick());

            } else if (ChatConstant.MESSAGE_CHAT_ORDER.equals(wrapperMsg.businessType)) {
                ChatUser user = new Gson().fromJson(wrapperMsg.msgContent, ChatUser.class);
                UserUtils.saveUser(user);

                if (EMClient.getInstance().isConnected())
                    TechApplication.getNotifier().showNotification(TechNotifier.CHAT_ORDER, user.getUsername(), user.getNick());

            } else if (ChatConstant.MESSAGE_CHAT_REWARD.equals(wrapperMsg.businessType)) {
                ChatUser user = new Gson().fromJson(wrapperMsg.msgContent, ChatUser.class);
                UserUtils.saveUser(user);

                if (EMClient.getInstance().isConnected())
                    TechApplication.getNotifier().showNotification(TechNotifier.CHAT_REWARD, user.getUsername(), user.getNick());

            } else if (ChatConstant.MESSAGE_CHAT_PAID_COUPON.equals(wrapperMsg.businessType)) {
                ChatUser user = new Gson().fromJson(wrapperMsg.msgContent, ChatUser.class);
                UserUtils.saveUser(user);

                if (EMClient.getInstance().isConnected())
                    TechApplication.getNotifier().showNotification(TechNotifier.CHAT_PAID_COUPON, user.getUsername(), user.getNick());

            } else if (Constant.PUSH_MESSAGE_BUSINESS_PAY_NOTIFY.equals(wrapperMsg.businessType)) {
                //买单通知,获取最新数据
                PayNotifyInfoManager.getInstance().getRecentDataAndSendNotify(Constant.PAY_NOTIFY_MAIN_PAGE_TIME_LIMIT);
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
