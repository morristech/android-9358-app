package com.xmd.chat.message;

import android.text.SpannableString;
import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.EmojiManager;
import com.xmd.app.user.User;
import com.xmd.chat.MessageManager;

import java.util.Calendar;

/**
 * Created by heyangya on 17-6-5.
 * 基本聊天消息
 */

public class ChatMessage {
    private static final String TAG = "ChatMessage";


    /**
     * 原始数据类型:
     * TXT,IMAGE,VIDEO,LOCATION,VOICE,FILE,CMD
     * 自定义类型:
     */
    public static final String MSG_TYPE_ORIGIN_TXT = "TXT";
    public static final String MSG_TYPE_ORIGIN_IMAGE = "IMAGE";
    public static final String MSG_TYPE_ORIGIN_VIDEO = "VIDEO";
    public static final String MSG_TYPE_ORIGIN_LOCATION = "LOCATION";
    public static final String MSG_TYPE_ORIGIN_VOICE = "VOICE";
    public static final String MSG_TYPE_ORIGIN_FILE = "FILE";
    public static final String MSG_TYPE_ORIGIN_CMD = "CMD";
    public static final String MSG_TYPE_TIP = "tip"; //提示消息

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
    public static final String MSG_TYPE_COUPON_TIP = "couponTip"; //用户领取优惠券
    public static final String MSG_TYPE_PAID_COUPON_TIP = "paidCouponTip";//用户购买点钟券
    public static final String MSG_TYPE_CREDIT_GIFT = "gift"; //积分礼物
    public static final String MSG_TYPE_DICE_GAME = "diceGame"; //骰子游戏

    public static final String MSG_TAG_CUSTOMER_SERVICE = "customer_service";//客服消息
    public static final String MSG_TAG_HELLO = "hello"; //打招呼消息


    public static final String ATTRIBUTE_MESSAGE_TYPE = "msgType";
    private static final String ATTRIBUTE_TAG = "xmd_tag";

    private static final String ATTRIBUTE_USER_ROLES = "userRoles";
    private static final String ATTRIBUTE_USER_ID = "userId";
    private static final String ATTRIBUTE_USER_NAME = "name";
    private static final String ATTRIBUTE_USER_AVATAR = "header";
    private static final String ATTRIBUTE_TIME = "time";

    private static final String ATTRIBUTE_SERIAL_NO = "no";
    private static final String ATTRIBUTE_TECH_ID = "techId";
    private static final String ATTRIBUTE_CLUB_ID = "clubId";
    private static final String ATTRIBUTE_CLUB_NAME = "clubName";


    //内部是否已处理此消息
    private static final String ATTR_INNER_PROCESSED = "inner_processed";

    //预约消息
    private EMMessage emMessage;

    private SpannableString contentText; //缓存emoji格式化后的数据
    protected String formatTime; //缓存格式化后的时间

