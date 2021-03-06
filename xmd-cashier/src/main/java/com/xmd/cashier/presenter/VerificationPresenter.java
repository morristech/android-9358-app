package com.xmd.cashier.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerificationContract;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.TreatInfo;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponQRCodeScanResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.manager.VerifyManager;

import java.util.List;

import rx.Subscription;

/**
 * Created by heyangya on 16-8-24.
 */

public class VerificationPresenter implements VerificationContract.Presenter {
    private final static String TAG = "VerificationPresenter";
    private Context mContext;

    private Subscription mGetVerifyListSubscription;
    private Subscription mGetVerifyCouponSubscription;
    private Subscription mGetVerifyOrderSubscription;
    private Subscription mGetVerifyTreatSubscription;
    private Subscription mGetVerifyServiceItemSubscription;
    private Subscription mGetVerifyTypeSubscription;

    private VerificationContract.View mView;

    private TradeManager mTradeManager;

    public VerificationPresenter(Context context, VerificationContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        List<VerificationItem> data = mTradeManager.getVerificationList();
        if (data != null) {
            mView.showVerificationData(data);
        }
    }

    @Override
    public void onStart() {
        if (mTradeManager.getVerificationList().size() > 0) {
            mView.hideKeyboard();
        }
    }

    @Override
    public void onDestroy() {
        if (mGetVerifyListSubscription != null) {
            mGetVerifyListSubscription.unsubscribe();
        }
        if (mGetVerifyCouponSubscription != null) {
            mGetVerifyCouponSubscription.unsubscribe();
        }
        if (mGetVerifyOrderSubscription != null) {
            mGetVerifyOrderSubscription.unsubscribe();
        }
        if (mGetVerifyTreatSubscription != null) {
            mGetVerifyTreatSubscription.unsubscribe();
        }
        if (mGetVerifyTypeSubscription != null) {
            mGetVerifyTypeSubscription.unsubscribe();
        }
        if (mGetVerifyServiceItemSubscription != null) {
            mGetVerifyServiceItemSubscription.unsubscribe();
        }
    }

    @Override
    public void onClickSearch() {
        searchNumber(mView.getNumber());
    }

    @Override
    public void onClickScan() {
        mView.hideKeyboard();
        Intent intent = new Intent(mContext, CaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.WIDTH, 480);
        intent.putExtra(Intents.Scan.HEIGHT, 640);
        ((Activity) mContext).startActivityForResult(intent, 1);
    }

    @Override
    public void onScanResult(Intent intent) {
        if (intent != null && intent.getAction().equals(Intents.Scan.ACTION)) {
            String result = intent.getStringExtra(Intents.Scan.RESULT);

            if (TextUtils.isEmpty(result)) {
                mView.showToast("二维码扫描失败");
                return;
            }

            // 需要解析二维码结果：为json需要解析，否则长度>=12的为核销码，其他为无效二维码
            XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款核销二维码扫码结果：" + result);
            if (Utils.checkJson(result)) {
                //Json解析
                try {
                    CouponQRCodeScanResult qrCodeScanResult = new Gson().fromJson(result, CouponQRCodeScanResult.class);
                    result = qrCodeScanResult.qrNo;
                    if (TextUtils.isEmpty(result)) {
                        mView.showToast("二维码解析失败");
                    } else {
                        if (mTradeManager.getVerificationById(result) != null) {
                            mView.showToast(mContext.getString(R.string.coupon_info_is_added));
                        } else {
                            searchNumber(result);
                        }
                    }
                } catch (Exception ignore) {
                    mView.showToast("二维码解析异常");
                }
            } else if (Utils.checkCode(result)) {
                //核销码
                if (mTradeManager.getVerificationById(result) != null) {
                    mView.showToast(mContext.getString(R.string.coupon_info_is_added));
                } else {
                    searchNumber(result);
                }
            } else {
                //其他
                mView.showToast("无效二维码");
            }
        }
    }

    @Override
    public void onVerificationItemChecked(VerificationItem item, boolean isChecked) {
        // 核销项选中或者未选中
        mView.hideKeyboard();
        mTradeManager.setVerificationSelectedStatus(item, isChecked);
    }

