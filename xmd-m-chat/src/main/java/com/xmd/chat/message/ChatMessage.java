package com.xmd.chat.message;

import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.shidou.commonlibrary.helper.XLogger;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;
import com.xmd.app.user.User;
import com.xmd.chat.xmdchat.ImMessageParseManager;
import com.xmd.chat.xmdchat.constant.XmdMessageType;
import com.xmd.chat.xmdchat.contract.XmdChatMessageInterface;
import com.xmd.chat.xmdchat.messagebean.ImageMessageBean;
import com.xmd.chat.xmdchat.messagebean.VoiceMessageBean;
import com.xmd.chat.xmdchat.model.XmdChatModel;
import com.xmd.chat.xmdchat.present.EmChatMessagePresent;
import com.xmd.chat.xmdchat.present.ImChatMessageManagerPresent;
import com.xmd.chat.xmdchat.present.ImChatMessagePresent;

/**
 * Created by heyangya on 17-6-5.
 * 基本聊天消息
 */

public class ChatMessage<T> {
    public static final String TAG = "ChatMessage";
    /**
     * 原始数据类型:
     * TXT,IMAGE,VIDEO,LOCATION,VOICE,FILE,CMD
     * 自定义类型:
     */
    public static final String MSG_TYPE_ORIGIN_TXT = "TXT";
    public static final String MSG_TYPE_ORIGIN_IMAGE = "IMAGE";
    public static final String MSG_TYPE_ORIGIN_VOICE = "VOICE";
    public static final String MSG_TYPE_ORIGIN_CMD = "CMD";
    public static final String MSG_TYPE_TIP = "tip"; //提示消息
    public static final String MSG_TYPE_ORIGIN_VIDEO = "VIDEO";
    public static final String MSG_TYPE_ORIGIN_LOCATION = "LOCATION";
    public static final String MSG_TYPE_ORIGIN_FILE = "FILE";

    public static final String MSG_TYPE_CLUB_LOCATION = "clubLocation"; //位置消息
    public static final String MSG_TYPE_ORDER_START = "order_start";
    public static final String MSG_TYPE_ORDER_REFUSE = "order_refuse";
    public static final String MSG_TYPE_ORDER_CONFIRM = "order_confirm";
    public static final String MSG_TYPE_ORDER_CANCEL = "order_cancel";
    public static final String MSG_TYPE_ORDER_SUCCESS = "order_success";
    public static final String MSG_TYPE_ORDER_REQUEST = "order_request"; //求预约
    public static final String MSG_TYPE_NEW_ORDER = "order";//原始订单消息
    //活动消息
    public static final String MSG_TYPE_JOURNAL = "journal"; //电子期刊
    public static final String MSG_TYPE_ONCE_CARD = "itemCard"; //次卡
    public static final String MSG_TYPE_TIME_LIMIT_TYPE = "timeLimit";//限时购
    public static final String MSG_TYPE_ONE_YUAN_TYPE = "oneYuan";//夺宝
    public static final String MSG_TYPE_LUCKY_WHEEL_TYPE = "luckyWheel"; //大转盘
    public static final String MSG_TYPE_REQUEST_REWARD = "begReward"; //求打赏
    public static final String MSG_TYPE_REWARD = "reward"; //用户打赏
    public static final String MSG_TYPE_COUPON = "ordinaryCoupon"; //优惠券
    public static final String MSG_TYPE_PAID_COUPON = "paidCoupon"; //点钟券
    public static final String MSG_TYPE_COUPON_TIP = "couponTip"; //用户领取优惠券
    public static final String MSG_TYPE_PAID_COUPON_TIP = "paidCouponTip";//用户购买点钟券
    public static final String MSG_TYPE_CREDIT_GIFT = "gift"; //积分礼物
    public static final String MSG_TYPE_DICE_GAME = "diceGame"; //骰子游戏
    public static final String MSG_TYPE_INVITE_GIFT_TYPE = "inviteGift"; //邀请有礼


    public static final String MSG_TAG_CUSTOMER_SERVICE = "customer_service";//客服消息
    public static final String MSG_TAG_HELLO = "hello"; //打招呼消息
    public static final String REVERT_MSG = "revert_msg"; //撤回消息


    public static final String ATTRIBUTE_MESSAGE_TYPE = "msgType";
    public static final String ATTRIBUTE_TAG = "tag";

    public static final String ATTRIBUTE_USER_ROLES = "userRoles";
    public static final String ATTRIBUTE_USER_ID = "userId";
    public static final String ATTRIBUTE_USER_NAME = "name";
    public static final String ATTRIBUTE_USER_AVATAR = "header";
    public static final String ATTRIBUTE_USER_AVATAR_ID = "avatar";
    public static final String ATTRIBUTE_TIME = "time";

