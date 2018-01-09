package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerMethodContract;
import com.xmd.cashier.dal.bean.InnerOrderInfo;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

/**
 * Created by zr on 17-11-2.
 */

public class InnerMethodPresenter implements InnerMethodContract.Presenter {
    private static final String TAG = "InnerMethodPresenter";

    private TradeManager mTradeManager;
    private Context mContext;
    private InnerMethodContract.View mView;

    private boolean mHoleSelect = true;

    public InnerMethodPresenter(Context context, InnerMethodContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
        updateAmount();
    }

    private void updateAmount() {
        Trade trade = mTradeManager.getCurrentTrade();
        int origin = trade.getOriginMoney();
        int reduction = trade.getWillReductionMoney();
        int verify = TradeManager.getInstance().getDiscountAmount(trade.getCouponList());
        int already = trade.getAlreadyDiscountMoney();
        trade.setWillDiscountMoney(verify);
        trade.setAlreadyCutMoney(verify + reduction + already);
        if (origin < verify + reduction + already) {
            trade.setWillPayMoney(0);
        } else {
            trade.setWillPayMoney(origin - verify - reduction - already);
        }
        mView.showDiscountAmount(trade.getAlreadyCutMoney());
        mView.showNeedPayAmount(trade.getWillPayMoney());
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onVerifySelect() {
        Trade trade = mTradeManager.getCurrentTrade();
        if (trade.getWillReductionMoney() > 0 || trade.getWillDiscountMoney() > 0 || trade.getAlreadyDiscountMoney() > 0) {
            new CustomAlertDialogBuilder(mContext)
                    .setMessage("已经添加优惠，是否继续添加?")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            UiNavigation.gotoInnerDiscountActivity(mContext);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            UiNavigation.gotoInnerDiscountActivity(mContext);
        }
    }

    @Override
    public void onPayClick() {
        if (AppConstants.INNER_METHOD_SOURCE_NORMAL.equals(mView.returnSource())) {
            int count = InnerManager.getInstance().getSelectCount();
            if (count > 1) {
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("本次消费有" + count + "笔订单，确认合并进行支付吗?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                gotoModify();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                gotoModify();
            }
        } else {
            gotoModify();
        }
    }

    @Override
    public void onSelectChange() {
        if (mHoleSelect) {
            // 取消
            InnerManager.getInstance().unselectedOrderInfos();
        } else {
            // 全选
            InnerManager.getInstance().selectedOrderInfos();
        }
        mHoleSelect = !mHoleSelect;
        mView.updateStatus(mHoleSelect);
        mView.updateAll();
        mView.showSelectCount(InnerManager.getInstance().getSelectCount());
        // 订单金额|核销金额
        mTradeManager.getCurrentTrade().setOriginMoney(InnerManager.getInstance().getOrderAmount());
        mTradeManager.setDiscountOriginAmount();
        updateAmount();
    }

    @Override
    public void onOrderClick(InnerOrderInfo info, int position) {
        info.selected = !info.selected;

        int select = InnerManager.getInstance().getSelectCount();
        int total = InnerManager.getInstance().getInnerOrderInfos().size();

        mView.updateItem(position);
        mView.showSelectCount(select);

        if (select == 0 || select < total) {
            mHoleSelect = false;
            mView.updateStatus(mHoleSelect);
        } else if (select == total) {
            mHoleSelect = true;
            mView.updateStatus(mHoleSelect);
        } else {
            // do nothing
        }

        // 订单金额|核销金额
        mTradeManager.getCurrentTrade().setOriginMoney(InnerManager.getInstance().getOrderAmount());
        mTradeManager.setDiscountOriginAmount();
        updateAmount();
    }

    @Override
    public void processData() {
        mView.showStepView();       //显示StepView
        String source = mView.returnSource();
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单数据源:" + source);
        switch (source) {
            case AppConstants.INNER_METHOD_SOURCE_NORMAL:   //如果是正常查找
                mView.showOrderList(InnerManager.getInstance().getInnerOrderInfos());   //显示列表
                mView.setStatusLayout(true);            //显示其他项
                mView.updateStatus(mHoleSelect);
                mView.showSelectCount(InnerManager.getInstance().getSelectCount());
                break;
            case AppConstants.INNER_METHOD_SOURCE_RECORD:   //如果是记录列表
            case AppConstants.INNER_METHOD_SOURCE_PUSH:     //如果是推送
            case AppConstants.INNER_METHOD_SOURCE_CONTINUE:
                InnerRecordInfo recordInfo = mView.returnRecordInfo();
                mView.showOrderList(recordInfo.details);    //显示列表
                mView.setStatusLayout(false);               //隐藏设置项
                break;
            default:
                break;
        }
        updateAmount();     //更新页面金额
        mView.showDiscountEnter(mTradeManager.getCurrentTrade().getAlreadyPayMoney() <= 0); //控制添加优惠入口
    }

    private void gotoModify() {
        UiNavigation.gotoInnerModifyActivity(mContext);
    }
}
