package com.xmd.cashier.presenter;

import android.content.Context;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.InnerResultContract;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.dal.event.InnerFinishEvent;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.TradeManager;

import org.greenrobot.eventbus.EventBus;

import rx.Subscription;

/**
 * Created by zr on 17-11-4.
 */

public class InnerResultPresenter implements InnerResultContract.Presenter {
    private static final String TAG = "InnerResultPresenter";

    private Context mContext;
    private InnerResultContract.View mView;

    private Subscription mGetBatchOrderSubscription;
    private TradeRecordInfo mRecordInfo;

    public InnerResultPresenter(Context context, InnerResultContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mView.showStepView();
        mRecordInfo = null;
        switch (TradeManager.getInstance().getCurrentTrade().tradeStatus) {
            case AppConstants.TRADE_STATUS_SUCCESS:
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银交易状态：成功");
                mView.statusSuccess("收款金额：￥" + Utils.moneyToStringEx(TradeManager.getInstance().getCurrentTrade().getWillPayMoney()));
                mRecordInfo = TradeManager.getInstance().getCurrentTrade().resultOrderInfo;
                if (mRecordInfo != null) {
                    printNormal();
                }
                break;
            case AppConstants.TRADE_STATUS_ERROR:
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银交易状态：失败");
                mView.statusError(TradeManager.getInstance().getCurrentTrade().tradeStatusError);
                break;
            case AppConstants.TRADE_STATUS_EXCEPTION:
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银交易状态：异常");
                mView.statusException();
                break;
            default:
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银交易状态：失败取消或其他");
                break;
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        if (mGetBatchOrderSubscription != null) {
            mGetBatchOrderSubscription.unsubscribe();
        }
    }

    @Override
    public void onDetail() {
        if (mRecordInfo == null) {
            XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单支付结果手动获取详情");
            mView.showLoading();
            if (mGetBatchOrderSubscription != null) {
                mGetBatchOrderSubscription.unsubscribe();
            }
            mGetBatchOrderSubscription = TradeManager.getInstance().getHoleBatchDetail(TradeManager.getInstance().getCurrentTrade().payOrderId, new Callback<TradeRecordInfo>() {
                @Override
                public void onSuccess(TradeRecordInfo o) {
                    mView.hideLoading();
                    mRecordInfo = o;
                    UiNavigation.gotoInnerDetailActivity(mContext, AppConstants.INNER_DETAIL_SOURCE_OTHER, mRecordInfo);
                }

                @Override
                public void onError(String error) {
                    mView.hideLoading();
                    mView.showToast(error);
                    mRecordInfo = null;
                }
            });
        } else {
            UiNavigation.gotoInnerDetailActivity(mContext, AppConstants.INNER_DETAIL_SOURCE_OTHER, mRecordInfo);
        }
    }

    @Override
    public void onDone() {
        newInnerTrade();
    }

    @Override
    public void onContinue() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单支付完成后继续收款");
        InnerManager.getInstance().clearInnerOrderInfos();
        TradeManager.getInstance().initTradeByRecord(mRecordInfo);
        mView.finishSelf();
        EventBus.getDefault().post(new InnerFinishEvent());
        if (mRecordInfo.paidAmount > 0) {
            UiNavigation.gotoInnerModifyActivity(mContext);
        } else {
            UiNavigation.gotoInnerMethodActivity(mContext, AppConstants.INNER_METHOD_SOURCE_CONTINUE, mRecordInfo);
        }
    }

    private void printNormal() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单支付结果自动打印小票");
        TradeManager.getInstance().printTradeRecordInfoAsync(mRecordInfo, false);
    }

    @Override
    public void onClose() {
        newInnerTrade();
    }

    @Override
    public void onEventBack() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单支付结果选择回退");
        newInnerTrade();
    }

    private void newInnerTrade() {
        TradeManager.getInstance().newTrade();
        InnerManager.getInstance().clearInnerOrderInfos();
        mView.finishSelf();
        EventBus.getDefault().post(new InnerFinishEvent());
    }
}
