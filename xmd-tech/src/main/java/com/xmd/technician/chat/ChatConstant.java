package com.xmd.technician.chat;

/**
 * Created by sdcm on 16-3-23.
 */
public class ChatConstant {

    public static final int CHAT_TYPE_SINGLE = 1;
    public static final int CHAT_TYPE_GROUP = 2;
    public static final int CHAT_TYPE_CHATROOM = 3;

    public static final int CHAT_USER_TYPE_TECH = 1; //技师
    public static final int CHAT_USER_TYPE_MANAGER = 2;//管理者
    public static final int CHAT_USER_TYPE_CUSTOMER = 3;//普通用户
    public static final String TO_CHAT_USER_TYPE_TECH = "tech";
    public static final String TO_CHAT_USER_TYPE_MANAGER = "manager";
    public static final String TO_CHAT_USER_TYPE_CUSTOMER = "customer";



    public static final String MESSAGE_ATTR_AT_MSG = "em_at_list";
    public static final String MESSAGE_ATTR_VALUE_AT_MSG_ALL = "ALL";



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
    public static final String KEY_SAY_HI_POSITION = "say_hi_position";

    //环信消息失败原因
    public static final String ERROR_SERVER_NOT_REACHABLE = "server_not_reachable";
    public static final String ERROR_IN_BLACKLIST = "in_blacklist";

    //重构代码
    public static final String TO_CHAT_USER_ID = "userId";



    //自定义消息
    public static final int MESSAGE_SENT_REWARD_TYPE = 0x001;
    public static final int MESSAGE_RECEIVE_REWARD_TYPE = 0x002;
    public static final int MESSAGE_SENT_ORDER_TYPE=0x03;
    public static final int MESSAGE_RECEIVE_ORDER_TYPE = 0x004;
    public static final int MESSAGE_SENT_COUPON_TYPE = 0x05;
    public static final int MESSAGE_RECEIVE_COUPON_TYPE = 0x006;
    public static final int MESSAGE_SENT_PAID_PAID_COUPON_TYPE = 0x007;
    public static final int MESSAGE_RECEIVE_PAID_COUPON_TYPE = 0x008;
    public static final int MESSAGE_SENT_GAME_REQUEST_TYPE = 0x009;
    public static final int MESSAGE_RECEIVE_GAME_REQUEST_TYPE = 0x010;
    public static final int MESSAGE_SENT_GAME_REFUSED_TYPE = 0x011;
    public static final int MESSAGE_RECEIVE_GAME_REFUSED_TYPE = 0x012;
    public static final int MESSAGE_SENT_GAME_ACCEPT_TYPE = 0x013;
    public static final int MESSAGE_RECEIVE_GAME_ACCEPT_TYPE = 0x014;
    public static final int MESSAGE_SENT_GAME_CANCEL_TYPE = 0x015;
    public static final int MESSAGE_RECEIVE_GAME_CANCEL_TYPE = 0x016;
    public static final int MESSAGE_SENT_GAME_OVER_TIME_TYPE = 0x017;
    public static final int MESSAGE_RECEIVE_GAME_OVER_TIME_TYPE = 0x018;
    public static final int MESSAGE_SENT_GAME_OVER_TYPE = 0x019;
    public static final int MESSAGE_RECEIVE_GAME_OVER_TYPE = 0x020;
    public static final int MESSAGE_SENT_GIFT_TYPE = 0x21;
    public static final int MESSAGE_CREDIT_GIFT_TYPE = 0x22;
    public static final int MESSAGE_SENT_LOCATION_TYPE = 0x23;
    public static final int MESSAGE_RECEIVE_LOCATION_TYPE = 0x24;
    public static final int MESSAGE_SENT_REVOKE_MESSAGE_TYPE = 0x25;
    public static final int MESSAGE_RECEIVE_REVOKE_MESSAGE_TYPE = 0x26;
    public static final int MESSAGE_SENT_ACTIVITY_TYPE = 0x27;
    public static final int MESSAGE_RECEIVE_ACTIVITY_TYPE = 0x28;

    public static final int CHAT_VIEW_TYPE_ORDER = 0x1100; //view 类型


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
    public static final String KEY_COUPON_PAID_TYPE = "paid";