    @Override
    public void onVerificationItemClicked(VerificationItem item) {
        mView.hideKeyboard();
        // 根据不同的类型跳转到相应的详情页
        switch (item.type) {
            case AppConstants.TYPE_COUPON:
            case AppConstants.TYPE_CASH_COUPON:
            case AppConstants.TYPE_PAID_COUPON:
            case AppConstants.TYPE_DISCOUNT_COUPON:
            case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                UiNavigation.gotoVerifyCouponActivity(mContext, item.couponInfo, false);
                break;
            case AppConstants.TYPE_ORDER:
                UiNavigation.gotoVerifyOrderActivity(mContext, item.order, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNavigationBack() {
        updateWillDiscountMoney();
        mView.finishSelf();
    }

    @Override
    public void onClickOk() {
        updateWillDiscountMoney();
        mView.finishSelf();
    }

    private void updateWillDiscountMoney() {
        Trade trade = mTradeManager.getCurrentTrade();
        trade.setWillDiscountMoney(mTradeManager.getDiscountAmount(trade.getCouponList()));
        trade.setVerificationCount(mTradeManager.getDiscountCount(trade.getCouponList()));
    }

    @Override
    public void onClickCleanAll() {
        mTradeManager.cleanVerificationList();
        mView.showVerificationData(mTradeManager.getVerificationList());
    }

    private void searchNumber(String number) {
        number = number.replace(" ", "");
        if (!Utils.checkSearchNumber(number)) {
            mView.showError("输入的手机号或者优惠码有误!");
            return;
        }
        getVerifyType(number);
    }

    private void getVerifyType(final String number) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款核销查询核销码类型：" + number);
        if (mGetVerifyTypeSubscription != null) {
            mGetVerifyTypeSubscription.unsubscribe();
        }
        mView.showLoadingView();
        mGetVerifyTypeSubscription = VerifyManager.getInstance().getVerifyType(number, new Callback<StringResult>() {
            @Override
            public void onSuccess(StringResult o) {
                switch (o.getRespData()) {
                    case AppConstants.TYPE_PHONE:
                        // 手机号
                        getList(number);
                        break;
                    case AppConstants.TYPE_COUPON:  //体验券
                    case AppConstants.TYPE_CASH_COUPON: //现金券
                    case AppConstants.TYPE_DISCOUNT_COUPON: //折扣券
                    case AppConstants.TYPE_PAID_COUPON:     //点钟券
                        getCoupon(number, o.getRespData());
                        break;
                    case AppConstants.TYPE_ORDER:
                        // 预约
                        getOrder(number, o.getRespData());
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        // 请客
                        getTreat(number, o.getRespData());
                        break;
                    case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                        // 项目券
                        getServiceItem(number, o.getRespData());
                        break;
                    default:    // 收银不处理礼品券
                        mView.hideLoadingView();
                        mView.showError("收银过程中暂不支持此类核销");
                        break;
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }

    private void getList(String phoneNumber) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款核销查询核销列表：" + phoneNumber);
        if (mGetVerifyListSubscription != null) {
            mGetVerifyListSubscription.unsubscribe();
        }
        mGetVerifyListSubscription = mTradeManager.getVerifyList(phoneNumber, new Callback<CheckInfoListResult>() {
            @Override
            public void onSuccess(CheckInfoListResult o) {
                mView.hideLoadingView();
                if (o.getRespData() == null || o.getRespData().isEmpty()) {
                    mView.showError("未查询到有效的优惠信息");
                    return;
                }
                Gson gson = new Gson();
                for (CheckInfo info : o.getRespData()) {
                    if (info.getValid()) {
                        // 可用
                        switch (info.getType()) {
                            case AppConstants.TYPE_COUPON:  //体验券
                            case AppConstants.TYPE_CASH_COUPON: //现金券
                            case AppConstants.TYPE_DISCOUNT_COUPON: //折扣券
                            case AppConstants.TYPE_PAID_COUPON: //点钟券
                            case AppConstants.TYPE_SERVICE_ITEM_COUPON://项目券
                                if (info.getInfo() instanceof String) {
                                    info.setInfo(gson.fromJson((String) info.getInfo(), CouponInfo.class));
                                } else {
                                    info.setInfo(gson.fromJson(gson.toJson(info.getInfo()), CouponInfo.class));
                                }
                                CouponInfo couponInfo = (CouponInfo) info.getInfo();
                                couponInfo.valid = info.getValid();
                                VerificationItem couponItem = new VerificationItem();
                                couponItem.code = info.getCode();
                                couponItem.type = info.getType();
                                couponItem.couponInfo = couponInfo;
                                mTradeManager.addVerificationInfo(couponItem);
                                break;
                            case AppConstants.TYPE_ORDER:   // 付费预约
                                if (info.getInfo() instanceof String) {
                                    info.setInfo(gson.fromJson((String) info.getInfo(), OrderInfo.class));
                                } else {
                                    info.setInfo(gson.fromJson(gson.toJson(info.getInfo()), OrderInfo.class));
                                }
                                VerificationItem orderItem = new VerificationItem();
                                orderItem.code = info.getCode();
                                orderItem.type = info.getType();
                                orderItem.order = (OrderInfo) info.getInfo();
                                mTradeManager.addVerificationInfo(orderItem);
                                break;
                            default:
                                // 收银不处理:礼品券
                                break;
                        }
                    }
                }
                mView.hideKeyboard();
                mView.showVerificationData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }

    private void getCoupon(String couponNo, final String type) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款核销查询优惠券：" + couponNo);
        if (mGetVerifyCouponSubscription != null) {
            mGetVerifyCouponSubscription.unsubscribe();
        }
        mGetVerifyCouponSubscription = mTradeManager.getVerifyCoupon(couponNo, new Callback<CouponResult>() {
            @Override
            public void onSuccess(CouponResult o) {
                CouponInfo info = o.getRespData();
                info.valid = true;
                VerificationItem item = new VerificationItem();
                item.code = info.couponNo;
                item.type = type;
                item.couponInfo = info;
                item.selected = true;
                mTradeManager.addVerificationInfo(item);

                mView.hideLoadingView();
                mView.hideKeyboard();
                mView.showVerificationData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }

    private void getServiceItem(String couponNo, final String type) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款核销查询项目券：" + couponNo);
        if (mGetVerifyServiceItemSubscription != null) {
            mGetVerifyServiceItemSubscription.unsubscribe();
        }
        mGetVerifyServiceItemSubscription = mTradeManager.getVerifyServiceItem(couponNo, new Callback<CouponResult>() {
            @Override
            public void onSuccess(CouponResult o) {
                CouponInfo info = o.getRespData();
                info.valid = true;
                VerificationItem item = new VerificationItem();
                item.code = info.couponNo;
                item.type = type;
                item.couponInfo = info;
                item.selected = true;
                mTradeManager.addVerificationInfo(item);

                mView.hideLoadingView();
                mView.hideKeyboard();
                mView.showVerificationData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }

    private void getOrder(String orderNo, final String type) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款核销查询付费预约：" + orderNo);
        if (mGetVerifyOrderSubscription != null) {
            mGetVerifyOrderSubscription.unsubscribe();
        }
        mGetVerifyOrderSubscription = mTradeManager.getVerifyOrder(orderNo, new Callback<OrderResult>() {
            @Override
            public void onSuccess(OrderResult o) {
                OrderInfo info = o.getRespData();
                VerificationItem item = new VerificationItem();
                item.code = info.orderNo;
                item.type = type;
                item.order = info;
                item.selected = true;
                mTradeManager.addVerificationInfo(item);
                mView.hideLoadingView();
                mView.hideKeyboard();
                mView.showVerificationData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }

    private void getTreat(String treatNo, final String type) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "补收款核销查询会员请客：" + treatNo);
        if (mGetVerifyTreatSubscription != null) {
            mGetVerifyTreatSubscription.unsubscribe();
        }
        mGetVerifyTreatSubscription = mTradeManager.getVerifyTreat(treatNo, new Callback<CommonVerifyResult>() {
            @Override
            public void onSuccess(CommonVerifyResult o) {
                TreatInfo info = new TreatInfo();
                info.authorizeCode = o.getRespData().code;
                info.amount = Integer.parseInt(o.getRespData().info.amount);
                info.userName = o.getRespData().userName;
                info.userPhone = o.getRespData().userPhone;
                info.setExtraMemberInfo(o.getRespData().info.extra);
                VerificationItem item = new VerificationItem();
                item.code = info.authorizeCode;
                item.type = type;
                item.treatInfo = info;
                item.selected = true;
                mTradeManager.addVerificationInfo(item);
                mView.hideLoadingView();
                mView.hideKeyboard();
                mView.showVerificationData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }
}
