package com.xmd.manager.msgctrl;

/**
 * Created by sdcm on 15-10-22.
 */
public class MsgDef {

    public static final int MSG_DEF_AUTO_CHECK_UPGRADE = 0x0001;

    public static final int MSG_DEF_MANUALLY_CHECK_UPGRADE = 0x0002;

    public static final int MSG_DEF_LOGIN = 0x0003;

    public static final int MSG_DEF_LOGOUT = 0x0004;

    public static final int MSG_DEF_GET_CLUB_INFO = 0x0009;

    public static final int MSG_DEF_GET_CLUB_COUPON_LIST = 0x0005;

    public static final int MSG_DEF_GET_USER_COUPON_LIST = 0x0006;

    public static final int MSG_DEF_USE_COUPON = 0x0008;

    public static final int MSG_DEF_TOKEN_EXPIRE = 0x0007;

    public static final int MSG_DEF_GET_NEW_ORDER_COUNT = 0x000B;

    public static final int MSG_DEF_FILTER_ORDER_LIST = 0x000C;

    public static final int MSG_DEF_GET_ORDER_LIST = 0x000D;

    public static final int MSG_DEF_MANAGE_ORDER = 0x000E;

    public static final int MSG_DEF_SUBMIT_FEEDBACK = 0x000F;

    public static final int MSG_DEF_GETUI_BIND_CLIENT_ID = 0x0010;

    public static final int MSG_DEF_SHARE_TO_FRIEND = 0x0110;

    public static final int MSG_DEF_SHARE_TO_TIMELINE = 0x0111;

    public static final int MSG_DEF_SHARE_TO_OTHER = 0x0112;

    public static final int MSG_DEF_SHOW_SHARE_PLATFORM = 0x0013;

    public static final int MSG_DEF_GET_CLUB_COUPON_VIEW = 0x0014;

    public static final int MSG_DEF_GET_USER_COUPON_VIEW = 0x0015;

    public static final int MSG_DEF_GET_COUPON_USE_DATA = 0x0016;

    public static final int MSG_DEF_STAT_APP_START = 0x0017;

    public static final int MSG_DEF_GETUI_UNBIND_CLIENT_ID = 0x0018;

    public static final int MSG_DEF_GET_PAID_ORDER_LIST = 0x0019;

    public static final int MSG_DEF_PAID_ORDER_USE = 0x0020;

    public static final int MSG_DEF_PAID_ORDER_VIEW = 0x0021;

    public static final int MSG_DEF_CHECK_PAID_ORDER_SWITCH = 0x0022;

    public static final int MSG_DEF_GET_CUSTOMER_LIST = 0x0023;

    public static final int MSG_DEF_SEARCH_CUSTOMERS = 0x0231;

    /**
     * ChatController, start to chat with someone
     */
    public static final int MSG_DEF_START_CHAT = 0x0024;
    public static final int MSG_DEF_SAVE_CHAT_USER = 0x0241;
    public static final int MSG_DEF_GET_CONVERSATION_LIST = 0x0242;
    public static final int MSG_DEF_GET_CHAT_USER = 0x0243;

    public static final int MSG_DEF_GET_CONVERSATION_LIST_FROM_DB = 0x025;


    public static final int MSG_DEF_GET_COUPON_LIST = 0x0026;


    public static final int MSG_DEF_GET_CUSTOMER_COMMENTS = 0x0027;
    public static final int MSG_DEF_GET_CUSTOMER_COUPONS = 0x0028;
    public static final int MSG_DEF_GET_CUSTOMER_ORDERS = 0x0029;

    public static final int MSG_DEF_GET_STATISTICS_HOME_DATA = 0x0030;

    public static final int MSG_DEF_GET_STATISTICS_MAIN_PAGE_DATA = 0x0031;

    public static final int MSG_DEF_DELETE_USE_COMMENT = 0x0032;


