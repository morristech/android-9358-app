package com.xmd.chat;

import android.text.TextUtils;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.beans.CreditGift;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.event.EventNewUiMessage;
import com.xmd.chat.event.EventSendMessage;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CouponChatMessage;
import com.xmd.chat.message.CustomLocationMessage;
import com.xmd.chat.message.RevokeChatMessage;
import com.xmd.chat.message.TipChatMessage;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.m.notify.push.XmdPushMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

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

    private String currentChatId; //当前正在聊天的用户chatId,收到此人消息自动设置已读
    private Map<String, CreditGift> creditGiftMap = new HashMap<>(); //积分礼物

    public void init() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(final List<EMMessage> list) {

                ThreadPoolManager.postToUI(new Runnable() {
                    @Override
                    public void run() {
                        for (EMMessage message : list) {
                            String chatId = message.getFrom();
                            ChatMessage chatMessage = ChatMessageFactory.create(message);
                            User user;
                            if (!TextUtils.isEmpty(chatMessage.getUserId())) {
                                //更新用户信息
                                user = new User(chatMessage.getUserId());
                                user.setChatId(chatId);
                                user.setName(chatMessage.getUserName());
                                user.setAvatar(chatMessage.getUserAvatar());
                                userInfoService.saveUser(user);
                            } else {
                                user = userInfoService.getUserByChatId(chatId);
                            }
                            if (user == null) {
                                XLogger.e("无法找到用户 by chatId: " + chatId + ", so delete the message!");
                                EMClient.getInstance().chatManager().getAllConversations().remove(chatId);
                                EMClient.getInstance().chatManager().deleteConversation(chatId, true);
                                continue;
                            }
                            if (currentChatId != null && currentChatId.equals(chatMessage.getFromChatId())) {
                                ConversationManager.getInstance().markAllMessagesRead(currentChatId);
                                EventBus.getDefault().post(new EventNewUiMessage(chatMessage));
                            }

                            //对于打赏消息特殊处理，需要插入一条提示消息
                            if (ChatMessage.MSG_TYPE_REWARD.equals(chatMessage.getMsgType())) {
                                TipChatMessage tipChatMessage = TipChatMessage.create(
                                        user.getChatId(),
                                        String.format("%s的打赏已存入您的账户", user.getName()),
                                        ChatMessage.MSG_TYPE_REWARD);
                                tipChatMessage.setUser(user);
                                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(chatId);
                                conversation.appendMessage(tipChatMessage.getEmMessage());
                                EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
                            }

                            displayNotification(chatMessage);
                        }

                        EventBus.getDefault().post(new EventNewMessages(list));
                        EventBus.getDefault().post(new EventTotalUnreadCount(EMClient.getInstance().chatManager().getUnreadMessageCount()));
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

        //加载积分礼物
        Observable<BaseBean<List<CreditGift>>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .listCreditGift();
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<List<CreditGift>>>() {
            @Override
            public void onCallbackSuccess(BaseBean<List<CreditGift>> result) {
                XLogger.d("load credit gift list ok!");
                creditGiftMap.clear();
                for (CreditGift gift : result.getRespData()) {
                    creditGiftMap.put(gift.id, gift);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.d("load credit list failed:" + e.getMessage());
            }
        });
    }

    //发送文本消息
    public ChatMessage sendTextMessage(String remoteChatId, String text) {
        return sendMessage(ChatMessage.createTextMessage(remoteChatId, text));
    }

    //发送图片消息
    public ChatMessage sendImageMessage(String remoteChatId, String filePath) {
        return sendMessage(ChatMessage.createImageMessage(remoteChatId, filePath));
    }

    //发送位置消息
    public ChatMessage sendLocationMessage(User remoteUser, Location location) {
        CustomLocationMessage message = CustomLocationMessage.create(
                remoteUser, location.latitude, location.longitude, location.street, location.staticMapUrl);
        return sendMessage(message);
    }

    //发送撤回命令
    public void sendRevokeMessage(String remoteChatId, String msgId) {
        RevokeChatMessage chatMessage = RevokeChatMessage.create(remoteChatId, msgId);
        sendMessage(chatMessage);
    }

    //发送tip消息
    public ChatMessage sendTipMessage(EMConversation conversation, User remoteUser, String tip) {
        TipChatMessage tipChatMessage = TipChatMessage.create(remoteUser.getChatId(), tip);
        tipChatMessage.setUser(AccountManager.getInstance().getUser());
        conversation.appendMessage(tipChatMessage.getEmMessage());
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        return tipChatMessage;
    }

    public void sendCouponMessage(String remoteChatId, String content, String actId, String inviteCode) {
        CouponChatMessage chatMessage = CouponChatMessage.create(remoteChatId, actId, content, inviteCode);
        sendMessage(chatMessage);
    }

    public ChatMessage sendVoiceMessage(User remoteUser, String path, int length) {
        return sendMessage(ChatMessage.createVoiceSendMessage(remoteUser.getChatId(), path, length));
    }

    public ChatMessage sendMessage(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        User user = AccountManager.getInstance().getUser();
        if (user == null) {
            XToast.show("无法发送消息，没有用户信息!");
            return null;
        }
        chatMessage.setUser(user);
        EMClient.getInstance().chatManager().sendMessage(chatMessage.getEmMessage());
        EventBus.getDefault().post(new EventSendMessage(chatMessage));
        if (!chatMessage.getMsgType().equals(ChatMessage.MSG_TYPE_ORIGIN_CMD)) {
            EventBus.getDefault().post(new EventNewUiMessage(chatMessage));
        }
        return chatMessage;
    }


    //将消息保存到本地
    public void saveMessage(final ChatMessage chatMessage) {
        ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().chatManager().updateMessage(chatMessage.getEmMessage());
            }
        });
    }

    public void setCurrentChatId(String currentChatId) {
        this.currentChatId = currentChatId;
    }

    public CreditGift getGift(String giftId) {
        return creditGiftMap.get(giftId);
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
