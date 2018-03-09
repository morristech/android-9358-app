package com.xmd.chat;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import com.shidou.commonlibrary.widget.ScreenUtils;
import com.xmd.app.Constants;
import com.xmd.app.XmdApp;
import com.xmd.app.user.User;
import com.xmd.app.user.UserInfoService;
import com.xmd.app.user.UserInfoServiceImpl;
import com.xmd.chat.beans.CreditGift;
import com.xmd.chat.beans.Location;
import com.xmd.chat.event.ChatUmengStatisticsEvent;
import com.xmd.chat.event.EventNewUiMessage;
import com.xmd.chat.event.EventSendMessage;
import com.xmd.chat.message.ChatMessage;
import com.xmd.chat.message.TipChatMessage;
import com.xmd.chat.xmdchat.contract.XmdChatMessageManagerInterface;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.EmChatMessageManagerPresent;
import com.xmd.chat.xmdchat.present.ImChatMessageManagerPresent;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.m.notify.push.XmdPushMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

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
    private String currentChatId; //当前正在聊天的用户chatId,收到此人消息自动设置已读
    private Map<String, CreditGift> creditGiftMap = new HashMap<>(); //积分礼物
    private XmdChatMessageManagerInterface managerInterface;

    public void init() {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            managerInterface = EmChatMessageManagerPresent.getInstance();
        } else {
            if(managerInterface == null){
                managerInterface = ImChatMessageManagerPresent.getInstance();
                managerInterface.init(userInfoService);
                managerInterface.setCurrentChatId(currentChatId);
            }

        }


        //加载积分礼物
//        Observable<BaseBean<List<CreditGift>>> observable = XmdNetwork.getInstance()
//                .getService(NetService.class)
//                .listCreditGift();
//
//        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<List<CreditGift>>>() {
//            @Override
//            public void onCallbackSuccess(BaseBean<List<CreditGift>> result) {
//                XLogger.d("load credit gift list ok!");
//                creditGiftMap.clear();
//                for (CreditGift gift : result.getRespData()) {
//                    creditGiftMap.put(gift.id, gift);
//                }
//            }
//
//            @Override
//            public void onCallbackError(Throwable e) {
//                XLogger.d("load credit list failed:" + e.getMessage());
//            }
//        });
    }

    //发送文本消息
    public ChatMessage sendTextMessage(String remoteChatId, String text) {
        return managerInterface.sendTextMessage(remoteChatId, text);
    }

    //发送图片消息
    public ChatMessage sendImageMessage(String remoteChatId, String filePath) {
        EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_PICTURE_SEND));
        return managerInterface.sendImageMessage(remoteChatId, filePath);
    }

    //发送位置消息
    public ChatMessage sendLocationMessage(User remoteUser, Location location) {
        return managerInterface.sendLocationMessage(remoteUser, location);
    }

    //发送邀请有礼
    public ChatMessage sendInviteGiftMessage(String remoteChatId) {
        return managerInterface.sendInviteGiftMessage(remoteChatId);
    }

    //发送撤回命令
    public void sendRevokeMessage(String remoteChatId, String msgId) {
        managerInterface.sendRevokeMessage(remoteChatId, msgId);
    }

    //发送tip消息
    public ChatMessage sendTipMessage(Object conversation, User remoteUser, String tip) {
        return managerInterface.sendTipMessage(conversation, remoteUser, tip);
    }

    public ChatMessage sendTipMessageWithoutUpdateUI(Object conversation, User remoteUser, String tip) {
        return managerInterface.sendTipMessageWithoutUpdateUI(conversation, remoteUser, tip);
    }

    //发送tip消息
    public ChatMessage sendTipMessage(TipChatMessage tipChatMessage) {
        EventBus.getDefault().post(new EventSendMessage(tipChatMessage));
        EventBus.getDefault().post(new EventNewUiMessage(ChatRowViewFactory.createViewModel(tipChatMessage)));
        return managerInterface.sendTipMessage(tipChatMessage);
    }

    /**
     * 发送优惠券消息
     *
     * @param remoteChatId
     * @param paid         是否为点钟券（需要支付）
     * @param content
     * @param actId
     * @param inviteCode
     * @param typeName     优惠券类型
     * @param timeLimit    时间限制
     */
    public void sendCouponMessage(String remoteChatId, boolean paid, String content, String actId, String inviteCode, String typeName, String timeLimit) {
        managerInterface.sendCouponMessage(remoteChatId, paid, content, actId, inviteCode, typeName, timeLimit);
    }

    public ChatMessage sendVoiceMessage(User remoteUser, String path, int length) {
        return managerInterface.sendVoiceMessage(remoteUser, path, length);
    }

    public ChatMessage sendMessage(ChatMessage chatMessage) {
        return sendMessage(chatMessage, true);
    }

    public ChatMessage sendMessage(ChatMessage chatMessage, boolean show) {
        return managerInterface.sendMessage(chatMessage, show);
    }

    public void resendMessage(ChatMessage chatMessage) {
        managerInterface.resendMessage(chatMessage);
    }

    //删除消息
    public void removeMessage(ChatMessage chatMessage) {
        managerInterface.removeMessage(chatMessage);
    }


    //将消息保存到本地
    public void saveMessage(final ChatMessage chatMessage) {
        managerInterface.saveMessage(chatMessage);
    }

    public void setCurrentChatId(String currentChatId) {
        this.currentChatId = currentChatId;
        managerInterface.setCurrentChatId(currentChatId);
    }

    public CreditGift getGift(String giftId) {
        return creditGiftMap.get(giftId);
    }

    public static void displayNotification(ChatMessage chatMessage, String currentChatId,User user) {
        //忽略一些由个推推送的消息
        if (chatMessage.getMsgType().equals(ChatMessage.MSG_TYPE_NEW_ORDER)) {
            return;
        }
        XmdDisplay display = new XmdDisplay();
        display.setBusinessType(XmdPushMessage.BUSINESS_TYPE_CHAT_MESSAGE);
        display.setScene(XmdDisplay.SCENE_BG);
        display.setStyle(XmdDisplay.STYLE_NOTIFICATION);
        display.setTitle(user != null ? user.getName() : chatMessage.getUserName());
        display.setMessage(chatMessage.getContentText().toString());
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


    public static void vibrateAndPlayTone() {
        Ringtone ringtone = null; //手机铃声
        Vibrator vibrator; //手机震动
        vibrator = (Vibrator) XmdApp.getInstance().getContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = new long[]{0, 180, 80, 120};
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
                final Ringtone finalRingtone = ringtone;
                Thread ctlThread = new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            if (finalRingtone.isPlaying()) {
                                finalRingtone.stop();
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
