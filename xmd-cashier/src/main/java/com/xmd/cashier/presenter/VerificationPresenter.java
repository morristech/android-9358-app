package com.xmd.cashier.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerificationContract;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.response.CouponQRCodeScanResult;
import com.xmd.cashier.dal.net.response.GetTreatResult;
import com.xmd.cashier.dal.net.response.OrderOrCouponResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.net.response.UserCouponListResult;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.VerificationManager;
import com.xmd.cashier.manager.VerifyManager;

import java.util.List;

import rx.Subscription;

/**
 * Created by heyangya on 16-8-24.
 */

public class VerificationPresenter implements VerificationContract.Presenter {
    private Context mContext;

    private Subscription mUserCouponListSubscription;
    private Subscription mGetOrderOrCouponInfoSubscription;
    private Subscription mGetTreatInfoSubscription;
    private Subscription mGetVerifyTypeSubscription;

    private VerificationContract.View mView;

    private VerificationManager mVerificationManager;

    public VerificationPresenter(Context context, VerificationContract.View view) {
        mContext = context;
        mView = view;
        mView.setPresenter(this);
        mVerificationManager = VerificationManager.getInstance();
    }

    @Override
    public void onCreate() {
        List<VerificationItem> data = mVerificationManager.getVerificationList();
        if (data != null) {
            mView.showVerificationData(data);
        }
    }

    @Override
    public void onStart() {
        if (mVerificationManager.getVerificationList().size() > 0) {
            mView.hideKeyboard();
        }
    }

    @Override
    public void onDestroy() {
        if (mUserCouponListSubscription != null) {
            mUserCouponListSubscription.unsubscribe();
        }

        if (mGetOrderOrCouponInfoSubscription != null) {
            mGetOrderOrCouponInfoSubscription.unsubscribe();
        }

        if (mGetTreatInfoSubscription != null) {
            mGetTreatInfoSubscription.unsubscribe();
        }

        if (mGetVerifyTypeSubscription != null) {
            mGetVerifyTypeSubscription.unsubscribe();
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
            if (VerificationManager.getInstance().getVerificationById(result) != null) {
                XToast.show(mContext.getString(R.string.coupon_info_is_added));
            } else {
                try {
                    CouponQRCodeScanResult qrCodeScanResult = new Gson().fromJson(result, CouponQRCodeScanResult.class);
                    result = qrCodeScanResult.qrNo;
                } catch (Exception ignore) {

                }
                searchNumber(result);
            }
        }
    }

    @Override
    public void onVerificationItemChecked(VerificationItem item, boolean isChecked) {
        mView.hideKeyboard();
        mVerificationManager.setVerificationSelectedStatus(item, isChecked);
    }

    @Override
    public void onVerificationItemClicked(VerificationItem item) {
        mView.hideKeyboard();
        if (item.type.equals(AppConstants.TYPE_COUPON)) {
            UiNavigation.gotoVerificationItemDetailActivity(mContext, item);
        }
    }

    @Override
    public void onNavigationBack() {
        calculatorVerificationMoney();
        mView.finishSelf();
    }

    @Override
    public void onClickOk() {
        calculatorVerificationMoney();
        mView.finishSelf();
    }

    @Override
    public void onClickCleanAll() {
        mVerificationManager.cleanVerificationList();
        mView.showVerificationData(mVerificationManager.getVerificationList());
    }

