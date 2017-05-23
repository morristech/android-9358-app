package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.ActionCouponBean;
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

public class CouponActionVerificationFragment extends BaseFragment {


    @Bind(R.id.coupon_action_verification_code)
    TextView couponActionVerificationCode;
    @Bind(R.id.coupon_action_verification_name)
    TextView couponActionVerificationName;
    @Bind(R.id.btn_coupon_action_verification)
    Button btnCouponActionVerification;
    @Bind(R.id.coupon_name)
    TextView couponName;
    @Bind(R.id.coupon_verification_time)
    TextView couponVerificationTime;
    @Bind(R.id.coupon_verification_available_time)
    TextView couponVerificationAvailableTime;
    @Bind(R.id.coupon_verification_user_time)
    TextView couponVerificationUserTime;
    @Bind(R.id.action_coupon_supplement)
    WebView actionCouponSupplement;
    @Bind(R.id.action_type_name)
    TextView actionTypeName;
    @Bind(R.id.coupon_status)
    TextView couponStatus;
    @Bind(R.id.text_supplement_null)
    TextView textSupplementNull;
    @Bind(R.id.coupon_verification_get_time)
    TextView mCouponVerificationGetTime;


    private ActionCouponBean mActionBean;
    private String mCouponNo;

    public static CouponActionVerificationFragment getInstance(ActionCouponBean actionBean) {
        CouponActionVerificationFragment cf = new CouponActionVerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VerificationManagementHelper.VERIFICATION_COUPON_ACTION, actionBean);
        cf.setArguments(bundle);
        return cf;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_action_verification, container, false);
        ButterKnife.bind(this, view);
        mActionBean = (ActionCouponBean) getArguments().getSerializable(VerificationManagementHelper.VERIFICATION_COUPON_ACTION);
        mCouponNo = mActionBean.couponNo;
        return view;
    }

    @Override
    protected void initView() {
        actionCouponSupplement.getSettings().setJavaScriptEnabled(false);
        actionCouponSupplement.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        if (Utils.isNotEmpty(mActionBean.couponNo)) {
            couponActionVerificationCode.setText(mActionBean.couponNo);
        }
        if (Utils.isNotEmpty(mActionBean.actSubTitle)) {
            couponActionVerificationName.setText(mActionBean.actSubTitle);
        }
        if (Utils.isNotEmpty(mActionBean.useTypeName)) {
            actionTypeName.setText(mActionBean.useTypeName);
        }
        if (Utils.isNotEmpty(mActionBean.actTitle)) {
            couponName.setText(mActionBean.actTitle);
        }
        couponVerificationAvailableTime.setText(mActionBean.couponPeriod);
        couponVerificationUserTime.setText(DescribeMesaageUtil.getTimePeriodDes(mActionBean.useTimePeriod));
        if (Utils.isNotEmpty(mActionBean.actContent)) {
            actionCouponSupplement.setVisibility(View.VISIBLE);
            actionCouponSupplement.loadDataWithBaseURL(null, mActionBean.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
        } else {
            actionCouponSupplement.setVisibility(View.GONE);
            textSupplementNull.setVisibility(View.VISIBLE);
        }
        if (Utils.couponIsCanUse(mActionBean.startDate, mActionBean.endUseDate, mActionBean.useTimePeriod)) {
            btnCouponActionVerification.setEnabled(true);
            couponStatus.setText("可用");
        } else {
            btnCouponActionVerification.setEnabled(false);
            btnCouponActionVerification.setBackgroundResource(R.drawable.bg_verification_unuseable);
            couponStatus.setText("不可用");
            couponStatus.setTextColor(ResourceUtils.getColor(R.color.primary_color));
        }
        if (Utils.isNotEmpty(mActionBean.getDate)) {
            mCouponVerificationGetTime.setText(mActionBean.getDate);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_coupon_action_verification)
    public void onClick() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_VERIFICATION_SERVICE_ITEM_COUPON_SAVE, mCouponNo);
    }
}
