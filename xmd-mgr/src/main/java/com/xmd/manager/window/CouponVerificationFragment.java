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

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.VerificationCouponDetailBean;
import com.xmd.manager.common.DescribeMesaageUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.VerificationManagementHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lhj on 2016/12/6.
 */

public class CouponVerificationFragment extends BaseFragment {

    @BindView(R.id.coupon_no)
    TextView mCouponNo;
    @BindView(R.id.coupon_no_section)
    LinearLayout mCouponNoSection;
    @BindView(R.id.coupon_name)
    TextView mCouponName;
    @BindView(R.id.coupon_type)
    TextView mCouponType;
    @BindView(R.id.coupon_desc)
    TextView mCouponDesc;
    @BindView(R.id.coupon_status)
    TextView mCouponStatus;
    @BindView(R.id.activity_duration)
    TextView mActivityDuration;
    @BindView(R.id.coupon_duration)
    TextView mCouponDuration;
    @BindView(R.id.coupon_use_duration_label)
    TextView mCouponUseDurationLabel;
    @BindView(R.id.coupon_use_duration)
    TextView mCouponUseDuration;
    @BindView(R.id.coupon_use)
    Button mCouponUse;
    @BindView(R.id.coupon_supplement)
    WebView mCouponSupplement;
    @BindView(R.id.coupon_supplement_section)
    LinearLayout mCouponSupplementSection;
    @BindView(R.id.coupon_item)
    TextView mCouponItem;
    @BindView(R.id.coupon_items_container)
    LinearLayout mCouponItemsContainer;
    @BindView(R.id.coupon_use_supplement)
    WebView mCouponUseSupplement;
    @BindView(R.id.coupon_commission)
    TextView mCouponCommission;
    @BindView(R.id.coupon_commission_section)
    LinearLayout mCouponCommissionSection;
    @BindView(R.id.coupon_use_data)
    Button mCouponUseData;
    @BindView(R.id.ll_activity_limit)
    LinearLayout mLlActivityLimit;
    @BindView(R.id.activity_limit)
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
        mCouponStatus.setText("");
        mCouponUse.setEnabled(true);
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
    }

    @OnClick(R.id.coupon_use)
    public void onClick() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_COUPON_SAVE, mCouponNoDetail);
    }
}
