package com.xmd.manager.window;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponRecordBean;
import com.xmd.manager.common.ResourceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Lhj on 17-10-30.
 */

public class CouponCardFragment extends BaseFragment {

    @BindView(R.id.coupon_title)
    TextView couponTitle;
    @BindView(R.id.coupon_type)
    TextView couponType;
    @BindView(R.id.tv_money_mark)
    TextView tvMoneyMark;
    @BindView(R.id.tv_coupon_value)
    TextView tvCouponValue;
    @BindView(R.id.tv_coupon_use_info)
    TextView tvCouponUseInfo;
    @BindView(R.id.img_coupon_mark)
    ImageView imgCouponMark;
    @BindView(R.id.coupon_number)
    TextView couponNumber;
    @BindView(R.id.bottom_view)
    View bottomView;
    Unbinder unbinder;
    private CouponRecordBean mRecordBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_card, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mRecordBean = (CouponRecordBean) getArguments().getParcelable(CouponReceiveAndUseDetailActivity.KEY_RECORD_COUPON_INFO);
        setViewDataByStatus();
    }

    private void setViewDataByStatus() {
        if (mRecordBean == null) {
            getActivity().finish();
            return;
        }
        couponTitle.setText(mRecordBean.title);
        couponType.setText(String.format("(%s)", mRecordBean.couponTypeName));
        couponNumber.setText(mRecordBean.couponNo);
        tvCouponUseInfo.setText(TextUtils.isEmpty(mRecordBean.consumeMoneyDescription) ? "" : mRecordBean.consumeMoneyDescription);
        tvCouponValue.setText(mRecordBean.amount > 0 ? String.format("%1.1f", mRecordBean.amount / 100f) : "0");
        switch (mRecordBean.status) {
            case Constant.COUPON_STATUS_VERIFIED:
                imgCouponMark.setImageResource(R.drawable.icon_mark_verificationed);
                bottomView.setBackgroundDrawable(ResourceUtils.getDrawable(R.drawable.coupon_unusable_bottom_bg));
                break;
            case Constant.COUPON_STATUS_CAN_USE:
                bottomView.setBackgroundDrawable(ResourceUtils.getDrawable(R.drawable.coupon_delivery_bottom_bg));
                imgCouponMark.setImageResource(R.drawable.icon_mark_usable);
                tvMoneyMark.setTextColor(Color.parseColor("#edba50"));
                tvCouponValue.setTextColor(Color.parseColor("#edba50"));
                break;
            case Constant.COUPON_STATUS_EXPIRED:
                imgCouponMark.setImageResource(R.drawable.icon_mark_unusable);
                bottomView.setBackgroundDrawable(ResourceUtils.getDrawable(R.drawable.coupon_unusable_bottom_bg));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
