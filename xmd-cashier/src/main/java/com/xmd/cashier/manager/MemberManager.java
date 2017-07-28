package com.xmd.cashier.manager;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.MemberCardProcess;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.MemberRechargeProcess;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.PackagePlanItem;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaOkHttp;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.MemberCardResult;
import com.xmd.cashier.dal.net.response.MemberListResult;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.net.response.MemberSettingResult;
import com.xmd.cashier.dal.net.response.MemberUrlResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-7-11.
 */

public class MemberManager {
    private IPos mPos;
    private MemberCardProcess mCardProcess;
    private MemberRechargeProcess mRechargeProcess;
    private Trade mTrade;
    public AtomicBoolean mInPosPay = new AtomicBoolean(false);

    private MemberManager() {
        mPos = PosFactory.getCurrentCashier();
        mCardProcess = new MemberCardProcess();
        mRechargeProcess = new MemberRechargeProcess();
        mTrade = new Trade();
    }

    public void newTrade() {
        mTrade = new Trade();
    }

    public Trade getTrade() {
        return mTrade;
    }

    private static MemberManager mInstance = new MemberManager();

    public static MemberManager getInstance() {
        return mInstance;
    }

    // 读取磁条卡刷卡
    public String getMagneticReaderResult() {
        return mPos.getMagneticReaderInfo();
    }

    // 会所会员配置
    private int mCardMode = 2;  //默认只发电子卡
    private String mRechargeMode = null;    //默认不支持Pos充值
    private String mMemberSwitch = AppConstants.APP_REQUEST_NO; //默认会员功能关闭

    public int getmCardMode() {
        return mCardMode;
    }

    public String getmRechargeMode() {
        return mRechargeMode;
    }

    public String getmMemberSwitch() {
        return mMemberSwitch;
    }

    public void getClubMemberSetting() {
        Observable<MemberSettingResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberSettingConfig(AccountManager.getInstance().getToken());
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberSettingResult>() {
            @Override
            public void onCallbackSuccess(MemberSettingResult result) {
                if (result != null) {
                    mCardMode = result.getRespData().cardMode;
                    mRechargeMode = result.getRespData().rechargeMode;
                    mMemberSwitch = result.getRespData().memberSwitch;
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(e.getLocalizedMessage());
            }
        });
    }

    //----------------开卡-------------------
    // 退出登录/完成一次开卡后
    public void newCardProcess() {
        mCardProcess = new MemberCardProcess();
    }

    public MemberCardProcess getCurrentMemberCardProcess() {
        return mCardProcess;
    }

    public void setPhone(String phone) {
        mCardProcess.getMemberInfo().phoneNum = phone;
    }

    public String getPhone() {
        return mCardProcess.getMemberInfo().phoneNum;
    }

    public void setName(String name) {
        mCardProcess.getMemberInfo().name = name;
    }

    public String getName() {
        return mCardProcess.getMemberInfo().name;
    }

    public void setBirth(String birth) {
        mCardProcess.getMemberInfo().birth = birth;
    }

    public void setGender(String gender) {
        mCardProcess.getMemberInfo().gender = gender;
    }

    public void setCardNo(String cardNo) {
        mCardProcess.getMemberInfo().cardNo = cardNo;
    }

    public void setCardMemberInfo(MemberInfo info) {
        mCardProcess.setMemberInfo(info);
    }

    public MemberInfo getCardMemberInfo() {
        return mCardProcess.getMemberInfo();
    }

