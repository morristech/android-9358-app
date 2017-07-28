package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.McardInfoContract;
import com.xmd.cashier.manager.MemberManager;

/**
 * Created by zr on 17-7-11.
 */

public class McardInfoPresenter implements McardInfoContract.Presenter {
    private Context mContext;
    private McardInfoContract.View mView;

    public McardInfoPresenter(Context context, McardInfoContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mView.showInfo(MemberManager.getInstance().getPhone(), MemberManager.getInstance().getName());
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfirm() {
        if (TextUtils.isEmpty(MemberManager.getInstance().getName())) {
            mView.showError("请设置姓名");
            return;
        }
        if (TextUtils.isEmpty(MemberManager.getInstance().getPhone())) {
            mView.showError("请设置手机号");
            return;
        }
        UiNavigation.gotoMemberReadActivity(mContext, AppConstants.MEMBER_BUSINESS_TYPE_CARD);
        mView.showEnterAnim();
    }

    @Override
    public void onGenderSelect(String gender) {
        MemberManager.getInstance().setGender(gender);
    }

    @Override
    public void onBirthSelect(String birth) {
        MemberManager.getInstance().setBirth(birth);
        mView.showBirth(birth);
    }

    @Override
    public void onBirthDelete() {
        MemberManager.getInstance().setBirth(null);
        mView.showBirth(null);
    }

    @Override
    public void onNameChange(String name) {
        MemberManager.getInstance().setName(name);
    }
}
