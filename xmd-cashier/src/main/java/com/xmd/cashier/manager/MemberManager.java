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
import com.xmd.cashier.dal.bean.MemberCardProcess;
import com.xmd.cashier.dal.bean.MemberInfo;
import com.xmd.cashier.dal.bean.MemberRechargeProcess;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.PackagePlanItem;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.MemberCardResult;
import com.xmd.cashier.dal.net.response.MemberListResult;
import com.xmd.cashier.dal.net.response.MemberSettingResult;
import com.xmd.cashier.dal.net.response.MemberUrlResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

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
        mRetryGetMemberSetting = new RetryPool.RetryRunnable(3000, 1.0f, new RetryPool.RetryExecutor() {
            @Override
            public boolean run() {
                return getFastPayCount();
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

    private boolean getFastPayCount() {
        callMemberSetting = XmdNetwork.getInstance().getService(SpaService.class)
                .getMemberSettingConfig(AccountManager.getInstance().getToken());
        XmdNetwork.getInstance().requestSync(callMemberSetting, new NetworkSubscriber<MemberSettingResult>() {
            @Override
            public void onCallbackSuccess(MemberSettingResult result) {
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
                XLogger.i("getMemberSetting error :" + e.getLocalizedMessage());
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

    //----------------开卡-------------------
    // 退出登录/完成一次开卡后
    public void newCardProcess() {
        mCardProcess = new MemberCardProcess();
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

    public void setPackageInfo(PackagePlanItem info) {
        mRechargeProcess.setPackageInfo(info);
    }

    public PackagePlanItem getPackageInfo() {
        return mRechargeProcess.getPackageInfo();
    }

    public void setAmount(int amount) {
        mRechargeProcess.setAmount(amount);
    }

    public void setAmountGive(int amountGive) {
        mRechargeProcess.setAmountGive(amountGive);
    }

    public int getAmount() {
        return mRechargeProcess.getAmount();
    }

    public int getAmountGive() {
        return mRechargeProcess.getAmountGive();
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
    public Subscription requestRecharge(String password, final Callback<MemberUrlResult> callback) {
        int amount = 0;
        int amountGive = 0;
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
                amountGive = mRechargeProcess.getAmountGive();
                if (amountGive > 0) {
                    description = "充" + Utils.moneyToStringEx(amount) + "送" + Utils.moneyToStringEx(amountGive);
                } else {
                    description = "指定金额" + Utils.moneyToStringEx(amount) + "元";
                }
                packageId = null;
                break;
            default:
                break;
        }
        Observable<MemberUrlResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                .rechargeMemberInfo(AccountManager.getInstance().getToken(),
                        String.valueOf(amount),
                        String.valueOf(mRechargeProcess.getAmountGive()),
                        description,
                        String.valueOf(mRechargeProcess.getMemberId()),
                        packageId,
                        mRechargeProcess.getTechInfo().id,
                        password,
                        RequestConstant.DEFAULT_SIGN_VALUE);
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
                    mTrade.setOriginMoney(money);
                    mTrade.tradeTime = DateUtils.doDate2String(new Date());
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

    public void printMemberRecordInfo(MemberRecordInfo info, boolean retry, boolean keep, Callback<?> callback) {
        mPos.setPrintListener(callback);
        mPos.printCenter("小摩豆结账单");
        mPos.printCenter((keep ? "商户存根" : "客户联") + (retry ? "(补打小票)" : ""));
        mPos.printDivide();
        mPos.printText("商户名：" + AccountManager.getInstance().getClubName());
        mPos.printDivide();
        mPos.printText("会员卡号：", (keep ? info.cardNo : Utils.formatCode(info.cardNo)) + "(" + Utils.formatName(info.name, keep) + ")");
        mPos.printText("手机号码：", (keep ? info.telephone : Utils.formatPhone(info.telephone)));
        if (AppConstants.MEMBER_RECORD_TYPE_CONSUME.equals(info.businessCategory) || AppConstants.MEMBER_RECORD_TYPE_RECHARGE.equals(info.businessCategory)) {
            mPos.printText("会员等级：", info.memberTypeName + "(" + String.format("%.02f", info.discount / 100.0f) + "折)");
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
                mPos.printText("打印时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
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
                        mPos.printText(info.memberTypeName + "，" + String.format("%.02f", info.discount / 100.0f) + "折/" + Utils.formatCode(info.cardNo));
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
                mPos.printText("打印时间：", Utils.getFormatString(new Date(), DateUtils.DF_DEFAULT));
                break;
            default:
                break;
        }
        if (!keep) {
            byte[] qrCodeBytes = TradeManager.getInstance().getClubQRCodeSync();
            if (qrCodeBytes != null) {
                mPos.printBitmap(qrCodeBytes);
            }
            mPos.printCenter("微信扫码，选技师、抢优惠");
        }
        mPos.printEnd();
    }
}
