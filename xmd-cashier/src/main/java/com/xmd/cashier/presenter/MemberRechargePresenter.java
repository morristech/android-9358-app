package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
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
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.cashier.widget.InputPasswordDialog;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-7-11.
 */

public class MemberRechargePresenter implements MemberRechargeContract.Presenter {
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
        Observable<MemberPlanResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberPlanList(AccountManager.getInstance().getToken());
        mGetMemberPlanSubscription = XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberPlanResult>() {
            @Override
            public void onCallbackSuccess(MemberPlanResult result) {
                mView.showPlanData(result.getRespData().activity);
            }

            @Override
            public void onCallbackError(Throwable e) {
                mView.errorPlanData(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onRechargePos() {
        if (mGetTradeNoSubscription != null) {
            mGetTradeNoSubscription.unsubscribe();
        }
        mView.showLoading();
        mGetTradeNoSubscription = MemberManager.getInstance().fetchTradeNo(new Callback<GetTradeNoResult>() {
            @Override
            public void onSuccess(GetTradeNoResult o) {
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
        MemberManager.getInstance().posRecharge(mContext, amount, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                doReportRecharge();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
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
        mRetryReportRecharge = new RetryPool.RetryRunnable(3000, 1.0f, new RetryPool.RetryExecutor() {
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
        callReportRecharge = XmdNetwork.getInstance().getService(SpaService.class)
                .reportMemberRecharge(AccountManager.getInstance().getToken(),
                        MemberManager.getInstance().getRechargeOrderId(),
                        Utils.getPayTypeChannel(CashierManager.getInstance().getPayType(MemberManager.getInstance().getTrade().posPayReturn)),
                        MemberManager.getInstance().getTrade().tradeNo,
                        RequestConstant.DEFAULT_SIGN_VALUE);
        XmdNetwork.getInstance().requestSync(callReportRecharge, new NetworkSubscriber<MemberRecordResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordResult result) {
                resultReportRecharge = true;
                MemberRecordInfo record = result.getRespData();
                record.packageInfo = MemberManager.getInstance().getPackageInfo();
                printClient(record);
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.d("汇报失败:" + e.getLocalizedMessage());
                resultReportRecharge = false;
            }
        });
        return resultReportRecharge;
    }

    private void printClient(final MemberRecordInfo info) {
        MemberManager.getInstance().printInfo(info, false, true, new Callback() {
            @Override
            public void onSuccess(Object o) {
                mView.hideLoading();
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("是否需要打印客户联小票?")
                        .setPositiveButton("打印", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Observable
                                        .create(new Observable.OnSubscribe<Void>() {
                                            @Override
                                            public void call(Subscriber<? super Void> subscriber) {
                                                // POS充值:银联|现金
                                                MemberManager.getInstance().printInfo(info, false, false, null);
                                                subscriber.onNext(null);
                                                subscriber.onCompleted();
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();
                                MemberManager.getInstance().newTrade();
                                MemberManager.getInstance().newRechargeProcess();
                                mView.finishSelf();
                            }
                        })
                        .setNegativeButton("完成交易", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MemberManager.getInstance().newTrade();
                                MemberManager.getInstance().newRechargeProcess();
                                mView.finishSelf();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast("打印异常:" + error);
                MemberManager.getInstance().newTrade();
                MemberManager.getInstance().newRechargeProcess();
                mView.finishSelf();
            }
        });
    }

    // 扫码支付
    @Override
    public void onRechargeScan() {
        // 跳转到扫码支付页面
        UiNavigation.gotoMemberScanActivity(mContext);
    }

    @Override
    public void onRecharge(int type) {
        if (type == AppConstants.CASHIER_TYPE_ERROR) {
            mView.showToast("充值流程异常...");
            return;
        }
        doInputPassword(type);
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
                    case AppConstants.CASHIER_TYPE_POS:
                        onRechargePos();
                        break;
                    case AppConstants.CASHIER_TYPE_XMD_ONLINE:
                        onRechargeScan();
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
