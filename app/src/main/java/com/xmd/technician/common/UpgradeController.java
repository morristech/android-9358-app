package com.xmd.technician.common;

import android.os.Message;

import com.widget.sdcm.appgradelib.AppUpgrade;
import com.xmd.technician.TechApplication;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;


/**
 * Created by sdcm on 15-10-22.
 */
public class UpgradeController extends AbstractController {

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MsgDef.MSG_DEF_AUTO_CHECK_UPGRADE:
                autoCheckUpdate();
                break;
            case MsgDef.MSG_DEF_MANUALLY_CHECK_UPGRADE:
                manuallyCheckUpdate();
                break;
        }

        return true;
    }

    private void autoCheckUpdate() {
        AppUpgrade.getInstance().autoUpdate(TechApplication.getAppContext(), new AppUpgrade.CallBack() {
            @Override
            public void exitApplication() {

            }
        });
    }

    private void manuallyCheckUpdate() {
        AppUpgrade.getInstance().manuallyUpdate(TechApplication.getAppContext());
    }
}
