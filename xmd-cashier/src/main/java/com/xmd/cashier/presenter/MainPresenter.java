package com.xmd.cashier.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MainContract;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.SwitchInfo;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponQRCodeScanResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.PrizeResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.MemberManager;
import com.xmd.cashier.manager.VerifyManager;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscription;

/**
 * Created by heyangya on 16-8-24.
 */

public class MainPresenter implements MainContract.Presenter {
    private static final String TAG = "MainPresenter";

    private final static int REQUEST_CODE_SCAN = 1;

    private Context mContext;
    private MainContract.View mView;

    private Subscription mGetVerifyTypeSubscription;    //查询核销类型
    private Subscription mGetOrderInfoSubscription;     //预约订单
    private Subscription mGetNormalCouponInfoSubscription;  //券
    private Subscription mGetServiceCouponInfoSubscription; //项目券
    private Subscription mGetPrizeInfoSubscription; //奖品
    private Subscription mGetCommonVerifySubscription;  //通用核销+请客

    public MainPresenter(Context context, MainContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onVerifyClick() {
        // 核销按钮
        onCodeCheck(mView.getVerifyCode().replace(" ", ""));
    }

    @Override
    public void onScanClick() {
        // 扫码核销
        XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "扫码核销");
        Intent intent = new Intent(mContext, CaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.WIDTH, 480);
        intent.putExtra(Intents.Scan.HEIGHT, 640);
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onClickDrawer() {
        mView.onNavigationMenu();
    }

    @Override
    public void onClickLogout() {
        AccountManager.getInstance().logout();
        UiNavigation.gotoLoginActivity(mContext);
        mView.finishSelf();
    }

    @Override
    public void onClickSetting() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "访问收银台系统设置");
        UiNavigation.gotoSettingActivity(mContext);
    }

    @Override
    public void onClickVersion() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "访问收银台配置信息");
        UiNavigation.gotoConfigurationActivity(mContext);
    }

    @Override
    public void onCashierLayoutClick() {
        if (InnerManager.getInstance().getInnerSwitch()) {
            //内网开启
            UiNavigation.gotoInnerSelectActivity(mContext);
        } else {
            //原始流程
            UiNavigation.gotoCashierActivity(mContext);
        }
    }

    @Override
    public void onMemberLayoutClick() {
        if (!AppConstants.APP_REQUEST_NO.equals(MemberManager.getInstance().getMemberSwitch())) {
            UiNavigation.gotoMemberNavigationActivity(mContext);
        } else {
            mView.showError("会所会员功能未开通!");
        }
    }

    @Override
    public void onOrderLayoutClick() {
        UiNavigation.gotoOrderRecordActivity(mContext);
    }

    @Override
    public void onOnlinePayLayoutClick() {
        UiNavigation.gotoOnlinePayActivity(mContext);
    }

    @Override
    public void onRecordLayoutClick() {
        UiNavigation.gotoRecordNavigationActivity(mContext);
    }

    @Override
    public void onAssistCashierLayoutClick() {
        UiNavigation.gotoCashierActivity(mContext); //原始收银流程
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SCAN:
                // 二维码扫描
                if (data != null && data.getAction().equals(Intents.Scan.ACTION)) {
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "首页核销扫码结果：" + result);
                    if (TextUtils.isEmpty(result)) {
                        mView.showToast("二维码扫描失败");
                    }
                    // 需要解析二维码结果：为json需要解析，否则长度>=12的为核销码，其他为无效二维码
                    if (Utils.checkJson(result)) {
                        //Json解析
                        try {
                            CouponQRCodeScanResult qrCodeScanResult = new Gson().fromJson(result, CouponQRCodeScanResult.class);
                            result = qrCodeScanResult.qrNo;
                            if (TextUtils.isEmpty(result)) {
                                mView.showToast("二维码解析失败");
                            } else {
                                onCodeCheck(result);
                            }
                        } catch (Exception ignore) {
                            mView.showToast("二维码解析异常");
                        }
                    } else if (Utils.checkCode(result)) {
                        //核销码
                        onCodeCheck(result);
                    } else {
                        //其他
                        mView.showToast("无效二维码");
                    }
                }
                break;
            default:
                break;
        }
    }

    private void onCodeCheck(String code) {
        mView.showLoading();
        if (!Utils.checkSearchNumber(code)) {
            mView.hideLoading();
            mView.showToast("请输入有效的核销信息");
            return;
        }
        getVerifyType(code);
    }

    // 获取核销类型
    private void getVerifyType(final String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mGetVerifyTypeSubscription != null) {
            mGetVerifyTypeSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询核销类型：" + RequestConstant.URL_GET_VERIFY_TYPE + " (" + code + ") ");
        mGetVerifyTypeSubscription = VerifyManager.getInstance().getVerifyType(code, new Callback<StringResult>() {
            @Override
            public void onSuccess(StringResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询核销类型---成功：" + o.getRespData());
                switch (o.getRespData()) {
                    case AppConstants.TYPE_PHONE:   //手机号
                        mView.hideLoading();
                        UiNavigation.gotoVerifyCheckInfoActivity(mContext, code);
                        break;
                    case AppConstants.TYPE_COUPON:  // 体验券
                    case AppConstants.TYPE_CASH_COUPON: //现金券
                    case AppConstants.TYPE_GIFT_COUPON: //礼品券
                    case AppConstants.TYPE_PAID_COUPON: //点钟券
                    case AppConstants.TYPE_DISCOUNT_COUPON: //折扣券
                        getNormalCouponInfo(code);
                        break;
                    case AppConstants.TYPE_ORDER:   //预约订单
                        getOrderInfo(code);
                        break;
                    case AppConstants.TYPE_SERVICE_ITEM_COUPON: //项目券
                        getServiceCouponInfo(code);
                        break;
                    case AppConstants.TYPE_LUCKY_WHEEL: //大转盘:奖品
                        getPrizeInfo(code);
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:   //会员请客
                    default:    //默认核销
                        getCommonVerifyInfo(code);
                        break;
                }
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询核销类型---失败：" + error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    // 查询优惠券
    private void getNormalCouponInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mGetNormalCouponInfoSubscription != null) {
            mGetNormalCouponInfoSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询优惠券：" + RequestConstant.URL_INFO_COUPON + " (" + code + ") ");
        mGetNormalCouponInfoSubscription = VerifyManager.getInstance().getCouponInfo(code, new Callback<CouponResult>() {
            @Override
            public void onSuccess(CouponResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询优惠券---成功");
                mView.hideLoading();
                CouponInfo couponInfo = o.getRespData();
                couponInfo.valid = true;
                UiNavigation.gotoVerifyCouponActivity(mContext, couponInfo, true);
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询优惠券---失败：" + error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    // 查询预约订单
    private void getOrderInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }

        if (mGetOrderInfoSubscription != null) {
            mGetOrderInfoSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询付费预约：" + RequestConstant.URL_INFO_PAID_ORDER + " (" + code + ") ");
        mGetOrderInfoSubscription = VerifyManager.getInstance().getPaidOrderInfo(code, new Callback<OrderResult>() {
            @Override
            public void onSuccess(OrderResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询付费预约---成功");
                mView.hideLoading();
                UiNavigation.gotoVerifyOrderActivity(mContext, o.getRespData(), true);
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询付费预约---失败：" + error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }


    // 查询项目券
    private void getServiceCouponInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mGetServiceCouponInfoSubscription != null) {
            mGetServiceCouponInfoSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询项目券：" + RequestConstant.URL_INFO_SERVICE_ITEM + " (" + code + ") ");
        mGetServiceCouponInfoSubscription = VerifyManager.getInstance().getServiceCouponInfo(code, new Callback<CouponResult>() {
            @Override
            public void onSuccess(CouponResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询项目券---成功");
                mView.hideLoading();
                CouponInfo couponInfo = o.getRespData();
                couponInfo.valid = true;
                UiNavigation.gotoVerifyCouponActivity(mContext, couponInfo, true);
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询项目券---失败：" + error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    // 查询奖品
    private void getPrizeInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mGetPrizeInfoSubscription != null) {
            mGetPrizeInfoSubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询奖品：" + RequestConstant.URL_INFO_LUCKY_WHEEL + " (" + code + ") ");
        mGetPrizeInfoSubscription = VerifyManager.getInstance().getPrizeInfo(code, new Callback<PrizeResult>() {
            @Override
            public void onSuccess(PrizeResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询奖品---成功");
                mView.hideLoading();
                UiNavigation.gotoVerifyPrizeActivity(mContext, o.getRespData());
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询奖品---失败：" + error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    // 查询通用核销和请客
    private void getCommonVerifyInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError(mContext.getString(R.string.network_disabled));
            return;
        }
        if (mGetCommonVerifySubscription != null) {
            mGetCommonVerifySubscription.unsubscribe();
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询通用核销和请客：" + RequestConstant.URL_INFO_COMMON + " (" + code + ") ");
        mGetCommonVerifySubscription = VerifyManager.getInstance().getCommonVerifyInfo(code, null, new Callback<CommonVerifyResult>() {
            @Override
            public void onSuccess(CommonVerifyResult o) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询通用核销和请客---成功");
                mView.hideLoading();
                UiNavigation.gotoVerifyCommonActivity(mContext, o.getRespData());
            }

            @Override
            public void onError(String error) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MAIN_VERIFY + "查询通用核销和请客---失败：" + error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    @Override
    public boolean onKeyEventBack() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "是否退出9358收银台程序？");
        new CustomAlertDialogBuilder(mContext)
                .setMessage(R.string.main_exit_confirm)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "取消退出，继续使用！");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "确认退出收银台程序！");
                        dialog.dismiss();
                        mView.finishSelf();
                    }
                })
                .create()
                .show();
        return true;
    }

    @Override
    public void onCreate() {
        EventBusSafeRegister.register(this);
        mView.showLoading();
        initPos();
        updateAssistCashier();
    }

    private void initPos() {
        //初始化支付环境
        CashierManager.getInstance().init(mContext, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                mView.hideLoading();
                mView.showToast("初始化支付环境成功！");
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                XLogger.e(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "初始化支付环境失败: " + error);
                new CustomAlertDialogBuilder(mContext)
                        .setMessage("初始化支付环境错误：" + error + "\n请联系开发人员或者重新打开应用！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainApplication.getInstance().exitApplication();
                                mView.finishSelf();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    @Override
    public void onStart() {
        //显示侧边栏信息
        mView.showUserName(AccountManager.getInstance().getUser().userName);
        mView.showVersionName(Utils.getAppVersionName());
        mView.showClubName(AccountManager.getInstance().getClubName());
        mView.showClubIcon(AccountManager.getInstance().getClubIcon());
    }

    @Override
    public void onDestroy() {
        EventBusSafeRegister.unregister(this);
        mView.hideLoading();
        if (mGetVerifyTypeSubscription != null) {
            mGetVerifyTypeSubscription.unsubscribe();
        }
        if (mGetServiceCouponInfoSubscription != null) {
            mGetServiceCouponInfoSubscription.unsubscribe();
        }
        if (mGetPrizeInfoSubscription != null) {
            mGetPrizeInfoSubscription.unsubscribe();
        }
        if (mGetCommonVerifySubscription != null) {
            mGetCommonVerifySubscription.unsubscribe();
        }
        if (mGetNormalCouponInfoSubscription != null) {
            mGetNormalCouponInfoSubscription.unsubscribe();
        }
        if (mGetOrderInfoSubscription != null) {
            mGetOrderInfoSubscription.unsubscribe();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SwitchInfo info) {
        updateAssistCashier();
    }

    private void updateAssistCashier() {
        mView.updateAssistCashier(InnerManager.getInstance().getInnerSwitch());
    }
}
