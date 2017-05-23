package com.xmd.cashier.manager;

import com.google.gson.Gson;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.ErrCode;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.net.NetworkSubscriber;
import com.xmd.cashier.dal.net.SpaRetrofit;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.GetTreatResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.PrizeResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.net.response.VerifyRecordDetailResult;
import com.xmd.cashier.dal.net.response.VerifyRecordResult;
import com.xmd.cashier.dal.net.response.VerifyTypeResult;
import com.xmd.cashier.exceptions.NetworkException;
import com.xmd.cashier.exceptions.ServerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-5-19.
 */

public class VerifyManager {
    private List<CheckInfo> mVerifyList;

    private static VerifyManager mInstance = new VerifyManager();

    public static VerifyManager getInstance() {
        return mInstance;
    }

    private VerifyManager() {
        mVerifyList = new ArrayList<>();
    }

    /**********************************************************************************************/
    public List<CheckInfo> getVerifyList() {
        return mVerifyList;
    }

    public void clearVerifyList() {
        if (mVerifyList != null) {
            mVerifyList.clear();
        }
    }

    public List<CheckInfo> getResultList() {
        List<CheckInfo> resultList = new ArrayList<>();
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && !info.getSuccess()) {
                resultList.add(info);
            }
        }
        return resultList;
    }

    public boolean hasFailed() {
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && !info.getSuccess()) {
                return true;
            }
        }
        return false;
    }

    public int getSelectedCount() {
        int count = 0;
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected()) {
                count++;
            }
        }
        return count;
    }

    public void setItemSelectedStatus(CheckInfo info, boolean selected) {
        int index = mVerifyList.indexOf(info);
        if (index >= 0) {
            mVerifyList.get(index).setSelected(selected);
        }
    }

    public int getSuccessCount() {
        int count = 0;
        for (CheckInfo info : mVerifyList) {
            if (info.getSelected() && info.getSuccess()) {
                count++;
            }
        }
        return count;
    }

    public int getFailedCount() {
        return getSelectedCount() - getSuccessCount();
    }

    /********************************************* 核销记录 ****************************************/
    // 核销记录类型列表
    public Subscription getVerifyTypeList(final Callback<VerifyTypeResult> callback) {
        return SpaRetrofit.getService().getVerifyTypeList(AccountManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<VerifyTypeResult>() {
                    @Override
                    public void onCallbackSuccess(VerifyTypeResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // 核销记录列表
    public Subscription getVerifyRecordList(int page, int pageSize, String telephone, String type, String startDate, String endDate, final Callback<VerifyRecordResult> callback) {
        return SpaRetrofit.getService().getVerifyRecordList(AccountManager.getInstance().getToken(), String.valueOf(page), String.valueOf(pageSize), telephone, type, AppConstants.APP_REQUEST_YES, startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<VerifyRecordResult>() {
                    @Override
                    public void onCallbackSuccess(VerifyRecordResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // 核销记录详情
    public Subscription getVerifyRecordDetail(String recordId, final Callback<VerifyRecordDetailResult> callback) {
        return SpaRetrofit.getService().getVerifyRecordDetail(AccountManager.getInstance().getToken(), recordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<VerifyRecordDetailResult>() {
                    @Override
                    public void onCallbackSuccess(VerifyRecordDetailResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    /***************************************** 核销详情 ********************************************/
    // ---根据核销码查询核销码类型---
    public Subscription getVerifyType(String code, final Callback<StringResult> callback) {
        return SpaRetrofit.getService().getVerifyType(AccountManager.getInstance().getToken(), code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<StringResult>() {
                    @Override
                    public void onCallbackSuccess(StringResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---根据手机号获取核销列表:券+付费预约---
    public Subscription getCheckInfoList(String code, final Callback<CheckInfoListResult> callback) {
        return SpaRetrofit.getService().getCheckInfoList(code, AccountManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<CheckInfoListResult>() {
                    @Override
                    public void onCallbackSuccess(CheckInfoListResult result) {
                        // 处理没有数据的情况
                        if (result == null || result.respData == null || result.respData.isEmpty()) {
                            callback.onError("暂未查询到可用的核销信息");
                            return;
                        }

                        // 处理内容
                        Gson gson = new Gson();
                        for (CheckInfo info : result.respData) {
                            switch (info.getType()) {
                                case AppConstants.TYPE_COUPON:
                                case AppConstants.TYPE_PAID_COUPON:
                                case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                                    // 券
                                    if (info.getInfo() instanceof String) {
                                        info.setInfo(gson.fromJson((String) info.getInfo(), CouponInfo.class));
                                    } else {
                                        info.setInfo(gson.fromJson(gson.toJson(info.getInfo()), CouponInfo.class));
                                    }
                                    break;
                                case AppConstants.TYPE_ORDER:
                                    // 付费预约
                                    if (info.getInfo() instanceof String) {
                                        info.setInfo(gson.fromJson((String) info.getInfo(), OrderInfo.class));
                                    } else {
                                        info.setInfo(gson.fromJson(gson.toJson(info.getInfo()), OrderInfo.class));
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        // 排序
                        Collections.sort(result.respData, new Comparator<CheckInfo>() {
                            @Override
                            public int compare(CheckInfo lhs, CheckInfo rhs) {
                                if (lhs.getValid() && rhs.getValid()) {
                                    return 0;
                                } else if (lhs.getValid()) {
                                    return -1;
                                } else if (rhs.getValid()) {
                                    return 1;
                                }
                                return 0;
                            }
                        });
                        mVerifyList = result.respData;
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---查询核销详情：优惠券---
    public Subscription getCouponInfo(String couponNo, final Callback<CouponResult> callback) {
        return SpaRetrofit.getService().getCouponInfo(AccountManager.getInstance().getToken(), couponNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<CouponResult>() {
                    @Override
                    public void onCallbackSuccess(CouponResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---查询核销详情：付费预约---
    public Subscription getPaidOrderInfo(String orderNo, final Callback<OrderResult> callback) {
        return SpaRetrofit.getService().getPaidOrderInfo(AccountManager.getInstance().getToken(), orderNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<OrderResult>() {
                    @Override
                    public void onCallbackSuccess(OrderResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---查询核销详情：奖品---
    public Subscription getPrizeInfo(String verifyCode, final Callback<PrizeResult> callback) {
        return SpaRetrofit.getService().getPrizeInfo(AccountManager.getInstance().getToken(), verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<PrizeResult>() {
                    @Override
                    public void onCallbackSuccess(PrizeResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---查询核销详情：项目券---
    public Subscription getServiceCouponInfo(String couponNo, final Callback<CouponResult> callback) {
        return SpaRetrofit.getService().getServiceCouponInfo(AccountManager.getInstance().getToken(), couponNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<CouponResult>() {
                    @Override
                    public void onCallbackSuccess(CouponResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---查询核销详情：默认---
    public Subscription getCommonVerifyInfo(String code, String type, final Callback<CommonVerifyResult> callback) {
        return SpaRetrofit.getService().getCommonVerifyInfo(AccountManager.getInstance().getToken(), code, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<CommonVerifyResult>() {
                    @Override
                    public void onCallbackSuccess(CommonVerifyResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // 获取请客授权信息  long long ago
    public Subscription getTreatInfo(String treatNo, final Callback<GetTreatResult> callback) {
        return SpaRetrofit.getService().getTreatInfo(AccountManager.getInstance().getToken(), AppConstants.SESSION_TYPE, treatNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<GetTreatResult>() {
                    @Override
                    public void onCallbackSuccess(GetTreatResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    /******************************************** 处理核销 *****************************************/
    // ---核销：优惠券---
    public Subscription verifyCoupon(String couponNo, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().verifyCoupon(AccountManager.getInstance().getToken(), couponNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BaseResult>() {
                    @Override
                    public void onCallbackSuccess(BaseResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---核销：付费预约---
    public Subscription verifyPaidOrder(String orderNo, String processType, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().verifyPaidOrder(AccountManager.getInstance().getToken(), orderNo, processType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BaseResult>() {
                    @Override
                    public void onCallbackSuccess(BaseResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---核销：奖品---
    public Subscription verifyLuckyWheel(String verifyCode, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().verifyLuckyWheel(AccountManager.getInstance().getToken(), verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BaseResult>() {
                    @Override
                    public void onCallbackSuccess(BaseResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---核销：项目券---
    public Subscription verifyServiceCoupon(String couponNo, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().verifyServiceCoupon(AccountManager.getInstance().getToken(), couponNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BaseResult>() {
                    @Override
                    public void onCallbackSuccess(BaseResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---核销：默认---
    public Subscription verifyCommon(int amount, String code, String type, final Callback<BaseResult> callback) {
        return SpaRetrofit.getService().verifyCommon(AccountManager.getInstance().getToken(), String.valueOf(amount), code, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BaseResult>() {
                    @Override
                    public void onCallbackSuccess(BaseResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    // ---核销:批量---
    public Subscription verifyCheckInfo(final Callback<List<CheckInfo>> callback) {
        final List<CheckInfo> infos = mVerifyList;
        return Observable
                .create(new Observable.OnSubscribe<List<CheckInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<CheckInfo>> subscriber) {
                        for (final CheckInfo info : infos) {
                            if (!info.getSelected() || info.getSuccess()) {
                                continue;
                            }
                            SpaRetrofit.getService().verifyCommon(AccountManager.getInstance().getToken(), null, info.getCode(), null).subscribe(new NetworkSubscriber<BaseResult>() {
                                @Override
                                public void onCallbackSuccess(BaseResult result) {
                                    info.setErrorCode(result.statusCode);
                                    info.setSuccess(true);
                                    info.setErrorMsg(AppConstants.APP_REQUEST_YES);
                                }

                                @Override
                                public void onCallbackError(Throwable e) {
                                    info.setSuccess(false);
                                    info.setErrorMsg(e.getLocalizedMessage());
                                    if (e instanceof NetworkException) {
                                        info.setErrorCode(ErrCode.ERRCODE_NETWORK);
                                    } else if (e instanceof ServerException) {
                                        info.setErrorCode(ErrCode.ERRCODE_SERVER);
                                    }
                                }
                            });
                        }
                        subscriber.onNext(infos);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CheckInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<CheckInfo> list) {
                        callback.onSuccess(list);
                    }
                });
    }
}