package com.xmd.technician.chat;

/**
 * Created by sdcm on 16-3-23.
 */
public class ChatConstant {
    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;

    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EMCHAT_ID = "emchatId";
    public static final String EMCHAT_NICKNAME = "emchatNickname";
    public static final String EMCHAT_AVATAR = "emchatAvatar";
    public static final String EMCHAT_USER_TYPE = "userType";
    public static final String EMCHAT_IS_TECH = "isTech";
    public static final String EMCHAT_CLUB_ID = "clubId";

    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_SYSTEM_NOTICE = "notice_msg";
    public static final String MESSAGE_CHAT_TEXT = "chat_text";
    public static final String MESSAGE_CHAT_ORDER = "chat_order";
    public static final String MESSAGE_CHAT_REWARD = "chat_reward";
    public static final String MESSAGE_CHAT_PAID_COUPON = "chat_paid_coupon";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";

    //自定义消息
    public static final int MESSAGE_TYPE_SENT_REWARD = 0x81;
    public static final int MESSAGE_TYPE_RECV_REWARD = 0x82;
    public static final int MESSAGE_TYPE_SENT_ORDER = 0x83;
    public static final int MESSAGE_TYPE_RECV_ORDER = 0x84;
    public static final int MESSAGE_TYPE_SENT_PAID_COUPON_TIP = 0x85;
    public static final int MESSAGE_TYPE_RECV_PAID_COUPON_TIP = 0x86;
    public static final int MESSAGE_TYPE_SENT_BEG_REWARD = 0x87;
    public static final int MESSAGE_TYPE_RECV_BEG_REWARD = 0x88;
    public static final int MESSAGE_TYPE_SENT_PAID_COUPON = 0x89;
    public static final int MESSAGE_TYPE_RECV_PAID_COUPON = 0x8A;
    public static final int MESSAGE_TYPE_SENT_ORDINARY_COUPON = 0x8B;
    public static final int MESSAGE_TYPE_RECV_ORDINARY_COUPON = 0x8C;
    public static final int MESSAGE_TYPE_SENT_COUPON_TIP = 0x8E;
    public static final int MESSAGE_TYPE_RECV_COUPON_TIP = 0x8F;
    public static final int MESSAGE_TYPE_SEND_GAME_INVITE = 0x91;
    public static final int MESSAGE_TYPE_RECV_GAME_INVITE = 0x92;
    public static final int MESSAGE_TYPE_SEND_GAME_ACCEPT = 0x93;
    public static final int MESSAGE_TYPE_RECV_GAME_ACCEPT = 0x94;
    public static final int MESSAGE_TYPE_SEND_GAME_REJECT = 0x95;
    public static final int MESSAGE_TYPE_RECV_GAME_REJECT = 0x96;
    public static final int MESSAGE_TYPE_SEND_GAME_OVERTIME = 0x97;
    public static final int MESSAGE_TYPE_RECV_GAME_OVERTIME = 0x98;
    public static final int MESSAGE_TYPE_SEND_GAME_OVER = 0x99;
    public static final int MESSAGE_TYPE_RECV_GAME_OVER = 0x100;
    public static final int MESSAGE_TYPE_RECV_GAME_CANCEL = 0x101;
    public static final int MESSAGE_TYPE_SEND_GAME_CANCEL = 0x102;
    public static final int MESSAGE_TYPE_RECV_CREDIT_GIFT = 0x102;

    public static final String KEY_CHAT_TYPE = "chatType";
    public static final String KEY_MSG = "msg";
    public static final String KEY_TO = "to";
    public static final String KEY_IMAGE_PATH = "imagePath";
    public static final String KEY_CUSTOM_TYPE = "msgType";
    public static final String KEY_ACT_ID = "actId";
    public static final String KEY_ORDER_ID = "orderId";
    public static final String KEY_TECH_CODE = "techCode";
    public static final String KEY_NAME = "name";
    public static final String KEY_HEADER = "header";
    public static final String KEY_TIME = "time";
    public static final String KEY_TITLE = "title";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_IMAGE_URL = "imageUrl";
    public static final String KEY_LINK_URL = "linkUrl";
    public static final String KEY_TECH_ID = "techId";
    public static final String KEY_SERIAL_NO = "no";
    public static final String KEY_GAME_ACCEPT = "accept";
    public static final String KEY_GAME_REJECT = "reject";
    public static final String KEY_GAME_CLUB_ID = "clubId";
    public static final String KEY_GAME_CLUB_NAME = "clubName";
    public static final String KEY_GAME_STATUS = "gameStatus";
    public static final String KEY_GAME_INVITE = "gameInvite";
    public static final String KEY_GAME_ID = "gameId";
    public static final String KEY_GAME_RESULT = "gameResult";
    public static final String KEY_ADVERSE_NAME = "adverseName";
    public static final String KEY_REQUEST_GAME = "request";
    public static final String KEY_ACCEPT_GAME = "accept";
    public static final String KEY_REFUSED_GAME = "refused";
    public static final String KEY_OVERTIME_GAME = "overtime";
    public static final String KEY_MSG_GAME_TYPE = "diceGame";
    public static final String KEY_OVER_GAME_TYPE = "over";
    public static final String KEY_CANCEL_GAME_TYPE = "cancel";
    public static final String KEY_GIFT_TYPE = "gift";
    public static final String KEY_CREDIT_GIFT_VALUE = "giftValue";
    public static final String KEY_CREDIT_GIFT_NAME = "giftName";
    public static final String KEY_CREDIT_GIFT_ID = "giftId";

}
