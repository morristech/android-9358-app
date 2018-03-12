package com.xmd.cashier.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.contract.InnerDiscountContract;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.TradeDiscountInfo;
import com.xmd.cashier.dal.bean.TreatInfo;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.TradeManager;
import com.xmd.cashier.manager.VerifyManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by zr on 17-12-11.
 */

public class InnerDiscountPresenter implements InnerDiscountContract.Presenter {
    private static final String TAG = "InnerDiscountPresenter";
    private Context mContext;
    private InnerDiscountContract.View mView;
    private TradeManager mTradeManager;

    private Subscription mGetVerifyListSubscription;
    private Subscription mGetVerifyCouponSubscription;
    private Subscription mGetVerifyOrderSubscription;
    private Subscription mGetVerifyTreatSubscription;
    private Subscription mGetVerifyServiceItemSubscription;
    private Subscription mGetVerifyTypeSubscription;

    public InnerDiscountPresenter(Context context, InnerDiscountContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mTradeManager = TradeManager.getInstance();
    }

    @Override
    public void onCreate() {
        List<VerificationItem> data = mTradeManager.getVerificationList();
        if (data != null) {
            mView.showVerifyList();
            mView.showVerifyData(data);
        } else {
            mView.hideVerifyList();
        }

        List<TradeDiscountInfo> verified = mTradeManager.getVerifiedList();
        if (verified != null && !verified.isEmpty()) {
            mView.showVerifiedLayout(verified);
        } else {
            mView.hideVerifiedLayout();
        }

        mView.setReductionMoney(mTradeManager.getCurrentTrade().getWillReductionMoney());
        mView.setDiscountMoney(mTradeManager.getCurrentTrade().getWillReductionMoney()
                + mTradeManager.getCurrentTrade().getWillDiscountMoney()
                + mTradeManager.getCurrentTrade().getAlreadyDiscountMoney());

        mView.showFlowData(getVerifyTag());
    }

    @Override
    public void onStart() {

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
    public void confirmDiscount() {
        mView.finishSelf();
    }

    @Override
    public void searchDiscount() {
        if (TextUtils.isEmpty(mView.getSearchContent())) {
            mView.showToast("请输入手机号或核销码");
            return;
        }
        getVerifyType(mView.getSearchContent());
    }

    @Override
    public void onReductionChange() {
        mTradeManager.getCurrentTrade().setWillReductionMoney(mView.getReductionMoney());
        mView.setDiscountMoney(mTradeManager.getCurrentTrade().getWillReductionMoney()
                + mTradeManager.getCurrentTrade().getWillDiscountMoney()
                + mTradeManager.getCurrentTrade().getAlreadyDiscountMoney());
    }

    @Override
    public void onVerifySelect(VerificationItem item, int position) {
        item.selected = !item.selected;
        mTradeManager.setVerificationSelectedStatus(item, item.selected);
        mView.updateVerifyItem(position);
        Trade trade = mTradeManager.getCurrentTrade();
        int verify = mTradeManager.getDiscountAmount(trade.getCouponList());
        trade.setWillDiscountMoney(verify);
        mView.setDiscountMoney(mTradeManager.getCurrentTrade().getWillReductionMoney()
                + mTradeManager.getCurrentTrade().getWillDiscountMoney()
                + mTradeManager.getCurrentTrade().getAlreadyDiscountMoney());
        mView.showFlowData(getVerifyTag());
    }

    @Override
    public void onVerifyClick(VerificationItem item, int position) {
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
    public void onVerifiedClick(String code) {
        // TODO do nothing
    }

    private void getVerifyType(final String number) {
        if (mGetVerifyTypeSubscription != null) {
            mGetVerifyTypeSubscription.unsubscribe();
        }
        mView.showLoading();
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
                        mView.hideLoading();
                        mView.showError("收银过程中暂不支持此类核销");
                        break;
                }
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showToast(error);
            }
        });
    }

    private List<String> getVerifyTag() {
        List<String> temp = new ArrayList<>();
        List<TradeDiscountInfo> verified = mTradeManager.getVerifiedList();
        if (verified != null && !verified.isEmpty()) {
            for (TradeDiscountInfo orderDiscountInfo : verified) {
                temp.add(orderDiscountInfo.checkInfo.title);
            }
        }
        List<VerificationItem> data = mTradeManager.getVerificationList();
        if (data != null && !data.isEmpty()) {
            for (VerificationItem item : data) {
                if (item.selected) {
                    switch (item.type) {
                        case AppConstants.TYPE_COUPON:
                        case AppConstants.TYPE_CASH_COUPON:
                        case AppConstants.TYPE_DISCOUNT_COUPON:
                        case AppConstants.TYPE_PAID_COUPON:
                        case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                            CouponInfo couponInfo = item.couponInfo;
                            temp.add(couponInfo.actTitle);
                            break;
                        case AppConstants.TYPE_ORDER:
                            temp.add("付费预约");
                            break;
                        case AppConstants.TYPE_PAY_FOR_OTHER:
                            temp.add("会员请客");
                            break;
                    }
                }
            }
        }
        return temp;
    }

    private void getList(String phoneNumber) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银核销查询核销列表：" + phoneNumber);
        if (mGetVerifyListSubscription != null) {
            mGetVerifyListSubscription.unsubscribe();
        }
        mGetVerifyListSubscription = mTradeManager.getVerifyList(phoneNumber, new Callback<CheckInfoListResult>() {
            @Override
            public void onSuccess(CheckInfoListResult o) {
                mView.hideLoading();
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
                mView.showVerifyData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    private void getCoupon(String couponNo, final String type) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银核销查询优惠券：" + couponNo);
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
                mView.hideLoading();
                mView.showVerifyData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    private void getServiceItem(String couponNo, final String type) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银核销查询项目券：" + couponNo);
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

                mView.hideLoading();
                mView.hideKeyboard();
                mView.showVerifyData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    private void getOrder(String orderNo, final String type) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银核销查询付费预约：" + orderNo);
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
                mView.hideLoading();
                mView.showVerifyData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }

    private void getTreat(String treatNo, final String type) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网收银核销查询会员请客：" + treatNo);
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
                mView.hideLoading();
                mView.showVerifyData(mTradeManager.getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoading();
                mView.showError(error);
            }
        });
    }
}
