package com.xmd.chat.xmdchat.present;


import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.AppUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.ext.message.TIMMessageRevokedListener;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.xmd.app.XmdApp;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.ChatAccountManager;
import com.xmd.chat.ChatMessageFactory;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.ChatRowViewFactory;
import com.xmd.chat.ConversationManager;
import com.xmd.chat.NetService;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.event.EventNewUiMessage;
import com.xmd.chat.event.EventSendMessage;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CouponChatMessage;
import com.xmd.chat.message.CustomLocationMessage;
import com.xmd.chat.message.ShareChatMessage;
import com.xmd.chat.message.TipChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;
import com.xmd.chat.xmdchat.constant.XmdMessageType;
import com.xmd.chat.xmdchat.contract.XmdChatMessageManagerInterface;
import com.xmd.chat.xmdchat.messagebean.RevokeMessageBean;
import com.xmd.chat.xmdchat.messagebean.TextMessageBean;
import com.xmd.chat.xmdchat.messagebean.XmdChatMessageBaseBean;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Observable;

/**
 * Created by Lhj on 18-1-22.
 */

public class ImChatMessageManagerPresent implements XmdChatMessageManagerInterface<TIMConversation> {

    private static ImChatMessageManagerPresent managerPresent;
    private TIMConversation mConversation;
    private String currentChatId;

    private ImChatMessageManagerPresent() {

    }

    public static ImChatMessageManagerPresent getInstance() {
        if (managerPresent == null) {
            synchronized (ImChatMessageManagerPresent.class) {
                if (managerPresent == null) {
                    managerPresent = new ImChatMessageManagerPresent();
                }
            }
        }
        return managerPresent;
    }

