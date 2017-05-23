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
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.LocalPersistenceManager;
import com.xmd.cashier.dal.bean.ClubQrcodeBytes;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.NetworkSubscriber;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaOkHttp;
import com.xmd.cashier.dal.net.SpaRetrofit;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.dal.net.response.GetMemberInfo;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.MemberPayResult;
import com.xmd.cashier.dal.net.response.OnlinePayDetailResult;
import com.xmd.cashier.dal.net.response.StringResult;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by heyangya on 16-8-23.
 */

public class TradeManager {
    private static TradeManager mInstance = new TradeManager();
    private CashierManager mPosManager = CashierManager.getInstance();
    private Trade mTrade;
    private byte[] clubQrcodeBytes;

    private TradeManager() {
        newTrade();
    }

    public static TradeManager getInstance() {
        return mInstance;
    }

    public Trade getCurrentTrade() {
        return mTrade;
    }

    public AtomicBoolean mInPosPay = new AtomicBoolean(false);

    public void newTrade() {
        XLogger.i("===new trade===");
        mTrade = new Trade();
    }

    /**
     * 生成Pos在线买单单号
     */
    public Subscription fetchOnlinePayId(final Callback<StringResult> callback) {
        return SpaRetrofit.getService().getXMDOnlineOrderId(AccountManager.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<StringResult>() {
                    @Override
                    public void onCallbackSuccess(StringResult result) {
                        mTrade.tradeNo = result.respData;
                        mTrade.tradeTime = DateUtils.doDate2String(new Date());
                        XLogger.i("online pay order id: " + mTrade.tradeNo);
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    /**
     * 查询Pos买单详情
     */
    public Subscription getXMDOnlinePayDetail(String orderId, final Callback<OnlinePayDetailResult> callback) {
        return SpaRetrofit.getService().getXMDOnlinePayDetail(AccountManager.getInstance().getToken(), orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<OnlinePayDetailResult>() {
                    @Override
                    public void onCallbackSuccess(OnlinePayDetailResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    /**
     * 查询二维码扫码状态
     */
    public Subscription getXMDScanStatus(String orderId, final Callback<StringResult> callback) {
        return SpaRetrofit.getService().getXMDOnlineScanStatus(AccountManager.getInstance().getToken(), orderId)
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


    /**
     * 生成订单号
     */
    public Subscription fetchTradeNo(final Callback<GetTradeNoResult> callback) {
        return SpaRetrofit.getService().getTradeNo(
                AccountManager.getInstance().getToken(),
                mTrade.getOriginMoney(),
                VerificationManager.formatCouponList(mTrade.getCouponList()),
                RequestConstant.DEFAULT_SIGN_VALUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<GetTradeNoResult>() {
                    @Override
                    public void onCallbackSuccess(GetTradeNoResult result) {
                        mTrade.tradeNo = result.respData;
                        XLogger.i("pos trade number: " + mTrade.tradeNo);
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    /**
     * 获取会员信息
     */
    public Subscription fetchMemberInfo(final String memberToken, final Callback<MemberInfo> callback) {
        return SpaRetrofit.getService().getMemberInfo(AccountManager.getInstance().getToken(),
                memberToken, RequestConstant.DEFAULT_SIGN_VALUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<GetMemberInfo>() {

                    @Override
                    public void onCallbackSuccess(GetMemberInfo result) {
                        mTrade.memberInfo = result.respData;
                        mTrade.memberInfo.token = memberToken;
                        callback.onSuccess(mTrade.memberInfo);
                        XLogger.i("get member info:" + mTrade.memberInfo.toString());
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }


    /**
     * 会员支付
     */
    public Subscription memberPay(final Callback<MemberPayResult.PayResult> callback) {
        return SpaRetrofit.getService().memberPay(
                AccountManager.getInstance().getToken(),
                mTrade.memberInfo.token,
                mTrade.tradeNo,
                mTrade.memberNeedPayMoney,
                mTrade.memberCanDiscount,
                "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<MemberPayResult>() {

                    @Override
                    public void onCallbackSuccess(MemberPayResult result) {
                        mTrade.setMemberPaidMoney(result.respData.payMoney);
                        mTrade.memberPoints = result.respData.creditAmount;
                        mTrade.memberPayCertificate = result.respData.tradeId;
                        mTrade.memberPayResult = AppConstants.PAY_RESULT_SUCCESS;
                        callback.onSuccess(result.respData);
                        XLogger.i("member pay success,payMoney:" + mTrade.getMemberPaidMoney() + ",creditAmount:" + mTrade.memberPoints);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }

    /**
     * 收银台支付
     */
    public void posPay(Context context, final int money, final Callback<Void> callback) {
        if (!mInPosPay.compareAndSet(false, true)) {
            callback.onError("错误，已经进入了支付界面，请重启POS！");
            return;
        }
        mTrade.newCashierTradeNo();
        mPosManager.pay(context,
                mTrade.getPosTradeNo(),
                money,
                new PayCallback<Object>() {
                    @Override
                    public void onResult(String error, Object o) {
                        mInPosPay.set(false);
                        mTrade.posPayReturn = o;
                        if (error == null) {
                            mTrade.posMoney = money;
                            mTrade.posPayResult = AppConstants.PAY_RESULT_SUCCESS;
                            mTrade.posPayTypeString = Utils.getPayTypeString(mPosManager.getPayType(o));
                            callback.onSuccess(null);
                        } else {
                            if (mPosManager.isUserCancel(o)) {
                                mTrade.posPayResult = AppConstants.PAY_RESULT_CANCEL;
                            } else {
                                mTrade.posPayResult = AppConstants.PAY_RESULT_ERROR;
                            }
                            callback.onError(error);
                        }
                    }
                });
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

    //汇报手机号获得积分
    public Subscription gainPoints(String phone, final Callback<Void> callback) {
        return SpaRetrofit.getService().gainPoints(AccountManager.getInstance().getToken(),
                mTrade.tradeNo,
                phone,
                RequestConstant.DEFAULT_SIGN_VALUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<BaseResult>() {
                    @Override
                    public void onCallbackSuccess(BaseResult result) {
                        callback.onSuccess(null);
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });
    }


    /*************************************************************************************************/
    // 打印在线买单交易信息
    public void printOnlinePay() {
        if (mTrade.tradeStatus != AppConstants.TRADE_STATUS_SUCCESS) {
            return;
        }
        mTrade.qrCodeBytes = getClubQRCodeSync();
        mPosManager.printTextCenter(AccountManager.getInstance().getClubName());
        mPosManager.printTextCenter("(结账单)");
        mPosManager.printDivide();

        mPosManager.printText("消费", Utils.moneyToStringEx(mTrade.getOriginMoney()) + " 元");
        mPosManager.printText("减免", Utils.moneyToStringEx(mTrade.getCouponDiscountMoney() + mTrade.getUserDiscountMoney()) + " 元");

        mPosManager.printDivide();
        mPosManager.printTextRight("实收 " + Utils.moneyToStringEx(mTrade.getOnlinePayPaidMoney()) + " 元");
        mPosManager.printDivide();

        mPosManager.printText("交易号:", mTrade.tradeNo);
        mPosManager.printText("付款方式:", "小摩豆在线买单");
        mPosManager.printText("交易时间:", mTrade.tradeTime);
        mPosManager.printText("打印时间:", DateUtils.doDate2String(new Date()));
        mPosManager.printText("收银员:", AccountManager.getInstance().getUser().userName);

        if (mTrade.qrCodeBytes != null) {
            mPosManager.printBitmap(mTrade.qrCodeBytes);
        }
        mPosManager.printTextCenter("-- 微信扫码，选技师，抢优惠 --");
        mPosManager.printEnd();
    }

    // 打印交易信息
    public void print() {
        if (mTrade.tradeStatus != AppConstants.TRADE_STATUS_SUCCESS) {
            return;
        }
        XLogger.i("print trade info ....");
        mTrade.qrCodeBytes = getQRCodeSync(); //获取二维码
        mPosManager.printText("交易号 ：" + mTrade.tradeNo);

        mPosManager.printText("订单金额：", "￥" + Utils.moneyToStringEx(mTrade.getOriginMoney()));

        mPosManager.printText("减免金额：", "￥" + Utils.moneyToStringEx(mTrade.getReallyDiscountMoney() + mTrade.getMemberPaidDiscountMoney()));
        XLogger.i("减扣类型：" + mTrade.getDiscountType());
        switch (mTrade.getDiscountType()) {
            case Trade.DISCOUNT_TYPE_COUPON:
                mPosManager.printText("|--优惠金额：", "￥" + Utils.moneyToStringEx(mTrade.getCouponDiscountMoney()));
                break;
            case Trade.DISCOUNT_TYPE_USER:
                mPosManager.printText("|--手动减免：", "￥" + Utils.moneyToStringEx(mTrade.getUserDiscountMoney()));
                break;
            case Trade.DISCOUNT_TYPE_NONE:
                mPosManager.printText("|--其他优惠：", "￥" + Utils.moneyToStringEx(mTrade.getCouponDiscountMoney() + mTrade.getUserDiscountMoney()));
            default:
                break;
        }
        mPosManager.printText("|--会员折扣：", "￥" + Utils.moneyToStringEx(mTrade.getMemberPaidDiscountMoney()));

        mPosManager.printText("实收金额：", "￥" + Utils.moneyToStringEx(mTrade.getPosMoney() + mTrade.getMemberPaidMoney()));
        mPosManager.printText("|--" + (TextUtils.isEmpty(mTrade.getPosPayTypeString()) ? "其他支付" : mTrade.getPosPayTypeString()) + "：", "￥" + Utils.moneyToStringEx(mTrade.getPosMoney()));
        mPosManager.printText("|--会员支付：", "￥" + Utils.moneyToStringEx(mTrade.getMemberPaidMoney()));

        if (mTrade.memberPoints > 0) {
            mPosManager.printText("获赠会员积分：" + mTrade.memberPoints);
        }
        mPosManager.printText("交易时间：" + mTrade.tradeTime);
        mPosManager.printText("收银员  ：" + AccountManager.getInstance().getUser().userName);
        if (mTrade.qrCodeBytes != null) {
            mPosManager.printBitmap(mTrade.qrCodeBytes);
        }
        if (mTrade.posPoints > 0) {
            if (mTrade.posPointsPhone == null) {
                mPosManager.printTextCenter("微信扫描二维码，立即领取" + mTrade.posPoints + "积分");
            } else {
                mPosManager.printTextCenter("已赠送" + mTrade.posPoints + "积分到"
                        + Utils.getSecretFormatPhoneNumber(mTrade.posPointsPhone) + ",关注9358立即查看");
            }
        } else {
            mPosManager.printTextCenter("扫一扫，关注9358，约技师，享优惠");
        }
        mPosManager.printEnd();
    }


    //汇报交易信息
    public void reportTradeDataSync(int tradeStatus) {
        mTrade.tradeStatus = tradeStatus;
        mTrade.tradeTime = DateUtils.doDate2String(new Date());
        DataReportManager.getInstance().reportData(mTrade);
    }

    public byte[] getQRCodeSync() {
        if (mTrade.posMoney > 0 && mTrade.posPoints > 0 && TextUtils.isEmpty(mTrade.posPointsPhone)) {
            // 交易二维码
            return getTradeQRCodeSync();
        } else {
            // 会所活动二维码
            return getClubQRCodeSync();
        }
    }

    public byte[] getTradeQRCodeSync() {
        byte[] result = null;
        String contents = SpaOkHttp.getTradeQrcode(AccountManager.getInstance().getToken(), mTrade.tradeNo);
        if (!TextUtils.isEmpty(contents)) {
            try {
                Bitmap bitmap = MyQrEncoder.encode(contents, 240, 240);
                if (bitmap != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                        result = bos.toByteArray();
                    } else {
                        XLogger.e("bitmap.compress failed!");
                    }
                    bitmap.recycle();
                } else {
                    XLogger.e("qr code encode failed!");
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    public byte[] getClubQRCodeSync() {
        final String clubId = AccountManager.getInstance().getClubId();
        ClubQrcodeBytes c = LocalPersistenceManager.getClubQrcode(clubId);
        if (c != null) {
            clubQrcodeBytes = c.data;
            return clubQrcodeBytes;
        }
        SpaRetrofit.getService().getClubWXQrcode(AccountManager.getInstance().getClubId(), mTrade.tradeNo)
                .subscribe(new Action1<StringResult>() {
                    @Override
                    public void call(StringResult stringResult) {
                        byte[] bitmapBytes = SpaOkHttp.getClubWXQrcode(stringResult.respData);
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
                                XLogger.e("bitmap.compress failed!");
                            }
                            bitmap.recycle();
                        } else {
                            XLogger.e("can not get qrcode !");
                        }
                    }
                });
        return clubQrcodeBytes;
    }

    public void finishPay(final Context context, final int tradeStatus, final Callback0<Void> callback) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        mTrade.setCouponDiscountMoney(mTrade.getVerificationSuccessfulMoney());
                        //设置减扣类型
                        if (mTrade.getWillDiscountMoney() == 0) {
                            mTrade.setDiscountType(Trade.DISCOUNT_TYPE_NONE);
                        } else {
                            if (mTrade.getVerificationSuccessfulMoney() + mTrade.getVerificationNoUseTreatMoney() != mTrade.getWillDiscountMoney()) {
                                mTrade.setDiscountType(Trade.DISCOUNT_TYPE_USER);
                                mTrade.setUserDiscountMoney(mTrade.getWillDiscountMoney());
                            } else {
                                mTrade.setDiscountType(Trade.DISCOUNT_TYPE_COUPON);
                            }
                        }

                        switch (mTrade.currentCashier) {
                            case AppConstants.CASHIER_TYPE_XMD_ONLINE:
                                mTrade.tradeStatus = tradeStatus;
                                printOnlinePay();   // 打印
                                newTrade();         // 重置
                                break;
                            case AppConstants.CASHIER_TYPE_POS:
                            case AppConstants.CASHIER_TYPE_MEMBER:
                            default:
                                reportTradeDataSync(tradeStatus);
                                if (mTrade.posPoints > 0) {
                                    UiNavigation.gotoPointsPhoneActivity(context);
                                } else {
                                    print();
                                    newTrade();
                                }
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
}
