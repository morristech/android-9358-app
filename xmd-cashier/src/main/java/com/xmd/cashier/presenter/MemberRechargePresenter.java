package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MemberRechargeContract;
import com.xmd.cashier.dal.bean.MemberPlanInfo;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.MemberPlanResult;
import com.xmd.cashier.dal.net.response.MemberUrlResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;
import rx.Subscription;

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
        loadPlanData();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        MemberManager.getInstance().stopGetRechargeResult();
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

    private void doPosCashier() {
        MemberManager.getInstance().posRecharge(mContext, (MemberManager.getInstance().getAmount() > 0) ? MemberManager.getInstance().getAmount() : MemberManager.getInstance().getPackageAmount(), new Callback<Void>() {
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
        // 支付成功
        MemberManager.getInstance().reportTrade();
        MemberManager.getInstance().startGetRechargeResult();
    }


    @Override
    public void onRechargeScan() {
        // 跳转到扫码支付页面
        UiNavigation.gotoMemberScanActivity(mContext);
    }

    @Override
    public void onRecharge(final int type) {
        if (type == AppConstants.CASHIER_TYPE_ERROR) {
            mView.showToast("充值流程异常...");
            return;
        }
        MemberManager.getInstance().setRechargePayType(type);
        MemberManager.getInstance().setDescription("会员卡充值");
        // 扫码支付
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mRechargeByScanSubscription != null) {
            mRechargeByScanSubscription.unsubscribe();
        }
        mView.showLoading();
        mRechargeByScanSubscription = MemberManager.getInstance().requestRecharge(new Callback<MemberUrlResult>() {
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
            MemberManager.getInstance().setAmount(Utils.stringToMoney(amount));
        }
    }

    @Override
    public void clearAmount() {
        MemberManager.getInstance().setAmount(0);
        mView.clearAmount();
    }

    @Override
    public void onPackageSet(MemberPlanInfo.PackagePlanItem item) {
        // 设置套餐
        MemberManager.getInstance().setPackageId(String.valueOf(item.id));
        MemberManager.getInstance().setPackageAmount(item.amount);
        MemberManager.getInstance().setPackageName(item.name);
    }

    @Override
    public void clearPackage() {
        MemberManager.getInstance().setPackageId(null);
        MemberManager.getInstance().setPackageName(null);
        MemberManager.getInstance().setPackageAmount(0);
        mView.clearPackage();
    }

    @Override
    public void onReportResult(MemberRecordInfo info) {
        mView.hideLoading();
        MemberManager.getInstance().printInfo(info, false);
        MemberManager.getInstance().newTrade();
        MemberManager.getInstance().newRechargeProcess();
        mView.finishSelf();
    }
}