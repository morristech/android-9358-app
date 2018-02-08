package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberRechargeContract;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.PackagePlanItem;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.MemberPlanResult;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.net.response.MemberUrlResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.widget.InputPasswordDialog;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;

/**
 * Created by zr on 17-7-11.
 */

public class MemberRechargePresenter implements MemberRechargeContract.Presenter {
    private static final String TAG = "MemberRechargePresenter";
    private Context mContext;
    private MemberRechargeContract.View mView;

    private Subscription mGetMemberPlanSubscription;
    private Subscription mRechargeByScanSubscription;

    private Subscription mGetTradeNoSubscription;

    public MemberRechargePresenter(Context context, MemberRechargeContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onCreate() {
        mView.showMemberInfo(MemberManager.getInstance().getRechargeMemberInfo());
        clearAmount();
        clearAmountGive();
        clearPackage();
        MemberManager.getInstance().setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_NONE);
        loadPlanData();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        stopReportRecharge();
        if (mGetMemberPlanSubscription != null) {
            mGetMemberPlanSubscription.unsubscribe();
        }
        if (mRechargeByScanSubscription != null) {
            mRechargeByScanSubscription.unsubscribe();
        }
        if (mGetTradeNoSubscription != null) {
            mGetTradeNoSubscription.unsubscribe();
        }
    }

