package com.xmd.manager.share;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.xmd.manager.ManagerApplication;

import java.util.Map;

/**
 * Created by sdcm on 15-12-11.
 */
public class WXShareUtil extends BaseShareUtil {

    private IWXAPI mWxApi;

    private static class InstanceHolder {
        private static WXShareUtil sInstance = new WXShareUtil();
    }

    private WXShareUtil() {
        mWxApi = WXAPIFactory.createWXAPI(ManagerApplication.getAppContext(), ShareConstant.WX_APP_ID);
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
}
