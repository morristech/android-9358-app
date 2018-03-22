package com.xmd.cashier.manager;

import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.dal.event.EventPushReact;
import com.xmd.cashier.dal.event.InnerPushEvent;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.sp.SPManager;
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
        MonitorManager.getInstance().wakeupScreen();
        switch (message.getBusinessType()) {
            case AppConstants.PUSH_TAG_MEMBER_PRINT:
                // 会员账户记录
                XLogger.i(TAG, AppConstants.LOG_BIZ_MEMBER_MANAGER + "On XmdPushMessage：" + AppConstants.PUSH_TAG_MEMBER_PRINT);
                EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_MEMBER_PRINT));
                MemberRecordInfo memberRecordInfo = new Gson().fromJson(message.getData(), MemberRecordInfo.class);
                MemberManager.getInstance().printMemberRecordInfoAsync(memberRecordInfo, false);
                break;
            case AppConstants.PUSH_TAG_ORDER_PRINT:
                // 预约订单
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "On XmdPushMessage：" + AppConstants.PUSH_TAG_ORDER_PRINT);
                EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_ORDER_PRINT));
                OrderRecordInfo orderRecordInfo = new Gson().fromJson(message.getData(), OrderRecordInfo.class);
                NotifyManager.getInstance().printOrderRecordAsync(orderRecordInfo, false);
                break;
            case AppConstants.PUSH_TAG_FASTPAY_PRINT:
                // 在线买单
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "On XmdPushMessage：" + AppConstants.PUSH_TAG_FASTPAY_PRINT);
                EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_FASTPAY_PRINT));
                TradeRecordInfo onlinePayInfo = new Gson().fromJson(message.getData(), TradeRecordInfo.class);
                NotifyManager.getInstance().printOnlinePayRecordAsync(onlinePayInfo, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRawMessage(String message) {
        MonitorManager.getInstance().wakeupScreen();
        try {
            JSONObject jsonObject = new JSONObject(message);
            switch (jsonObject.getString(RequestConstant.KEY_BUSINESS_TYPE)) {
                case AppConstants.PUSH_TAG_FASTPAY:
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "On RawMessage：" + message);
                    EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_FASTPAY));
                    SPManager.getInstance().setFastPayPushTag(jsonObject.getInt(RequestConstant.KEY_COUNT));
                    NotifyManager.getInstance().notifyOnlinePayList();
                    break;
                case AppConstants.PUSH_TAG_ORDER:
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "On RawMessage：" + message);
                    EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_ORDER));
                    SPManager.getInstance().setOrderPushTag(jsonObject.getInt(RequestConstant.KEY_COUNT));
                    NotifyManager.getInstance().notifyOrderRecordList();
                    break;
                case AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY:   //内网订单支付
                    PosFactory.getCurrentCashier().speech("您有一笔新结账订单待处理");
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "On RawMessage：" + message);
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "On RawMessage(" + AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY + ") 内网订单支付查询详情");
                    EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY));
                    EventBus.getDefault().post(new InnerPushEvent());
                    TradeManager.getInstance().getHoleBatchDetail(jsonObject.getString(RequestConstant.KEY_PUSH_DATA), new Callback<TradeRecordInfo>() {
                        @Override
                        public void onSuccess(TradeRecordInfo o) {
                            XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "On RawMessage(" + AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY + ") 内网订单支付查询详情---成功:" + o.status);
                            EventBus.getDefault().post(o);
                        }

                        @Override
                        public void onError(String error) {
                            XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "On RawMessage(" + AppConstants.PUSH_TAG_CLUB_ORDER_TO_PAY + ") 内网订单支付查询详情---失败:" + error);
                        }
                    });
                    break;
                case AppConstants.PUSH_TAG_FAST_PAY_SUCCESS:    // 内网订单打印
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "On RawMessage：" + message);
                    XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "On RawMessage(" + AppConstants.PUSH_TAG_FAST_PAY_SUCCESS + ") 内网订单打印查询详情");
                    EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_FAST_PAY_SUCCESS));
                    EventBus.getDefault().post(new InnerPushEvent());
                    TradeManager.getInstance().getHoleBatchDetail(jsonObject.getString(RequestConstant.KEY_PUSH_DATA), new Callback<TradeRecordInfo>() {
                        @Override
                        public void onSuccess(TradeRecordInfo o) {
                            XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "On RawMessage(" + AppConstants.PUSH_TAG_FAST_PAY_SUCCESS + ") 内网订单打印查询详情---成功:" + o.status);
                            if (AppConstants.ONLINE_PAY_STATUS_PASS.equals(o.status)) {
                                // 当订单状态为确认状态时打印
                                TradeManager.getInstance().printTradeRecordInfoAsync(o, false);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            XLogger.e(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "On RawMessage(" + AppConstants.PUSH_TAG_FAST_PAY_SUCCESS + ") 内网订单打印查询详情---失败:" + error);
                        }
                    });
                    break;
                case AppConstants.PUSH_TAG_UPLOAD_LOG:  //上传日志
                    XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "On RawMessage：" + message);
                    XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "On RawMessage：" + AppConstants.PUSH_TAG_UPLOAD_LOG);
                    EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_UPLOAD_LOG));
                    MonitorManager.getInstance().pullLogFile(jsonObject.getString(RequestConstant.KEY_EN), jsonObject.getString(RequestConstant.KEY_DATE));
                    break;
                case AppConstants.PUSH_TAG_UPDATE_PAY_CHANNEL:  //更新支付方式
                    XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "On RawMessage：" + message);
                    XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "On RawMessage(" + AppConstants.PUSH_TAG_UPDATE_PAY_CHANNEL + ") 更新会所支付方式");
                    EventBus.getDefault().post(new EventPushReact(AppConstants.PUSH_TAG_UPDATE_PAY_CHANNEL));
                    ChannelManager.getInstance().getPayChannelList(null);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
