package com.xmd.manager.msgctrl;

/**
 * Created by sdcm on 15-10-22.
 */
public class ControllerRegister {

    public static void initialize() {
        initUpgradeController();
        initRequestController();
        initShareController();
        initStatController();
        initChatController();
    }

    private static void initUpgradeController() {
        initController(ControllerId.UPGRADE_CONTROLLER, new int[]{
                MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE,
                MsgDef.MSG_DEF_MANUALLY_CHECK_UPGRADE
        });
    }

    private static void initRequestController() {
        initController(ControllerId.REQUEST_CONTROLLER, new int[]{
                MsgDef.MSG_DEF_GET_CLUB_COUPON_LIST,
                MsgDef.MSG_DEF_TOKEN_EXPIRE,
                MsgDef.MSG_DEF_LOGIN,
                MsgDef.MSG_DEF_LOGOUT,
                MsgDef.MSG_DEF_GET_CLUB_INFO,
                MsgDef.MSG_DEF_GET_CLUB_LIST,
                MsgDef.MSG_DEF_ENTER_CLUB_VIEW,
                MsgDef.MSG_DEF_USE_COUPON,
                MsgDef.MSG_DEF_FILTER_ORDER_LIST,
                MsgDef.MSG_DEF_GET_ORDER_LIST,
                MsgDef.MSG_DEF_MANAGE_ORDER,
                MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT,
                MsgDef.MSG_DEF_SUBMIT_FEEDBACK,
                MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID,
                MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID,
                MsgDef.MSG_DEF_GET_CLUB_COUPON_VIEW,
                MsgDef.MSG_DEF_GET_USER_COUPON_LIST,
                MsgDef.MSG_DEF_GET_USER_COUPON_VIEW,
                MsgDef.MSG_DEF_GET_COUPON_USE_DATA,
                // Paid Order
                MsgDef.MSG_DEF_CHECK_PAID_ORDER_SWITCH,
                MsgDef.MSG_DEF_GET_PAID_ORDER_LIST,
                MsgDef.MSG_DEF_PAID_ORDER_USE,
                MsgDef.MSG_DEF_PAID_ORDER_VIEW,
                MsgDef.MSG_DEF_CUSTOMER_SORT_COMPLETED,
                MsgDef.MSG_DEF_GET_TECH_CUSTOMER_LIST,
                MsgDef.MSG_DEF_FILTER_CUSTOMER_TYPE,
                MsgDef.MSG_DEF_GET_COUPON_LIST,
                MsgDef.MSG_DEF_GET_CUSTOMER_COUPONS,
                MsgDef.MSG_DEF_GET_CUSTOMER_ORDERS,
                MsgDef.MSG_DEF_GET_STATISTICS_HOME_DATA,
                MsgDef.MSG_DEF_GET_STATISTICS_MAIN_PAGE_DATA,
                MsgDef.MSG_DEF_DELETE_USE_COMMENT,
                MsgDef.MSG_DEF_SEARCH_CUSTOMERS,
                MsgDef.MSG_DEF_GET_ORDER_OR_COUPON_VIEW,
                MsgDef.MSG_DEF_GET_ORDER_DETAIL,
                MsgDef.MSG_DEF_GET_DELIVERY_DETAIL,
                MsgDef.MSG_DEG_GET_REGISTER_DETAIL,
                MsgDef.MSG_DEF_GET_APP_UPDATE_CONFIG,
                MsgDef.MSG_DEF_PAY_DETAIL,
                MsgDef.MSG_DEF_PAY_BY_CONSUME,
                MsgDef.MSG_DEF_COUPON_CHECK,
                MsgDef.MSG_DEF_GET_AUTH_CONFIG,
                MsgDef.MSG_DEF_GET_WIFI_REPORT,
                MsgDef.MSG_DEF_GET_VISIT_REPORT,
                MsgDef.MSG_DEF_GET_COUPON_REPORT,
                MsgDef.MSG_DEF_GET_REGISTER_REPORT,
                MsgDef.MSG_DEF_GET_VISIT_LIST,
                MsgDef.MSG_DEF_GET_GROUP_LIST,
                MsgDef.MSG_DEF_GET_GROUP_INFO,
                MsgDef.MSG_DEF_GROUP_MESSAGE_SEND,
                MsgDef.MSG_DEF_GET_WIFI_DATA,
                MsgDef.MSG_DEF_GET_CLUB_VISIT_DATA,
                MsgDef.MSG_DEF_GET_REGISTRY_DATA,
                MsgDef.MSG_DEF_GET_COUPON_DATA_INDEX,
                MsgDef.MSG_DEF_GET_ORDER_DATA,
                MsgDef.MSG_DEF_GET_TECH_RANK_DATA,
                MsgDef.MSG_DEF_GET_CLUB_TECH_LIST,
                MsgDef.MSG_DEF_GET_CUSTOMER_VIEW,
                MsgDef.MSG_DEF_GET_MARKETING_ITEMS,
                MsgDef.MSG_DEF_GET_SERVICE_ITEMS_LIST,
                MsgDef.MSG_DEF_GET_ClUB_FAVOURABLE_ACTIVITY,
                MsgDef.MSG_DEF_GET_BAD_COMMENT_LIST,
                MsgDef.MSG_DEF_BAD_COMMENT_DETAIL,
                MsgDef.MSG_DEF_CHANGE_COMMENT_STATUS,
                MsgDef.MSG_DEF_TECH_BAD_COMMENT,
                MsgDef.MSG_DEF_MODIFY_PASSWORD,
                MsgDef.MSG_DEF_GET_CHECK_INFO_TYPE_GET,
                MsgDef.MSG_DEF_GET_PAY_ORDER_DETAIL,
                MsgDef.MSG_DEF_GET_CHECK_INFO_COUPON_DETAIL,
                MsgDef.MSG_DEF_GET_VERIFICATION_COMMON_DETAIL,
                MsgDef.MSG_DEF_CHECK_INFO_ORDER_SAVE,
                MsgDef.MSG_DEF_DO_VERIFICATION_COMMON_SAVE,
                MsgDef.MSG_DEF_DO_VERIFICATION_COUPON_SAVE,
                MsgDef.MSG_DEF_DO_VERIFICATION_SERVICE_ITEM_COUPON_SAVE,
                MsgDef.MSG_DEF_DO_VERIFICATION_SERVICE_ITEM_COUPON,
                MsgDef.MSG_DEF_GET_VERIFICATION_AWARD_DETAIL,
                MsgDef.MSG_DEF_DO_VERIFICATION_AWARD_SAVE,
                MsgDef.MSG_DEF_GET_SEARCH_ORDER_LIST,
                MsgDef.MSG_DEF_SWITCH_INDEX,
                MsgDef.MSG_DEF_GET__INDEX_ORDER_DATA,
                MsgDef.MSG_DEF_GET_ClUB_GROUP_LIST,
                MsgDef.MSG_DEF_DO_GROUP_SAVE,
                MsgDef.MSG_DEF_GET_GROUP_USER_LIST,
                MsgDef.MSG_DEF_GET_GROUP_TAG_LIST,
                MsgDef.MSG_DEF_DO_GROUP_DELETE,
                MsgDef.MSG_DEF_GET_GROUP_DETAILS,
                MsgDef.MSG_DEF_DO_USER_ADD_GROUP,
                MsgDef.MSG_DEF_DO_USER_EDIT_GROUP,
                MsgDef.MSG_DEF_DO_GROUP_MESSAGE_ALBUM_UPLOAD,
                MsgDef.MSG_DEF_USER_GET_COUPON,
                MsgDef.MSG_DEF_GET_USER_REGISTER_LIST,
                MsgDef.MSG_DEF_CHECK_INFO_RECORD_LIST,
                MsgDef.MSG_DEF_CHECK_INFO_RECORD_DETAIL,
                MsgDef.MSG_DEF_CHECK_INFO_TYPE_LIST,
                MsgDef.MSG_DEF_APP_COMMENT_LIST,
                MsgDef.MSG_DEF_DATA_STATISTICS_WIFI_DATA,
                MsgDef.MSG_DEF_DATA_STATISTICS_ACCOUNT_DATA,
                MsgDef.MSG_DEF_FAST_PAY_ORDER_LIST,
                MsgDef.MSG_DEF_DATA_STATISTICS_SALE_DATA,
                MsgDef.MSG_DEF_TECH_PK_RANKING,
                MsgDef.MSG_DEF_TECH_PK_ACTIVITY_LIST,
                MsgDef.MSG_DEF_TECH_PK_TEAM_RANKING_LIST,
                MsgDef.MSG_DEF_TECH_PK_PERSONAL_RANKING_LIST,
                MsgDef.MSG_DEF_TECH_RANKING_LIST,
                MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT,
                MsgDef.MSG_DEF_GET_CHECK_INFO
        });
    }