    private void calculatorVerificationMoney() {
        mVerificationManager.calculateVerificationValue();
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
        if (mGetVerifyTypeSubscription != null) {
            mGetVerifyTypeSubscription.unsubscribe();
        }
        mView.showLoadingView();
        mGetVerifyTypeSubscription = VerifyManager.getInstance().getVerifyType(number, new Callback<StringResult>() {
            @Override
            public void onSuccess(StringResult o) {
                switch (o.respData) {
                    case AppConstants.TYPE_PHONE:
                        // 手机号
                        getUserCouponList(number);
                        break;
                    case AppConstants.TYPE_COUPON:
                    case AppConstants.TYPE_ORDER:
                        // 优惠券 & 付费预约
                        getOrderOrCouponInfo(number, o.respData);
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        // 请客
                        getFriendTreatInfo(number, o.respData);
                        break;
                    default:
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


    //通过用户手机号查询优惠券信息
    private void getUserCouponList(String phoneNumber) {
        if (mUserCouponListSubscription != null) {
            mUserCouponListSubscription.unsubscribe();
        }
        mUserCouponListSubscription = VerificationManager.getInstance().listCoupons(phoneNumber, AppConstants.TYPE_COUPON, new Callback<UserCouponListResult>() {
            @Override
            public void onSuccess(UserCouponListResult o) {
                mView.hideLoadingView();
                if (o.respData.canUseList == null || o.respData.canUseList.size() == 0) {
                    mView.showError("没有查询到任何有效的优惠券！");
                    return;
                }
                for (CouponInfo couponInfo : o.respData.canUseList) {
                    if (couponInfo.isTimeValid()) {
                        VerificationItem verificationItem = new VerificationItem();
                        verificationItem.type = AppConstants.TYPE_COUPON;
                        verificationItem.couponInfo = couponInfo;
                        VerificationManager.getInstance().addVerificationInfo(verificationItem);
                    }
                }
                mView.hideKeyboard();
                mView.showVerificationData(VerificationManager.getInstance().getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }

    //通过优惠码查询信息
    private void getOrderOrCouponInfo(final String number, final String type) {
        if (mGetOrderOrCouponInfoSubscription != null) {
            mGetOrderOrCouponInfoSubscription.unsubscribe();
        }
        mGetOrderOrCouponInfoSubscription = mVerificationManager.getOrderOrCouponView(number, new Callback<OrderOrCouponResult>() {
            @Override
            public void onSuccess(OrderOrCouponResult o) {
                if (o.respData.type.equals(RequestConstant.RESULT_TYPE_COUPON)) {
                    // 优惠券
                    CouponInfo couponInfo = o.respData.userAct;
                    if (couponInfo.isTimeValid()) {
                        if (couponInfo.isPaidValid()) {
                            VerificationItem verificationItem = new VerificationItem();
                            verificationItem.type = type;
                            verificationItem.couponInfo = couponInfo;
                            verificationItem.selected = true;
                            VerificationManager.getInstance().addVerificationInfo(verificationItem);
                        } else {
                            mView.showError("优惠券未支付,请支付后再使用！");
                        }
                    } else {
                        mView.showError("优惠券当前时间不可使用！");
                    }
                } else if (o.respData.type.equals(RequestConstant.RESULT_TYPE_ORDER)) {
                    // 付费预约
                    OrderInfo order = o.respData.order;
                    VerificationItem verificationItem = new VerificationItem();
                    verificationItem.type = type;
                    verificationItem.order = order;
                    verificationItem.order.orderNo = number;
                    verificationItem.selected = true;
                    VerificationManager.getInstance().addVerificationInfo(verificationItem);
                }
                mView.hideLoadingView();
                mView.hideKeyboard();
                mView.showVerificationData(VerificationManager.getInstance().getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }

    // 获取请客信息
    private void getFriendTreatInfo(final String number, final String type) {
        if (mGetTreatInfoSubscription != null) {
            mGetTreatInfoSubscription.unsubscribe();
        }
        mGetTreatInfoSubscription = VerifyManager.getInstance().getTreatInfo(number, new Callback<GetTreatResult>() {
            @Override
            public void onSuccess(GetTreatResult o) {
                VerificationItem verificationItem = new VerificationItem();
                verificationItem.type = type;
                verificationItem.treatInfo = o.respData;
                verificationItem.selected = true;
                VerificationManager.getInstance().addVerificationInfo(verificationItem);
                mView.hideLoadingView();
                mView.hideKeyboard();
                mView.showVerificationData(VerificationManager.getInstance().getVerificationList());
            }

            @Override
            public void onError(String error) {
                mView.hideLoadingView();
                mView.showError(error);
            }
        });
    }
}
