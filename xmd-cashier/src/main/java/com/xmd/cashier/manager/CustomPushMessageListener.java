package com.xmd.cashier.manager;

import android.os.SystemClock;

import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.event.InnerPushEvent;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.pos.PosImpl;
import com.xmd.m.network.BaseBean;
import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.m.notify.push.XmdPushMessageListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zr on 17-11-10.
 */
public class CustomPushMessageListener implements XmdPushMessageListener {
    private static final String TAG = "CustomPushMessageListener";
    private static CustomPushMessageListener mInstance = new CustomPushMessageListener();

    private CustomPushMessageListener() {

    }

    public static CustomPushMessageListener getInstance() {
        return mInstance;
    }

    @Override
    public void onMessage(XmdPushMessage message) {
        switch (message.getBusinessType()) {
            case AppConstants.PUSH_TAG_MEMBER_PRINT:
                // 会员账户记录
                XLogger.i(TAG, "On XmdPushMessage :" + AppConstants.PUSH_TAG_MEMBER_PRINT);
                MemberRecordInfo memberRecordInfo = new Gson().fromJson(message.getData(), MemberRecordInfo.class);
                MemberManager.getInstance().printMemberRecordInfoAsync(memberRecordInfo, false);
                break;
            case AppConstants.PUSH_TAG_ORDER_PRINT:
                // 预约订单
                XLogger.i(TAG, "On XmdPushMessage :" + AppConstants.PUSH_TAG_ORDER_PRINT);
                OrderRecordInfo orderRecordInfo = new Gson().fromJson(message.getData(), OrderRecordInfo.class);
                NotifyManager.getInstance().printOrderRecordAsync(orderRecordInfo, false);
                break;
            case AppConstants.PUSH_TAG_FASTPAY_PRINT:
                // 在线买单
                XLogger.i(TAG, "On XmdPushMessage :" + AppConstants.PUSH_TAG_FASTPAY_PRINT);
                OnlinePayInfo onlinePayInfo = new Gson().fromJson(message.getData(), OnlinePayInfo.class);
                NotifyManager.getInstance().printOnlinePayRecordAsync(onlinePayInfo, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRawMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            switch (jsonObject.getString(RequestConstant.KEY_BUSINESS_TYPE)) {
                case AppConstants.PUSH_TAG_FASTPAY:
                    XLogger.i(TAG, "On RawMessage :" + AppConstants.PUSH_TAG_FASTPAY);
                    SPManager.getInstance().setFastPayPushTag(jsonObject.getInt(RequestConstant.KEY_COUNT));
                    NotifyManager.getInstance().startRepeatOnlinePay(SystemClock.elapsedRealtime());
                    break;
                case AppConstants.PUSH_TAG_ORDER:
                    XLogger.i(TAG, "On RawMessage :" + AppConstants.PUSH_TAG_ORDER);
                    SPManager.getInstance().setOrderPushTag(jsonObject.getInt(RequestConstant.KEY_COUNT));
                    NotifyManager.getInstance().startRepeatOrderRecord(SystemClock.elapsedRealtime());
                    break;
                case AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY:   //内网订单支付
                    XLogger.i(TAG, "On RawMessage(" + AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY + ") 内网订单支付查询详情");
                    EventBus.getDefault().post(new InnerPushEvent());
                    InnerManager.getInstance().getInnerHoleBatchSubscription(jsonObject.getString(RequestConstant.KEY_PUSH_DATA), new Callback<InnerRecordInfo>() {
                        @Override
                        public void onSuccess(InnerRecordInfo o) {
                            XLogger.i(TAG, "On RawMessage(" + AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY + ") 内网订单支付查询详情---成功");
                            EventBus.getDefault().post(o);
                        }

                        @Override
                        public void onError(String error) {
                            XLogger.e(TAG, "On RawMessage(" + AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY + ") 内网订单支付查询详情---失败:" + error);
                        }
                    });
                    break;
                case AppConstants.PUSH_TAG_FAST_PAY_SUCCESS:    // 内网订单打印
                    XLogger.i(TAG, "On RawMessage(" + AppConstants.PUSH_TAG_FAST_PAY_SUCCESS + ") 内网订单打印查询详情");
                    EventBus.getDefault().post(new InnerPushEvent());
                    InnerManager.getInstance().getInnerHoleBatchSubscription(jsonObject.getString(RequestConstant.KEY_PUSH_DATA), new Callback<InnerRecordInfo>() {
                        @Override
                        public void onSuccess(InnerRecordInfo o) {
                            XLogger.i(TAG, "On RawMessage(" + AppConstants.PUSH_TAG_FAST_PAY_SUCCESS + ") 内网订单打印查询详情---成功");
                            InnerManager.getInstance().printInnerRecordInfoAsync(o, false);
                        }

                        @Override
                        public void onError(String error) {
                            XLogger.e(TAG, "On RawMessage(" + AppConstants.PUSH_TAG_FAST_PAY_SUCCESS + ") 内网订单打印查询详情---失败:" + error);
                        }
                    });
                    break;
                case AppConstants.PUSH_TAG_UPLOAD_LOG:  //上传日志
                    XLogger.i(TAG, "On RawMessage :" + AppConstants.PUSH_TAG_UPLOAD_LOG);
                    if (PosImpl.getInstance().getPosIdentifierNo().equals(jsonObject.getString(RequestConstant.KEY_PUSH_DATA))) {
                        if (!Utils.isWifiNetwork(MainApplication.getInstance().getApplicationContext())) {
                            XLogger.i(TAG, "当前网络非Wifi网络");
                            return;
                        }
                        // 设备匹配
                        MonitorManager.getInstance().uploadLogFile(new Callback<BaseBean>() {
                            @Override
                            public void onSuccess(BaseBean o) {
                                XLogger.i(TAG, "推送上传日志---成功");
                            }

                            @Override
                            public void onError(String error) {
                                XLogger.i(TAG, "推送上传日志---失败:" + error);
                            }
                        });
                    } else {
                        XLogger.i(TAG, "设备信息不匹配");
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
