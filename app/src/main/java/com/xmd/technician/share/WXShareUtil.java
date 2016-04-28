package com.xmd.technician.share;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.xmd.technician.Constant;
import com.xmd.technician.TechApplication;
import com.xmd.technician.common.Utils;

import java.util.Map;

/**
 * Created by sdcm on 15-12-11.
 */
public class WXShareUtil {

    private IWXAPI mWxApi;
    private String mShareUrl;
    private String mShareTitle;
    private String mShareDescription;
    private Bitmap mShareThumbnail;

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

    private void explainParams(Map<String, Object> params) {
        Object url = params.get(Constant.PARAM_SHARE_URL);
        if (url != null) {
            mShareUrl = url.toString();
        }

        Object title = params.get(Constant.PARAM_SHARE_TITLE);
        if (title != null) {
            mShareTitle = title.toString();
        }

        Object description = params.get(Constant.PARAM_SHARE_DESCRIPTION);
        if (description != null) {
            mShareDescription = description.toString();
        }

        Object thumbnail = params.get(Constant.PARAM_SHARE_THUMBNAIL);
        if (thumbnail != null) {
            mShareThumbnail = (Bitmap) thumbnail;
        }

    }

    private void shareToWeiXin(Map<String, Object> params, int flag) {

        explainParams(params);

        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = mShareUrl;

        WXMediaMessage wxMediaMessage = new WXMediaMessage(webpageObject);
        if (mShareThumbnail != null) {
            wxMediaMessage.setThumbImage(mShareThumbnail);
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
