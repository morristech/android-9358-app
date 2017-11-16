package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.contact.httprequest.ConstantResources;
import com.xmd.m.comment.CustomerInfoDetailActivity;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponRecordBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.VerificationManagementHelper;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.VerificationSaveResult;
import com.xmd.manager.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-10-30.
 */

public class CouponReceiveAndUseDetailActivity extends BaseActivity {
    @BindView(R.id.fragment_coupon_detail)
    FrameLayout fragmentCouponDetail;
    @BindView(R.id.img_user_avatar)
    CircleImageView imgUserAvatar;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_user_phone)
    TextView tvUserPhone;
    @BindView(R.id.tv_receive_time)
    TextView tvReceiveTime;
    @BindView(R.id.tv_use_able_time)
    TextView tvUseAbleTime;
    @BindView(R.id.tv_share_name)
    TextView tvShareName;
    @BindView(R.id.tv_verification_time)
    TextView tvVerificationTime;
    @BindView(R.id.tv_verification_name)
    TextView tvVerificationName;
    @BindView(R.id.ll_verification_info)
    LinearLayout llVerificationInfo;
    @BindView(R.id.rl_verification_detail)
    RelativeLayout rlVerificationDetail;
    @BindView(R.id.btn_to_do)
    Button btnToDo;

    public static String KEY_RECORD_COUPON_INFO = "couponInfo";

    private CouponCardFragment mCouponCardFragment;
    private CouponRecordBean mCouponBean;
    private Subscription mVerificationSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_receive_and_use_detail);
        ButterKnife.bind(this);
        initView();
        initFragmentView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.coupon_receive_and_use_info));
        mCouponCardFragment = new CouponCardFragment();
        mCouponBean = (CouponRecordBean) getIntent().getParcelableExtra(CouponRecordListFragment.KEY_INTENT_COUPON_RECORD);
        setViewData(mCouponBean);
        mVerificationSubscription = RxBus.getInstance().toObservable(VerificationSaveResult.class).subscribe(
                result -> handleVerificationResult(result));
    }

    private void handleVerificationResult(VerificationSaveResult result) {
        if (result.statusCode == 200) {
            mCouponBean.status = Constant.COUPON_STATUS_VERIFIED;
            btnToDo.setText(ResourceUtils.getString(R.string.string_return));
        }
    }

    public void setViewData(CouponRecordBean couponBean) {
        if (couponBean == null) {
            this.finish();
            return;
        }
        Glide.with(CouponReceiveAndUseDetailActivity.this).load(mCouponBean.userHeadImage).error(R.drawable.icon22).into(imgUserAvatar);
        tvUserName.setText(TextUtils.isEmpty(mCouponBean.userName) ? ResourceUtils.getString(R.string.string_default_name) : mCouponBean.userName);
        tvUserPhone.setText(TextUtils.isEmpty(mCouponBean.userPhoneNum) ? "" : mCouponBean.userPhoneNum);
        tvReceiveTime.setText(TextUtils.isEmpty(mCouponBean.getTime) ? "-" : mCouponBean.getTime);
        tvUseAbleTime.setText(TextUtils.isEmpty(mCouponBean.useEndTime) ? "长期有效" : mCouponBean.useStartTime);
        if (Utils.isNotEmpty(mCouponBean.techName) && Utils.isNotEmpty(mCouponBean.techNo)) {
            tvShareName.setText(String.format("%s [%s]", mCouponBean.techName, mCouponBean.techNo));
        } else {
            tvShareName.setText(TextUtils.isEmpty(mCouponBean.techName) ? "会所" : mCouponBean.techName);
        }
        if (mCouponBean.status.equals(Constant.COUPON_STATUS_VERIFIED)) {
            rlVerificationDetail.setVisibility(View.VISIBLE);
            tvVerificationTime.setText(mCouponBean.verifyTime);
            tvVerificationTime.setText(TextUtils.isEmpty(mCouponBean.verifyOperator) ? "-" : mCouponBean.verifyOperator);
        } else {
            rlVerificationDetail.setVisibility(View.INVISIBLE);
        }
        btnToDo.setText(mCouponBean.status.equals(Constant.COUPON_STATUS_CAN_USE) ? ResourceUtils.getString(R.string.coupon_data_verification) : ResourceUtils.getString(R.string.string_return));
    }

    private void initFragmentView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_RECORD_COUPON_INFO, mCouponBean);
        mCouponCardFragment.setArguments(bundle);
        ft.replace(R.id.fragment_coupon_detail, mCouponCardFragment);
        ft.commit();
    }

    @OnClick({R.id.rl_user_info, R.id.btn_to_do})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_user_info:
                CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(this, mCouponBean.userId, ConstantResources.APP_TYPE_MANAGER, false);
                break;
            case R.id.btn_to_do:
                switch (mCouponBean.status) {
                    case Constant.COUPON_STATUS_CAN_USE:
                        VerificationManagementHelper.checkVerificationType(mCouponBean.couponNo);
                        break;
                    case Constant.COUPON_STATUS_VERIFIED:
                    case Constant.COUPON_STATUS_EXPIRED:
                        this.finish();
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mVerificationSubscription);
    }
}
