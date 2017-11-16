package com.xmd.manager.common;

import com.xmd.manager.Constant;
import com.xmd.manager.beans.CouponBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-11-15.
 */

public class CouponDataFilterManager {
    private List<CouponBean> mOnlineCoupons;
    private List<CouponBean> mOfflineCoupons;
    private static CouponDataFilterManager couponDataFilterManager;
    private CouponBean mSelectedCouponBean;
    private int mSelectedCouponPosition;

    public CouponDataFilterManager() {
        if (mOnlineCoupons == null) {
            mOnlineCoupons = new ArrayList<>();
        } else {
            mOnlineCoupons.clear();
        }
        if (mOfflineCoupons == null) {
            mOfflineCoupons = new ArrayList<>();
        } else {
            mOfflineCoupons.clear();
        }
    }

    public static CouponDataFilterManager getCouponFilterManagerInstance() {
        if (couponDataFilterManager == null) {
            couponDataFilterManager = new CouponDataFilterManager();
        }
        return couponDataFilterManager;
    }

    public void setOnlineCoupons(List<CouponBean> onlineData) {
        this.mOnlineCoupons = onlineData;
        for (CouponBean bean : mOnlineCoupons) {
            bean.isUsable = Constant.COUPON_ONLINE_TRUE;
        }
    }

    public void setOfflineCoupons(List<CouponBean> offlineCoupons) {
        this.mOfflineCoupons = offlineCoupons;
        for (CouponBean bean : mOfflineCoupons) {
            bean.isUsable = Constant.COUPON_ONLINE_FALSE;
        }
    }

    public List<CouponBean> getOnlineCoupons() {
        return mOnlineCoupons;
    }

    public List<CouponBean> getOfflineCoupons() {
        return mOfflineCoupons;
    }

    public void clearAllSetting() {
        for (CouponBean bean : mOnlineCoupons) {
            bean.isSelected = Constant.COUPON_IS_SELECTED_FALSE;
        }
        for (CouponBean bean : mOfflineCoupons) {
            bean.isSelected = Constant.COUPON_IS_SELECTED_FALSE;
        }
    }

    public boolean isNeedLoadData() {
        if (mOfflineCoupons.size() == 0 && mOfflineCoupons.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setSelectedCouponBeanPosition(int position) {
        mSelectedCouponPosition = position;
    }

    public void setSelectedCoupon(CouponBean bean, int position) {
        this.mSelectedCouponBean = bean;
        this.mSelectedCouponPosition = position;
        if (bean.online.equals(Constant.COUPON_ONLINE_TRUE)) {
            mOnlineCoupons.get(position).isSelected = Constant.COUPON_IS_SELECTED_TRUE;
        } else {
            mOfflineCoupons.get(position).isSelected = Constant.COUPON_IS_SELECTED_TRUE;
        }
    }

    public CouponBean getSelectedCoupon() {
        return mSelectedCouponBean;
    }

    public int getSelectedCouponBeanPosition() {
        return mSelectedCouponPosition;
    }


    public void onDestroyManager() {
        couponDataFilterManager = null;
    }


}
