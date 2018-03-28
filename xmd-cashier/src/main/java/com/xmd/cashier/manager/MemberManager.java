package com.xmd.cashier.manager;

import android.content.Context;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.RetryPool;
import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.cashier.IPos;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.PackagePlanItem;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.dal.bean.TradeChannelInfo;
import com.xmd.cashier.dal.net.AuthPayRetrofit;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GetMemberInfo;
import com.xmd.cashier.dal.net.response.MemberCardResult;
import com.xmd.cashier.dal.net.response.MemberListResult;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.net.response.MemberSettingResult;
import com.xmd.cashier.dal.net.response.MemberUrlResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import java.util.Date;

import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-7-11.
 */

public class MemberManager {
    private static final String TAG = "MemberManager";
    private IPos mPos;

    private MemberManager() {
        mPos = PosFactory.getCurrentCashier();
    }

    private static MemberManager mInstance = new MemberManager();

    public static MemberManager getInstance() {
        return mInstance;
    }

    // 读取磁条卡刷卡
    public String getMagneticReaderResult() {
        return mPos.getMagneticReaderInfo();
    }

    // ————————————————————————会所会员配置——————————————————————————
    private int mCardMode = 2;  //默认只发电子卡
    private String mRechargeMode = null;    //默认不支持Pos充值
    private String mMemberSwitch = AppConstants.APP_REQUEST_NO; //默认会员功能关闭
    private String mVerificationSwitch = AppConstants.APP_REQUEST_YES;  //默认需要输入交易验证密码

    public int getCardMode() {
        return mCardMode;
    }

    public String getRechargeMode() {
        return mRechargeMode;
    }

    public String getMemberSwitch() {
        return mMemberSwitch;
    }

    public String getVerificationSwitch() {
        return mVerificationSwitch;
    }

    private Call<MemberSettingResult> callMemberSetting;
    private RetryPool.RetryRunnable mRetryGetMemberSetting;
    private boolean resultMemberSetting;

