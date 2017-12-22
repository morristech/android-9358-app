package com.xmd.technician.share;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;

import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.xmd.technician.Constant;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.widget.ShareCouponDialog;
import com.xmd.technician.window.SharePlatformPopupWindow;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdcm on 15-12-11.
 */
public class ShareController extends AbstractController {

    //actId  用于统计
    public static void doShare(String imageUrl, String userShareUrl, String title, String description, String type, String actId) {

        ThreadPoolManager.run(() -> {
            Bitmap thumbnail;
            if (Utils.isEmpty(imageUrl)) {
                thumbnail = ImageLoader.readBitmapFromImgUrl(SharedPreferenceHelper.getUserAvatar());
            } else {
                thumbnail = ImageLoader.readBitmapFromImgUrl(imageUrl);
            }
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> {
                Map<String, Object> params = new HashMap<>();
                params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
                params.put(Constant.PARAM_SHARE_URL, userShareUrl);
                params.put(Constant.PARAM_SHARE_TITLE, title);
                params.put(Constant.PARAM_SHARE_DESCRIPTION, description);
                params.put(Constant.PARAM_SHARE_TYPE, type);
                params.put(Constant.PARAM_ACT_ID, actId);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);

            });
        });
    }

    public static void doShareImage(String localImageUrl) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PARAM_SHARE_LOCAL_IMAGE, localImageUrl);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM:
                showPlatformWindow((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SHARE_TO_TIMELINE:
                shareToTimeline((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SHARE_TO_FRIEND:
                shareToFriends((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SHARE_TO_OTHER:
                shareToOther((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEG_SHARE_QR_CODE:
                showShareDialog((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SHARE_IMAGE_TO_TIMELINE:
                shareImageToTimeline((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SHARE_IMAGE_TO_FRIENDS:
                shareImageToFriends((Map<String, Object>) msg.obj);
                break;
        }

        return true;
    }

    private void showPlatformWindow(Map<String, String> params) {
        SharePlatformPopupWindow popupWindow = new SharePlatformPopupWindow(params);
        popupWindow.showAtBottom();
    }

    private void showShareDialog(Map<String, Object> params) {
        ShareCouponDialog dialog = new ShareCouponDialog((Context) params.get("context"), params);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * @param params
     */
    private void shareToOther(Map<String, Object> params) {
        OtherShareUtil.getInstance().share(params);
    }

    /**
     * 分享到朋友圈
     *
     * @param params
     */
    private void shareToTimeline(Map<String, Object> params) {
        WXShareUtil.getInstance().shareToTimeLine(params);
    }

    /**
     * 分享给朋友
     *
     * @param params
     */
    private void shareToFriends(Map<String, Object> params) {
        WXShareUtil.getInstance().shareToFriends(params);
    }

    /**
     * 分享到朋友圈
     *
     * @param params
     */
    private void shareImageToTimeline(Map<String, Object> params) {
        WXShareUtil.getInstance().shareImageToTimeLine((String) params.get(Constant.PARAM_SHARE_LOCAL_IMAGE));
    }

    /**
     * 分享给朋友
     *
     * @param params
     */
    private void shareImageToFriends(Map<String, Object> params) {
        WXShareUtil.getInstance().shareImageToFriends((String) params.get(Constant.PARAM_SHARE_LOCAL_IMAGE));
    }

}
