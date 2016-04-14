package com.xmd.technician.msgctrl;


import com.xmd.technician.chat.ChatController;
import com.xmd.technician.common.UpgradeController;
import com.xmd.technician.http.RequestController;

/**
 * Created by sdcm on 15-10-22.
 */
public class ControllerFactory {

    private static UpgradeController sUpgradeController;
    private static RequestController sRequestController;
    private static ChatController sChatController;

    public static AbstractController createController(int controllerId) {
        switch (controllerId) {
            case ControllerId.UPGRADE_CONTROLLER:
                if (sUpgradeController == null) {
                    sUpgradeController = new UpgradeController();
                }
                return sUpgradeController;
            case ControllerId.REQUEST_CONTROLLER:
                if (sRequestController == null) {
                    sRequestController = new RequestController();
                }
                return sRequestController;
            case ControllerId.CHAT_CONTROLLER:
                if (sChatController == null) {
                    sChatController = new ChatController();
                }
                return sChatController;
        }
        return null;
    }
}
