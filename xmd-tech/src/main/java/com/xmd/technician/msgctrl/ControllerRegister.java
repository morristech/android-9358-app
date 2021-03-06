package com.xmd.technician.msgctrl;

/**
 * Created by sdcm on 15-10-22.
 */
public class ControllerRegister {

    public static void initialize() {
        initUpgradeController();
        initRequestController();
        initShareController();
        initChatController();
    }

    private static void initUpgradeController() {
        int controllerId = ControllerId.UPGRADE_CONTROLLER;
        int[] msgIds = {
                MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE,
                MsgDef.MSG_DEF_MANUALLY_CHECK_UPGRADE
        };
        MsgDispatcher.register(controllerId, msgIds);
    }

    private static void initRequestController() {
        int controllerId = ControllerId.REQUEST_CONTROLLER;
        int[] msgIds = {
                MsgDef.MSG_DEF_GET_CLUB_COUPON_LIST,
                MsgDef.MSG_DEF_TOKEN_EXPIRE,
                MsgDef.MSG_DEF_LOGIN,
                MsgDef.MSG_DEF_LOGIN_BY_TECH_NO,
                MsgDef.MSG_DEF_GET_UNUSED_TECH_NO,
                MsgDef.MSG_DEF_LOGOUT,
                MsgDef.MSG_DEF_GET_TECH_EDIT_INFO,
                MsgDef.MSG_DEF_GET_ACCOUNT_MONEY,
                MsgDef.MSG_DEF_FILTER_ORDER_LIST,
                MsgDef.MSG_DEF_MANAGE_ORDER,
                MsgDef.MSG_DEF_HIDE_ORDER,
                MsgDef.MSG_DEF_GET_TECH_PERSONAL_DATA,
                MsgDef.MSG_DEF_SUBMIT_FEEDBACK,
                MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID,
                MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID,
                MsgDef.MSG_DEF_GET_CONSUME_DETAIL,
                MsgDef.MSG_DEF_GET_USER_COUPON_LIST,
                MsgDef.MSG_DEF_GET_USER_COUPON_VIEW,
                MsgDef.MSG_DEF_GET_COUPON_USE_DATA,
                MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST,
                MsgDef.MSG_DEF_JOIN_CLUB,
                MsgDef.MSG_DEF_GET_ICODE,
                MsgDef.MSG_DEF_REGISTER,
                MsgDef.MSG_DEF_GET_WORK_TIME,
                MsgDef.MSG_DEF_UPDATE_WORK_TIME,
                MsgDef.MSG_DEF_UPLOAD_AVATAR,
                MsgDef.MSG_DEF_DELETE_ALBUM,
                MsgDef.MSG_DEF_SORT_ALBUM,
                MsgDef.MSG_DEF_UPLOAD_ALBUM,
                MsgDef.MSG_DEF_MODIFY_PASSWORD,
                MsgDef.MSG_DEF_UPDATE_WORK_STATUS,
                MsgDef.MSG_DEF_RESET_PASSWORD,
                MsgDef.MSG_DEF_UPDATE_TECH_INFO,
                MsgDef.MSG_DEF_UPDATE_SERVICE_ITEM_LIST,
                MsgDef.MSG_DEF_GET_COUPON_LIST,
                MsgDef.MSG_DEF_GET_COUPON_INFO,
                MsgDef.MSG_DEF_GET_PAID_COUPON_USER_DETAIL,
                MsgDef.MSG_DEF_COUPON_SHARE_EVENT_COUNT,
                MsgDef.MSG_DEF_QUIT_CLUB,
                MsgDef.MSG_DEF_ADD_OR_EDIT_CUSTOMER,
                MsgDef.MSG_DEF_GET_CUSTOMER_LIST,
                MsgDef.MSG_DEF_GET_CUSTOMER_INFO_DETAIL,
                MsgDef.MSG_DEF_GET_APP_UPDATE_CONFIG,
                MsgDef.MSG_DEF_DELETE_CONTACT,
                MsgDef.MSG_DEF_DO_DRAW_MONEY,
                MsgDef.MSG_DEF_GET_USER_RECORDE,
                MsgDef.MSG_DEF_GET_CREDIT_STATUS,
                MsgDef.MSG_DEF_GET_CREDIT_ACCOUNT,
                MsgDef.MSG_DEF_DO_CREDIT_EXCHANGE,
                MsgDef.MSG_DEF_DO_INITIATE_GAME,
                MsgDef.MSG_DEF_DO_GAME_ACCEPT_OR_REJECT,
                MsgDef.MSG_DEF_GET_CREDIT_APPLICATIONS,
                MsgDef.MSG_DEF_GET_USER_CLUB_SWITCHES,
                MsgDef.MSG_DEF_GET_RECENTLY_VISITOR,
                MsgDef.MSG_DEF_GET_CREDIT_GIFT_LIST,
                MsgDef.MSG_DEF_DO_SAY_HI,
                MsgDef.MSG_DEF_GET_VISIT_VIEW,
                MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT,
                MsgDef.MSF_DEF_GET_TECH_INFO,
                MsgDef.MSF_DEF_GET_TECH_ORDER_LIST,
                MsgDef.MSF_DEF_GET_TECH_STATISTICS_DATA,
                MsgDef.MSF_DEF_GET_TECH_RANK_INDEX_DATA,
                MsgDef.MSF_DEF_GET_TECH_DYNAMIC_LIST,
                MsgDef.MSF_DEF_SET_PAGE_SELECTED,
                MsgDef.MSG_DEF_ORDER_INNER_READ,
                MsgDef.MSG_DEF_USER_GET_COUPON,
                MsgDef.MSG_DEF_GET_PAY_NOTIFY,
                MsgDef.MSG_DEF_GET_CARD_SHARE_LIST,
                MsgDef.MSG_DEF_GET_ACTIVITY_LIST,
                MsgDef.MSG_DEF_GET_PROPAGANDA_LIST,
                MsgDef.MSG_DEF_GET_CARD_LIST_DETAIL,
                MsgDef.MSG_DEF_GET_ONCE_CARD_LIST_DETAIL,
                MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST_DETAIL,
                MsgDef.MSG_DEF_GET_REWARD_ACTIVITY_LIST,
                MsgDef.MSG_DEF_GET_INVITATION_REWARD_ACTIVITY_LIST,
                MsgDef.MSG_DEF_GET_CLUB_JOURNAL_LIST,
                MsgDef.MSG_DEF_GET_PAY_FOR_ME_LIST_DETAIL,
                MsgDef.MSG_DEF_JOURNAL_SHARE_COUNT,
                MsgDef.MSG_DEF_GET_ROLE_PERMISSION,
                MsgDef.MSG_DEF_GET_ROLE_LIST,

                // -------------------------------------> 附近的人 <---------------------------------
                MsgDef.MSG_DEF_GET_CLUB_POSITION_INFO,
                MsgDef.MSG_DEF_GET_NEARBY_CUS_COUNT,
                MsgDef.MSG_DEF_GET_NEARBY_CUS_LIST,
                MsgDef.MSG_DEF_GET_HELLO_LEFT_COUNT,
                MsgDef.MSG_DEF_GET_HELLO_RECORD_LIST,
                MsgDef.MSG_DEF_GET_CONTACT_PERMISSION,
                MsgDef.MSG_DEF_GET_SET_TEMPLATE,
                MsgDef.MSG_DEF_SAVE_SET_TEMPLATE,
                MsgDef.MSG_DEF_GET_SYS_TEMPLATE_LIST,
                MsgDef.MSG_DEF_UPLOAD_HELLO_TEMPLATE_IMG,
                MsgDef.MSG_DEF_DOWNLOAD_HELLO_IMAGE_CACHE,
                MsgDef.MSG_DEF_TECH_ACCOUNT_LIST,
                //-------------------------------------> 技师pk排行榜 <---------------------------------
                MsgDef.MSG_DEF_TECH_PK_RANKING,
                MsgDef.MSG_DEF_TECH_PK_ACTIVITY_LIST,
                MsgDef.MSG_DEF_TECH_PK_TEAM_RANKING_LIST,
                MsgDef.MSG_DEF_TECH_PK_PERSONAL_RANKING_LIST,
                MsgDef.MSG_DEF_TECH_RANKING_LIST,
                //---------聊天优化---
                MsgDef.MSG_DEF_MARK_CHAT_TO_USER,
                MsgDef.MSG_DEF_GET_CHAT_CATEGORY_LIST,
                MsgDef.MSG_DEF_GET_TECH_MARKETING_LIST,

                //-------------------------------------> 技师海报 <---------------------------------
                MsgDef.MSG_DEF_TECH_POSTER_SAVE,
                MsgDef.MSG_DEF_TECH_POSTER_DELETE,
                MsgDef.MSG_DEF_TECH_POSTER_IMAGE_UPLOAD,
                MsgDef.MSG_DEF_TECH_POSTER_LIST,
                MsgDef.MSG_DEF_TECH_POSTER_DETAIL,
                //-------------------------------------> 技师订单数量 <---------------------------------
                MsgDef.MSG_DEF_TECH_ORDER_COUNT,
                //-------------------------------------> 技师入职 <---------------------------------
                MsgDef.MSG_DEF_TECH_AUDIT_MODIFY,
                MsgDef.MSG_DEF_TECH_AUDIT_CANCEL,
                MsgDef.MSG_DEF_TECH_AUDIT_CONFIRM,
                //-------------------------------------> 分享统计 <---------------------------------
                MsgDef.MSG_DEF_TECH_SHARE_COUNT_UPDATE,
                //-------------------------------------> 提现规则 <---------------------------------
                MsgDef.MSG_DEF_TECH_WITHDRAW_RULE,
                MsgDef.MSG_DEF_GROUP_BUY_ACTIVITY


        };
        MsgDispatcher.register(controllerId, msgIds);
    }

