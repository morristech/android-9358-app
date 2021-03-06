package com.xmd.technician.msgctrl;

/**
 * Created by sdcm on 15-10-22.
 */
public class MsgDef {

    public static final int MSG_DEF_AUTO_CHECK_UPGRADE = 0x0001;

    public static final int MSG_DEF_MANUALLY_CHECK_UPGRADE = 0x0002;

    public static final int MSG_DEF_LOGIN = 0x0003;

    public static final int MSG_DEF_LOGOUT = 0x0004;

    public static final int MSG_DEF_GET_TECH_EDIT_INFO = 0x0009;

    public static final int MSG_DEF_GET_CLUB_COUPON_LIST = 0x0005;

    public static final int MSG_DEF_GET_USER_COUPON_LIST = 0x0006;

    public static final int MSG_DEF_GET_ACCOUNT_MONEY = 0x0061;

    public static final int MSG_DEF_TOKEN_EXPIRE = 0x0007;

    public static final int MSG_DEF_MODIFY_PASSWORD = 0x0008;

    public static final int MSG_DEF_GET_SERVICE_ITEM_LIST = 0x000A;

    public static final int MSG_DEF_GET_COMMENT_LIST = 0x0017;

    public static final int MSG_DEF_GET_TECH_PERSONAL_DATA = 0x000B;

    public static final int MSG_DEF_FILTER_ORDER_LIST = 0x000C;

    public static final int MSG_DEF_MANAGE_ORDER = 0x000E;

    public static final int MSG_DEF_SUBMIT_FEEDBACK = 0x000F;

    public static final int MSG_DEF_GETUI_BIND_CLIENT_ID = 0x0010;

    public static final int MSG_DEF_SHARE_TO_FRIEND = 0x0011;

    public static final int MSG_DEF_SHARE_TO_TIMELINE = 0x0012;

    public static final int MSG_DEF_SHOW_SHARE_PLATFORM = 0x0013;

    public static final int MSG_DEF_GET_CONSUME_DETAIL = 0x0014;

    public static final int MSG_DEF_GET_USER_COUPON_VIEW = 0x0015;

    public static final int MSG_DEF_GET_COUPON_USE_DATA = 0x0016;

    public static final int MSG_DEF_GETUI_UNBIND_CLIENT_ID = 0x0018;

    public static final int MSG_DEF_GET_ICODE = 0x0019;

    public static final int MSG_DEF_JOIN_CLUB = 0x001A;

    public static final int MSG_DEF_REGISTER = 0x001B;

    public static final int MSG_DEF_GET_WORK_TIME = 0x001C;

    public static final int MSG_DEF_UPDATE_WORK_TIME = 0x001D;

    public static final int MSG_DEF_UPLOAD_AVATAR = 0x001E;

    public static final int MSG_DEF_UPLOAD_ALBUM = 0x001F;

    public static final int MSG_DEF_DELETE_ALBUM = 0x0020;

    public static final int MSG_DEF_UPDATE_WORK_STATUS = 0x0021;

    public static final int MSG_DEF_RESET_PASSWORD = 0x0023;

    public static final int MSG_DEF_UPDATE_TECH_INFO = 0x0024;

    public static final int MSG_DEF_UPDATE_SERVICE_ITEM_LIST = 0x0025;

    /**
     * ChatController, start to chat with someone
     */
    public static final int MSG_DEF_START_CHAT = 0x0026;

    public static final int MSG_DEF_GET_COUPON_LIST = 0x0027;

    public static final int MSG_DEF_GET_COUPON_INFO = 0x0028;

    public static final int MSG_DEF_GET_PAID_COUPON_USER_DETAIL = 0x0029;

    public static final int MSG_DEF_SHARE_TO_OTHER = 0x0030;

    public static final int MSG_DEF_SAVE_CHAT_USER = 0x0031;
    public static final int MSG_DEF_GET_CONVERSATION_LIST = 0x0032;
    public static final int MSG_DEF_SYSTEM_NOTICE_NOTIFY = 0x0033;

    public static final int MSG_DEF_SORT_ALBUM = 0x0034;

    public static final int MSG_DEF_HIDE_ORDER = 0x0035;

    public static final int MSG_DEF_QUIT_CLUB = 0x0036;

