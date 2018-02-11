package com.xmd.cashier.presenter;

import android.content.Context;

import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerDetailContract;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.TradeManager;

/**
 * Created by zr on 17-11-7.
 */

public class InnerDetailPresenter implements InnerDetailContract.Presenter {
    private Context mContext;
    private InnerDetailContract.View mView;

    public InnerDetailPresenter(Context context, InnerDetailContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        switch (mView.returnSource()) {
            case AppConstants.INNER_DETAIL_SOURCE_RECORD:
                mView.showOperate();
                break;
            case AppConstants.INNER_DETAIL_SOURCE_OTHER:
            default:
                mView.showPositive();
                break;
        }

        TradeRecordInfo info = mView.returnRecordInfo();
        if (info != null) {
            mView.showAmount(info);
            if (info.details != null && !info.details.isEmpty()) {
                mView.showRecordDetail(info.details);
            } else {
                mView.showToast("未查询到相应详情");
                return;
            }
            mView.setOperate(AppConstants.INNER_BATCH_STATUS_UNPAID.equals(info.status));
        } else {
            mView.showRecordDetail(InnerManager.getInstance().getSelectedInnerOrderInfos());
            mView.showAmount(InnerManager.getInstance().getOrderAmount());
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onDetailNegative() {
        mView.finishSelf();
    }

    @Override
    public void onDetailPositive() {
        mView.finishSelf();
    }

    @Override
    public void onDetailPrint() {
        TradeRecordInfo recordInfo = mView.returnRecordInfo();
        TradeManager.getInstance().printTradeRecordInfoAsync(recordInfo, true);
    }

    @Override
    public void onDetailPay() {
        TradeRecordInfo recordInfo = mView.returnRecordInfo();
        TradeManager.getInstance().initTradeByRecord(recordInfo);
        if (recordInfo.paidAmount > 0) {
            UiNavigation.gotoInnerModifyActivity(mContext);
        } else {
            UiNavigation.gotoInnerMethodActivity(mContext, AppConstants.INNER_METHOD_SOURCE_RECORD, recordInfo);
        }
        mView.finishSelf();
    }
}
