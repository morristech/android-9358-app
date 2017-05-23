package com.xmd.manager.msgctrl;

import com.xmd.manager.ManagerApplication;
import com.xmd.manager.chat.ChatController;
import com.xmd.manager.service.RequestController;
import com.xmd.manager.share.ShareController;
import com.xmd.manager.stat.StatController;
import com.xmd.manager.upgrade.UpgradeController;

/**
 * Created by sdcm on 15-10-22.
 */
public class ControllerFactory {

    private static UpgradeController sUpgradeController;
    private static RequestController sRequestController;
    private static ShareController sShareController;
    private static StatController sStatController;
    private static ChatController sChatController;

    public static AbstractController createController(int controllerId) {
        switch (controllerId) {
            case ControllerId.UPGRADE_CONTROLLER:
                if (sUpgradeController == null) {
                    sUpgradeController = new UpgradeController();
                    sUpgradeController.init(ManagerApplication.getAppContext());
                }
                return sUpgradeController;

            case ControllerId.REQUEST_CONTROLLER:
                if (sRequestController == null) {
                    sRequestController = new RequestController();
                }
                return sRequestController;
            case ControllerId.SHARE_CONTROLLER:
                if (sShareController == null) {
                    sShareController = new ShareController();
                }
                return sShareController;
            case ControllerId.STAT_CONTROLLER:
                if (sStatController == null) {
                    sStatController = new StatController();
                }
                return sStatController;
            case ControllerId.CHAT_CONTROLLER:
                if (sChatController == null) {
                    sChatController = new ChatController();
                }
                return sChatController;
        }
        return null;
    }
}