    public void startGetMemberSetting() {
        mRetryGetMemberSetting = new RetryPool.RetryRunnable(AppConstants.TINNY_INTERVAL, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getSetting();
            }
        });
        RetryPool.getInstance().postWork(mRetryGetMemberSetting);
    }

    public void stopGetMemberSetting() {
        if (callMemberSetting != null && !callMemberSetting.isCanceled()) {
            callMemberSetting.cancel();
        }
        if (mRetryGetMemberSetting != null) {
            RetryPool.getInstance().removeWork(mRetryGetMemberSetting);
            mRetryGetMemberSetting = null;
        }
    }

    private boolean getSetting() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "获取会所会员配置信息：" + RequestConstant.URL_GET_MEMBER_SETTING_CONFIG);
        callMemberSetting = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberSettingConfig(AccountManager.getInstance().getToken());
        XmdNetwork.getInstance().requestSync(callMemberSetting, new NetworkSubscriber<MemberSettingResult>() {
            @Override
            public void onCallbackSuccess(MemberSettingResult result) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "获取会所会员配置---成功");
                if (result != null && result.getRespData() != null) {
                    mCardMode = result.getRespData().cardMode;
                    mRechargeMode = result.getRespData().rechargeMode;
                    mMemberSwitch = result.getRespData().memberSwitch;
                    mVerificationSwitch = result.getRespData().verificationSwitch;
                }
                resultMemberSetting = true;
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "获取会所会员配置信息---失败：" + e.getLocalizedMessage());
                if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                    // token过期
                    resultMemberSetting = true;
                } else {
                    resultMemberSetting = false;
                }
            }
        });
        return resultMemberSetting;
    }

    // ————————————————————————开卡——————————————————————————
    private MemberInfo cardMemberInfo;

    public void newCardProcess() {
        cardMemberInfo = new MemberInfo();
    }

    public void setPhone(String phone) {
        this.cardMemberInfo.phoneNum = phone;
    }

    public String getPhone() {
        return cardMemberInfo.phoneNum;
    }

    public void setName(String name) {
        this.cardMemberInfo.name = name;
    }

    public String getName() {
        return cardMemberInfo.name;
    }

    public void setBirth(String birth) {
        this.cardMemberInfo.birth = birth;
    }

    public void setGender(String gender) {
        this.cardMemberInfo.gender = gender;
    }

    public void setCardNo(String cardNo) {
        this.cardMemberInfo.cardNo = cardNo;
    }

    public void setCardMemberInfo(MemberInfo info) {
        this.cardMemberInfo = info;
    }

    public MemberInfo getCardMemberInfo() {
        return cardMemberInfo;
    }

    // 开卡
    public Subscription requestCard(final Callback<MemberCardResult> callback) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员开卡发起请求：" + RequestConstant.URL_REQUEST_MEMBER_CARD);
        Observable<MemberCardResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .cardMemberInfo(AccountManager.getInstance().getToken(), cardMemberInfo.birth, cardMemberInfo.gender, cardMemberInfo.cardNo, cardMemberInfo.phoneNum, cardMemberInfo.name);
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
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员开卡发起请求---失败：" + e.getLocalizedMessage());
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 校验用户手机号
    public Subscription checkMemberPhone(final String phone, final Callback callback) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员开卡校验手机号：" + RequestConstant.URL_CHECK_MEMBER_CARD_PHONE + " (" + phone + ") ");
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
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员开卡校验手机号---失败：" + e.getLocalizedMessage());
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // ————————————————————————充值——————————————————————————
    public int tradeStatus; //充值交易结果
    public String tradeStatusError; //充值交易失败描述

    public String currentChannelName;
    public String currentChannelType;
    public String currentChannelMark;

    private String memberId;  // 会员ID
    private MemberInfo memberInfo;  // 会员实例

    // 充值指定套餐
    private PackagePlanItem packageInfo;
    // 充值指定金额
    private int amount;     //充
    private int amountGive; //送

    private String rechargeAmountType;
    private String payUrl;  // 充值订单二维码URL
    private TechInfo techInfo;  // 营销人员ID
    private String orderId; // 充值订单ID

    private String tradeNo; // 银联支付时支付号
    private String posTradeNo;      //收银台订单号

    public MemberRecordInfo recordInfo;

    public void newRechargeProcess() {
        tradeStatus = 0;
        tradeStatusError = null;
        currentChannelName = null;
        currentChannelType = null;
        currentChannelMark = null;
        memberId = null;
        memberInfo = new MemberInfo();
        packageInfo = new PackagePlanItem();
        amount = 0;
        amountGive = 0;
        rechargeAmountType = null;
        payUrl = null;
        techInfo = new TechInfo();
        orderId = null;
        tradeNo = null;
        posTradeNo = null;
        recordInfo = null;
    }

    public synchronized String getPosTradeNo() {
        if (posTradeNo == null) {
            newCashierTradeNo();
        }
        return posTradeNo;
    }

    //生成新的订单号给收银APP使用
    public void newCashierTradeNo() {
        int seed = 0;
        if (posTradeNo != null) {
            seed = Integer.parseInt(posTradeNo.subSequence(posTradeNo.length() - 4, posTradeNo.length()).toString());
            seed++;
        }
        posTradeNo = String.format("%s%04d", tradeNo, seed);
    }

    public void setCurrentChannel(TradeChannelInfo info) {
        currentChannelName = info.name;
        currentChannelMark = info.mark;
        currentChannelType = info.type;
    }

    public void setRechargeTechInfo(TechInfo info) {
        this.techInfo = info;
    }

    public void setRechargeMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }

    public MemberInfo getRechargeMemberInfo() {
        return memberInfo;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setPackageInfo(PackagePlanItem info) {
        packageInfo = info;
    }

    public PackagePlanItem getPackageInfo() {
        return packageInfo;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setAmountGive(int amountGive) {
        this.amountGive = amountGive;
    }

    public int getAmount() {
        return amount;
    }

    public int getAmountGive() {
        return amountGive;
    }

    public String getRechargeUrl() {
        return payUrl;
    }

    public String getRechargeOrderId() {
        return orderId;
    }

    public void setAmountType(String type) {
        this.rechargeAmountType = type;
    }

    public String getAmountType() {
        return rechargeAmountType;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    // 生成充值请求
    public Subscription requestRecharge(String password, final Callback<MemberUrlResult> callback) {
        int amountValue = 0;
        int amountGiveValue = 0;
        String description = "充值";
        String packageId = null;
        switch (getAmountType()) {
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_PACKAGE:
                if (packageInfo != null) {
                    amountValue = packageInfo.amount;
                    description = "套餐" + packageInfo.name;
                    packageId = String.valueOf(packageInfo.id);
                }
                break;
            case AppConstants.MEMBER_RECHARGE_AMOUNT_TYPE_MONEY:
                amountValue = amount;
                amountGiveValue = amountGive;
                if (amountGiveValue > 0) {
                    description = "充" + Utils.moneyToStringEx(amountValue) + "送" + Utils.moneyToStringEx(amountGiveValue);
                } else {
                    description = "指定金额" + Utils.moneyToStringEx(amountValue) + "元";
                }
                packageId = null;
                break;
            default:
                break;
        }
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值发起充值请求：" + RequestConstant.URL_REQUEST_MEMBER_RECHARGE);
        Observable<MemberUrlResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .rechargeMemberInfo(AccountManager.getInstance().getToken(),
                        String.valueOf(amountValue),
                        String.valueOf(amountGiveValue),
                        description,
                        memberId,
                        packageId,
                        techInfo.id,
                        password,
                        AppConstants.PAY_CHANNEL_QRCODE.equals(currentChannelType) ? null : currentChannelType,
                        RequestConstant.DEFAULT_SIGN_VALUE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<MemberUrlResult>() {
            @Override
            public void onCallbackSuccess(MemberUrlResult result) {
                if (result != null && result.getRespData() != null) {
                    orderId = result.getRespData().orderId;
                    payUrl = result.getRespData().payUrl;
                    tradeNo = result.getRespData().tradeNo;
                    XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值发起充值请求---成功：[" + orderId + "]" + payUrl);
                    callback.onSuccess(result);
                } else {
                    XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值发起充值请求---成功：数据解析异常");
                    callback.onError("解析数据异常");
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "会员充值发起充值请求---失败：" + e.getLocalizedMessage());
                callback.onError(e.getLocalizedMessage());
            }
        });
    }

    // 收银台支付
    public void posRecharge(Context context, final int money, final Callback<Void> callback) {
        newCashierTradeNo();
        CashierManager.getInstance().pay(context, getPosTradeNo(), money, new PayCallback<Object>() {
            @Override
            public void onResult(String error, Object o) {
                if (error == null) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(error);
                }
            }
        });
    }

    // 获取会员信息: 扫码
    public Subscription fetchMemberInfo(final String memberToken, final Callback<MemberInfo> callback) {
        Observable<GetMemberInfo> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberInfo(AccountManager.getInstance().getToken(), memberToken, RequestConstant.DEFAULT_SIGN_VALUE);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<GetMemberInfo>() {
            @Override
            public void onCallbackSuccess(GetMemberInfo result) {
                callback.onSuccess(result.getRespData());
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onError(e.getLocalizedMessage());
            }
        });
    }


    // 获取会员信息: code
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

    // 更新会员信息
    public Subscription updateMemberInfo(String memberId, String telephone, final Callback<BaseBean> callBack) {
        Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .updateMemberInfo(AccountManager.getInstance().getToken(), memberId, telephone);
        return XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                callBack.onSuccess(result);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callBack.onError(e.getLocalizedMessage());
            }
        });
    }

    public void printMemberRecordInfoAsync(final MemberRecordInfo memberRecordInfo, final boolean retry) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        printMemberRecordInfo(memberRecordInfo, retry, true, null);
                        if (SPManager.getInstance().getPrintClientSwitch()) {
                            printMemberRecordInfo(memberRecordInfo, retry, false, null);
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void printMemberRecordInfoAsync(final MemberRecordInfo memberRecordInfo, final boolean retry, final boolean keep) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        printMemberRecordInfo(memberRecordInfo, retry, keep, null);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void printMemberRecordInfo(MemberRecordInfo info, boolean retry, boolean keep, Callback<?> callback) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "打印会员账户记录");
        mPos.setPrintListener();
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter((keep ? "商户存根" : "客户联") + (retry ? "(补打小票)" : ""));
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("会员卡号：", (keep ? info.cardNo : Utils.formatCode(info.cardNo)) + "(" + Utils.formatName(info.name, keep) + ")");
        mPos.printText("手机号码：", (keep ? info.telephone : Utils.formatPhone(info.telephone)));
        if (AppConstants.MEMBER_RECORD_TYPE_CONSUME.equals(info.businessCategory) || AppConstants.MEMBER_RECORD_TYPE_RECHARGE.equals(info.businessCategory)) {
            mPos.printText("会员等级：", info.memberTypeName + "(" + String.format("%.02f", info.memberDiscount / 100.0f) + "折)");
        } else {
            mPos.printText("会员等级：", info.memberTypeName);
        }
        mPos.printDivide();

        switch (info.tradeType) {
            case AppConstants.MEMBER_TRADE_TYPE_INCOME:     //充值
                mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(info.orderAmount));
                mPos.printText("赠送金额：", "+￥ " + Utils.moneyToStringEx(info.discountAmount));
                mPos.printDivide();
                mPos.printRight("实收金额：" + Utils.moneyToStringEx(info.orderAmount) + " 元", true);
                mPos.printRight("会员卡余额：" + "￥ " + Utils.moneyToStringEx(info.accountAmount));
                mPos.printDivide();

                mPos.printText("赠送详情");
                switch (info.businessCategory) {
                    case AppConstants.MEMBER_RECORD_TYPE_RECHARGE:
                        mPos.printText("[会员充值]" + (info.packageId != null ? (TextUtils.isEmpty(info.activityName) ? "活动充值" : "活动充值，" + info.activityName) : "直接充值"));
                        mPos.printText("    充" + Utils.moneyToString(info.orderAmount) + ((info.discountAmount > 0) ? "送" + Utils.moneyToString(info.discountAmount) : ""));
                        break;
                    case AppConstants.MEMBER_RECORD_TYPE_REFUND:
                    case AppConstants.MEMBER_RECORD_TYPE_OTHER:
                    default:
                        mPos.printText("[会员充值]" + info.businessCategoryName);
                        mPos.printText("    充" + Utils.moneyToString(info.orderAmount));
                        break;
                }
                mPos.printDivide();

                mPos.printText("交易号：", info.tradeNo);
                mPos.printText("交易时间：", info.createTime);
                mPos.printText("支付方式：", info.payChannelName + "(" + Utils.getPlatform(info.platform) + ")");
                if (!TextUtils.isEmpty(info.techName)) {
                    mPos.printText("营销人员：", info.techName + (TextUtils.isEmpty(info.techNo) ? "" : "[" + info.techNo + "]"));
                }

                mPos.printText("收款人员：", (TextUtils.isEmpty(info.operatorName) ? AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")" : info.operatorName));
                mPos.printText("打印时间：", DateUtils.doDate2String(new Date()));
                break;
            case AppConstants.MEMBER_TRADE_TYPE_PAY:        //消费
                mPos.printText("订单金额：", "￥ " + Utils.moneyToStringEx(info.orderAmount));
                mPos.printText("会员优惠：", "-￥ " + Utils.moneyToStringEx(info.discountAmount));
                mPos.printDivide();
                mPos.printRight("实收金额：" + Utils.moneyToStringEx(info.amount) + " 元", true);
                mPos.printRight("会员卡余额：" + "￥ " + Utils.moneyToStringEx(info.accountAmount));
                mPos.printDivide();

                mPos.printText("优惠详情");
                switch (info.businessCategory) {
                    case AppConstants.MEMBER_RECORD_TYPE_CONSUME:   //消费支付
                        mPos.printText("[会员消费]消费支付", "(-" + Utils.moneyToStringEx(info.discountAmount) + "元)");
                        mPos.printText(info.memberTypeName + "，" + String.format("%.02f", info.memberDiscount / 100.0f) + "折/" + Utils.formatCode(info.cardNo));
                        break;
                    case AppConstants.MEMBER_RECORD_TYPE_SUBTRACT:  //错充扣回
                    case AppConstants.MEMBER_RECORD_TYPE_OTHER:     //其他
                    default:
                        mPos.printText("[会员消费]" + info.businessCategoryName, "(-" + Utils.moneyToStringEx(info.orderAmount) + "元)");
                        break;
                }
                mPos.printDivide();

                mPos.printText("交易号：", info.tradeNo);
                mPos.printText("交易时间：", info.createTime);
                mPos.printText("支付方式：", "会员消费" + "(" + Utils.getPlatform(info.platform) + ")");
                if (!TextUtils.isEmpty(info.techName)) {
                    mPos.printText("服务技师：", info.techName + (TextUtils.isEmpty(info.techNo) ? "" : "[" + info.techNo + "]"));
                }
                mPos.printText("收款人员：", (TextUtils.isEmpty(info.operatorName) ? AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")" : info.operatorName));
                mPos.printText("打印时间：", DateUtils.doDate2String(new Date()));
                break;
            default:
                break;
        }
        if (!keep) {
            byte[] qrCodeBytes = QrcodeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
            }
            mPos.printCenter("微信扫码，选技师、抢优惠");
        }
        mPos.printEnd();
    }


    public Subscription activeAuthPay(String authCode, String orderId, final Callback<MemberRecordResult> callback) {
        Observable<MemberRecordResult> observable = AuthPayRetrofit.getService()
                .doAuthCodeRecharge(AccountManager.getInstance().getToken(), orderId, authCode, RequestConstant.DEFAULT_SIGN_VALUE);
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

    public Subscription callbackRechargeOrder(String orderId, String payChannel, final Callback<MemberRecordResult> callback) {
        Observable<MemberRecordResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .doMemberRecharge(AccountManager.getInstance().getToken(), orderId, payChannel, null, RequestConstant.DEFAULT_SIGN_VALUE);
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
}
