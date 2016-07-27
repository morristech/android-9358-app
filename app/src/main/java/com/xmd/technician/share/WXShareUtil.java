package com.xmd.technician.share;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xmd.technician.Constant;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.Utils;

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

    private void shareToWeiXin(Map<String, Object> params, int flag) {

        explainParams(params);

        WXWebpageObject webpageObject = new WXWebpageObject();

        webpageObject.webpageUrl = mShareUrl;

        WXMediaMessage wxMediaMessage = new WXMediaMessage(webpageObject);
        if (mShareThumbnail != null) {
            wxMediaMessage.setThumbImage(mShareThumbnail);
            if(wxMediaMessage.thumbData.length > 1024*32){
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
    public void loginWX(){
        SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            mWxApi.sendReq(req);
    }


}
