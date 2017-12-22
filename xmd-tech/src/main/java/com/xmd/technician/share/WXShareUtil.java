package com.xmd.technician.share;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.FileUtils;
import com.xmd.technician.http.gson.RolePermissionListResult;

import java.io.File;
import java.util.Map;

/**
 * Created by sdcm on 15-12-11.
 */
public class WXShareUtil extends BaseShareUtil {

    private static final int THUMB_SIZE = 150;
    private IWXAPI mWxApi;

    private WXShareUtil() {
        mWxApi = WXAPIFactory.createWXAPI(TechApplication.getAppContext(), ShareConstant.WX_APP_ID);
        mWxApi.registerApp(ShareConstant.WX_APP_ID);
    }

    public static WXShareUtil getInstance() {
        return InstanceHolder.sInstance;
    }

    public void shareToTimeLine(Map<String, Object> params) {
        shareToWeiXin(params, ShareConstant.SHARE_TO_TIMELINE);
    }

    public void shareToFriends(Map<String, Object> params) {
        shareToWeiXin(params, ShareConstant.SHARE_TO_FRIEND);
    }

    public void shareImageToTimeLine(String localImageUrl) {
        shareToWeiXinImage(localImageUrl, ShareConstant.SHARE_TO_TIMELINE);
    }

    public void shareImageToFriends(String localImageUrl) {
        shareToWeiXinImage(localImageUrl, ShareConstant.SHARE_TO_FRIEND);
    }

    private void shareToWeiXin(Map<String, Object> params, int flag) {

        explainParams(params);

        WXWebpageObject webpageObject = new WXWebpageObject();

        webpageObject.webpageUrl = mShareUrl;

        WXMediaMessage wxMediaMessage = new WXMediaMessage(webpageObject);
        if (mShareThumbnail != null) {
            wxMediaMessage.setThumbImage(mShareThumbnail);
            if (wxMediaMessage.thumbData.length > 1024 * 32) {
                wxMediaMessage.thumbData = null;
            }
        }
        wxMediaMessage.title = mShareTitle;
        wxMediaMessage.description = mShareDescription;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;
        req.scene = flag == ShareConstant.SHARE_TO_FRIEND ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        mWxApi.sendReq(req);

    }

    private void shareToWeiXinImage(String imagePath, int flag) {
        File file = new File(imagePath);
        if (!file.exists()) {
            String tip = "文件不存在";
            XToast.show(tip + "path= " + imagePath);
            return;
        }
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(imagePath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        Bitmap thumBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        bitmap.recycle();
        msg.thumbData = FileUtils.bmpToByteArray(thumBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == ShareConstant.SHARE_TO_FRIEND ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        mWxApi.sendReq(req);
    }

    public void loginWX() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        mWxApi.sendReq(req);
    }

    private static class InstanceHolder {
        private static WXShareUtil sInstance = new WXShareUtil();
    }


}
