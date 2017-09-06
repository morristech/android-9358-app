package com.xmd.manager.common;


/**
 * Created by linms@xiaomodo.com on 16-5-23.
 */

import android.content.Context;

import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.xmd.manager.Constant;
import com.xmd.manager.beans.CheckInfo;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.service.RequestConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sdcm on 16-3-22.
 */
public class CommonUtils {



    public static void userGetCoupon(String actId, String channel, String emchatId, CouponInfo couponInfo) {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER_COUPON_ACT_ID, actId);
        params.put(RequestConstant.KEY_USER_COUPON_CHANEL, channel);
        params.put(RequestConstant.KEY_USER_COUPON_EMCHAT_ID, emchatId);
        params.put(RequestConstant.KEY_USER_COUPON_INFO, couponInfo);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_USER_GET_COUPON, params);
    }



    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }




    public static int verifyInfoTypeToViewType(int infoType) {
        switch (infoType) {
            case CheckInfo.INFO_TYPE_TICKET:
                return Constant.VERIFICATION_VIEW_COUPON;
            case CheckInfo.INFO_TYPE_ORDER:
                return Constant.VERIFICATION_VIEW_ORDER;
            default:
                return Constant.VERIFICATION_VIEW_COMMON;
        }
    }
}