    public static final int MSG_DEF_GET_ORDER_OR_COUPON_VIEW = 0x0034;

    public static final int MSG_DEF_LOGIN_EMCHAT = 0x0035;

    public static final int MSG_DEF_LOGOUT_EMCHAT = 0x0036;
    public static final int MSG_DEF_CUSTOMER_SORT_COMPLETED = 0x0037;
    public static final int MSG_DEF_GET_ORDER_DETAIL = 0X0038;
    public static final int MSG_DEF_GET_DELIVERY_DETAIL = 0X0039;
    public static final int MSG_DEG_GET_REGISTER_DETAIL = 0X0040;

    public static final int MSG_DEG_DELETE_CONVERSATION_FROM_DB = 0X0041;

    public static final int MSG_DEF_GET_APP_UPDATE_CONFIG = 0x1000;

    public static final int MSG_DEF_GET_CLUB_LIST = 0x0042;

    public static final int MSG_DEF_ENTER_CLUB_VIEW = 0x0043;
    public static final int MSG_DEF_PAY_DETAIL = 0x0044;
    public static final int MSG_DEF_PAY_BY_CONSUME = 0x0045;
    public static final int MSG_DEF_COUPON_CHECK = 0x0046;

    public static final int MSG_DEF_GET_TECH_CUSTOMER_LIST = 0x0047;

    public static final int MSG_DEF_FILTER_CUSTOMER_TYPE = 0x0049;

    public static final int MSG_DEF_GET_AUTH_CONFIG = 0x0050;
    public static final int MSG_DEF_GET_WIFI_REPORT = 0x0051;
    public static final int MSG_DEF_GET_REGISTER_REPORT = 0x0052;
    public static final int MSG_DEF_GET_VISIT_REPORT = 0x0053;
    public static final int MSG_DEF_GET_COUPON_REPORT = 0x0054;
    public static final int MSG_DEF_GET_VISIT_LIST = 0x0055;
    public static final int MSG_DEF_GROUP_MESSAGE_SEND = 0x0056;
    public static final int MSG_DEF_GET_GROUP_LIST = 0x0057;
    public static final int MSG_DEF_GET_GROUP_INFO = 0x0058;

    public static final int MSG_DEF_GET_WIFI_DATA = 0x0059;
    public static final int MSG_DEF_GET_COUPON_DATA_INDEX = 0x0060;
    public static final int MSG_DEF_GET_REGISTRY_DATA = 0x0061;
    public static final int MSG_DEF_GET_CLUB_VISIT_DATA = 0x0062;
    public static final int MSG_DEF_GET_ORDER_DATA = 0x0063;
    public static final int MSG_DEF_GET_TECH_RANK_DATA = 0x0064;
    public static final int MSG_DEF_GET_CLUB_TECH_LIST = 0x0048;
    public static final int MSG_DEF_GET_CUSTOMER_VIEW = 0x0033;
    public static final int MSG_DEF_GET_ClUB_FAVOURABLE_ACTIVITY = 0x0065;


    public static final int MSG_DEF_GET_MARKETING_ITEMS = 0x0067;
    public static final int MSG_DEF_GET_SERVICE_ITEMS_LIST = 0x0068;
    public static final int MSG_DEF_MODIFY_PASSWORD = 0x0069;
    public static final int MSG_DEF_GET_BAD_COMMENT_LIST = 0x0070;
    public static final int MSG_DEF_BAD_COMMENT_DETAIL = 0x0071;
    public static final int MSG_DEF_CHANGE_COMMENT_STATUS = 0x0072;
    public static final int MSG_DEF_TECH_BAD_COMMENT = 0x0073;
    public static final int MSG_DEG_CUSTOMER_DETAIL_VIEW = 0x0074;

