package com.xmd.chat.xmdchat.present;

import android.text.TextUtils;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.AppUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.XmdApp;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.chat.ChatAccountManager;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.ConversationManager;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.event.EventNewUiMessage;
import com.xmd.chat.event.EventSendMessage;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CouponChatMessage;
import com.xmd.chat.message.CustomLocationMessage;
import com.xmd.chat.message.RevokeChatMessage;
import com.xmd.chat.message.ShareChatMessage;
import com.xmd.chat.message.TipChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;
import com.xmd.chat.xmdchat.contract.XmdChatMessageManagerInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Lhj on 18-1-22.
 */

public class EmChatMessageManagerPresent implements XmdChatMessageManagerInterface<EMConversation> {
    private String currentChatId;
    private static final EmChatMessageManagerPresent ourInstance = new EmChatMessageManagerPresent();

    public static EmChatMessageManagerPresent getInstance() {
        return ourInstance;
    }

    private EmChatMessageManagerPresent() {

    }
    @Override
    public void init(final UserInfoService userInfoService) {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(final List<EMMessage> list) {
                ThreadPoolManager.postToUI(new Runnable() {
                    @Override
                    public void run() {
                        for (EMMessage message : list) {
                            String chatId = message.getFrom();
                            ChatMessage chatMessage = ChatMessageFactory.createMessage(message);
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

                            //对于打赏消息特殊处理，需要插入一条提示消息
                            if (ChatMessage.MSG_TYPE_REWARD.equals(chatMessage.getMsgType())) {
                                TipChatMessage tipChatMessage = TipChatMessage.create(
                                        user.getChatId(),
                                        String.format("%s的打赏已存入您的账户", user.getName()),
                                        ChatMessage.MSG_TYPE_REWARD);
                                tipChatMessage.setUser(user);
                                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(chatId);
                                conversation.appendMessage((EMMessage) tipChatMessage.getMessage());
                                EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
                            }
                            if (currentChatId != null && currentChatId.equals(chatMessage.getFromChatId())) {
                                ConversationManager.getInstance().markAllMessagesRead(currentChatId);
                                EventBus.getDefault().post(new EventNewUiMessage(ChatRowViewFactory.createViewModel(chatMessage)));
                            }
                            ChatMessageManager.displayNotification(chatMessage, currentChatId,user);
                            if (AppUtils.isBackground(XmdApp.getInstance().getContext())) {
                                ChatMessageManager.vibrateAndPlayTone();
                            }
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
    }

    @Override
    public ChatMessage sendTextMessage(String remoteChatId, String text) {

        return sendMessage(ChatMessage.createTextMessage(remoteChatId, text));
    }

    @Override
    public ChatMessage sendImageMessage(String remoteChatId, String filePath) {
        return sendMessage(ChatMessage.createImageMessage(remoteChatId, filePath,null));
    }

    @Override
    public ChatMessage sendLocationMessage(User remoteUser, Location location) {
        CustomLocationMessage message = CustomLocationMessage.create(
                remoteUser, location.latitude, location.longitude, location.street, location.staticMapUrl);
        return sendMessage(message);
    }

    @Override
    public ChatMessage sendInviteGiftMessage(String remoteChatId) {
        ShareChatMessage message = ShareChatMessage.createInvitationMessage(remoteChatId);
        return sendMessage(message);
    }

    @Override
    public void sendRevokeMessage(String remoteChatId, String msgId) {
        RevokeChatMessage chatMessage = RevokeChatMessage.create(remoteChatId, msgId);
        sendMessage(chatMessage);
    }

    @Override
    public ChatMessage sendTipMessage(EMConversation conversation, User remoteUser, String tip) {
        TipChatMessage tipChatMessage = TipChatMessage.create(remoteUser.getChatId(), tip);
        tipChatMessage.setUser(ChatAccountManager.getInstance().getUser());
        conversation.appendMessage((EMMessage) tipChatMessage.getMessage());
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        EventBus.getDefault().post(new EventNewUiMessage(ChatRowViewFactory.createViewModel(tipChatMessage)));
        return tipChatMessage;
    }

    @Override
    public ChatMessage sendTipMessageWithoutUpdateUI(EMConversation conversation, User remoteUser, String tip) {
        TipChatMessage tipChatMessage = TipChatMessage.create(remoteUser.getChatId(), tip);
        tipChatMessage.setUser(ChatAccountManager.getInstance().getUser());
        conversation.appendMessage((EMMessage) tipChatMessage.getMessage());
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        return tipChatMessage;
    }

    @Override
    public ChatMessage sendTipMessage(TipChatMessage tipChatMessage) {
        tipChatMessage.getEMConversation().appendMessage((EMMessage) tipChatMessage.getMessage());
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        EventBus.getDefault().post(new EventNewUiMessage(ChatRowViewFactory.createViewModel(tipChatMessage)));
        return tipChatMessage;
    }

    @Override
    public ChatMessage sendEmptyMessage(String remoteChatId, String msgId) {
        return null;
    }

    @Override
    public void sendCouponMessage(String remoteChatId, boolean paid, String content, String actId, String inviteCode, String typeName, String timeLimit) {
        CouponChatMessage chatMessage = CouponChatMessage.create(remoteChatId, paid, actId, content, inviteCode,null,null,null);
        sendMessage(chatMessage);
    }

    @Override
    public ChatMessage sendVoiceMessage(User remoteUser, String path, int length) {
        return sendMessage(ChatMessage.createVoiceSendMessage(remoteUser.getChatId(), path, length));
    }

    @Override
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        return sendMessage(chatMessage, true);
    }

    @Override
    public ChatMessage sendMessage(ChatMessage chatMessage, boolean show) {
        if (chatMessage == null) {
            return null;
        }
        User user = ChatAccountManager.getInstance().getUser();
        if (user == null) {
            XToast.show("无法发送消息，没有用户信息!");
            return null;
        }
        chatMessage.setUser(user);
        //在发送之前创建ChatRowViewModel,避免消息发送成功，但是没有收到成功的状态
        ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage((EMMessage) chatMessage.getMessage());
        if (show) {
            EventBus.getDefault().post(new EventSendMessage(chatMessage));
            if (!chatMessage.getMsgType().equals(ChatMessage.MSG_TYPE_ORIGIN_CMD)) {
                EventBus.getDefault().post(new EventNewUiMessage(data));
            }
        }
        return chatMessage;
    }

    @Override
    public void resendMessage(ChatMessage chatMessage) {
        EMClient.getInstance().chatManager().sendMessage((EMMessage) chatMessage.getMessage());
    }

    @Override
    public void removeMessage(ChatMessage chatMessage) {
        chatMessage.getEMConversation().removeMessage(((EMMessage) chatMessage.getMessage()).getMsgId());
    }

    @Override
    public void saveMessage(final ChatMessage chatMessage) {
        ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().chatManager().updateMessage((EMMessage) chatMessage.getMessage());
            }
        });
    }

    @Override
    public void setCurrentChatId(String chatId) {
        this.currentChatId = chatId;
    }

    @Override
    public int getUnReadMessageTotal() {
        return 0;
    }


}
