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
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.widget.ActionSheetDialog;
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

    private MemberManager mMemberManager;

    public MemberRechargePresenter(Context context, MemberRechargeContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mMemberManager = MemberManager.getInstance();
    }

    @Override
    public void onCreate() {
        mMemberManager.newTrade();
        mView.showMemberInfo(mMemberManager.getRechargeMemberInfo());
        clearAmount();
        clearAmountGive();
        clearPackage();
        mMemberManager.setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_NONE);
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
        mGetTradeNoSubscription = mMemberManager.fetchTradeNo(new Callback<GetTradeNoResult>() {
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
        switch (mMemberManager.getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:
                amount = mMemberManager.getAmount();
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                PackagePlanItem info = mMemberManager.getPackageInfo();
                if (info != null) {
                    amount = info.amount;
                }
                break;
            default:
                break;
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付");
        mMemberManager.posRecharge(mContext, amount, new Callback<Void>() {
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
                        mMemberManager.getRechargeOrderId(),
                        mMemberManager.getTrade().currentChannelType,
                        mMemberManager.getTrade().tradeNo,
                        RequestConstant.DEFAULT_SIGN_VALUE);
        XmdNetwork.getInstance().requestSync(callReportRecharge, new NetworkSubscriber<MemberRecordResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付成功后汇报支付结果---成功");
                PosFactory.getCurrentCashier().speech("会员充值成功");
                resultReportRecharge = true;
                MemberRecordInfo record = result.getRespData();
                mMemberManager.printMemberRecordInfoAsync(record, false);
                mMemberManager.newTrade();
                mMemberManager.newRechargeProcess();
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
        UiNavigation.gotoMemberPaymentActivity(mContext, AppConstants.MEMBER_CASHIER_METHOD_SCAN);
    }

    // 现金支付
    private void onRechargeCash() {
        UiNavigation.gotoMemberPaymentActivity(mContext, AppConstants.MEMBER_CASHIER_METHOD_CASH);
    }

    private void onRecharge() {
        switch (mMemberManager.getTrade().currentChannelType) {
            case AppConstants.PAY_CHANNEL_WX: //Pos银联
            case AppConstants.PAY_CHANNEL_ALI:
            case AppConstants.PAY_CHANNEL_UNION:  // 扫码买单
                doRechargeRequest(null);
                break;
            case AppConstants.PAY_CHANNEL_CASH:    // 现金支付
                if (!AppConstants.APP_REQUEST_NO.equals(mMemberManager.getVerificationSwitch())) {
                    // 需要输入收银员密码
                    doInputPassword();
                } else {
                    // 无需校验密码
                    doRechargeRequest(null);
                }
                break;
            case AppConstants.PAY_CHANNEL_OTHER:   // 其他
            default:
                mView.showToast("充值流程异常...");
                break;
        }
    }

    private void doInputPassword() {
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
                doRechargeRequest(password);
            }
        });
    }

    private void doRechargeRequest(String password) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mRechargeByScanSubscription != null) {
            mRechargeByScanSubscription.unsubscribe();
        }
        mView.showLoading();
        mRechargeByScanSubscription = mMemberManager.requestRecharge(password, new Callback<MemberUrlResult>() {
            @Override
            public void onSuccess(MemberUrlResult o) {
                mView.hideLoading();
                switch (mMemberManager.getTrade().currentChannelType) {
                    case AppConstants.PAY_CHANNEL_UNION: //Pos支付
                        onRechargePos();
                        break;
                    case AppConstants.PAY_CHANNEL_WX:  //扫码支付
                    case AppConstants.PAY_CHANNEL_ALI:
                        onRechargeScan();
                        break;
                    case AppConstants.PAY_CHANNEL_CASH:    //现金支付
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
        mMemberManager.setRechargeTechInfo(info);
    }

    @Override
    public void onTechDelete() {
        mView.deleteTechInfo();
        mMemberManager.setRechargeTechInfo(new TechInfo());
    }

    @Override
    public void onAmountSet(String amount) {
        // 设置金额
        if (TextUtils.isEmpty(amount)) {
            mMemberManager.setAmount(0);
        } else {
            // 以分为单位
            mMemberManager.setAmount(Utils.stringToMoney(amount));
        }
        mMemberManager.setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY);
    }

    @Override
    public void onAmountGiveSet(String amountGive) {
        if (TextUtils.isEmpty(amountGive)) {
            mMemberManager.setAmountGive(0);
        } else {
            mMemberManager.setAmountGive(Utils.stringToMoney(amountGive));
        }
        mMemberManager.setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY);
    }

    @Override
    public void clearAmount() {
        mMemberManager.setAmount(0);
        mView.clearAmount();
    }

    @Override
    public void clearAmountGive() {
        mMemberManager.setAmountGive(0);
        mView.clearAmountGive();
    }

    @Override
    public void onPackageSet(PackagePlanItem item) {
        // 设置套餐
        mMemberManager.setPackageInfo(item);
        mMemberManager.setAmountType(AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE);
    }

    @Override
    public void clearPackage() {
        mMemberManager.setPackageInfo(null);
        mView.clearPackage();
    }

    @Override
    public void onConfirm() {
        switch (mMemberManager.getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_NONE:
                mView.showError("请选择充值内容!");
                return;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:
                // 充值金额
                if (mMemberManager.getAmount() <= 0) {
                    mView.showError("请输入充值金额!");
                    return;
                }
                showDialog();
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                // 充值套餐
                if (mMemberManager.getPackageInfo() == null) {
                    mView.showError("请添加充值套餐!");
                    return;
                }
                showDialog();
                break;
        }
    }

    private void showDialog() {
        ActionSheetDialog dialog = new ActionSheetDialog(mContext);
        dialog.setContents(new String[]{AppConstants.CASHIER_TYPE_WX_TEXT, AppConstants.CASHIER_TYPE_ALI_TEXT, AppConstants.CASHIER_TYPE_UNION_TEXT, AppConstants.CASHIER_TYPE_CASH_TEXT,});
        dialog.setCancelText("取消");
        dialog.setEventListener(new ActionSheetDialog.OnEventListener() {
            @Override
            public void onActionItemClick(ActionSheetDialog dialog, String item, int position) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值选择支付方式：" + item);
                String type = AppConstants.PAY_CHANNEL_OTHER;
                switch (item) {
                    case AppConstants.CASHIER_TYPE_WX_TEXT:
                        type = AppConstants.PAY_CHANNEL_WX;
                        break;
                    case AppConstants.CASHIER_TYPE_ALI_TEXT:
                        type = AppConstants.PAY_CHANNEL_ALI;
                        break;
                    case AppConstants.CASHIER_TYPE_UNION_TEXT:
                        type = AppConstants.PAY_CHANNEL_UNION;
                        break;
                    case AppConstants.CASHIER_TYPE_CASH_TEXT:
                        type = AppConstants.PAY_CHANNEL_CASH;
                        break;
                    default:
                        break;
                }
                mMemberManager.getTrade().currentChannelType = type;
                onRecharge();
                dialog.dismiss();
            }

            @Override
            public void onCancelItemClick(ActionSheetDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
