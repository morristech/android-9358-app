package com.xmd.technician.msgctrl;

/**
 * Created by sdcm on 15-10-22.
 */
public class ControllerRegister {

    public static void initialize(){
        initUpgradeController();
        initRequestController();
        initShareController();
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
                MsgDef.MSG_DEF_GET_CLUB_INFO,
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
                MsgDef.MSG_DEF_GET_COUPON_USE_DATA
        };
        MsgDispatcher.register(controllerId, msgIds);
    }

    private static void initShareController() {
        int controllerId = ControllerId.SHARE_CONTROLLER;
        int[] msgIds = {
                MsgDef.MSG_DEF_SHARE_TO_FRIEND,
                MsgDef.MSG_DEF_SHARE_TO_TIMELINE,
                MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM
        };
        MsgDispatcher.register(controllerId, msgIds);
    }
}
