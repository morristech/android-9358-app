package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.MemberNavigationContract;
import com.xmd.cashier.manager.MemberManager;

/**
 * Created by zr on 17-7-11.
 */

public class MemberNavigationPresenter implements MemberNavigationContract.Presenter {
    private Context mContext;
    private MemberNavigationContract.View mView;

    public MemberNavigationPresenter(Context context, MemberNavigationContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        if (!TextUtils.isEmpty(MemberManager.getInstance().getRechargeMode())) {
            mView.enterRecharge(MemberManager.getInstance().getRechargeMode().contains(AppConstants.MEMBER_RECHARGE_MODEL_POS));
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onRecharge() {
        // 读卡-->选择套餐-->支付
        UiNavigation.gotoMemberReadActivity(mContext, AppConstants.MEMBER_BUSINESS_TYPE_RECHARGE);
    }

    @Override
    public void onPayment() {
        // 跳转收银
        UiNavigation.gotoCashierActivity(mContext);
    }

    @Override
    public void onCard() {
        // 跳转开卡
        MemberManager.getInstance().newCardProcess();
        UiNavigation.gotoMcardPhoneActivity(mContext);
        mView.showEnterAnim();
    }

    @Override
    public void onRecord() {
        // 会员账户记录
        UiNavigation.gotoMemberRecordActivity(mContext);
    }
}
