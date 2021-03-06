package com.xmd.technician.msgctrl;


import com.xmd.technician.TechApplication;
import com.xmd.technician.common.UpgradeController;
import com.xmd.technician.http.RequestController;
import com.xmd.technician.share.ShareController;

/**
 * Created by sdcm on 15-10-22.
 */
public class ControllerFactory {

    private static UpgradeController sUpgradeController;
    private static RequestController sRequestController;
    private static ShareController sShareController;

    public static AbstractController createController(int controllerId) {
        switch (controllerId) {
            case ControllerId.UPGRADE_CONTROLLER:
                if (sUpgradeController == null) {
                    sUpgradeController = new UpgradeController();
                    sUpgradeController.init(TechApplication.getAppContext());
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
        }
        return null;
    }
}