    public static final int MSG_DEF_LOGIN_EMCHAT = 0x0037;

    public static final int MSG_DEF_COUPON_SHARE_EVENT_COUNT = 0x0038;

    public static final int MSG_DEF_GET_APP_UPDATE_CONFIG = 0x0039;


    public static final int MSG_DEF_ADD_OR_EDIT_CUSTOMER = 0x0041;
    public static final int MSG_DEF_GET_CUSTOMER_INFO_DETAIL = 0x0042;
    public static final int MSG_DEF_GET_CUSTOMER_LIST = 0x0043;
    public static final int MSG_DEF_DELETE_CONTACT = 0x0046;
    public static final int MSG_DEF_DO_DRAW_MONEY = 0x0047;
    public static final int MSG_DEF_GET_USER_RECORDE = 0x0050;
    public static final int MSG_DEF_GET_CREDIT_STATUS = 0x0051;
    public static final int MSG_DEF_GET_CREDIT_ACCOUNT = 0x0052;
    public static final int MSG_DEF_DO_CREDIT_EXCHANGE = 0x0053;
    //public static final int MSG_DEF_GET_CONTACT_MARK = 0x0054;
    public static final int MSG_DEF_DO_INITIATE_GAME = 0x0055;
    public static final int MSG_DEF_DO_GAME_ACCEPT_OR_REJECT = 0x0056;
    public static final int MSG_DEF_GET_CREDIT_APPLICATIONS = 0x0057;
    public static final int MSG_DEF_GET_USER_CLUB_SWITCHES = 0x0058;
    public static final int MSG_DEF_GET_RECENTLY_VISITOR = 0x0059;
    public static final int MSG_DEF_GET_CREDIT_GIFT_LIST = 0x0060;
    public static final int MSG_DEF_DO_SAY_HI = 0x0062;
    public static final int MSG_DEF_GET_VISIT_VIEW = 0x0063;
    public static final int MSG_DEF_SAVE_CHAT_TO_CHONTACT = 0x0064;

    public static final int MSF_DEF_GET_TECH_INFO = 0x0065;
    public static final int MSF_DEF_GET_TECH_ORDER_LIST = 0x0066;
    public static final int MSF_DEF_GET_TECH_STATISTICS_DATA = 0x0067;
    public static final int MSF_DEF_GET_TECH_RANK_INDEX_DATA = 0x0068;
    public static final int MSF_DEF_GET_TECH_DYNAMIC_LIST = 0x0069;
    public static final int MSF_DEF_SET_PAGE_SELECTED = 0x0070;
    public static final int MSG_DEF_ORDER_INNER_READ = 0x0071;

    public static final int MSG_DEF_LOGIN_BY_TECH_NO = 0x0072;

    public static final int MSG_DEF_GET_UNUSED_TECH_NO = 0x0073;
    public static final int MSG_DEF_USER_GET_COUPON = 0x0074;

    public static final int MSG_DEF_GET_PAY_NOTIFY = 0x0075;

    public static final int MSG_DEF_GET_CARD_SHARE_LIST = 0x0076;
    public static final int MSG_DEF_GET_ACTIVITY_LIST = 0x0077;
    public static final int MSG_DEF_GET_PROPAGANDA_LIST = 0x0078;
    public static final int MSG_DEF_GET_ONCE_CARD_LIST_DETAIL = 0x0079;
    public static final int MSG_DEF_GET_CARD_LIST_DETAIL = 0x0080;
    public static final int MSG_DEF_GET_SERVICE_ITEM_LIST_DETAIL = 0x0081;
    public static final int MSG_DEF_GET_REWARD_ACTIVITY_LIST = 0x0082;
    public static final int MSG_DEF_GET_CLUB_JOURNAL_LIST = 0x0083;
    public static final int MSG_DEF_GET_PAY_FOR_ME_LIST_DETAIL = 0x0084;
    public static final int MSG_DEF_JOURNAL_SHARE_COUNT = 0x0085;
    public static final int MSG_DEF_TECH_ACCOUNT_LIST = 0x0086;
    public static final int MSG_DEG_DELETE_CONVERSATION_FROM_DB = 0x0087;
    public static final int MSG_DEG_SHARE_QR_CODE = 0x0088;

