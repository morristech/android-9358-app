package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.McardInfoContract;
import com.xmd.cashier.dal.net.response.MemberCardResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;

import rx.Subscription;

/**
 * Created by zr on 17-7-11.
 */

public class McardInfoPresenter implements McardInfoContract.Presenter {
    private Context mContext;
    private McardInfoContract.View mView;
    private Subscription mCardMemberInfoSubscription;

    public McardInfoPresenter(Context context, McardInfoContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mView.showStepView(MemberManager.getInstance().getCardMode());
        mView.showInfo(MemberManager.getInstance().getPhone(), MemberManager.getInstance().getName());
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mCardMemberInfoSubscription != null) {
            mCardMemberInfoSubscription.unsubscribe();
        }
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

        switch (MemberManager.getInstance().getCardMode()) {
            case AppConstants.MEMBER_CARD_MODEL_NORMAL:
                UiNavigation.gotoMemberReadActivity(mContext, AppConstants.MEMBER_BUSINESS_TYPE_CARD);
                mView.showEnterAnim();
                break;
            default:
                doCardMember();
                break;
        }
    }


    private void doCardMember() {
        if (mCardMemberInfoSubscription != null) {
            mCardMemberInfoSubscription.unsubscribe();
        }
        mView.showLoading();
        mCardMemberInfoSubscription = MemberManager.getInstance().requestCard(new Callback<MemberCardResult>() {
            @Override
            public void onSuccess(MemberCardResult o) {
                mView.hideLoading();
                UiNavigation.gotoMcardSuccessActivity(mContext);
                mView.showEnterAnim();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("会员开卡失败:" + error);
            }
        });
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