    // 开卡
    public Subscription requestCard(final Callback<MemberCardResult> callback) {
        MemberInfo info = mCardProcess.getMemberInfo();
        Observable<MemberCardResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .cardMemberInfo(AccountManager.getInstance().getToken(), info.birth, info.gender, info.cardNo, info.phoneNum, info.name);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberCardResult>() {
            @Override
            public void onCallbackSuccess(MemberCardResult result) {
                // 开卡成功
                if (result != null && result.getRespData() != null && result.getRespData().member != null) {
                    setCardMemberInfo(result.getRespData().member);
                    callback.onSuccess(result);
                } else {
                    callback.onError("数据解析异常");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 校验用户手机号
    public Subscription checkMemberPhone(final String phone, final Callback callback) {
        Observable<StringResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .checkMemberPhone(AccountManager.getInstance().getToken(), phone);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<StringResult>() {
            @Override
            public void onCallbackSuccess(StringResult result) {
                if (result != null && !TextUtils.isEmpty(result.getRespData())) {
                    // 已注册为小摩豆用户
                    setName(result.getRespData());
                }
                setPhone(phone);
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    //----------------充值-------------------    // 退出登录/完成一次充值后
    public void newRechargeProcess() {
        mRechargeProcess = new MemberRechargeProcess();
    }

    public MemberRechargeProcess getCurrentMemberRechargeProcess() {
        return mRechargeProcess;
    }

    public void setRechargeTechInfo(TechInfo info) {
        mRechargeProcess.setTechInfo(info);
    }

    public void setRechargeMemberInfo(MemberInfo memberInfo) {
        mRechargeProcess.setMemberInfo(memberInfo);
    }

    public MemberInfo getRechargeMemberInfo() {
        return mRechargeProcess.getMemberInfo();
    }

    public void setMemberId(long memberId) {
        mRechargeProcess.setMemberId(memberId);
    }

    public void setRechargePayType(int type) {
        mRechargeProcess.setRechargePayType(type);
    }

    public void setPackageInfo(PackagePlanItem info) {
        mRechargeProcess.setPackageInfo(info);
    }

    public PackagePlanItem getPackageInfo() {
        return mRechargeProcess.getPackageInfo();
    }

    public void setAmount(int amount) {
        mRechargeProcess.setAmount(amount);
    }

    public int getAmount() {
        return mRechargeProcess.getAmount();
    }

    public String getRechargeUrl() {
        return mRechargeProcess.getPayUrl();
    }

    public String getRechargeOrderId() {
        return mRechargeProcess.getOrderId();
    }

    public void setAmountType(String type) {
        mRechargeProcess.setRechargeAmountType(type);
    }

    public String getAmountType() {
        return mRechargeProcess.getRechargeAmountType();
    }

    // 生成充值请求
    public Subscription requestRecharge(final Callback<MemberUrlResult> callback) {
        int amount = 0;
        String description = "充值";
        String packageId = null;
        switch (getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                PackagePlanItem info = mRechargeProcess.getPackageInfo();
                if (info != null) {
                    amount = info.amount;
                    description = "套餐" + info.name;
                    packageId = String.valueOf(info.id);
                }
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:
                amount = mRechargeProcess.getAmount();
                description = "指定金额" + Utils.moneyToStringEx(amount) + "元";
                packageId = null;
                break;
            default:
                break;
        }
        Observable<MemberUrlResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .rechargeMemberInfo(AccountManager.getInstance().getToken(),
                        String.valueOf(amount),
                        description,
                        String.valueOf(mRechargeProcess.getMemberId()),
                        packageId,
                        mRechargeProcess.getTechInfo().id);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberUrlResult>() {
            @Override
            public void onCallbackSuccess(MemberUrlResult result) {
                if (result != null && result.getRespData() != null) {
                    mRechargeProcess.setOrderId(result.getRespData().orderId);
                    mRechargeProcess.setPayUrl(result.getRespData().payUrl);
                    callback.onSuccess(result);
                } else {
                    callback.onError("解析数据异常");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 查询充值详情
    public Subscription requestRechargeDetail(final Callback<MemberRecordResult> callback) {
        Observable<MemberRecordResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberRechargeDetail(AccountManager.getInstance().getToken(), mRechargeProcess.getOrderId());
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

    // POS支付获取TradeNo
    public Subscription fetchTradeNo(final Callback<GetTradeNoResult> callback) {
        int amount = 0;
        switch (getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                PackagePlanItem info = mRechargeProcess.getPackageInfo();
                if (info != null) {
                    amount = info.amount;
                }
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:
                amount = mRechargeProcess.getAmount();
                break;
            default:
                break;
        }
        Observable<GetTradeNoResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getTradeNo(AccountManager.getInstance().getToken(), amount, null, RequestConstant.DEFAULT_SIGN_VALUE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<GetTradeNoResult>() {
            @Override
            public void onCallbackSuccess(GetTradeNoResult result) {
                mTrade.tradeNo = result.getRespData();
                callback.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 收银台支付
    public void posRecharge(Context context, final int money, final Callback<Void> callback) {
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
                    mTrade.tradeStatus = AppConstants.TRADE_STATUS_SUCCESS;

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

    public void reportTrade() {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        DataReportManager.getInstance().reportData(MemberManager.getInstance().getTrade(), AppConstants.REPORT_DATA_BIZ_MEMBER);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private RetryPool.RetryRunnable mRetryGetRechargeResult;
    private boolean resultRecharge;

    public void startGetRechargeResult() {
        stopGetRechargeResult();
        mRetryGetRechargeResult = new RetryPool.RetryRunnable(3000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return reportRechargeResult();
            }
        });
        RetryPool.getInstance().postWork(mRetryGetRechargeResult);
    }

    public void stopGetRechargeResult() {
        if (mRetryGetRechargeResult != null) {
            RetryPool.getInstance().removeWork(mRetryGetRechargeResult);
            mRetryGetRechargeResult = null;
        }
    }

    private boolean reportRechargeResult() {
        SpaOkHttp.reportRechargeDataSync(new Callback<MemberRecordResult>() {
            @Override
            public void onSuccess(MemberRecordResult o) {
                MemberRecordInfo record = o.getRespData();
                record.packageInfo = mRechargeProcess.getPackageInfo();
                EventBus.getDefault().post(record);
                resultRecharge = true;
            }

            @Override
            public void onError(String error) {
                resultRecharge = false;
            }
        });
        return resultRecharge;
    }

    // 获取会员信息
    public Subscription requestMemberInfo(String code, final Callback<MemberInfo> callback) {
        Observable<MemberListResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberInfo(AccountManager.getInstance().getToken(), code);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberListResult>() {
            @Override
            public void onCallbackSuccess(MemberListResult result) {
                if (result != null && result.getRespData() != null && !result.getRespData().isEmpty()) {
                    MemberInfo memberInfo = result.getRespData().get(0);
                    callback.onSuccess(memberInfo);
                } else {
                    callback.onError("无法获取会员数据");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    public void printInfo(MemberRecordInfo info, boolean retry, boolean keep, Callback<?> callback) {
        byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
        mPos.setPrintListener(callback);
        mPos.printCenter(AccountManager.getInstance().getClubName());
        if (retry) {
            mPos.printCenter("--补打小票--");
        } else {
            mPos.printCenter(keep ? "商户存根" : "客户联");
        }
        mPos.printDivide();
        mPos.printText("会员卡号  " + Utils.formatCode(info.cardNo));
        mPos.printText("会员等级  " + info.memberTypeName);
        switch (info.tradeType) {
            case AppConstants.MEMBER_TRADE_TYPE_INCOME:
                // 充值:
                if (info.packageId != null) {
                    // 套餐:充XXX送XXX
                    mPos.printText("充值内容  " + "充" + Utils.moneyToString(info.orderAmount) + (info.discountAmount > 0 ? "送" + Utils.moneyToString(info.discountAmount) : ""));
                } else {
                    // 金额:指定金额XX元
                    mPos.printText("充值内容  " + "指定金额" + Utils.moneyToStringEx(info.orderAmount) + "元");
                }
                if (info.packageInfo != null && info.packageInfo.packageItems != null && !info.packageInfo.packageItems.isEmpty()) {
                    mPos.printText("套餐优惠");
                    for (PackagePlanItem.PackageItem item : info.packageInfo.packageItems) {
                        switch (item.type) {
                            case AppConstants.MEMBER_PLAN_ITEM_TYPE_CREDIT:
                                mPos.printText("|--" + item.name + "积分");
                                break;
                            case AppConstants.MEMBER_PLAN_ITEM_TYPE_MONEY:
                                mPos.printText("|--现金" + item.name + "元");
                                break;
                            case AppConstants.MEMBER_PLAN_ITEM_TYPE_GIF:
                                mPos.printText("|--" + item.name + " * " + item.itemCount, "礼品券");
                                break;
                            case AppConstants.MEMBER_PLAN_ITEM_TYPE_COUPON:
                                mPos.printText("|--" + item.name + " * " + item.itemCount, "优惠券");
                                break;
                            case AppConstants.MEMBER_PLAN_ITEM_TYPE_SERVICE:
                                mPos.printText("|--" + item.name + " * " + item.itemCount, "项目券");
                                break;
                            default:
                                mPos.printText("|--" + item.name + " * " + item.itemCount);
                                break;
                        }
                    }
                }
                mPos.printDivide();
                mPos.printText("支付金额  ", Utils.moneyToStringEx(info.orderAmount) + "元", true);
                mPos.printText("账户余额  ", Utils.moneyToStringEx(info.accountAmount) + "元", true);
                mPos.printDivide();
                break;
            case AppConstants.MEMBER_TRADE_TYPE_PAY:
                // 消费:消费金额|折扣信息|实收金额|当前余额
                mPos.printText("消费金额  " + Utils.moneyToStringEx(info.orderAmount) + "元");
                mPos.printText("折扣金额  " + Utils.moneyToStringEx(info.discountAmount) + "元");
                mPos.printDivide();
                mPos.printText("实收金额  ", Utils.moneyToStringEx(info.amount) + "元", true);
                mPos.printText("账户余额  ", Utils.moneyToStringEx(info.accountAmount) + "元", true);
                mPos.printDivide();
                break;
            default:
                break;
        }

        mPos.printText("交易号:", info.tradeNo);
        mPos.printText("付款方式:", info.payChannelName);
        mPos.printText("交易时间:", info.createTime);
        mPos.printText("打印时间:", Utils.getFormatString(new Date(), AppConstants.DEFAULT_DATE_FORMAT));
        mPos.printText("收银人员:", TextUtils.isEmpty(info.operatorName) ? AccountManager.getInstance().getUser().userName : info.operatorName);
        if (!TextUtils.isEmpty(info.techName)) {
            mPos.printText("营销人员:", info.techName + (TextUtils.isEmpty(info.techNo) ? "" : "[" + info.techNo + "]"));
        }
        if (!keep) {
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
            }
            mPos.printCenter("扫一扫，关注9358，约技师，享优惠");
        }
        mPos.printEnd();
    }
}
