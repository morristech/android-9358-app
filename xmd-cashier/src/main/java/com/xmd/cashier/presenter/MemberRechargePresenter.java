package com.xmd.cashier.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberRechargeContract;
import com.xmd.cashier.dal.bean.PackagePlanItem;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.dal.bean.TradeChannelInfo;
import com.xmd.cashier.dal.event.RechargeDoneEvent;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.MemberPlanResult;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.net.response.MemberUrlResult;
import com.xmd.cashier.dal.net.response.TradeChannelListResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.ChannelManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.widget.ActionSheetDialog;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;
import com.xmd.cashier.widget.InputPasswordDialog;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

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
    private Subscription mGetRechargeChannelSubscription;

    private MemberManager mMemberManager;
    private ChannelManager mChannelManager;

    public MemberRechargePresenter(Context context, MemberRechargeContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mMemberManager = MemberManager.getInstance();
        mChannelManager = ChannelManager.getInstance();
    }

    @Override
    public void onCreate() {
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
        if (mGetRechargeChannelSubscription != null) {
            mGetRechargeChannelSubscription.unsubscribe();
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
    public void onRechargeDone() {
        switch (mMemberManager.tradeStatus) {
            case AppConstants.TRADE_STATUS_SUCCESS:
                mMemberManager.printMemberRecordInfoAsync(mMemberManager.recordInfo, false);
                mMemberManager.newRechargeProcess();
                mView.finishSelf();
                break;
            case AppConstants.TRADE_STATUS_ERROR:
                mView.showError(mMemberManager.tradeStatusError);
                break;
            default:
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("充值交易状态未知，详情请查看会员账户列表！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mView.finishSelf();
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public void onConfirm() {
        // 处理条件判断
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
                onChannel();
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                // 充值套餐
                if (mMemberManager.getPackageInfo() == null) {
                    mView.showError("请添加充值套餐!");
                    return;
                }
                onChannel();
                break;
        }
    }

    // 获取会所支付方式
    private void onChannel() {
        if (mChannelManager.getTradeChannelInfos() != null && !mChannelManager.getTradeChannelInfos().isEmpty()) {
            mChannelManager.formatRechargeChannel();
            showDialog();
        } else {
            XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "会员充值获取会所支付方式：" + RequestConstant.URL_GET_PAY_CHANNEL_LIST);
            mView.showLoading();
            if (mGetRechargeChannelSubscription != null) {
                mGetRechargeChannelSubscription.unsubscribe();
            }
            mGetRechargeChannelSubscription = mChannelManager.getPayChannelList(new Callback<TradeChannelListResult>() {
                @Override
                public void onSuccess(TradeChannelListResult o) {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "会员充值获取会所支付方式---成功");
                    mView.hideLoading();
                    mChannelManager.formatRechargeChannel();
                    showDialog();
                }

                @Override
                public void onError(String error) {
                    XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "会员充值获取会所支付方式---失败：" + error);
                    mView.hideLoading();
                    mView.showToast("获取支付方式失败：" + error);
                }
            });
        }
    }

    // 支付方式选项
    private void showDialog() {
        ActionSheetDialog dialog = new ActionSheetDialog(mContext);
        dialog.setContents(mChannelManager.getTradeChannelTexts());
        dialog.setCancelText("取消");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setEventListener(new ActionSheetDialog.OnEventListener() {
            @Override
            public void onActionItemClick(ActionSheetDialog dialog, String item, int position) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值选择支付方式：" + item);
                TradeChannelInfo channel = mChannelManager.getCurrentChannelByText(item);
                if (channel == null) {
                    mView.showError("选择支付方式出现未知异常");
                } else {
                    mMemberManager.setCurrentChannel(channel);
                    onRecharge();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelItemClick(ActionSheetDialog dialog) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值选择支付方式后取消");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // 作输入密码限制
    private void onRecharge() {
        switch (mMemberManager.currentChannelType) {
            case AppConstants.PAY_CHANNEL_QRCODE:
            case AppConstants.PAY_CHANNEL_WX:
            case AppConstants.PAY_CHANNEL_ALI:
            case AppConstants.PAY_CHANNEL_UNION:
                doRechargeRequest(null);
                break;
            case AppConstants.PAY_CHANNEL_CASH:// 现金及其他记账
            default:
                if (!AppConstants.APP_REQUEST_NO.equals(mMemberManager.getVerificationSwitch())) {
                    // 需要输入收银员密码
                    doInputPassword();
                } else {
                    // 无需校验密码
                    doRechargeRequest(null);
                }
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

    // 发起会员充值请求
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
                switch (mMemberManager.currentChannelType) {
                    case AppConstants.PAY_CHANNEL_UNION:
                        //Pos支付
                        doPosCashier();
                        break;
                    case AppConstants.PAY_CHANNEL_QRCODE:
                    case AppConstants.PAY_CHANNEL_WX:
                    case AppConstants.PAY_CHANNEL_ALI:
                        // 扫码支付
                        UiNavigation.gotoTradeQrcodePayActivity(mContext, AppConstants.TRADE_TYPE_RECHARGE);
                        break;
                    case AppConstants.PAY_CHANNEL_CASH:
                    default:
                        //现金记账支付
                        UiNavigation.gotoTradeMarkPayActivity(mContext, AppConstants.TRADE_TYPE_RECHARGE);
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
                startReportRecharge();
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付---失败：" + error);
                mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_ERROR;
                mMemberManager.tradeStatusError = error;
                EventBus.getDefault().post(new RechargeDoneEvent());
            }
        });
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
                        mMemberManager.currentChannelType,
                        mMemberManager.getTradeNo(),
                        RequestConstant.DEFAULT_SIGN_VALUE);
        XmdNetwork.getInstance().requestSync(callReportRecharge, new NetworkSubscriber<MemberRecordResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付成功后汇报支付结果---成功");
                PosFactory.getCurrentCashier().speech("充值成功");
                resultReportRecharge = true;
                mMemberManager.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;
                mMemberManager.recordInfo = result.getRespData();
                EventBus.getDefault().post(new RechargeDoneEvent());
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值旺Pos渠道支付成功后汇报支付结果---失败：" + e.getLocalizedMessage());
                resultReportRecharge = false;
            }
        });
        return resultReportRecharge;
    }
}
