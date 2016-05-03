package com.xmd.technician.window;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CouponStatusFilter;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.BasePopupWindow;

import java.util.Map;

import butterknife.OnClick;

/**
 * Created by linms@xiaomodo.com on 16-5-3.
 */
public class PaidCouponUserDetailStatusPopupWindow extends BasePopupWindow {

    private String mActId;

    protected PaidCouponUserDetailStatusPopupWindow(View parentView, Map<String, String> params) {
        super(parentView, params);
        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.pw_paid_coupon_user_detail_status, null);
        initPopupWidnow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.anim_top_to_bottom_style);
    }

    @OnClick({R.id.coupo_status_all, R.id.coupon_status_accept, R.id.coupon_status_complete, R.id.coupon_status_expire})
    public void onClick(View v) {
        int couponStatus = Constant.COUPON_STATUS_ALL;
        String desc = ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_header_status);
        switch(v.getId()) {
            case R.id.coupo_status_all:
                couponStatus = Constant.COUPON_STATUS_ALL;
                break;
            case R.id.coupon_status_accept:
                couponStatus = Constant.COUPON_STATUS_ACCEPT;
                desc = ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_accept);
                break;
            case R.id.coupon_status_complete:
                couponStatus = Constant.COUPON_STATUS_COMPLETE;
                desc = ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_complete);
                break;
            case R.id.coupon_status_expire:
                couponStatus = Constant.COUPON_STATUS_EXPIRE;
                desc = ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_expire);
                break;
        }
        mParams.put(RequestConstant.KEY_COUPON_STATUS, String.valueOf(couponStatus));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PAID_COUPON_USER_DETAIL, mParams);
        RxBus.getInstance().post(new CouponStatusFilter(desc));
        dismiss();
    }
}