    private static void initShareController() {
        int controllerId = ControllerId.SHARE_CONTROLLER;
        int[] msgIds = {
                MsgDef.MSG_DEF_SHARE_TO_FRIEND,
                MsgDef.MSG_DEF_SHARE_TO_TIMELINE,
                MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM,
                MsgDef.MSG_DEF_SHARE_TO_OTHER,
                MsgDef.MSG_DEG_SHARE_QR_CODE,
                MsgDef.MSG_DEF_SHARE_IMAGE_TO_FRIENDS,
                MsgDef.MSG_DEF_SHARE_IMAGE_TO_TIMELINE

        };
        MsgDispatcher.register(controllerId, msgIds);
    }

    private static void initChatController() {
        int controllerId = ControllerId.CHAT_CONTROLLER;
        int[] msgIds = {
                MsgDef.MSG_DEF_START_CHAT,
                MsgDef.MSG_DEF_SAVE_CHAT_USER,
                MsgDef.MSG_DEF_GET_CONVERSATION_LIST,
                MsgDef.MSG_DEF_SYSTEM_NOTICE_NOTIFY,
                MsgDef.MSG_DEF_LOGIN_EMCHAT,
                MsgDef.MSG_DEG_DELETE_CONVERSATION_FROM_DB,
                MsgDef.MSG_DEF_LOGOUT_EMCHAT
        };
        MsgDispatcher.register(controllerId, msgIds);
    }
}
