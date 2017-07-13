package com.xmd.manager.window;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.ActionCouponBean;
import com.xmd.manager.beans.AwardVerificationBean;
import com.xmd.manager.beans.DefaultVerificationBean;
import com.xmd.manager.beans.PayOrderDetailBean;
import com.xmd.manager.beans.VerificationCouponDetailBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.VerificationManagementHelper;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.VerificationSaveResult;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.io.Serializable;


import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by lhj on 2016/12/6.
 */

public class ManagerVerificationActivity extends BaseActivity {

    @BindView(R.id.fragment_verification)
    FrameLayout fragmentVerification;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    private String mVerificationType;
    private Object mObject;
    private Subscription mVerificationResultSubscription;


    public static void startManagerVerificationActivity(Activity activity, String type, Object mObjectContent) {
        Intent intent = new Intent(activity, ManagerVerificationActivity.class);
        intent.putExtra(VerificationManagementHelper.VERIFICATION_TYPE, type);
        intent.putExtra(VerificationManagementHelper.VERIFICATION_OBJECT, (Serializable) mObjectContent);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_verification);
        ButterKnife.bind(this);
        initView();
        initFragment();
    }

    private void initView() {
        mVerificationResultSubscription = RxBus.getInstance().toObservable(VerificationSaveResult.class).subscribe(
                saveResult -> {
                    if (saveResult.statusCode == 200) {
                        new AlertDialogBuilder(this)
                                .setTitle(ResourceUtils.getString(R.string.verification_success))
                                .setMessage(saveResult.msg)
                                .setPositiveButton(ResourceUtils.getString(R.string.btn_confirm), v -> this.finish())
                                .setCancelable(false)
                                .show();
                    } else {
                        new AlertDialogBuilder(this)
                                .setTitle(ResourceUtils.getString(R.string.verification_failed))
                                .setMessage(saveResult.msg)
                                .setPositiveButton(ResourceUtils.getString(R.string.btn_confirm), v -> this.finish())
                                .setCancelable(true)
                                .show();
                    }
                }
        );
        mVerificationType = getIntent().getStringExtra(VerificationManagementHelper.VERIFICATION_TYPE);
        mObject = getIntent().getSerializableExtra(VerificationManagementHelper.VERIFICATION_OBJECT);

    }

    private void initFragment() {
        if (mVerificationType.equals(VerificationManagementHelper.VERIFICATION_COUPON_TYPE)) {
            VerificationCouponDetailBean bean = (VerificationCouponDetailBean) mObject;
            if (bean.couponType.equals("paid")) {
                toolbarTitle.setText(R.string.paid_coupon);
            } else if (bean.useType.equals("money")) {
                toolbarTitle.setText(R.string.coupon_cash);
            } else {
                toolbarTitle.setText(R.string.coupon_tips);
            }

            setFragment(CouponVerificationFragment.getInstance((VerificationCouponDetailBean) mObject));
        } else if (mVerificationType.equals(VerificationManagementHelper.VERIFICATION_ORDER_TYPE)) {
            toolbarTitle.setText(R.string.verification_pay_order);
            setFragment(OrderVerificationFragment.getInstance((PayOrderDetailBean) mObject));
        } else if (mVerificationType.equals(VerificationManagementHelper.VERIFICATION_AWARD_TYPE)) {
            toolbarTitle.setText(R.string.verification_reward);
            setFragment(AwardVerificationFragment.getInstance((AwardVerificationBean) mObject));
        } else if (mVerificationType.equals(VerificationManagementHelper.VERIFICATION_SERVICE_ITEM_COUPON_TYPE)) {
            toolbarTitle.setText(R.string.verification_coupon_action);
            setFragment(CouponActionVerificationFragment.getInstance((ActionCouponBean) mObject));
        } else {
            if (Utils.isNotEmpty(((DefaultVerificationBean) mObject).title)) {
                toolbarTitle.setText(((DefaultVerificationBean) mObject).title);
            } else {
                setTitle(ResourceUtils.getString(R.string.paid_consume_activity_order_consume));
            }
            setFragment(DefaultVerificationFragment.getInstance((DefaultVerificationBean) mObject));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_verification, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mVerificationResultSubscription);
    }
}