    private static void initShareController() {
        initController(ControllerId.SHARE_CONTROLLER, new int[]{
                MsgDef.MSG_DEF_SHARE_TO_FRIEND,
                MsgDef.MSG_DEF_SHARE_TO_TIMELINE,
                MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM,
                MsgDef.MSG_DEF_SHARE_TO_OTHER
        });
    }

    private static void initStatController() {
        initController(ControllerId.STAT_CONTROLLER, new int[]{
                MsgDef.MSG_DEF_STAT_APP_START
        });
    }

    private static void initChatController() {
        initController(ControllerId.CHAT_CONTROLLER, new int[]{
                MsgDef.MSG_DEF_START_CHAT,
                MsgDef.MSG_DEF_SAVE_CHAT_USER,
                MsgDef.MSG_DEF_GET_CONVERSATION_LIST,
                MsgDef.MSG_DEF_GET_CHAT_USER,
                MsgDef.MSG_DEF_GET_CONVERSATION_LIST_FROM_DB,
                MsgDef.MSG_DEF_LOGIN_EMCHAT,
                MsgDef.MSG_DEF_LOGOUT_EMCHAT,
                MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB
        });
    }


    private static void initController(int controllerId, int[] msgIds) {
        MsgDispatcher.register(controllerId, msgIds);
    }
}
