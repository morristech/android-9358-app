package com.xmd.chat;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.AppUtils;
import com.shidou.commonlibrary.widget.ScreenUtils;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.Constants;
import com.xmd.app.XmdApp;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.beans.CreditGift;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.ChatUmengStatisticsEvent;
import com.xmd.chat.event.EventGameDiceStatusChange;
import com.xmd.chat.event.EventNewMessages;
import com.xmd.chat.event.EventNewUiMessage;
import com.xmd.chat.event.EventSendMessage;
import com.xmd.chat.event.EventTotalUnreadCount;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.CouponChatMessage;
import com.xmd.chat.message.CustomLocationMessage;
import com.xmd.chat.message.DiceGameChatMessage;
import com.xmd.chat.message.RevokeChatMessage;
import com.xmd.chat.message.ShareChatMessage;
import com.xmd.chat.message.TipChatMessage;
import com.xmd.chat.viewmodel.ChatRowViewModel;
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

public class ChatMessageManager {
    private static final ChatMessageManager ourInstance = new ChatMessageManager();

    public static ChatMessageManager getInstance() {
        return ourInstance;
    }

    private ChatMessageManager() {

    }

    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    protected Ringtone ringtone = null; //手机铃声
    protected Vibrator vibrator; //手机震动
    private String currentChatId; //当前正在聊天的用户chatId,收到此人消息自动设置已读
    private Map<String, CreditGift> creditGiftMap = new HashMap<>(); //积分礼物

