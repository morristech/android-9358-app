package com.xmd.technician.notify;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.util.AppUtils;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.app.FloatNotifyManager;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.message.ChatMessage;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.event.EventReceiveMessage;
import com.xmd.technician.chat.utils.EaseCommonUtils;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.window.TechChatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by heyangya on 17-1-22.
 */
public class NotificationCenter {
    private static Context sContext;
    private static NotificationCenter ourInstance = new NotificationCenter();

    public static final int TYPE_CHAT_MESSAGE = 0x7604; //聊天
    public static final int TYPE_ORDER = 0x7605; //订单

    public static NotificationCenter getInstance() {
        return ourInstance;
    }

    private NotificationCenter() {
    }

    public void init(Context context) {
        sContext = context;
        //聊天消息
        RxBus.getInstance().toObservable(EventReceiveMessage.class).subscribe(this::onEventChatMessage);
    }

    private void notify(String businessType, String title, String message, String chatId) {
        XmdDisplay display = new XmdDisplay();
        display.setBusinessType(XmdPushMessage.BUSINESS_TYPE_CHAT_MESSAGE);
        display.setScene(XmdDisplay.SCENE_BG);
        display.setStyle(XmdDisplay.STYLE_NOTIFICATION);
        display.setTitle(title);
        display.setMessage(message);
        display.setFlags(XmdDisplay.FLAG_LIGHT | XmdDisplay.FLAG_RING | XmdDisplay.FLAG_VIBRATE);
        display.setAction(XmdDisplay.ACTION_CHAT_TO);
        display.setActionData(chatId);
        EventBus.getDefault().post(display);
    }

    private void notifyForeground() {
        XmdDisplay display = new XmdDisplay();
        display.setBusinessType(XmdPushMessage.BUSINESS_TYPE_CHAT_MESSAGE);
        display.setScene(XmdDisplay.SCENE_FG);
        display.setStyle(XmdDisplay.STYLE_NONE);
        display.setFlags(XmdDisplay.FLAG_RING);
        EventBus.getDefault().post(display);
    }

    /*****************************处理各种消息**************************************/

    //聊天消息
    private void onEventChatMessage(EventReceiveMessage eventReceiveMessage) {
        List<EMMessage> list = eventReceiveMessage.getList();
        if (list != null && list.size() > 0) {
            for (EMMessage message : list) {
                ChatMessage chatMessage = ChatMessageFactory.create(message);
                String userName = chatMessage.getUserName();
                Bundle bundle = new Bundle();
                bundle.putString(ChatConstant.TO_CHAT_USER_ID, message.getFrom());
                if (AppUtils.isBackground(sContext)) {
                    notify(XmdPushMessage.BUSINESS_TYPE_CHAT_MESSAGE, null, userName + ":" + EaseCommonUtils.getMessageDigest(message, sContext), message.getFrom());
                } else {
                    if (chatMessage.isCustomerService() && !chatMessage.getFromChatId().equals(TechChatActivity.getRemoteChatId())) {
                        FloatNotifyManager.getInstance()
                                .setIcon(R.drawable.ic_service_white)
                                .setClickableMessage("客服消息：您收到一条客服消息，", null, "点击查看", chatMessage.getFromChatId(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        UINavigation.gotoChatActivity(sContext, (String) v.getTag());
                                        FloatNotifyManager.getInstance().hide();
                                    }
                                })
                                .show(0, ScreenUtils.getScreenHeight() / 8, 3000);
                    }
                    notifyForeground();
                }
            }
        }
    }
}