    public ChatMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
        setTime(String.valueOf(emMessage.getMsgTime()));
    }

    public String getMsgType() {
        return getMsgType(emMessage);
    }

    public void setMsgType(String msgType) {
        setAttr(ATTRIBUTE_MESSAGE_TYPE, msgType);
    }

    public static String getMsgType(EMMessage emMessage) {
        String msgType = getSafeStringAttribute(emMessage, ATTRIBUTE_MESSAGE_TYPE);
        return TextUtils.isEmpty(msgType) ? emMessage.getType().name() : msgType;
    }

    //设置用户信息，发送时设置
    public void setUser(User user) {
        setUserRoles(user.getRoles());
        setUserId(user.getId());
        setUserName(user.getName());
        setUserAvatar(user.getAvatar());

        setClubId(user.getClubId());
        setClubName(user.getClubName());
        setTechNo(user.getTechNo());
    }

    public String getTag() {
        return getSafeStringAttribute(ATTRIBUTE_TAG);
    }

    public void addTag(String tag) {
        setAttr(ATTRIBUTE_TAG, TextUtils.isEmpty(getTag()) ? tag : getTag() + "," + tag);
    }

    public void clearTag() {
        setAttr(ATTRIBUTE_TAG, "");
    }

    public String getToChatId() {
        return emMessage.getTo();
    }

    public String getFromChatId() {
        return emMessage.getFrom();
    }


    public String getUserId() {
        return getSafeStringAttribute(ATTRIBUTE_USER_ID);
    }

    public void setUserId(String userId) {
        setAttr(ATTRIBUTE_USER_ID, userId);
    }

    public String getUserRoles() {
        return getSafeStringAttribute(ATTRIBUTE_USER_ROLES);
    }

    public void setUserRoles(String userRoles) {
        setAttr(ATTRIBUTE_USER_ROLES, userRoles);
    }

    public String getUserName() {
        return getSafeStringAttribute(ATTRIBUTE_USER_NAME);
    }

    public void setUserName(String userName) {
        setAttr(ATTRIBUTE_USER_NAME, userName);
    }

    public String getUserAvatar() {
        return getSafeStringAttribute(ATTRIBUTE_USER_AVATAR);
    }

    public void setUserAvatar(String userAvatar) {
        setAttr(ATTRIBUTE_USER_AVATAR, userAvatar);
    }

    public String getTime() {
        return getSafeStringAttribute(ATTRIBUTE_TIME);
    }

    public void setTime(String time) {
        setAttr(ATTRIBUTE_TIME, time);
    }

    public String getTechId() {
        return getSafeStringAttribute(ATTRIBUTE_TECH_ID);
    }

    public void setTechId(String techId) {
        setAttr(ATTRIBUTE_TECH_ID, techId);
    }

    public String getTechNo() {
        return getSafeStringAttribute(ATTRIBUTE_SERIAL_NO);
    }

    public void setTechNo(String techNo) {
        setAttr(ATTRIBUTE_SERIAL_NO, techNo);
    }

    public String getClubId() {
        return getSafeStringAttribute(ATTRIBUTE_CLUB_ID);
    }

    public void setClubId(String clubId) {
        setAttr(ATTRIBUTE_CLUB_ID, clubId);
    }

    public String getClubName() {
        return getSafeStringAttribute(ATTRIBUTE_CLUB_NAME);
    }

    public void setClubName(String clubName) {
        setAttr(ATTRIBUTE_CLUB_NAME, clubName);
    }


    protected void setAttr(String key, String value) {
        emMessage.setAttribute(key, value);
    }

    protected void setAttr(String attrKey, Long attr) {
        emMessage.setAttribute(attrKey, attr);
    }

    protected void setAttr(String attrKey, Integer attr) {
        emMessage.setAttribute(attrKey, attr);
    }

    protected void setAttr(String attrKey, Boolean attr) {
        emMessage.setAttribute(attrKey, attr);
    }

    public EMMessage getEmMessage() {
        return emMessage;
    }

    public void setEmMessage(EMMessage emMessage) {
        this.emMessage = emMessage;
    }

    public CharSequence getContentText() {
        if (contentText == null) {
            if (emMessage.getType().equals(EMMessage.Type.TXT)) {
                String message = ((EMTextMessageBody) emMessage.getBody()).getMessage();
                contentText = EmojiManager.getInstance().format(message);
            } else {
                contentText = new SpannableString("[" + emMessage.getType().name() + "]");
            }
        }
        return contentText;
    }

    public String getOriginContentText() {
        if (emMessage.getType().equals(EMMessage.Type.TXT)) {
            return ((EMTextMessageBody) emMessage.getBody()).getMessage();
        } else {
            return "[" + emMessage.getType().name() + "]";
        }
    }

    public boolean isCustomerService() {
        return getTag() != null && getTag().contains(MSG_TAG_CUSTOMER_SERVICE);
    }

    public boolean isReceivedMessage() {
        return emMessage.direct().equals(EMMessage.Direct.RECEIVE);
    }

    public String getRemoteChatId() {
        if (getEmMessage().direct() == EMMessage.Direct.RECEIVE) {
            return getFromChatId();
        } else {
            return getToChatId();
        }
    }

    public String getFormatTime() {
        if (formatTime == null) {
            long msgTime = emMessage.getMsgTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msgTime);
            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.YEAR) > calendar.get(Calendar.YEAR)
                    || now.get(Calendar.DAY_OF_YEAR) - 2 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                formatTime = DateUtils.doLong2String(msgTime);
            } else if (now.get(Calendar.DAY_OF_YEAR) - 1 >= calendar.get(Calendar.DAY_OF_YEAR)) {
                formatTime = DateUtils.doLong2String(msgTime, "昨天 HH:mm");
            } else {
                formatTime = DateUtils.doLong2String(msgTime, "HH:mm");
            }
        }
        return formatTime;
    }

    public String getSafeStringAttribute(String key) {
        return getSafeStringAttribute(emMessage, key);
    }

    public Integer getSafeIntegerAttribute(String key) {
        return getSafeIntegerAttribute(emMessage, key);
    }

    public Long getSafeLongAttribute(String key) {
        return getSafeLongAttribute(emMessage, key);
    }


    public static String getSafeStringAttribute(EMMessage emMessage, String key) {
        try {
            return emMessage.getStringAttribute(key);
        } catch (HyphenateException e) {
            return null;
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

    public String getInnerProcessed() {
        return getSafeStringAttribute(ATTR_INNER_PROCESSED);
    }

    public void setInnerProcessed(String processedDesc) {
        setAttr(ATTR_INNER_PROCESSED, processedDesc);
        MessageManager.getInstance().saveMessage(this); //设置状态标记后，需要保存消息到本地
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


    public static ChatMessage createTextMessage(String remoteChatId, String text) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(text, remoteChatId);
        if (emMessage == null) {
            return null;
        }
        return new ChatMessage(emMessage);
    }

    public static ChatMessage createImageMessage(String remoteChatId, String imagePath) {
        EMMessage emMessage = EMMessage.createImageSendMessage(imagePath, true, remoteChatId);
        if (emMessage == null) {
            return null;
        }
        return new ChatMessage(emMessage);
    }

    public static ChatMessage createVoiceSendMessage(String remoteChatId, String audioPath, int length) {
        EMMessage emMessage = EMMessage.createVoiceSendMessage(audioPath, length, remoteChatId);
        if (emMessage == null) {
            return null;
        }
        return new ChatMessage(emMessage);
    }
}
