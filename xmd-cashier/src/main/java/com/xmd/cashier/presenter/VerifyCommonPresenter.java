package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerifyCommonContract;
import com.xmd.cashier.dal.bean.CommonVerifyInfo;
import com.xmd.cashier.dal.bean.TreatInfo;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerifyManager;

import rx.Subscription;

/**
 * Created by zr on 16-12-12.
 */

public class VerifyCommonPresenter implements VerifyCommonContract.Presenter {
    private Context mContext;
    private VerifyCommonContract.View mView;
    private Subscription mVerifyCommonSubscription;

    public VerifyCommonPresenter(Context context, VerifyCommonContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onClickVerify(final CommonVerifyInfo info) {
        if (mView.needAmount() && !Utils.matchAmountNumFormat(mView.getAmount())) {
            mView.showError("请输入正确的金额");
            return;
        }
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mVerifyCommonSubscription != null) {
            mVerifyCommonSubscription.unsubscribe();
        }
        mView.showLoadingView();
        mVerifyCommonSubscription = VerifyManager.getInstance().verifyWithMoney(Utils.stringToMoney(mView.getAmount()), mView.getCode(), mView.getType(), new Callback<BaseResult>() {
            @Override
            public void onSuccess(BaseResult o) {
                if (AppConstants.TYPE_PAY_FOR_OTHER.equals(info.type)) {
                    TreatInfo treatInfo = new TreatInfo();
                    treatInfo.userName = info.userName;
                    treatInfo.userPhone = info.userPhone;
                    treatInfo.amount = Integer.parseInt(info.info.amount);
                    treatInfo.useMoney = Utils.stringToMoney(mView.getAmount());
                    treatInfo.authorizeCode = info.code;
                    VerifyManager.getInstance().print(AppConstants.TYPE_PAY_FOR_OTHER, treatInfo);
                }
                mView.hideLoadingView();
                mView.showToast("操作成功");
                mView.finishSelf();
            }

            @Override
            public void onError(String error) {
                XLogger.i(error);
                mView.hideLoadingView();
                mView.showError("操作失败：" + error);
            }
        });
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mVerifyCommonSubscription != null) {
            mVerifyCommonSubscription.unsubscribe();
        }
    }
}