    public void init() {
        vibrator = (Vibrator) XmdApp.getInstance().getContext().getSystemService(Context.VIBRATOR_SERVICE);
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

                            if (ChatMessage.MSG_TYPE_DICE_GAME.equals(chatMessage.getMsgType())) {
                                if (processDiceGameMessage((DiceGameChatMessage) chatMessage)) {
                                    continue;
                                }
                            }

                            if (currentChatId != null && currentChatId.equals(chatMessage.getFromChatId())) {
                                ConversationManager.getInstance().markAllMessagesRead(currentChatId);
                                EventBus.getDefault().post(new EventNewUiMessage(ChatRowViewFactory.createViewModel(chatMessage)));
                            }

                            displayNotification(chatMessage);
                            if(AppUtils.isBackground(XmdApp.getInstance().getContext())){
                                vibrateAndPlayTone();
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
        EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_PICTURE_SEND));
        return sendMessage(ChatMessage.createImageMessage(remoteChatId, filePath));
    }

    //发送位置消息
    public ChatMessage sendLocationMessage(User remoteUser, Location location) {
        CustomLocationMessage message = CustomLocationMessage.create(
                remoteUser, location.latitude, location.longitude, location.street, location.staticMapUrl);
        return sendMessage(message);
    }
    //发送邀请有礼
    public ChatMessage sendInviteGiftMessage(String remoteChatId){
        ShareChatMessage message = ShareChatMessage.createInvitationMessage(remoteChatId);
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
        tipChatMessage.setUser(ChatAccountManager.getInstance().getUser());
        conversation.appendMessage(tipChatMessage.getEmMessage());
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        EventBus.getDefault().post(new EventNewUiMessage(ChatRowViewFactory.createViewModel(tipChatMessage)));
        return tipChatMessage;
    }

    public ChatMessage sendTipMessageWithoutUpdateUI(EMConversation conversation, User remoteUser, String tip) {
        TipChatMessage tipChatMessage = TipChatMessage.create(remoteUser.getChatId(), tip);
        tipChatMessage.setUser(ChatAccountManager.getInstance().getUser());
        conversation.appendMessage(tipChatMessage.getEmMessage());
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        return tipChatMessage;
    }

    //发送tip消息
    public ChatMessage sendTipMessage(TipChatMessage tipChatMessage) {
        tipChatMessage.getConversation().appendMessage(tipChatMessage.getEmMessage());
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        EventBus.getDefault().post(new EventNewUiMessage(ChatRowViewFactory.createViewModel(tipChatMessage)));
        return tipChatMessage;
    }

    /**
     * 发送优惠券消息
     *
     * @param remoteChatId
     * @param paid         是否为点钟券（需要支付）
     * @param content
     * @param actId
     * @param inviteCode
     */
    public void sendCouponMessage(String remoteChatId, boolean paid, String content, String actId, String inviteCode) {
        CouponChatMessage chatMessage = CouponChatMessage.create(remoteChatId, paid, actId, content, inviteCode);
        sendMessage(chatMessage);
    }

    public ChatMessage sendVoiceMessage(User remoteUser, String path, int length) {
        return sendMessage(ChatMessage.createVoiceSendMessage(remoteUser.getChatId(), path, length));
    }

    public ChatMessage sendMessage(ChatMessage chatMessage) {
        return sendMessage(chatMessage, true);
    }

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
        EMClient.getInstance().chatManager().sendMessage(chatMessage.getEmMessage());

        if (show) {
            EventBus.getDefault().post(new EventSendMessage(chatMessage));
            if (!chatMessage.getMsgType().equals(ChatMessage.MSG_TYPE_ORIGIN_CMD)) {
                EventBus.getDefault().post(new EventNewUiMessage(data));
            }
        }
        return chatMessage;
    }

    public void resendMessage(ChatMessage chatMessage) {
        EMClient.getInstance().chatManager().sendMessage(chatMessage.getEmMessage());
    }

    //删除消息
    public void removeMessage(ChatMessage chatMessage) {
        chatMessage.getConversation().removeMessage(chatMessage.getEmMessage().getMsgId());
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
        //忽略一些由个推推送的消息
        if (chatMessage.getMsgType().equals(ChatMessage.MSG_TYPE_NEW_ORDER)) {
            return;
        }
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
        fgDisplay.setFlags(XmdDisplay.FLAG_LIGHT | XmdDisplay.FLAG_RING);
        if (!chatMessage.getRemoteChatId().equals(currentChatId) && chatMessage.isCustomerService()) {
            fgDisplay.setX(ScreenUtils.dpToPx(0));
            fgDisplay.setY(ScreenUtils.getScreenHeight() / 10);
            fgDisplay.setStyle(XmdDisplay.STYLE_FLOAT_TOAST);
            fgDisplay.setMessage("您收到一条客服消息，点击可查看");
            fgDisplay.setDuration(3000);
            fgDisplay.setAction(XmdDisplay.ACTION_CHAT_TO);
            fgDisplay.setActionData(chatMessage.getRemoteChatId());
        } else {
            fgDisplay.setStyle(XmdDisplay.STYLE_NONE);
        }
        EventBus.getDefault().post(fgDisplay);
    }

    private boolean processDiceGameMessage(DiceGameChatMessage message) {
        EMConversation conversation = ConversationManager.getInstance().getConversation(message.getFromChatId());
        //更新邀请消息
        if (DiceGameChatMessage.STATUS_REJECT.equals(message.getGameStatus())
                || DiceGameChatMessage.STATUS_ACCEPT.equals(message.getGameStatus())
                || DiceGameChatMessage.STATUS_CANCEL.equals(message.getGameStatus())) {
            DiceGameChatMessage inviteMessage = null;
            for (EMMessage emMessage : conversation.getAllMessages()) {
                ChatMessage chatMessage = ChatMessageFactory.create(emMessage);
                if (ChatMessage.MSG_TYPE_DICE_GAME.equals(chatMessage.getMsgType())
                        && DiceGameChatMessage.STATUS_REQUEST.equals(((DiceGameChatMessage) chatMessage).getGameStatus())
                        && ((DiceGameChatMessage) chatMessage).getGameId().equals(message.getGameId())) {
                    inviteMessage = (DiceGameChatMessage) chatMessage;
                    break;
                }
            }
            if (inviteMessage != null) {
                inviteMessage.setInnerProcessed(DiceGameChatMessage.getStatusText(message.getGameStatus()));
            }
            EventBus.getDefault().post(new EventGameDiceStatusChange(message));
            if (DiceGameChatMessage.STATUS_ACCEPT.equals(message.getGameStatus())
                    || DiceGameChatMessage.STATUS_CANCEL.equals(message.getGameStatus())) {
                //接受 or 取消
                removeMessage(message);
                return true;
            }
        }
        return false;
    }

    private void vibrateAndPlayTone() {
        long[] pattern = new long[] { 0, 180, 80, 120 };
        vibrator.vibrate(pattern, -1);

        if (ringtone == null) {
            Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(XmdApp.getInstance().getContext(), notificationUri);
            if (ringtone == null) {
                return;
            }
        }

        if (!ringtone.isPlaying()) {
            String vendor = Build.MANUFACTURER;
            ringtone.play();
            if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                Thread ctlThread = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            if (ringtone.isPlaying()) {
                                ringtone.stop();
                            }
                        } catch (Exception e) {
                        }
                    }
                };
                ctlThread.run();
            }
        }
    }
}
