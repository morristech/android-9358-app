package com.xmd.chat;

import android.text.TextUtils;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.event.EventTotalUnreadMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.m.notify.push.XmdPushMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by mo on 17-6-28.
 * 消息管理
 */

public class MessageManager {
    private static final MessageManager ourInstance = new MessageManager();

    public static MessageManager getInstance() {
        return ourInstance;
    }

    private MessageManager() {
    }

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();

    public void init() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(final List<EMMessage> list) {

                ThreadPoolManager.postToUI(new Runnable() {
                    @Override
                    public void run() {
                        //从消息中解析更新用户信息
                        for (EMMessage message : list) {
                            ChatMessage chatMessage = ChatMessageFactory.get(message);
                            if (!TextUtils.isEmpty(chatMessage.getUserId())) {
                                User user = new User(chatMessage.getUserId());
                                user.setChatId(chatMessage.getEmMessage().getFrom());
                                user.setName(chatMessage.getUserName());
                                user.setAvatar(chatMessage.getUserAvatar());
                                userInfoService.saveUser(user);
                            }
                            displayNotification(chatMessage);
                        }

                        EventBus.getDefault().post(new EventNewMessages(list));
                        EventBus.getDefault().post(new EventTotalUnreadMessage(EMClient.getInstance().chatManager().getUnreadMessageCount()));


                    }
                });
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        });
    }

    public ChatMessage sendTextMessage(String remoteChatId, String text) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(text, remoteChatId);
        ChatMessage chatMessage = ChatMessageFactory.get(emMessage);
        return sendMessage(chatMessage);
    }

    public ChatMessage sendMessage(ChatMessage chatMessage) {
        User user = AccountManager.getInstance().getUser();
        if (user == null) {
            XToast.show("无法发送消息，没有用户信息!");
            return null;
        }
        chatMessage.setUser(user);
        EMClient.getInstance().chatManager().sendMessage(chatMessage.getEmMessage());
        return chatMessage;
    }

    private void displayNotification(ChatMessage chatMessage) {
        XmdDisplay display = new XmdDisplay();
        display.setBusinessType(XmdPushMessage.BUSINESS_TYPE_CHAT_MESSAGE);
        display.setScene(XmdDisplay.SCENE_BG);
        display.setStyle(XmdDisplay.STYLE_NOTIFICATION);
        display.setTitle(chatMessage.getUserName());
        display.setMessage(chatMessage.getOriginContentText());
        display.setFlags(XmdDisplay.FLAG_LIGHT | XmdDisplay.FLAG_RING | XmdDisplay.FLAG_VIBRATE);
        display.setAction(XmdDisplay.ACTION_CHAT_TO);
        display.setActionData(chatMessage.getRemoteChatId());
        EventBus.getDefault().post(display);

        XmdDisplay fgDisplay = new XmdDisplay();
        fgDisplay.setBusinessType(XmdPushMessage.BUSINESS_TYPE_CHAT_MESSAGE);
        fgDisplay.setScene(XmdDisplay.SCENE_FG);
        fgDisplay.setStyle(XmdDisplay.STYLE_NONE);
        fgDisplay.setFlags(XmdDisplay.FLAG_LIGHT | XmdDisplay.FLAG_RING);
        EventBus.getDefault().post(fgDisplay);
    }
}
