package com.xmd.technician.chat;

/**
 * Created by sdcm on 16-3-23.
 */
public class ChatConstant {
    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;

    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_USER_AVATAR = "userAvatar";

    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

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
}