    // ------------------------------------------> 附近的人 <----------------------------------------
    public static final int MSG_DEF_GET_CLUB_POSITION_INFO = 0x0090;    //获取会所位置信息
    public static final int MSG_DEF_GET_NEARBY_CUS_COUNT = 0x0091;      //获取会所附近客户数量
    public static final int MSG_DEF_GET_NEARBY_CUS_LIST = 0x0092;       //获取会所附近客户列表
    public static final int MSG_DEF_GET_HELLO_LEFT_COUNT = 0x0093;      // 获取剩余打招呼次数
    public static final int MSG_DEF_TECH_SAY_HELLO = 0x0094;            // 打招呼
    public static final int MSG_DEF_GET_HELLO_RECORD_LIST = 0x0095;     // 获取打招呼记录
    //    public static final int MSG_DEF_CHECK_HELLO_RECENTLY = 0x0096;          // 查询是否打过招呼
    public static final int MSG_DEF_GET_CONTACT_PERMISSION = 0x0097;     // 查询同客户关系
    public static final int MSG_DEF_GET_SET_TEMPLATE = 0x0098;          // 获取打招呼内容
    public static final int MSG_DEF_SAVE_SET_TEMPLATE = 0x0099;         // 保存打招呼内容
    public static final int MSG_DEF_GET_SYS_TEMPLATE_LIST = 0x0100;     // 获取系统招呼模版列表
    public static final int MSG_DEF_UPLOAD_HELLO_TEMPLATE_IMG = 0x0101; // 上传打招呼图片
    public static final int MSG_DEF_DOWNLOAD_HELLO_IMAGE_CACHE = 0x102; // 下载到招呼图片的缓存

    public static final int MSG_DEF_GET_ROLE_PERMISSION = 0x0103;
    public static final int MSG_DEF_GET_ROLE_LIST = 0x0104;
    // ------------------------------------------> 技师排行榜 <----------------------------------------
    public static final int MSG_DEF_TECH_PK_RANKING = 0x00105;
    public static final int MSG_DEF_TECH_PK_ACTIVITY_LIST = 0x0106;
    public static final int MSG_DEF_TECH_PK_TEAM_RANKING_LIST = 0x0107;
    public static final int MSG_DEF_TECH_PK_PERSONAL_RANKING_LIST = 0x0108;
    public static final int MSG_DEF_TECH_RANKING_LIST = 0x0109;

    //获取聊天定位
    public static final int MSG_DEF_MARK_CHAT_TO_USER = 0x0115;
    public static final int MSG_DEF_LOGOUT_EMCHAT = 0x0116;
    public static final int MSG_DEF_GET_CHAT_CATEGORY_LIST = 0x0117;
    public static final int MSG_DEF_GET_TECH_MARKETING_LIST = 0x0118;

    //------------------------------------------> 技师海报 <-----------------------------------------
    public static final int MSG_DEF_TECH_POSTER_SAVE = 0x124;
    public static final int MSG_DEF_TECH_POSTER_DELETE = 0x125;
    public static final int MSG_DEF_TECH_POSTER_IMAGE_UPLOAD = 0x126;
    public static final int MSG_DEF_TECH_POSTER_LIST = 0x127;
    public static final int MSG_DEF_TECH_POSTER_DETAIL = 0x128;
    //分享图片到微信
    public static final int MSG_DEF_SHARE_IMAGE_TO_TIMELINE = 0x129;
    public static final int MSG_DEF_SHARE_IMAGE_TO_FRIENDS = 0x130;
    //获取技师订单数量
    public static final int MSG_DEF_TECH_ORDER_COUNT = 0x131;
    //加入会所申请
    public static final int MSG_DEF_TECH_AUDIT_MODIFY = 0x132;
    public static final int MSG_DEF_TECH_AUDIT_CANCEL = 0x133;
    public static final int MSG_DEF_TECH_AUDIT_CONFIRM = 0x134;
    public static final int MSG_DEF_GET_INVITATION_REWARD_ACTIVITY_LIST = 0x0135;
    //分享统计
    public static final int MSG_DEF_TECH_SHARE_COUNT_UPDATE = 0x0136;
    //提成说明
    public static final int MSG_DEF_TECH_WITHDRAW_RULE = 0x137;
    //拼团活动
    public static final int MSG_DEF_GROUP_BUY_ACTIVITY = 0x138;



}