    public static final int MSG_DEF_GET_CHECK_INFO_TYPE_GET = 0x0076;
    public static final int MSG_DEF_GET_PAY_ORDER_DETAIL = 0x0077;
    public static final int MSG_DEF_GET_CHECK_INFO_COUPON_DETAIL = 0x0078;
    public static final int MSG_DEF_GET_VERIFICATION_COMMON_DETAIL = 0x79;
    public static final int MSG_DEF_CHECK_INFO_ORDER_SAVE = 0x0080;
    public static final int MSG_DEF_DO_VERIFICATION_COMMON_SAVE = 0x0081;
    public static final int MSG_DEF_DO_VERIFICATION_COUPON_SAVE = 0x0082;
    public static final int MSG_DEF_DO_VERIFICATION_SERVICE_ITEM_COUPON_SAVE = 0x0083;
    public static final int MSG_DEF_DO_VERIFICATION_SERVICE_ITEM_COUPON = 0x0084;
    public static final int MSG_DEF_GET_VERIFICATION_AWARD_DETAIL = 0x0085;
    public static final int MSG_DEF_DO_VERIFICATION_AWARD_SAVE = 0x0086;
    public static final int MSG_DEF_GET_SEARCH_ORDER_LIST = 0x0087;
    public static final int MSG_DEF_SWITCH_INDEX = 0x0088;
    public static final int MSG_DEF_GET__INDEX_ORDER_DATA = 0x0089;

    public static final int MSG_DEF_GET_ClUB_GROUP_LIST = 0x0090;
    public static final int MSG_DEF_DO_GROUP_SAVE = 0x0091;
    public static final int MSG_DEF_GET_GROUP_USER_LIST = 0x0092;
    public static final int MSG_DEF_DO_GROUP_DELETE = 0x0093;
    public static final int MSG_DEF_GET_GROUP_DETAILS = 0x0094;
    public static final int MSG_DEF_DO_USER_ADD_GROUP = 0x0095;
    public static final int MSG_DEF_DO_USER_EDIT_GROUP = 0x0096;
    public static final int MSG_DEF_DO_GROUP_MESSAGE_ALBUM_UPLOAD = 0x0097;
    public static final int MSG_DEF_USER_GET_COUPON = 0x0098;
    public static final int MSG_DEF_GET_USER_REGISTER_LIST = 0x0099;
    public static final int MSG_DEF_CHECK_INFO_RECORD_DETAIL = 0x0100;
    public static final int MSG_DEF_CHECK_INFO_RECORD_LIST = 0x0101;
    public static final int MSG_DEF_CHECK_INFO_TYPE_LIST = 0x0102;
    public static final int MSG_DEF_APP_COMMENT_LIST = 0x0103;
    public static final int MSG_DEF_DATA_STATISTICS_WIFI_DATA = 0x0104;
    public static final int MSG_DEF_DATA_STATISTICS_ACCOUNT_DATA = 0x105;
    public static final int MSG_DEF_FAST_PAY_ORDER_LIST = 0x106;
    public static final int MSG_DEF_DATA_STATISTICS_SALE_DATA = 0x107;
    // ------------------------------------------> 技师排行榜 <----------------------------------------
    public static final int MSG_DEF_TECH_PK_RANKING = 0x00121;
    public static final int MSG_DEF_TECH_PK_ACTIVITY_LIST = 0x0122;
    public static final int MSG_DEF_TECH_PK_TEAM_RANKING_LIST = 0x0123;
    public static final int MSG_DEF_TECH_PK_PERSONAL_RANKING_LIST = 0x0124;
    public static final int MSG_DEF_TECH_RANKING_LIST = 0x0125;
    public static final int MSG_DEF_SAVE_CHAT_TO_CHONTACT = 0x0126;

    //使用手机号查询核销信息
    public static final int MSG_DEF_GET_CHECK_INFO = 0x0127;

    //会所分组标签列表
    public static final int MSG_DEF_GET_GROUP_TAG_LIST = 0x0128;

    //群发效果分析显示开关
    public static final int MSG_DEF_GET_GROUP_STAT_SWITCH = 0x0129;
}
