package com.xmd.technician.share;

import android.os.Message;

import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.window.SharePlatformPopupWindow;

import java.util.Map;

/**
 * Created by sdcm on 15-12-11.
 */
public class ShareController extends AbstractController {

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM:
                showPlatfromWindow((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SHARE_TO_TIMELINE:
                shareToTimeline((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SHARE_TO_FRIEND:
                shareToFriends((Map<String, Object>) msg.obj);
                break;
        }

        return true;
    }

    private void showPlatfromWindow(Map<String, String> params) {
        SharePlatformPopupWindow popupWindow = new SharePlatformPopupWindow(params);
        popupWindow.showAtBottom();
    }

    /**
     * 分享到朋友圈
     * @param params
     */
    private void shareToTimeline(Map<String, Object> params) {
        WXShareUtil.getInstance().shareToTimeLine(params);
    }

    /**
     * 分享给朋友
     * @param params
     */
    private void shareToFriends(Map<String, Object> params) {
        WXShareUtil.getInstance().shareToFriends(params);
    }

}
