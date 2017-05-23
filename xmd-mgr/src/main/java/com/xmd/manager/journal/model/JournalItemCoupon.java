package com.xmd.manager.journal.model;

import com.xmd.manager.journal.manager.CouponActivityManager;

/**
 * Created by heyangya on 16-11-18.
 */

public class JournalItemCoupon extends JournalItemBase {
    private CouponActivity mCouponActivity;
    private int mSelectedItemIndex = -1;

    public JournalItemCoupon(CouponActivity couponActivity) {
        super(null);
        mCouponActivity = couponActivity;
    }

    public JournalItemCoupon(String data) {
        super(data);
        if (data.contains(":")) {
            String[] values = data.split(":");
            if (values.length > 1) {
                CouponActivity couponActivity = CouponActivityManager.getInstance().getCouponActivity(values[0], values[1]);
                if (couponActivity != null) {
                    mCouponActivity = couponActivity;
                    for (CouponActivity.Item item : couponActivity.getData()) {
                        if (item.getValue().equals(values[1])) {
                            mSelectedItemIndex = couponActivity.getData().indexOf(item);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String contentToString() {
        if (mCouponActivity != null && mCouponActivity.getData() != null && mCouponActivity.getData().size() > mSelectedItemIndex) {
            return mCouponActivity.getCategory() + ":" +
                    mCouponActivity.getData().get(mSelectedItemIndex).getValue();
        }
        return null;
    }

    public CouponActivity getCouponActivity() {
        return mCouponActivity;
    }

    public void setCouponActivity(CouponActivity couponActivity) {
        this.mCouponActivity = couponActivity;
    }

    public int getSelectedItemIndex() {
        return mSelectedItemIndex;
    }

    public void setSelectedItemIndex(int selectedItemIndex) {
        this.mSelectedItemIndex = selectedItemIndex;
    }
}
