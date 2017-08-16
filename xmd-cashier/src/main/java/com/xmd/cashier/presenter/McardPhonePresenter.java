package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.McardPhoneContract;
import com.xmd.cashier.contract.McardPhoneContract.Presenter;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;

import rx.Subscription;

/**
 * Created by zr on 17-7-11.
 */

public class McardPhonePresenter implements Presenter {
    private Context mContext;
    private McardPhoneContract.View mView;
    private Subscription mCheckMemberPhoneSubscription;

    public McardPhonePresenter(Context context, McardPhoneContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mView.showStepView(MemberManager.getInstance().getCardMode());
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mCheckMemberPhoneSubscription != null) {
            mCheckMemberPhoneSubscription.unsubscribe();
        }
    }

    @Override
    public void onConfirm() {
        String input = mView.getPhone();

        if (TextUtils.isEmpty(input)) {
            mView.showError("请输入正确的手机号");
            return;
        }

        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }

        if (mCheckMemberPhoneSubscription != null) {
            mCheckMemberPhoneSubscription.unsubscribe();
        }

        mView.showLoading();
        mCheckMemberPhoneSubscription = MemberManager.getInstance().checkMemberPhone(input, new Callback<StringResult>() {
            @Override
            public void onSuccess(StringResult o) {
                mView.hideLoading();
                UiNavigation.gotoMcardInfoActivity(mContext);
                mView.showEnterAnim();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }
}
