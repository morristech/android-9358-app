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
import com.shidou.commonlibrary.network.OkHttpUtil;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.MainContract;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponQRCodeScanResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.LogoutResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.PrizeResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.CashierManager;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.manager.UpdateManager;
import com.xmd.cashier.manager.VerifyManager;
import com.xmd.cashier.pos.PosImpl;
import com.xmd.cashier.service.CustomService;
import com.xmd.cashier.widget.CustomAlertDialogBuilder;

import rx.Subscription;

/**
 * Created by heyangya on 16-8-24.
 */

public class MainPresenter implements MainContract.Presenter {
    private final static int REQUEST_CODE_SCAN = 1;

    private Context mContext;
    private MainContract.View mView;

    private Subscription mLogoutSubscription;
    private Subscription mGetVerifyTypeSubscription;    //查询核销类型
    private Subscription mGetOrderInfoSubscription;     //预约订单
    private Subscription mGetNormalCouponInfoSubscription;  //券
    private Subscription mGetServiceCouponInfoSubscription; //项目券
    private Subscription mGetPrizeInfoSubscription; //奖品
    private Subscription mGetCommonVerifySubscription;  //通用核销+请客

    private TradeManager mTradeManager = TradeManager.getInstance();

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
        // 退出登录
        CustomService.refreshOnlinePayNotify(false);
        CustomService.refreshOrderRecordNotify(false);
        VerifyManager.getInstance().clearVerifyList();
        if (mLogoutSubscription != null) {
            mLogoutSubscription.unsubscribe();
        }
        mTradeManager.newTrade();
        UiNavigation.gotoLoginActivity(mContext);
        mView.finishSelf();
        mLogoutSubscription = AccountManager.getInstance().logout(new Callback<LogoutResult>() {
            @Override
            public void onSuccess(LogoutResult o) {
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    @Override
    public void onClickSetting() {
        UiNavigation.gotoSettingActivity(mContext);
    }

    @Override
    public void onClickVersion() {
        // do nothing
    }

    @Override
    public void onCashierLayoutClick() {
        UiNavigation.gotoCashierActivity(mContext);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SCAN:
                // 二维码扫描
                if (data != null && data.getAction().equals(Intents.Scan.ACTION)) {
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    XLogger.e(result);
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

    // 获取券类型
    private void getVerifyType(final String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mGetVerifyTypeSubscription != null) {
            mGetVerifyTypeSubscription.unsubscribe();
        }
        mGetVerifyTypeSubscription = VerifyManager.getInstance().getVerifyType(code, new Callback<StringResult>() {
            @Override
            public void onSuccess(StringResult o) {
                switch (o.respData) {
                    case AppConstants.TYPE_PHONE:
                        // 手机号:核销券和付费预约
                        mView.hideLoading();
                        UiNavigation.gotoVerifyCheckInfoActivity(mContext, code);
                        break;
                    case AppConstants.TYPE_COUPON:
                        // 券
                        getNormalCouponInfo(code);
                        break;
                    case AppConstants.TYPE_ORDER:
                        // 预约订单
                        getOrderInfo(code);
                        break;
                    case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                        // 项目券
                        getServiceCouponInfo(code);
                        break;
                    case AppConstants.TYPE_LUCKY_WHEEL:
                        // 大转盘
                        getPrizeInfo(code);
                        break;
                    default:
                        // 通用
                        getCommonVerifyInfo(code);
                        break;
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    // 查询优惠券
    private void getNormalCouponInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mGetNormalCouponInfoSubscription != null) {
            mGetNormalCouponInfoSubscription.unsubscribe();
        }
        mGetNormalCouponInfoSubscription = VerifyManager.getInstance().getCouponInfo(code, new Callback<CouponResult>() {
            @Override
            public void onSuccess(CouponResult o) {
                mView.hideLoading();
                UiNavigation.gotoVerifyCouponActivity(mContext, o.respData, true);
            }

            @Override
            public void onError(String error) {
                XLogger.i(error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    // 查询预约订单
    private void getOrderInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常，请检查网络后重试");
            return;
        }

        if (mGetOrderInfoSubscription != null) {
            mGetOrderInfoSubscription.unsubscribe();
        }

        mGetOrderInfoSubscription = VerifyManager.getInstance().getPaidOrderInfo(code, new Callback<OrderResult>() {
            @Override
            public void onSuccess(OrderResult o) {
                mView.hideLoading();
                UiNavigation.gotoVerifyOrderActivity(mContext, o.respData, true);
            }

            @Override
            public void onError(String error) {
                XLogger.i(error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }


    // 查询项目券
    private void getServiceCouponInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mGetServiceCouponInfoSubscription != null) {
            mGetServiceCouponInfoSubscription.unsubscribe();
        }

        mGetServiceCouponInfoSubscription = VerifyManager.getInstance().getServiceCouponInfo(code, new Callback<CouponResult>() {
            @Override
            public void onSuccess(CouponResult o) {
                mView.hideLoading();
                UiNavigation.gotoVerifyCouponActivity(mContext, o.respData, true);
            }

            @Override
            public void onError(String error) {
                XLogger.i(error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    // 查询奖品
    private void getPrizeInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mGetPrizeInfoSubscription != null) {
            mGetPrizeInfoSubscription.unsubscribe();
        }

        mGetPrizeInfoSubscription = VerifyManager.getInstance().getPrizeInfo(code, new Callback<PrizeResult>() {
            @Override
            public void onSuccess(PrizeResult o) {
                mView.hideLoading();
                UiNavigation.gotoVerifyPrizeActivity(mContext, o.respData);
            }

            @Override
            public void onError(String error) {
                XLogger.i(error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    // 查询通用核销和请客
    private void getCommonVerifyInfo(String code) {
        if (!Utils.isNetworkEnabled(mContext)) {
            mView.hideLoading();
            mView.showError("网络异常，请检查网络后重试");
            return;
        }
        if (mGetCommonVerifySubscription != null) {
            mGetCommonVerifySubscription.unsubscribe();
        }

        mGetCommonVerifySubscription = VerifyManager.getInstance().getCommonVerifyInfo(code, null, new Callback<CommonVerifyResult>() {
            @Override
            public void onSuccess(CommonVerifyResult o) {
                mView.hideLoading();
                UiNavigation.gotoVerifyCommonActivity(mContext, o.respData);
            }

            @Override
            public void onError(String error) {
                XLogger.i(error);
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    @Override
    public boolean onKeyEventBack() {
        new CustomAlertDialogBuilder(mContext)
                .setMessage(R.string.main_exit_confirm)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        mView.showLoading();
        if (CashierManager.getInstance().needCheckUpate()) {
            UpdateManager.getInstance().checkUpdate(mContext, CashierManager.getInstance().getAppCode(), AccountManager.getInstance().getUserId(), new Callback<Void>() {
                @Override
                public void onSuccess(Void o) {
                    initPos();
                }

                @Override
                public void onError(String error) {
                    XLogger.e("update error:" + error);
                    initPos();
                }
            });
        } else {
            initPos();
        }
    }

    private void initPos() {
        //初始化支付环境
        CashierManager.getInstance().init(mContext, new Callback<Void>() {
            @Override
            public void onSuccess(Void o) {
                mView.hideLoading();
                mView.showToast("初始化支付环境成功！");
                // 初始化成功之后添加请求头
                OkHttpUtil.getInstance().setCommonHeader("Device-Identifier", PosImpl.getInstance().getPosIdentifierNo());
                // 开始轮询
                CustomService.refreshOnlinePayNotify(true);
                CustomService.refreshOrderRecordNotify(true);
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
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
        mView.hideLoading();
        if (mLogoutSubscription != null) {
            mLogoutSubscription.unsubscribe();
        }
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
        if (CashierManager.getInstance().needCheckUpate()) {
            UpdateManager.getInstance().cancel();
        }
    }
}