    @Override
    public void loadPlanData() {
        mView.loadingPlanData();
        if (mGetMemberPlanSubscription != null) {
            mGetMemberPlanSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "获取会员充值套餐：" + RequestConstant.URL_GET_MEMBER_ACT_PLAN);
        Observable<MemberPlanResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberPlanList(AccountManager.getInstance().getToken());
        mGetMemberPlanSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberPlanResult>() {
            @Override
            public void onCallbackSuccess(MemberPlanResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "获取会员充值套餐---成功");
                mView.showPlanData(result.getRespData().activity);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "获取会员充值套餐---失败：" + e.getLocalizedMessage());
                mView.errorPlanData(e.getLocalizedMessage());
            }
        });
    }

    private void onRechargePos() {
        if (mGetTradeNoSubscription != null) {
            mGetTradeNoSubscription.unsubscribe();
        }
        mView.showLoading();
        mGetTradeNoSubscription = MemberManager.getInstance().fetchTradeNo(new Callback<GetTradeNoResult>() {
            @Override
            public void onSuccess(GetTradeNoResult o) {
                mView.hideLoading();
                doPosCashier();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError("生成交易失败：" + error);
            }
        });
    }

    // POS支付
    private void doPosCashier() {
        int amount = 0;
        switch (MemberManager.getInstance().getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:
                amount = MemberManager.getInstance().getAmount();
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                PackagePlanItem info = MemberManager.getInstance().getPackageInfo();
                if (info != null) {
                    amount = info.amount;
                }
                break;
            default:
                break;
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付");
        MemberManager.getInstance().posRecharge(mContext, amount, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付---成功");
                doReportRecharge();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付---失败：" + error);
                mView.showError("支付失败：" + error);
            }
        });
    }

    private void doReportRecharge() {
        // 支付成功:汇报交易流水&&汇报充值
        MemberManager.getInstance().reportTrade();
        startReportRecharge();
    }

    /**
     * Pos充值成功后回调
     */
    private Call<MemberRecordResult> callReportRecharge;
    private RetryPool.RetryRunnable mRetryReportRecharge;
    private boolean resultReportRecharge = false;

    public void startReportRecharge() {
        mRetryReportRecharge = new RetryPool.RetryRunnable(AppConstants.TINNY_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return reportRechargeTrade();
            }
        });
        RetryPool.getInstance().postWork(mRetryReportRecharge);
    }

    public void stopReportRecharge() {
        if (callReportRecharge != null && !callReportRecharge.isCanceled()) {
            callReportRecharge.cancel();
        }
        if (mRetryReportRecharge != null) {
            RetryPool.getInstance().removeWork(mRetryReportRecharge);
            mRetryReportRecharge = null;
        }
    }

    private boolean reportRechargeTrade() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付成功后汇报支付结果：" + RequestConstant.URL_REPORT_MEMBER_RECHARGE_TRADE);
        callReportRecharge = XmdNetwork.getInstance().getService(SpaService.class)
                .reportMemberRecharge(AccountManager.getInstance().getToken(),
                        MemberManager.getInstance().getRechargeOrderId(),
                        Utils.getPayTypeChannel(CashierManager.getInstance().getPayType(MemberManager.getInstance().getTrade().posPayReturn)),
                        MemberManager.getInstance().getTrade().tradeNo,
                        RequestConstant.DEFAULT_SIGN_VALUE);
        XmdNetwork.getInstance().requestSync(callReportRecharge, new NetworkSubscriber<MemberRecordResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付成功后汇报支付结果---成功");
                PosFactory.getCurrentCashier().speech("会员充值成功");
                resultReportRecharge = true;
                MemberRecordInfo record = result.getRespData();
                MemberManager.getInstance().printMemberRecordInfoAsync(record, false);
                MemberManager.getInstance().newTrade();
                MemberManager.getInstance().newRechargeProcess();
                mView.finishSelf();
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付成功后汇报支付结果---失败：" + e.getLocalizedMessage());
                resultReportRecharge = false;
            }
        });
        return resultReportRecharge;
    }

    // 扫码支付
    private void onRechargeScan() {
        UiNavigation.gotoMemberScanActivity(mContext, AppConstants.MEMBER_CASHIER_METHOD_SCAN);
    }

    // 现金支付
    private void onRechargeCash() {
        UiNavigation.gotoMemberScanActivity(mContext, AppConstants.MEMBER_CASHIER_METHOD_CASH);
    }

    @Override
    public void onRecharge(int type) {
        switch (type) {
            case AppConstants.CASHIER_TYPE_POS: //Pos银联
            case AppConstants.CASHIER_TYPE_QRCODE:  // 扫码买单
                doRechargeRequest(type, null);
                break;
            case AppConstants.CASHIER_TYPE_CASH:    // 现金支付
                if (!AppConstants.APP_REQUEST_NO.equals(MemberManager.getInstance().getVerificationSwitch())) {
                    // 需要输入收银员密码
                    doInputPassword(type);
                } else {
                    // 无需校验密码
                    doRechargeRequest(type, null);
                }
                break;
            case AppConstants.CASHIER_TYPE_ERROR:   // 其他
            default:
                mView.showToast("充值流程异常...");
                break;
        }
    }

    private void doInputPassword(final int type) {
        final InputPasswordDialog dialog = new InputPasswordDialog(mContext);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setTitle("会员充值");
        dialog.setCallBack(new InputPasswordDialog.BtnCallBack() {
            @Override
            public void onBtnNegative() {
                dialog.dismiss();
            }

            @Override
            public void onBtnPositive(String password) {
                if (TextUtils.isEmpty(password)) {
                    mView.showToast("请输入密码");
                    return;
                }
                dialog.dismiss();
                doRechargeRequest(type, password);
            }
        });
    }

    private void doRechargeRequest(final int type, String password) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mRechargeByScanSubscription != null) {
            mRechargeByScanSubscription.unsubscribe();
        }
        mView.showLoading();
        mRechargeByScanSubscription = MemberManager.getInstance().requestRecharge(password, new Callback<MemberUrlResult>() {
            @Override
            public void onSuccess(MemberUrlResult o) {
                mView.hideLoading();
                switch (type) {
                    case AppConstants.CASHIER_TYPE_POS: //Pos支付
                        onRechargePos();
                        break;
                    case AppConstants.CASHIER_TYPE_QRCODE:  //扫码支付
                        onRechargeScan();
                        break;
                    case AppConstants.CASHIER_TYPE_CASH:    //现金支付
                        onRechargeCash();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("充值请求失败:" + error);
            }
        });
    }

    @Override
    public void onTechClick() {
        UiNavigation.gotoTechnicianActivity(mContext);
    }

    @Override
    public void onTechSelect(TechInfo info) {
        mView.showTechInfo(info);
        MemberManager.getInstance().setRechargeTechInfo(info);
    }

    @Override
    public void onTechDelete() {
        mView.deleteTechInfo();
        MemberManager.getInstance().setRechargeTechInfo(new TechInfo());
    }

    @Override
    public void onAmountSet(String amount) {
        // 设置金额
        if (TextUtils.isEmpty(amount)) {
            MemberManager.getInstance().setAmount(0);
        } else {
            // 以分为单位
            MemberManager.getInstance().setAmount(Utils.stringToMoney(amount));
        }
        MemberManager.getInstance().setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY);
    }

    @Override
    public void onAmountGiveSet(String amountGive) {
        if (TextUtils.isEmpty(amountGive)) {
            MemberManager.getInstance().setAmountGive(0);
        } else {
            MemberManager.getInstance().setAmountGive(Utils.stringToMoney(amountGive));
        }
        MemberManager.getInstance().setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY);
    }

    @Override
    public void clearAmount() {
        MemberManager.getInstance().setAmount(0);
        mView.clearAmount();
    }

    @Override
    public void clearAmountGive() {
        MemberManager.getInstance().setAmountGive(0);
        mView.clearAmountGive();
    }

    @Override
    public void onPackageSet(PackagePlanItem item) {
        // 设置套餐
        MemberManager.getInstance().setPackageInfo(item);
        MemberManager.getInstance().setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE);
    }

    @Override
    public void clearPackage() {
        MemberManager.getInstance().setPackageInfo(null);
        mView.clearPackage();
    }

    @Override
    public void onConfirm() {
        switch (MemberManager.getInstance().getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_NONE:
                mView.showError("请选择充值内容!");
                return;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:
                // 充值金额
                if (MemberManager.getInstance().getAmount() <= 0) {
                    mView.showError("请输入充值金额!");
                    return;
                }
                mView.showDialog();
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                // 充值套餐
                if (MemberManager.getInstance().getPackageInfo() == null) {
                    mView.showError("请添加充值套餐!");
                    return;
                }
                mView.showDialog();
                break;
        }
    }
}