    @Override
    public void init(final UserInfoService userInfoService) {
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(final List<TIMMessage> list) {
                for (TIMMessage message : list) {
                    String chatId = message.getSender();
                    ChatMessage<TIMMessage> chatMessage = ChatMessageFactory.createMessage(message);
                    User user = userInfoService.getUserByChatId(chatId);
                    if (user == null) {
                        userInfoService.loadUserInfoByChatIdFromServer(chatId, null);
                        user = userInfoService.getUserByChatId(chatId);
                    }
                    if (user == null) {
                        XLogger.e("无法找到用户 by chatId: " + chatId + ", so delete the message!");
                        TIMManagerExt.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, chatId);
                        continue;
                    }
                    if (currentChatId != null && currentChatId.equals(chatMessage.getFromChatId())) {
                        ConversationManager.getInstance().markAllMessagesRead(chatMessage.getFromChatId());
                        EventBus.getDefault().post(new EventNewUiMessage(ChatRowViewFactory.createViewModel(chatMessage)));
                    }

                    if (AppUtils.isBackground(XmdApp.getInstance().getContext())) {
                        ChatMessageManager.vibrateAndPlayTone();
                        ChatMessageManager.displayNotification(chatMessage, currentChatId, user);
                    }

                }
                EventBus.getDefault().post(new EventNewMessages(list));
                EventBus.getDefault().post(new EventTotalUnreadCount(getUnReadMessageTotal()));
                return false;
            }
        });

    }

    public TIMUserConfig init(TIMUserConfig config) {
        TIMUserConfigMsgExt ext = new TIMUserConfigMsgExt(config);
        ext.setMessageRevokedListener(new TIMMessageRevokedListener() {
            @Override
            public void onMessageRevoked(TIMMessageLocator timMessageLocator) {
                XLogger.i(">>>", "收到撤销消息");
            }
        });
        ext.enableAutoReport(true);
        return ext;
    }

    @Override
    public ChatMessage sendTextMessage(String remoteChatId, String text) {
        TextMessageBean bean = new TextMessageBean();
        bean.setContent(text);
        TIMMessage message = wrapMessage(bean, XmdMessageType.TEXT_TYPE, null, null);
        ChatMessage chatMessage = new ChatMessage(message);
        return sendMessage(chatMessage);
    }

    public static TIMMessage wrapMessage(Object data, String messageType, String tag, String messageId) {
        User user = ChatAccountManager.getInstance().getUser();
        if (user == null) {
            XToast.show("无法发送消息，没有用户信息");
            return null;
        }

        TIMMessage message = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        final XmdChatMessageBaseBean<Object> xmdChatMessageBaseBean = new XmdChatMessageBaseBean<>();
        xmdChatMessageBaseBean.setMsgId(messageId);
        xmdChatMessageBaseBean.setUserId(user.getUserId());
        xmdChatMessageBaseBean.setType(messageType);
        xmdChatMessageBaseBean.setTag(tag);
        xmdChatMessageBaseBean.setTime(String.valueOf(System.currentTimeMillis()));
        xmdChatMessageBaseBean.setData(data);
        Gson gson = new Gson();
        String obj = gson.toJson(xmdChatMessageBaseBean);
        elem.setData(obj.getBytes());
        message.addElement(elem);
        return message;
    }

    @Override
    public ChatMessage sendImageMessage(String remoteChatId, String filePath) {
        return sendMessage(ChatMessage.createImageMessage(remoteChatId, filePath, null));
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
        RevokeMessageBean revokeMessage = new RevokeMessageBean();
        revokeMessage.setMsgId(msgId);
        TIMMessage message = wrapMessage(revokeMessage, XmdMessageType.REVERT_MSG_TYPE, null, null);
        ChatMessage chatMessage = new ChatMessage(message);
        sendMessage(chatMessage);

    }

    @Override
    public ChatMessage sendTipMessage(TIMConversation conversation, User remoteUser, String tip) {
        return null;
    }

    @Override
    public ChatMessage sendTipMessageWithoutUpdateUI(TIMConversation conversation, User remoteUser, String tip) {
        TipChatMessage tipChatMessage = TipChatMessage.create(remoteUser.getChatId(), tip);
        tipChatMessage.setUser(ChatAccountManager.getInstance().getUser());
        TIMConversationExt conversationExt = new TIMConversationExt(conversation);
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        return tipChatMessage;

    }

    @Override
    public ChatMessage sendTipMessage(TipChatMessage tipChatMessage) {
        return null;
    }

    @Override
    public ChatMessage sendEmptyMessage(String remoteChatId, String msgId) {
        return null;
    }

    @Override
    public void sendCouponMessage(String remoteChatId, boolean isPaid, String actId, String techCode, String typeName, String couponName, String discountValue, String validPeriod) {
        CouponChatMessage<TIMMessage> chatMessage = CouponChatMessage.create(remoteChatId, isPaid, actId, techCode, typeName, couponName, discountValue, validPeriod);
        sendMessage(chatMessage);
    }


    @Override
    public ChatMessage sendVoiceMessage(User remoteUser, String path, int length) {
        return sendMessage(ChatMessage.createVoiceSendMessage(remoteUser.getChatId(), path, length));
    }

    @Override
    public ChatMessage sendMessage(final ChatMessage chatMessage, final boolean show) {
        if (chatMessage == null) {
            return null;
        }
        User user = ChatAccountManager.getInstance().getUser();
        if (user == null) {
            XToast.show("无法发送消息，没有用户信息!");
            return null;
        }
        chatMessage.setUser(user);
        final ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
        mConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, currentChatId);
        mConversation.sendMessage((TIMMessage) chatMessage.getMessage(), new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                data.error.set(true);
                data.progress.set(false);
                XToast.show("发送消息失败了code:" + i + "Msg:" + s);
                switch (i) {
                    case 0:
                        break;
                }
            }

            @Override
            public void onSuccess(TIMMessage message) {
                data.error.set(false);
                data.progress.set(false);
                if (show) {
                    EventBus.getDefault().post(new EventSendMessage(chatMessage));
                    if (!chatMessage.getMsgType().equals(ChatMessage.MSG_TYPE_ORIGIN_CMD)) {
                        EventBus.getDefault().post(new EventNewUiMessage(data));
                    }
                }
            }
        });
        if (show) {
            EventBus.getDefault().post(new EventSendMessage(chatMessage));
        }
        return chatMessage;
    }

    @Override
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        return sendMessage(chatMessage, true);
    }


    @Override
    public void resendMessage(ChatMessage chatMessage) {
        final ChatRowViewModel data = ChatRowViewFactory.createViewModel(chatMessage);
        mConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, currentChatId);
        mConversation.sendMessage((TIMMessage) chatMessage.getMessage(), new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                data.error.set(true);
                data.progress.set(false);
                XToast.show("发送消息失败了code:" + i + "Msg:" + s);
                switch (i) {
                    case 0:
                        break;
                }
            }

            @Override
            public void onSuccess(TIMMessage message) {
                data.error.set(false);
                data.progress.set(false);
                ChatMessage chatMessage = new ChatMessage(message);
                String msgType = chatMessage.getMsgType();
                //通知服务器有新的消息
                Observable<BaseBean> observable = XmdNetwork.getInstance()
                        .getService(NetService.class)
                        .notifyServerChatMessage(
                                ChatAccountManager.getInstance().getChatId(),
                                ChatAccountManager.getInstance().getUserType(),
                                chatMessage.getRemoteChatId(),
                                UserInfoServiceImpl.getInstance().getUserByChatId(chatMessage.getRemoteChatId()).getUserType(),
                                message.getMsgId(),
                                msgType, chatMessage.getContentText().toString());
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        XLogger.d("notifyServerChatMessage success");
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        XLogger.d("notifyServerChatMessage failed:" + e.getMessage());
                    }
                });
            }
        });

    }

    @Override
    public void removeMessage(ChatMessage chatMessage) {
        TIMMessageExt ext = new TIMMessageExt((TIMMessage) chatMessage.getMessage());
        ext.remove();
    }

    @Override
    public void saveMessage(ChatMessage chatMessage) {

    }

    @Override
    public void setCurrentChatId(String chatId) {
        this.currentChatId = chatId;
    }

    @Override
    public int getUnReadMessageTotal() {
        int num = 0;
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        for (TIMConversation conversation : list) {
            TIMConversationExt ext = new TIMConversationExt(conversation);
            num += ext.getUnreadMessageNum();
        }
        return num;
    }

}
