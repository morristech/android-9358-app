package com.xmd.cashier.manager;

import android.content.Intent;

import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.activity.InnerMethodActivity;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.event.InnerPushEvent;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.m.notify.push.XmdPushMessageListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-11-10.
 */

public class CustomPushMessageListener implements XmdPushMessageListener {
    private static CustomPushMessageListener mInstance = new CustomPushMessageListener();

    private CustomPushMessageListener() {

    }

    public static CustomPushMessageListener getInstance() {
        return mInstance;
    }

    @Override
    public void onMessage(XmdPushMessage message) {
        // 按照指定格式处理消息
        switch (message.getBusinessType()) {
            case AppConstants.PUSH_TAG_MEMBER_PRINT:
                // 会员账户记录
                final MemberRecordInfo memberRecordInfo = new Gson().fromJson(message.getData(), MemberRecordInfo.class);
                Observable
                        .create(new Observable.OnSubscribe<Void>() {
                            @Override
                            public void call(Subscriber<? super Void> subscriber) {
                                MemberManager.getInstance().printMemberRecordInfo(memberRecordInfo, false, true, null);
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                break;
            case AppConstants.PUSH_TAG_ORDER_PRINT:
                // 预约订单
                final OrderRecordInfo orderRecordInfo = new Gson().fromJson(message.getData(), OrderRecordInfo.class);
                Observable
                        .create(new Observable.OnSubscribe<Void>() {
                            @Override
                            public void call(Subscriber<? super Void> subscriber) {
                                NotifyManager.getInstance().printOrderRecord(orderRecordInfo, false);
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                break;
            case AppConstants.PUSH_TAG_FASTPAY_PRINT:
                // 在线买单
                final OnlinePayInfo onlinePayInfo = new Gson().fromJson(message.getData(), OnlinePayInfo.class);
                Observable
                        .create(new Observable.OnSubscribe<Void>() {
                            @Override
                            public void call(Subscriber<? super Void> subscriber) {
                                NotifyManager.getInstance().printOnlinePayRecord(onlinePayInfo, false, true);
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRawMessage(String message) {
        // 处理原始透传消息
        try {
            JSONObject jsonObject = new JSONObject(message);
            switch (jsonObject.getString(RequestConstant.KEY_BUSINESS_TYPE)) {
                case AppConstants.PUSH_TAG_FASTPAY:
                    SPManager.getInstance().setFastPayPushTag(jsonObject.getInt(RequestConstant.KEY_COUNT));
                    break;
                case AppConstants.PUSH_TAG_ORDER:
                    SPManager.getInstance().setOrderPushTag(jsonObject.getInt(RequestConstant.KEY_COUNT));
                    break;
                case AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY:
                    EventBus.getDefault().post(new InnerPushEvent());
                    InnerManager.getInstance().getInnerHoleBatchSubscription(jsonObject.getString(RequestConstant.KEY_PUSH_DATA), new Callback<InnerRecordInfo>() {
                        @Override
                        public void onSuccess(InnerRecordInfo o) {
                            Intent intent = new Intent();
                            intent.setAction(AppConstants.ACTION_CUSTOM_NOTIFY_RECEIVER);
                            intent.putExtra(AppConstants.EXTRA_NOTIFY_TYPE, AppConstants.EXTRA_NOTIFY_TYPE_INNER_PAY);
                            intent.putExtra(AppConstants.EXTRA_NOTIFY_DATA, o);
                            MainApplication.getInstance().getApplicationContext().sendBroadcast(intent);
                        }

                        @Override
                        public void onError(String error) {
                            XLogger.e(error);
                        }
                    });
                    break;
                case AppConstants.PUSH_TAG_FAST_PAY_SUCCESS:
                    EventBus.getDefault().post(new InnerPushEvent());
                    InnerManager.getInstance().getInnerHoleBatchSubscription(jsonObject.getString(RequestConstant.KEY_PUSH_DATA), new Callback<InnerRecordInfo>() {
                        @Override
                        public void onSuccess(final InnerRecordInfo o) {
                            Observable
                                    .create(new Observable.OnSubscribe<Void>() {
                                        @Override
                                        public void call(Subscriber<? super Void> subscriber) {
                                            InnerManager.getInstance().printInnerRecordInfo(o, false, true, null);
                                            InnerManager.getInstance().printInnerRecordInfo(o, false, false, null);
                                            subscriber.onNext(null);
                                            subscriber.onCompleted();
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                        }

                        @Override
                        public void onError(String error) {
                            XLogger.e(error);
                        }
                    });
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