    public static final String ATTRIBUTE_SERIAL_NO = "no";
    public static final String ATTRIBUTE_TECH_ID = "techId";
    public static final String ATTRIBUTE_CLUB_ID = "clubId";
    public static final String ATTRIBUTE_CLUB_NAME = "clubName";


    //内部是否已处理此消息
    public static final String ATTR_INNER_PROCESSED = "inner_processed";
    //预约消息
    public T message;
    public XmdChatMessageInterface mInterface;

    public ChatMessage(T message) {
        this.message = message;
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            mInterface = new EmChatMessagePresent((EMMessage) message);
        } else {
            mInterface = new ImChatMessagePresent((TIMMessage) message);
        }
        setTime(String.valueOf(mInterface.getMsgTime()));
    }

    public String getMsgType() {
        return getMsgType(message);
    }

    public void setMsgType(String msgType) {
        mInterface.setMsgType(msgType);
    }


    public static <T> String getMsgType(T message) {
        if (message instanceof EMMessage) {
            String msgType = getSafeStringAttribute((EMMessage) message, ATTRIBUTE_MESSAGE_TYPE);
            return TextUtils.isEmpty(msgType) ? ((EMMessage) message).getType().name() : msgType;
        } else {
            return ImMessageParseManager.getInstance().getMessageType((TIMMessage) message);
        }

    }

    //设置用户信息，发送时设置
    public void setUser(User user) {
        setUserRoles(user.getUserRoles());
        setUserId(user.getId());
        setUserName(user.getName());
        setUserAvatar(user.getAvatar());
        setUserAvatarId(user.getAvatarId());
        setClubId(user.getClubId());
        setClubName(user.getClubName());
        setTechNo(user.getTechNo());
        setTechId(user.getId());
    }

    public String getTag() {
        return getSafeStringAttribute(ATTRIBUTE_TAG);
    }

    public void addTag(String tag) {
        mInterface.addTag(tag);
    }

    public void clearTag() {
        mInterface.clearTag();
    }

    public String getToChatId() {
        return mInterface.getToChatId();
    }

    public String getFromChatId() {
        return mInterface.getFromChatId();
    }

    public String getUserId() {
        return mInterface.getUserId();
    }

    public void setUserId(String userId) {
        mInterface.setUserId(userId);
    }

    public String getUserRoles() {
        return mInterface.getUserRoles();
    }

    public void setUserRoles(String userRoles) {
        mInterface.setUserRoles(userRoles);
    }

    public String getUserName() {
        return mInterface.getUserName();
    }

    public void setUserName(String userName) {
        mInterface.setUserName(userName);
    }

    public String getUserAvatar() {
        return mInterface.getUserAvatar();
    }

    public void setUserAvatar(String userAvatar) {
        mInterface.setUserAvatar(userAvatar);
    }

    public String getUserAvatarId() {
        return mInterface.getUserAvatarId();
    }

    public void setUserAvatarId(String userAvatarId) {
        mInterface.setUserAvatarId(userAvatarId);
    }

    public String getTime() {
        return mInterface.getTime();
    }

    public void setTime(String time) {
        mInterface.setTime(time);
    }

    public String getTechId() {
        return getSafeStringAttribute(ATTRIBUTE_TECH_ID);
    }

    public void setTechId(String techId) {
        mInterface.setTechId(techId);
    }

    public String getTechNo() {
        return mInterface.getTechNo();
    }

    public void setTechNo(String techNo) {
        mInterface.setTechNo(techNo);
    }

    public String getClubId() {
        return mInterface.getClubId();
    }

    public void setClubId(String clubId) {
        mInterface.setClubId(clubId);
    }

    public String getClubName() {
        return mInterface.getClubName();
    }

    public void setClubName(String clubName) {
        mInterface.setClubName(clubName);
    }


    public CharSequence getContentText() {
        return mInterface.getContentText();
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public String getOriginContentText() {
        return mInterface.getOriginContentText();
    }

    public boolean isCustomerService() {
        return getTag() != null && getTag().contains(MSG_TAG_CUSTOMER_SERVICE);
    }

    public boolean isReceivedMessage() {
        return mInterface.isReceivedMessage();
    }

    public String getRemoteChatId() {
        return mInterface.getRemoteChatId();
    }

    public String getFormatTime() {
        return mInterface.getFormatTime();
    }

    public String getChatRelativeTime() {
        return mInterface.getChatRelativeTime();
    }

    public String getSafeStringAttribute(String key) {
        return mInterface.getSafeStringAttribute(key);
    }

    public Integer getSafeIntegerAttribute(String key) {
       return mInterface.getSafeIntegerAttribute(key);
    }

    public Long getSafeLongAttribute(String key) {
        return mInterface.getSafeLongAttribute(key);
    }

    public String getAttrType() {
        return mInterface.getAttrType();
    }

    public void setAttr(String key, String value) {
        mInterface.setAttr(key, value);
    }


    protected void setAttr(String attrKey, Long attr) {
        mInterface.setAttr(attrKey, attr);
    }

    protected void setAttr(String attrKey, Integer attr) {
        mInterface.setAttr(attrKey, attr);
    }

    protected void setAttr(String attrKey, Boolean attr) {
        mInterface.setAttr(attrKey, attr);
    }

    public String getInnerProcessed() {
        return getSafeStringAttribute(ATTR_INNER_PROCESSED);
    }

    public void setInnerProcessed(String processedDesc) {
        mInterface.setInnerProcessed(processedDesc);
    }

    public EMConversation getEMConversation() {
        return EMClient.getInstance().chatManager().getConversation(getRemoteChatId());
    }

    public TIMConversation geIMConversation() {
        return TIMManager.getInstance().getConversation(TIMConversationType.C2C, getRemoteChatId());
    }

    public long getMessageTime() {
        return mInterface.getMsgTime();
    }

    public static ChatMessage createTextMessage(String remoteChatId, String text) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMMessage emMessage = EMMessage.createTxtSendMessage(text, remoteChatId);
            if (emMessage == null) {
                return null;
            }
            return new ChatMessage(emMessage);
        } else {
            TIMMessage message = new TIMMessage();
            return new ChatMessage(message);
        }

    }

    public static ChatMessage createImageMessage(String remoteChatId, String imagePath,String tag) {
        if (XmdChatModel.getInstance().chatModelIsEm()) {
            EMMessage emMessage = EMMessage.createImageSendMessage(imagePath, true, remoteChatId);
            if (emMessage == null) {
                return null;
            }
            return new ChatMessage(emMessage);
        } else {
            ImageMessageBean imageBean = new ImageMessageBean();
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(imageBean, XmdMessageType.IMAGE_TYPE,tag,null);
            TIMImageElem elem = new TIMImageElem();
            elem.setPath(imagePath);
            if(message.addElement(elem) != 0){
                XLogger.d("tag","addElement fail");
            }
            return new ChatMessage(message);
        }

    }

    public static ChatMessage createVoiceSendMessage(String remoteChatId, String audioPath, int length) {
        if(XmdChatModel.getInstance().chatModelIsEm()){
            EMMessage emMessage = EMMessage.createVoiceSendMessage(audioPath, length, remoteChatId);
            if (emMessage == null) {
                return null;
            }
            return new ChatMessage(emMessage);
        }else {
            VoiceMessageBean bean = new VoiceMessageBean();
            bean.setPath(audioPath);
            bean.setDuration(length);
            TIMMessage message = ImChatMessageManagerPresent.wrapMessage(bean,XmdMessageType.VOICE_TYPE,null,null);
            TIMSoundElem elem = new TIMSoundElem();
            elem.setPath(audioPath);
            elem.setDuration(length);
            message.addElement(elem);
            return new ChatMessage(message);
        }

    }

    public static String getSafeStringAttribute(EMMessage emMessage, String key) {
        if (emMessage == null || TextUtils.isEmpty(key)) {
            return "";
        }
        try {
            return emMessage.getStringAttribute(key);
        } catch (HyphenateException e) {
            e.getLocalizedMessage();
            return "";
        }
    }


    public static Integer getSafeIntegerAttribute(EMMessage emMessage, String key) {
        try {
            return emMessage.getIntAttribute(key);
        } catch (HyphenateException e) {
            return null;
        }
    }

    public static Long getSafeLongAttribute(EMMessage emMessage, String key) {
        try {
            return emMessage.getLongAttribute(key);
        } catch (HyphenateException e) {
            return null;
        }
    }

    public static String getMsgTypeText(String msgType) {
        switch (msgType) {
            case ChatMessage.MSG_TYPE_ORDER_START:
                return "发起预约";
            case ChatMessage.MSG_TYPE_ORDER_REFUSE:
                return "拒绝预约";
            case ChatMessage.MSG_TYPE_ORDER_CANCEL:
                return "预约取消";
            case ChatMessage.MSG_TYPE_ORDER_CONFIRM:
                return "预约确认";
            case ChatMessage.MSG_TYPE_ORDER_SUCCESS:
                return "预约成功";
        }
        return msgType;
    }
}
