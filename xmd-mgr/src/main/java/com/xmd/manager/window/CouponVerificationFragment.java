package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.VerificationCouponDetailBean;
import com.xmd.manager.common.DescribeMesaageUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.VerificationManagementHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lhj on 2016/12/6.
 */

public class CouponVerificationFragment extends BaseFragment {

    @Bind(R.id.coupon_no)
    TextView mCouponNo;
    @Bind(R.id.coupon_no_section)
    LinearLayout mCouponNoSection;
    @Bind(R.id.coupon_name)
    TextView mCouponName;
    @Bind(R.id.coupon_type)
    TextView mCouponType;
    @Bind(R.id.coupon_desc)
    TextView mCouponDesc;
    @Bind(R.id.coupon_status)
    TextView mCouponStatus;
    @Bind(R.id.activity_duration)
    TextView mActivityDuration;
    @Bind(R.id.coupon_duration)
    TextView mCouponDuration;
    @Bind(R.id.coupon_use_duration_label)
    TextView mCouponUseDurationLabel;
    @Bind(R.id.coupon_use_duration)
    TextView mCouponUseDuration;
    @Bind(R.id.coupon_use)
    Button mCouponUse;
    @Bind(R.id.coupon_supplement)
    WebView mCouponSupplement;
    @Bind(R.id.coupon_supplement_section)
    LinearLayout mCouponSupplementSection;
    @Bind(R.id.coupon_item)
    TextView mCouponItem;
    @Bind(R.id.coupon_items_container)
    LinearLayout mCouponItemsContainer;
    @Bind(R.id.coupon_use_supplement)
    WebView mCouponUseSupplement;
    @Bind(R.id.coupon_commission)
    TextView mCouponCommission;
    @Bind(R.id.coupon_commission_section)
    LinearLayout mCouponCommissionSection;
    @Bind(R.id.coupon_use_data)
    Button mCouponUseData;
    @Bind(R.id.ll_activity_limit)
    LinearLayout mLlActivityLimit;
    @Bind(R.id.activity_limit)
    TextView mActivityLimit;


    private VerificationCouponDetailBean mCouponInfo;
    private String mCouponNoDetail;


    public static CouponVerificationFragment getInstance(VerificationCouponDetailBean couponBean) {
        CouponVerificationFragment cf = new CouponVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VerificationManagementHelper.VERIFICATION_COUPON_TYPE, couponBean);
        cf.setArguments(bundle);
        return cf;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_verification, container, false);
        ButterKnife.bind(this, view);
        mCouponInfo = (VerificationCouponDetailBean) getArguments().getSerializable(VerificationManagementHelper.VERIFICATION_COUPON_TYPE);
        mCouponNoDetail = mCouponInfo.couponNo;
        return view;
    }

    @Override
    protected void initView() {
        mCouponName.setText("money".equals(mCouponInfo.useType) ? mCouponInfo.actAmount / 100 + "元" : mCouponInfo.actTitle);
        mCouponType.setText(mCouponInfo.useTypeName);
        mCouponDesc.setText(mCouponInfo.consumeMoneyDescription);
        mActivityDuration.setText(mCouponInfo.couponPeriod);
        mCouponDuration.setText(mCouponInfo.couponPeriod);
        mCouponUseDuration.setText(DescribeMesaageUtil.getTimePeriodDes(mCouponInfo.useTimePeriod));
        if (mCouponInfo.couponType.equals("paid")) {
            mLlActivityLimit.setVisibility(View.GONE);
        } else {
            mLlActivityLimit.setVisibility(View.VISIBLE);
        }

        mCouponUseSupplement.loadDataWithBaseURL(null, mCouponInfo.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
        if (Utils.couponIsCanUse(mCouponInfo.startDate, mCouponInfo.endUseDate, mCouponInfo.useTimePeriod)) {
            mCouponStatus.setText("可用");
            mCouponUse.setEnabled(true);
        } else {
            mCouponStatus.setText("不可用");
            mCouponStatus.setTextColor(ResourceUtils.getColor(R.color.primary_color));
            mCouponUse.setEnabled(false);
        }
        if (Utils.isNotEmpty(DescribeMesaageUtil.getLimitedItems(mCouponInfo.itemNames))) {
            mActivityLimit.setText(DescribeMesaageUtil.getLimitedItems(mCouponInfo.itemNames));
        } else {
            mActivityLimit.setText("不限");

        }

        mCouponNoSection.setVisibility(View.VISIBLE);
        mCouponNo.setText(mCouponInfo.couponNo);
        mCouponUse.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.coupon_use)
    public void onClick() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_COUPON_SAVE, mCouponNoDetail);
    }
}
