package com.xmd.technician.msgctrl;

/**
 * Created by sdcm on 15-10-22.
 */
public class ControllerRegister {

    public static void initialize(){
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
                MsgDef.MSG_DEF_LOGOUT,
                MsgDef.MSG_DEF_GET_TECH_EDIT_INFO,
                MsgDef.MSG_DEF_GET_ACCOUNT_MONEY,
                MsgDef.MSG_DEF_FILTER_ORDER_LIST,
                MsgDef.MSG_DEF_GET_ORDER_LIST,
                MsgDef.MSG_DEF_MANAGE_ORDER,
                MsgDef.MSG_DEF_HIDE_ORDER,
                MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT,
                MsgDef.MSG_DEF_SUBMIT_FEEDBACK,
                MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID,
                MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID,
                MsgDef.MSG_DEF_GET_CONSUME_DETAIL,
                MsgDef.MSG_DEF_GET_USER_COUPON_LIST,
                MsgDef.MSG_DEF_GET_USER_COUPON_VIEW,
                MsgDef.MSG_DEF_GET_COUPON_USE_DATA,
                MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST,
                MsgDef.MSG_DEF_GET_COMMENT_LIST,
                MsgDef.MSG_DEF_SUBMIT_INVITE_CODE,
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
                MsgDef.MSG_DEF_GET_TECH_CURRENT_INFO,
                MsgDef.MSG_DEF_RESET_PASSWORD,
                MsgDef.MSG_DEF_UPDATE_TECH_INFO,
                MsgDef.MSG_DEF_UPDATE_SERVICE_ITEM_LIST,
                MsgDef.MSG_DEF_GET_COUPON_LIST,
                MsgDef.MSG_DEF_GET_COUPON_INFO,
                MsgDef.MSG_DEF_GET_PAID_COUPON_USER_DETAIL,
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
        };
        MsgDispatcher.register(controllerId, msgIds);
    }

    private static void initChatController() {
        int controllerId = ControllerId.CHAT_CONTROLLER;
        int[] msgIds = {
                MsgDef.MSG_DEF_START_CHAT,
                MsgDef.MSG_DEF_SAVE_CHAT_USER,
                MsgDef.MSG_DEF_GET_CONVERSATION_LIST,
                MsgDef.MSG_DEF_SYSTEM_NOTICE_NOTIFY
        };
        MsgDispatcher.register(controllerId, msgIds);
    }
}
