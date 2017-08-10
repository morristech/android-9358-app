package com.xmd.cashier.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.google.zxing.WriterException;
import com.google.zxing.client.android.MyQrEncoder;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.UiNavigation;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.ErrCode;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.bean.ClubQrcodeBytes;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.VerificationItem;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.GetMemberInfo;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkException;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-5-26.
 */

public class TradeManager {
    private Trade mTrade;
    private IPos mPos;
    public AtomicBoolean mInPosPay = new AtomicBoolean(false);
    private static TradeManager mInstance = new TradeManager();

    public static TradeManager getInstance() {
        return mInstance;
    }

    private TradeManager() {
        mPos = PosFactory.getCurrentCashier();
        newTrade();
    }

    public void newTrade() {
        mTrade = new Trade();
    }

    // 当前交易
    public Trade getCurrentTrade() {
        return mTrade;
    }

    //检查POS收银程序是否在支付，如果在支付则切换到POS机收银程序
    public boolean checkAndProcessPosStatus(Context context) {
        if (mInPosPay.get()) {
            String packageName = CashierManager.getInstance().getCashierAppPackageName();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(Integer.MAX_VALUE);
            if (runningTaskInfoList.size() > 0) {
                for (ActivityManager.RunningTaskInfo taskInfo : runningTaskInfoList) {
                    if (taskInfo.topActivity.getPackageName().equals(packageName)) {
                        activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_NO_USER_ACTION);
                        return true;
                    }
                }
            }
            mInPosPay.set(false);
        }
        return false;
    }

    //汇报交易信息
    public void reportTradeDataSync(int tradeStatus) {
        mTrade.tradeStatus = tradeStatus;
        mTrade.tradeTime = DateUtils.doDate2String(new Date());
        DataReportManager.getInstance().reportData(mTrade, AppConstants.REPORT_DATA_BIZ_TRADE);
    }

    /*******************************************支付相关********************************************/
    // 生成订单号:可用来生成交易流水
    public Subscription fetchTradeNo(final Callback<GetTradeNoResult> callback) {
        Observable<GetTradeNoResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getTradeNo(AccountManager.getInstance().getToken(), mTrade.getOriginMoney(), formatCouponList(mTrade.getCouponList()), RequestConstant.DEFAULT_SIGN_VALUE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<GetTradeNoResult>() {
            @Override
            public void onCallbackSuccess(GetTradeNoResult result) {
                mTrade.tradeNo = result.getRespData();
                XLogger.i("PosTradeNo : " + mTrade.tradeNo);
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 获取会员信息
    public Subscription fetchMemberInfo(final String memberToken, final Callback<MemberInfo> callback) {
        Observable<GetMemberInfo> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberInfo(AccountManager.getInstance().getToken(), memberToken, RequestConstant.DEFAULT_SIGN_VALUE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<GetMemberInfo>() {
            @Override
            public void onCallbackSuccess(GetMemberInfo result) {
                mTrade.memberPayMethod = AppConstants.MEMBER_PAY_METHOD_SCAN;
                mTrade.memberInfo = result.getRespData();
                mTrade.memberToken = memberToken;
                callback.onSuccess(mTrade.memberInfo);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    public Subscription memberPay(String method, String password, final Callback<MemberRecordResult> callback) {
        Observable<MemberRecordResult> observable = null;
        switch (method) {
            case AppConstants.MEMBER_PAY_METHOD_CODE:
                observable = XmdNetwork.getInstance().getService(SpaService.class)
                        .requestMemberPayment(AccountManager.getInstance().getToken(), String.valueOf(mTrade.getNeedPayMoney()), "consume", "会员消费", String.valueOf(mTrade.memberInfo.id), mTrade.tradeNo, AppConstants.MEMBER_TRADE_TYPE_PAY, password);
                break;
            case AppConstants.MEMBER_PAY_METHOD_SCAN:
                observable = XmdNetwork.getInstance().getService(SpaService.class)
                        .memberPay(AccountManager.getInstance().getToken(), mTrade.memberToken, mTrade.tradeNo, mTrade.getNeedPayMoney(), null, RequestConstant.DEFAULT_SIGN_VALUE);
                break;
            default:
                break;
        }
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberRecordResult>() {
            @Override
            public void onCallbackSuccess(MemberRecordResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 收银台支付
    public void posPay(Context context, final int money, final Callback<Void> callback) {
        if (!mInPosPay.compareAndSet(false, true)) {
            callback.onError("错误，已经进入了支付界面，请重启POS！");
            return;
        }
        mTrade.newCashierTradeNo();
        CashierManager.getInstance().pay(context, mTrade.getPosTradeNo(), money, new PayCallback<Object>() {
            @Override
            public void onResult(String error, Object o) {
                mInPosPay.set(false);
                mTrade.posPayReturn = o;
                if (error == null) {
                    mTrade.posMoney = money;
                    mTrade.posPayResult = AppConstants.PAY_RESULT_SUCCESS;
                    mTrade.posPayTypeString = Utils.getPayTypeString(CashierManager.getInstance().getPayType(o));
                    callback.onSuccess(null);
                } else {
                    if (CashierManager.getInstance().isUserCancel(o)) {
                        mTrade.posPayResult = AppConstants.PAY_RESULT_CANCEL;
                    } else {
                        mTrade.posPayResult = AppConstants.PAY_RESULT_ERROR;
                    }
                    callback.onError(error);
                }
            }
        });
    }

    // 完成支付,处理支付结果
    public void finishPay(final Context context, final int tradeStatus, final Callback0<Void> callback) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        printVerificationList();    //打印核销内容
                        // 更新交易数据
                        mTrade.setCouponDiscountMoney(mTrade.getVerificationSuccessfulMoney());
                        if (mTrade.getWillDiscountMoney() == 0) {
                            mTrade.setDiscountType(AppConstants.DISCOUNT_TYPE_NONE);
                        } else {
                            if (mTrade.getVerificationSuccessfulMoney() + mTrade.getVerificationNoUseTreatMoney() != mTrade.getWillDiscountMoney()) {
                                mTrade.setDiscountType(AppConstants.DISCOUNT_TYPE_USER);
                                mTrade.setUserDiscountMoney(mTrade.getWillDiscountMoney());
                            } else {
                                mTrade.setDiscountType(AppConstants.DISCOUNT_TYPE_COUPON);
                            }
                        }

                        switch (mTrade.currentCashier) {
                            case AppConstants.CASHIER_TYPE_XMD_ONLINE:  //小摩豆买单支付
                                if (tradeStatus == AppConstants.TRADE_STATUS_SUCCESS) {
                                    printOnlinePay(mTrade.onlinePayInfo); // 打印
                                }
                                newTrade();         // 重置
                                break;
                            case AppConstants.CASHIER_TYPE_MEMBER:  //会员支付
                                if (tradeStatus == AppConstants.TRADE_STATUS_SUCCESS && mTrade.isMemberRemain) {
                                    printMemberPay(mTrade.memberRecordInfo);
                                }
                                newTrade();
                                break;
                            case AppConstants.CASHIER_TYPE_POS: //Pos支付
                                reportTradeDataSync(tradeStatus);   //汇报流水
                                if (mTrade.posPoints > 0) {
                                    // 如果送积分
                                    UiNavigation.gotoPointsPhoneActivity(context);
                                } else {
                                    print();
                                    newTrade();
                                }
                                break;
                            default:
                                newTrade();
                                break;
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        callback.onFinished(null);
                    }
                });
    }

    // 支付成功汇报手机号获得积分
    public Subscription gainPoints(String phone, final Callback<Void> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .gainPoints(AccountManager.getInstance().getToken(), mTrade.tradeNo, phone, RequestConstant.DEFAULT_SIGN_VALUE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                callback.onSuccess(null);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    /*******************************************二维码相关******************************************/
    public byte[] getQRCodeSync() {
        if (mTrade.posMoney > 0 && mTrade.posPoints > 0 && TextUtils.isEmpty(mTrade.posPointsPhone)) {
            return getTradeQRCodeSync();
        } else {
            return getClubQRCodeSync();
        }
    }


    // 会所活动二维码
    private byte[] clubQrcodeBytes;

    public byte[] getClubQRCodeSync() {
        final String clubId = AccountManager.getInstance().getClubId();
        ClubQrcodeBytes c = LocalPersistenceManager.getClubQrcode(clubId);
        if (c != null) {
            clubQrcodeBytes = c.data;
            return clubQrcodeBytes;
        }

        Call<StringResult> callGetUrl = XmdNetwork.getInstance().getService(SpaService.class).getClubWXQrcodeURL(AccountManager.getInstance().getClubId());
        XmdNetwork.getInstance().requestSync(callGetUrl, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                Call<ResponseBody> callGetBytes = XmdNetwork.getInstance().getService(SpaService.class).getClubQrcodeByWX(result.getRespData());
                XmdNetwork.getInstance().requestSync(callGetBytes, new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        XLogger.d("getClubQRCodeBytes error:" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            byte[] bitmapBytes = responseBody.bytes();
                            if (bitmapBytes != null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                Matrix matrix = new Matrix();
                                matrix.postScale(240.f / bitmap.getWidth(), 240.f / bitmap.getHeight());
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                                    clubQrcodeBytes = bos.toByteArray();
                                    ClubQrcodeBytes cc = new ClubQrcodeBytes();
                                    cc.data = clubQrcodeBytes;
                                    LocalPersistenceManager.writeClubQrcodeBytes(clubId, cc);
                                } else {
                                    XLogger.e("getClubQRCodeBytes : bitmap.compress failed!");
                                }
                                bitmap.recycle();
                            } else {
                                XLogger.d("getClubQRCodeBytes : can not get qrcode !");
                            }
                        } catch (IOException e) {
                            XLogger.d("getClubQRCodeBytes exception:" + e.getLocalizedMessage());
                        }
                    }
                });
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.d("getClubQRCodeURL error:" + e.getLocalizedMessage());
            }
        });
        return clubQrcodeBytes;
    }

    // 获取交易二维码
    private byte[] tradeQrcodeBytes;

    public byte[] getTradeQRCodeSync() {
        Call<StringResult> tradeCodeCall = XmdNetwork.getInstance().getService(SpaService.class)
                .getTradeQrcode(AccountManager.getInstance().getToken(), mTrade.tradeNo, RequestConstant.DEFAULT_SIGN_VALUE);
        XmdNetwork.getInstance().requestSync(tradeCodeCall, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                String content = result.getRespData();
                if (!TextUtils.isEmpty(content)) {
                    try {
                        Bitmap bitmap = MyQrEncoder.encode(content, 240, 240);
                        if (bitmap != null) {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                                tradeQrcodeBytes = bos.toByteArray();
                            } else {
                                XLogger.d("getTradeQrcode--bitmap compress failed");
                            }
                            bitmap.recycle();
                        } else {
                            XLogger.d("getTradeQrcode--Qrcode encode failed");
                        }
                    } catch (WriterException e) {
                        XLogger.d("getTradeQrcode--Qrcode encode exception:" + e.getLocalizedMessage());
                    }
                } else {
                    XLogger.d("getTradeQrcode--request success && isEmpty");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.d("getTradeQrcode--request error:" + e.getLocalizedMessage());
            }
        });
        return tradeQrcodeBytes;
    }

    /*********************************************核销相关******************************************/
    // 判断输入核销码类型: 同VerifyManager

    // 根据手机号获取可核销的 券+付费预约
    public Subscription getVerifyList(String phone, final Callback<CheckInfoListResult> callback) {
        Observable<CheckInfoListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getCheckInfoList(phone, AccountManager.getInstance().getToken());
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<CheckInfoListResult>() {
            @Override
            public void onCallbackSuccess(CheckInfoListResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 根据核销码获取券信息
    public Subscription getVerifyCoupon(String couponNo, final Callback<CouponResult> callback) {
        Observable<CouponResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getCouponInfo(AccountManager.getInstance().getToken(), couponNo);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<CouponResult>() {
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

    // 根据核销码获取预约信息
    public Subscription getVerifyOrder(String orderNo, final Callback<OrderResult> callback) {
        Observable<OrderResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getPaidOrderInfo(AccountManager.getInstance().getToken(), orderNo);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OrderResult>() {
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

    // 根据核销码获取请客信息
    public Subscription getVerifyTreat(String treatNo, final Callback<CommonVerifyResult> callback) {
        Observable<CommonVerifyResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getCommonVerifyInfo(AccountManager.getInstance().getToken(), treatNo, AppConstants.TYPE_PAY_FOR_OTHER);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<CommonVerifyResult>() {
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

    // 核销选中的券
    public Subscription doVerify(final int originMoney, final Callback<List<VerificationItem>> callback) {
        final List<VerificationItem> verificationItems = mTrade.getCouponList();
        return Observable
                .create(new Observable.OnSubscribe<List<VerificationItem>>() {
                    @Override
                    public void call(Subscriber<? super List<VerificationItem>> subscriber) {
                        for (final VerificationItem v : verificationItems) {
                            if (!v.selected) {
                                continue;
                            }
                            switch (v.type) {
                                case AppConstants.TYPE_COUPON:  //体验券
                                case AppConstants.TYPE_CASH_COUPON: //现金券
                                case AppConstants.TYPE_PAID_COUPON: //点钟券
                                    Call<BaseBean> commonCall = XmdNetwork.getInstance().getService(SpaService.class)
                                            .verifyCommonCall(AccountManager.getInstance().getToken(), v.code);
                                    XmdNetwork.getInstance().requestSync(commonCall, new NetworkSubscriber<BaseBean>() {
                                        @Override
                                        public void onCallbackSuccess(BaseBean result) {
                                            // 核销成功
                                            v.success = true;
                                            v.errorMsg = AppConstants.APP_REQUEST_YES;
                                        }

                                        @Override
                                        public void onCallbackError(Throwable e) {
                                            // 核销失败
                                            v.success = false;
                                            v.errorMsg = e.getLocalizedMessage();
                                            if (e instanceof NetworkException) {
                                                v.errorCode = ErrCode.ERRCODE_NETWORK;
                                            } else if (e instanceof ServerException) {
                                                v.errorCode = ErrCode.ERRCODE_SERVER;
                                            }
                                        }
                                    });
                                    break;
                                case AppConstants.TYPE_DISCOUNT_COUPON:
                                    Call<BaseBean> discountCall = XmdNetwork.getInstance().getService(SpaService.class)
                                            .verifyWithMoneyCall(AccountManager.getInstance().getToken(), String.valueOf(v.couponInfo.originAmount), v.code, v.type);
                                    XmdNetwork.getInstance().requestSync(discountCall, new NetworkSubscriber<BaseBean>() {
                                        @Override
                                        public void onCallbackSuccess(BaseBean result) {
                                            // 核销成功
                                            v.success = true;
                                            v.errorMsg = AppConstants.APP_REQUEST_YES;
                                        }

                                        @Override
                                        public void onCallbackError(Throwable e) {
                                            // 核销失败
                                            v.success = false;
                                            v.errorMsg = e.getLocalizedMessage();
                                            if (e instanceof NetworkException) {
                                                v.errorCode = ErrCode.ERRCODE_NETWORK;
                                            } else if (e instanceof ServerException) {
                                                v.errorCode = ErrCode.ERRCODE_SERVER;
                                            }
                                        }
                                    });
                                    break;
                                case AppConstants.TYPE_ORDER:
                                    // 处理预约
                                    Call<BaseBean> orderCall = XmdNetwork.getInstance().getService(SpaService.class)
                                            .verifyPaidOrderCall(AccountManager.getInstance().getToken(), v.code, AppConstants.PAID_ORDER_OP_VERIFIED);
                                    XmdNetwork.getInstance().requestSync(orderCall, new NetworkSubscriber<BaseBean>() {
                                        @Override
                                        public void onCallbackSuccess(BaseBean result) {
                                            // 核销成功
                                            v.success = true;
                                            v.errorMsg = AppConstants.APP_REQUEST_YES;
                                        }

                                        @Override
                                        public void onCallbackError(Throwable e) {
                                            // 核销失败
                                            v.success = false;
                                            v.errorMsg = e.getLocalizedMessage();
                                            if (e instanceof NetworkException) {
                                                v.errorCode = ErrCode.ERRCODE_NETWORK;
                                            } else if (e instanceof ServerException) {
                                                v.errorCode = ErrCode.ERRCODE_SERVER;
                                            }
                                        }
                                    });
                                    break;
                                default:
                                    break;
                            }
                        }

                        //计算请客券需要核销的金额
                        calculateTreatUseMoney(originMoney);

                        // 处理请客
                        for (final VerificationItem v : verificationItems) {
                            if (!v.selected) {
                                continue;
                            }
                            if (v.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                                if (v.treatInfo.useMoney == 0) {
                                    v.success = true;
                                    continue;
                                }
                                Call<BaseBean> withMoneyCall = XmdNetwork.getInstance().getService(SpaService.class)
                                        .verifyWithMoneyCall(AccountManager.getInstance().getToken(), String.valueOf(v.treatInfo.useMoney), v.treatInfo.authorizeCode, v.type);
                                XmdNetwork.getInstance().requestSync(withMoneyCall, new NetworkSubscriber<BaseBean>() {
                                    @Override
                                    public void onCallbackSuccess(BaseBean result) {
                                        // 核销成功
                                        v.success = true;
                                        v.errorMsg = AppConstants.APP_REQUEST_YES;
                                    }

                                    @Override
                                    public void onCallbackError(Throwable e) {
                                        // 核销失败
                                        v.success = false;
                                        v.errorMsg = e.getLocalizedMessage();
                                        if (e instanceof NetworkException) {
                                            v.errorCode = ErrCode.ERRCODE_NETWORK;
                                        } else if (e instanceof ServerException) {
                                            v.errorCode = ErrCode.ERRCODE_SERVER;
                                        }
                                    }
                                });
                            }
                        }
                        subscriber.onNext(verificationItems);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<VerificationItem>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<VerificationItem> items) {
                        callback.onSuccess(items);
                    }
                });
    }

    /********************************************其他功能********************************************/
    // 核销列表
    public List<VerificationItem> getVerificationList() {
        return mTrade.getCouponList();
    }

    // 清空核销列表
    public void cleanVerificationList() {
        mTrade.cleanCouponList();
    }

    public void printVerificationList() {
        List<VerificationItem> verificationItems = mTrade.getCouponList();
        for (VerificationItem item : verificationItems) {
            if (item.selected && item.success) {
                VerifyManager.getInstance().printByVerifyType(item);
            }
        }
    }

    // 添加优惠券
    public void addVerificationInfo(VerificationItem verificationItem) {
        List<VerificationItem> verificationItems = mTrade.getCouponList();
        if (!verificationItems.contains(verificationItem)) {
            switch (verificationItem.type) {
                case AppConstants.TYPE_PAY_FOR_OTHER:
                    verificationItems.add(0, verificationItem);
                    break;
                default:
                    verificationItems.add(verificationItem);
            }
        }
    }

    // 根据code判断列表中是否存在
    public VerificationItem getVerificationById(String id) {
        for (VerificationItem v : mTrade.getCouponList()) {
            if (v != null && v.code.equals(id)) {
                return v;
            }
        }
        return null;
    }

    // 设置选中的折扣券的消费金额
    public void setDiscountOriginAmount() {
        for (VerificationItem v : mTrade.getCouponList()) {
            if (v.selected && !v.success && AppConstants.TYPE_DISCOUNT_COUPON.equals(v.type)) {
                v.couponInfo.originAmount = mTrade.getOriginMoney();
            }
        }
    }

    // 设置某个核销项的选中状态
    public void setVerificationSelectedStatus(VerificationItem v, boolean selected) {
        int index = mTrade.getCouponList().indexOf(v);
        if (index >= 0) {
            VerificationItem verificationItem = mTrade.getCouponList().get(index);//查找到指定项
            verificationItem.selected = selected;
            // 处理折扣券
            if (AppConstants.TYPE_DISCOUNT_COUPON.equals(verificationItem.type)) {
                verificationItem.couponInfo.originAmount = selected ? mTrade.getOriginMoney() : 0;
            }
        }
    }

    //返回当前选择的优惠金额，单位为分
    public void calculateVerificationValue() {
        calculateVerificationValue(false);
    }

    public void calculateSuccessVerificationValue() {
        calculateVerificationValue(true);
    }

    public boolean haveFailed() {
        for (VerificationItem v : mTrade.getCouponList()) {
            if (v.selected && !v.success) {
                return true;
            }
        }
        return false;
    }

    public boolean haveSelected() {
        for (VerificationItem v : mTrade.getCouponList()) {
            if (v.selected) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param onlySuccess 是否只计算核销成功的
     * @return 核销的优惠金额，单位为分
     */
    private void calculateVerificationValue(boolean onlySuccess) {
        int total = 0;
        int noUseTreatMoney = 0;
        //首先计算出所有优惠券提供的总优惠金额
        int selectCount = 0;
        for (VerificationItem info : mTrade.getCouponList()) {
            if (info.selected && !onlySuccess || info.success) {
                selectCount++;
                switch (info.type) {
                    case AppConstants.TYPE_COUPON:
                    case AppConstants.TYPE_CASH_COUPON:
                    case AppConstants.TYPE_PAID_COUPON:
                    case AppConstants.TYPE_DISCOUNT_COUPON:
                        total += info.couponInfo.getReallyCouponMoney();
                        break;
                    case AppConstants.TYPE_ORDER:
                        total += info.order.downPayment;
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        if (onlySuccess) {
                            total += info.treatInfo.useMoney;
                            noUseTreatMoney += info.treatInfo.amount - info.treatInfo.useMoney;
                        } else {
                            total += info.treatInfo.amount;
                        }
                        break;
                }
            }
        }
        if (!onlySuccess) {
            if (mTrade.getVerificationMoney() != total) {
                //用户重新选择了优惠金额
                mTrade.setWillDiscountMoney(total);
            }
            mTrade.setVerificationMoney(total);
            mTrade.setVerificationCount(selectCount);
            XLogger.i("calculateVerificationValue:" + total);
        } else {
            mTrade.setVerificationSuccessfulMoney(total);
            mTrade.setVerificationNoUseTreatMoney(noUseTreatMoney);
            XLogger.i("calculateVerificationSuccessValue:" + total);
        }
    }

    private void calculateTreatUseMoney(int originMoney) {
        int total = 0;
        //首先计算出所有优惠券提供的总优惠金额
        for (VerificationItem info : mTrade.getCouponList()) {
            if (info.selected && info.success) {
                switch (info.type) {
                    case AppConstants.TYPE_COUPON:
                    case AppConstants.TYPE_CASH_COUPON:
                    case AppConstants.TYPE_PAID_COUPON:
                    case AppConstants.TYPE_DISCOUNT_COUPON:
                        total += info.couponInfo.getReallyCouponMoney();
                        break;
                    case AppConstants.TYPE_ORDER:
                        total += info.order.downPayment;
                        break;
                }
            }
        }
        int leftMoney = originMoney - total;
        //然后计算朋友请客可以抵扣的部分
        for (VerificationItem info : mTrade.getCouponList()) {
            if (info.selected && info.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                //计算实际应该核销的金额
                if (leftMoney > 0) {
                    if (leftMoney <= info.treatInfo.amount) {
                        info.treatInfo.useMoney = leftMoney;
                        break;
                    } else {
                        info.treatInfo.useMoney = info.treatInfo.amount;
                        leftMoney -= info.treatInfo.useMoney;
                    }
                }
            }
        }
    }

    public static String formatCouponList(List<VerificationItem> couponList) {
        StringBuilder result = new StringBuilder();
        for (VerificationItem item : couponList) {
            if (item.selected) {
                switch (item.type) {
                    case AppConstants.TYPE_COUPON:
                    case AppConstants.TYPE_CASH_COUPON:
                    case AppConstants.TYPE_PAID_COUPON:
                    case AppConstants.TYPE_DISCOUNT_COUPON:
                        result.append(item.couponInfo.couponNo + "|");
                        break;
                    case AppConstants.TYPE_ORDER:
                        result.append(item.order.orderNo + "|");
                        break;
                    case AppConstants.TYPE_PAY_FOR_OTHER:
                        result.append(item.treatInfo.authorizeCode + "|");
                        break;
                    default:
                        result.append("0000|");
                        break;
                }
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        } else {
            return null;
        }
    }

    public static String formatCouponResult(List<VerificationItem> couponList) {
        StringBuilder result = new StringBuilder();
        for (VerificationItem item : couponList) {
            if (item.selected) {
                if (item.errorMsg.equals(AppConstants.APP_REQUEST_YES)) {
                    result.append(AppConstants.APP_REQUEST_YES + "|");
                } else {
                    result.append(AppConstants.APP_REQUEST_NO + "|");
                }
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        } else {
            return null;
        }
    }

    /*********************************************打印相关******************************************/
    // 打印会员消费信息
    public void printMemberPay(MemberRecordInfo info) {
        // 客户联小票
        MemberManager.getInstance().printInfo(info, false, false, null);
    }

    // 打印在线买单交易信息
    public void printOnlinePay(OnlinePayInfo info) {
        if (!SPManager.getInstance().getOnlinePaySwitch()) {
            return;
        }
        NotifyManager.getInstance().print(info, false);
    }

    // 打印交易信息
    public void print() {
        if (mTrade.tradeStatus != AppConstants.TRADE_STATUS_SUCCESS) {
            return;
        }
        XLogger.i("print trade info ....");
        mTrade.qrCodeBytes = getQRCodeSync(); //获取二维码
        mPos.printCenter(AccountManager.getInstance().getClubName());
        mPos.printCenter("(消费单)");
        mPos.printDivide();
        mPos.printText("交易号 ：" + mTrade.tradeNo);
        mPos.printText("订单金额：", "￥" + Utils.moneyToStringEx(mTrade.getOriginMoney()), true);
        mPos.printText("减免金额：", "￥" + Utils.moneyToStringEx(mTrade.getReallyDiscountMoney()), true);
        XLogger.i("减扣类型：" + mTrade.getDiscountType());
        switch (mTrade.getDiscountType()) {
            case AppConstants.DISCOUNT_TYPE_COUPON:
                mPos.printText("|--优惠金额：", "￥" + Utils.moneyToStringEx(mTrade.getCouponDiscountMoney()));
                break;
            case AppConstants.DISCOUNT_TYPE_USER:
                mPos.printText("|--手动减免：", "￥" + Utils.moneyToStringEx(mTrade.getUserDiscountMoney()));
                break;
            case AppConstants.DISCOUNT_TYPE_NONE:
                mPos.printText("|--其他优惠：", "￥" + Utils.moneyToStringEx(mTrade.getCouponDiscountMoney() + mTrade.getUserDiscountMoney()));
            default:
                break;
        }
        mPos.printText("实收金额：", "￥" + Utils.moneyToStringEx(mTrade.getPosMoney()), true);
        mPos.printText("|--" + (TextUtils.isEmpty(mTrade.getPosPayTypeString()) ? "其他支付" : mTrade.getPosPayTypeString()) + "：", "￥" + Utils.moneyToStringEx(mTrade.getPosMoney()));
        mPos.printText("交易时间：" + mTrade.tradeTime);
        mPos.printText("收银员  ：" + AccountManager.getInstance().getUser().userName);
        if (mTrade.qrCodeBytes != null) {
            mPos.printBitmap(mTrade.qrCodeBytes);
        }
        if (mTrade.posPoints > 0) {
            if (mTrade.posPointsPhone == null) {
                mPos.printCenter("微信扫描二维码，立即领取" + mTrade.posPoints + "积分");
            } else {
                mPos.printCenter("已赠送" + mTrade.posPoints + "积分到"
                        + Utils.getSecretFormatPhoneNumber(mTrade.posPointsPhone) + ",关注9358立即查看");
            }
        } else {
            mPos.printCenter("扫一扫，关注9358，约技师，享优惠");
        }
        mPos.printEnd();
    }
}