    public static final String KEY_REQUEST_GAME_STATUS = "request";
    public static final String KEY_ACCEPT_GAME_STATUS = "accept";
    public static final String KEY_REFUSED_GAME_STATUS = "refused";
    public static final String KEY_OVER_TIME_GAME_STATUS = "overtime";
    public static final String KEY_MSG_GAME_STATUS = "diceGame";
    public static final String KEY_OVER_GAME_STATUS = "over";
    public static final String KEY_CANCEL_GAME_STATUS = "cancel";
    public static final String KEY_GIFT_TYPE = "gift";
    public static final String KEY_CREDIT_GIFT_VALUE = "giftValue";
    public static final String KEY_CREDIT_GIFT_NAME = "giftName";
    public static final String KEY_CREDIT_GIFT_ID = "giftId";
    public static final String KEY_GAME_DISABLE = "disable";
    public static final String KEY_CLUB_ID = "clubId";
    public static final String KEY_CLUB_NAME = "clubName";
    public static final String KEY_USER_TYPE = "userType";
    public static final String MESSAGE_TECH_TYPE = "tech";
    public static final String KEY_COUPON_ACT_ID = "userActId";
    public static final String KEY_PAID_COUPON_TIP = "paidCouponTip";
    public static final String KEY_ERROR_CODE = "errorCode";
    public static final String KEY_LOCATION_LAT = "lat";
    public static final String KEY_LOCATION_LNG = "lng";
    public static final String KEY_LOCATION_ADDRESS = "address";
    public static final String KEY_LOCATION_STATIC_MAP = "staticMap";
    public static final String KEY_MESSAGE_ID = "messageId";
    public static final String KEY_REVOKE_TYPE = "mark";
    public static final String KEY_SUB_TYPE_INDIANA = "indiana";//一元夺
    public static final String KEY_SUB_TYPE_SECKILL = "seckill";//抢项目
    public static final String KEY_SUB_TYPE_TURNTABLE = "turntable";//转盘
    public static final String KEY_SUB_TYPE_TIMES_SCARD = "timescard";//次卡
    public static final String KEY_SUB_TYPE_PACKAGE = "package"; //混合套餐
    public static final String KEY_SUB_TYPE_GIFT = "creditGift"; //积分礼品
    public static final String KEY_SUB_TYPE_JOURNAL = "journal";//电子期刊
    public static final String KEY_SUB_TEMPLATE_ID = "templateId";
    public static final String KEY_SUB_CARD_TYPE = "cardType";
    public static final String KEY_ACTIVITY_TIME_LIMIT_TYPE = "timeLimit";
    public static final String KEY_ACTIVITY_ONE_YUAN_TYPE ="oneYuan";
    public static final String KEY_ACTIVITY_LUCKY_WHEEL_TYPE = "luckyWheel";
    public static final String KEY_ACTIVITY_JOURNAL_TYPE ="journal";
    public static final String KEY_ACTIVITY_ITEM_CARD_TYPE = "itemCard";



    public static final String KEY_CHAT_SENT_REWARD_TYPE = "begReward";//技师求打赏
    public static final String KEY_CHAT_RECEIVE_REWARD = "reward"; //用户打赏
    public static final String KEY_CHAT_SENT_COUPON_TYPE = "ordinaryCoupon";//技师发送优惠券
    public static final String KEY_CHAT_RECEIVE_ORDER_TYPE = "order"; //用户预约
    public static final String KEY_CHAT_SENT_PAID_COUPON_TYPE = "paidCoupon";//技师求点钟
    public static final String KEY_CHAT_RECEIVE_PAID_COUPON_TYPE ="paidCouponTip";//用户购买点钟券
    public static final String KEY_CHAT_DICE_GAME = "diceGame";//用户发起游戏
    public static final String KEY_CHAT_GIFT_TYPE = "gift";
    public static final String KEY_CHAT_LOCATION_TYPE = "clubLocation";
    public static final String KEY_SUB_TYPE = "subType";
    public static final String KEY_USER_ID = "userId";


    public static final String KEY_CHAT_CMD_REVOKE_ACTION = "revoke";

    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String ACCOUNT_FORBIDDEN = "user_forbidden";
    public static final String CHAT_ROBOT = "item_robots";
    public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";

}
