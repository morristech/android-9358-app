package com.xmd.cashier.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.activity.InnerMethodActivity;
import com.xmd.cashier.activity.InnerModifyActivity;
import com.xmd.cashier.adapter.InnerRecordDetailAdapter;
import com.xmd.cashier.adapter.OnlinePayNotifyAdapter;
import com.xmd.cashier.adapter.OrderRecordNotifyAdapter;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.OnlinePayCouponResult;
import com.xmd.cashier.dal.net.response.OnlinePayListResult;
import com.xmd.cashier.dal.net.response.OrderRecordListResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.Callback;
import com.xmd.cashier.manager.InnerManager;
import com.xmd.cashier.manager.MonitorManager;
import com.xmd.cashier.manager.NotifyManager;
import com.xmd.cashier.widget.CustomNotifyLayoutManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Observable;

/**
 * Created by zr on 17-5-4.
 */

public class CustomService extends Service {
    public static final String ACTION = "com.xmd.cashier.CustomService";
    private static final String TAG = "CustomService";

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private PercentRelativeLayout mLayout;  // 布局容器

    private boolean isShow;     // 当前是否有弹框显示

    private final static int ONGOING_NOTIFICATION = 2017;

    public final static int CMD_REFRESH_ONLINE_PAY_NOTIFY = 1;
    public final static int CMD_REFRESH_ORDER_RECORD_NOTIFY = 2;
    public final static int CMD_POLLING_WIFI_STATUS = 3;

    // 轮询间隔
    private static long ONLINE_PAY_INTERVAL = 15 * 1000;
    private static long ORDER_RECORD_INTERVAL = 15 * 1000;
    private static long POLLING_WIFI_STATUS_INTERVAL = 30 * 1000;

    private Handler mOrderRecordHandler;
    private Handler mOnlinePayHandler;

    private class PollingWifiStatusThread extends Thread {
        @Override
        public void run() {
            XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + MonitorManager.getInstance().getWifiStatus());
            MonitorManager.getInstance().startPollingWifiStatus(SystemClock.elapsedRealtime() + POLLING_WIFI_STATUS_INTERVAL);
        }
    }

    private class WorKOnlinePayThread extends Thread {
        @Override
        public void run() {
            if (SPManager.getInstance().getFastPayPushTag() > 0) {
                NotifyManager.getInstance().notifyOnlinePayList();
            }
            NotifyManager.getInstance().startRepeatOnlinePay(SystemClock.elapsedRealtime() + ONLINE_PAY_INTERVAL);
        }
    }

    private class WorkOrderRecordThread extends Thread {
        @Override
        public void run() {
            if (SPManager.getInstance().getOrderPushTag() > 0) {
                NotifyManager.getInstance().notifyOrderRecordList();
            }
            NotifyManager.getInstance().startRepeatOrderRecord(SystemClock.elapsedRealtime() + ORDER_RECORD_INTERVAL);
        }
    }

    private Runnable notifyOnlinePay = new Runnable() {
        @Override
        public void run() {
            wakeupScreen();
            textToSound("客户已买单,请尽快处理");
            mOnlinePayHandler.postDelayed(this, ONLINE_PAY_INTERVAL);
        }
    };
    private Runnable notifyOrderRecord = new Runnable() {
        @Override
        public void run() {
            wakeupScreen();
            textToSound("您有新的小摩豆预约订单");
            mOrderRecordHandler.postDelayed(this, ORDER_RECORD_INTERVAL);
        }
    };

    public static void start() {
        Context context = MainApplication.getInstance().getApplicationContext();
        context.startService(new Intent(context, CustomService.class));
    }

    public static void stop() {
        Context context = MainApplication.getInstance().getApplicationContext();
        context.stopService(new Intent(context, CustomService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBusSafeRegister.register(this);

        mPowerManager = (PowerManager) MainApplication.getInstance().getApplicationContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CustomService.class.getName());
        mWakeLock.acquire();

        mOrderRecordHandler = new Handler();
        mOnlinePayHandler = new Handler();

        mLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);

        String packageName = CustomService.this.getPackageName();
        PackageManager packageManager = CustomService.this.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packageName));
        if (permission) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }

        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;

        mLayoutParams.width = mWindowManager.getDefaultDisplay().getWidth();
        mLayoutParams.height = mWindowManager.getDefaultDisplay().getHeight();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("9358收银台");
        builder.setContentText("小摩豆为您提供服务 ... ...");
        builder.setShowWhen(false);
        builder.setOngoing(true);
        Notification notification = builder.build();
        startForeground(ONGOING_NOTIFICATION, notification);

        if (intent != null) {
            int cmd = intent.getIntExtra(AppConstants.EXTRA_CMD, -1);
            switch (cmd) {
                case CMD_REFRESH_ONLINE_PAY_NOTIFY:
                    new WorKOnlinePayThread().start();
                    break;
                case CMD_REFRESH_ORDER_RECORD_NOTIFY:
                    new WorkOrderRecordThread().start();
                    break;
                case CMD_POLLING_WIFI_STATUS:
                    new PollingWifiStatusThread().start();
                    break;
                default:
                    break;
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusSafeRegister.unregister(this);

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }

        if (mLayout != null && isShow) {
            mWindowManager.removeView(mLayout);
        }
        mOnlinePayHandler.removeCallbacks(notifyOnlinePay);
        mOrderRecordHandler.removeCallbacks(notifyOrderRecord);

        stopForeground(true);
    }

    // 语音播放
    private void textToSound(String text) {
        if (!TextUtils.isEmpty(text)) {
            XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "语音播放:" + text);
            PosFactory.getCurrentCashier().textToSound(text);
        }
    }

    // 点亮屏幕
    private void wakeupScreen() {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + "Wake up Screen");
        PowerManager.WakeLock wakeLock;
        PowerManager pm = (PowerManager) MainApplication.getInstance().getApplicationContext().getSystemService(POWER_SERVICE);
        if (!pm.isScreenOn()) {
            wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "NOTIFY");
            wakeLock.acquire();
            wakeLock.release();
        }
    }

    // 显示预约订单弹框
    public void showOrderRecordNotify(List<OrderRecordInfo> list) {
        if (isShow) {
            XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单弹框提醒: 当前已有弹框显示");
            return;
        }
        NotifyManager.getInstance().stopRepeatOrderRecord();
        mLayout = (PercentRelativeLayout) LayoutInflater.from(MainApplication.getInstance().getApplicationContext()).inflate(R.layout.layout_custom_notify, null);
        mWindowManager.addView(mLayout, mLayoutParams);
        isShow = true;
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单弹框提醒: 显示");

        mOrderRecordHandler.post(notifyOrderRecord);
        RecyclerView rv = (RecyclerView) mLayout.findViewById(R.id.rv_custom_notify);
        final OrderRecordNotifyAdapter adapter = new OrderRecordNotifyAdapter();
        adapter.setData(list);
        adapter.setCallBack(new OrderRecordNotifyAdapter.OrderRecordNotifyCallBack() {
            @Override
            public void onAccept(final OrderRecordInfo info, final int position) {
                adapter.setLoadingStatus(position);    // 更新处理时的状态
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单接单:" + info.id);
                NotifyManager.getInstance().acceptOrder(info.id, new Callback<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean o) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单接单---成功:" + info.id);
                        XToast.show("接单成功");
                        SPManager.getInstance().updateOrderPushTag();
                        adapter.removeItem(position);
                        info.status = AppConstants.ORDER_RECORD_STATUS_ACCEPT;
                        info.receiverName = AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")";
                        if (SPManager.getInstance().getOrderAcceptSwitch()) {
                            NotifyManager.getInstance().printOrderRecordAsync(info, false);
                        }
                        if (adapter.getItemCount() == 0) {
                            mOrderRecordHandler.removeCallbacks(notifyOrderRecord);
                            hide();
                            NotifyManager.getInstance().startRepeatOrderRecord(SystemClock.elapsedRealtime() + ORDER_RECORD_INTERVAL);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单接单---失败:" + error);
                        if (error.contains("处理")) {
                            // FIXME 后台描述变更,则相应变更
                            error = "订单已被处理，详情请查看付费预约列表";
                        }
                        adapter.setNormalStatus(position, error);
                    }
                });
            }

            @Override
            public void onReject(final OrderRecordInfo info, final int position) {
                adapter.setLoadingStatus(position);    // 更新处理时的状态
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单拒绝:" + info.id);
                NotifyManager.getInstance().rejectOrder(info.id, new Callback<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean o) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单拒绝---成功:" + info.id);
                        XToast.show("已拒绝");
                        SPManager.getInstance().updateOrderPushTag();
                        adapter.removeItem(position);
                        info.status = AppConstants.ORDER_RECORD_STATUS_REJECT;
                        if (SPManager.getInstance().getOrderRejectSwitch()) {
                            NotifyManager.getInstance().printOrderRecordAsync(info, false);
                        }
                        if (adapter.getItemCount() == 0) {
                            mOrderRecordHandler.removeCallbacks(notifyOrderRecord);
                            hide();
                            NotifyManager.getInstance().startRepeatOrderRecord(SystemClock.elapsedRealtime() + ORDER_RECORD_INTERVAL);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单拒绝---失败:" + error);
                        if (error.contains("处理")) {
                            // FIXME 后台描述变更,则相应变更
                            error = "订单已被处理，详情请查看付费预约列表";
                        }
                        adapter.setNormalStatus(position, error);
                    }
                });
            }

            @Override
            public void onClose(int position) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "预约订单 on close");
                adapter.removeItem(position);
                if (adapter.getItemCount() == 0) {
                    mOrderRecordHandler.removeCallbacks(notifyOrderRecord);
                    hide();
                    NotifyManager.getInstance().startRepeatOrderRecord(SystemClock.elapsedRealtime() + ORDER_RECORD_INTERVAL);
                }
            }
        });
        rv.setLayoutManager(new CustomNotifyLayoutManager());

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(500);
        animator.setRemoveDuration(500);
        rv.setItemAnimator(animator);

        rv.setAdapter(adapter);
    }

    // 显示在线买单弹框
    public void showOnlinePayNotify(List<OnlinePayInfo> list) {
        if (isShow) {
            XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单弹框提醒: 当前已有弹框显示");
            return;
        }
        NotifyManager.getInstance().stopRepeatOnlinePay();
        mLayout = (PercentRelativeLayout) LayoutInflater.from(MainApplication.getInstance().getApplicationContext()).inflate(R.layout.layout_custom_notify, null);
        mWindowManager.addView(mLayout, mLayoutParams);
        isShow = true;
        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单弹框提醒: 显示");
        mOnlinePayHandler.post(notifyOnlinePay);
        RecyclerView rv = (RecyclerView) mLayout.findViewById(R.id.rv_custom_notify);
        final OnlinePayNotifyAdapter adapter = new OnlinePayNotifyAdapter();
        adapter.setData(list);
        adapter.setCallBack(new OnlinePayNotifyAdapter.OnlinePayNotifyCallBack() {
            @Override
            public void onPass(final OnlinePayInfo info, final int position) {
                adapter.setLoadingStatus(position);
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单确认:" + info.id);
                NotifyManager.getInstance().passOnlinePay(info.id, AppConstants.ONLINE_PAY_STATUS_PASS, new Callback<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean o) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单确认---成功:" + info.id);
                        XToast.show("买单确认成功");
                        adapter.removeItem(position);
                        SPManager.getInstance().updateFastPayPushTag();
                        info.status = AppConstants.ONLINE_PAY_STATUS_PASS;
                        info.operatorName = AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")";
                        if (SPManager.getInstance().getOnlinePassSwitch()) {
                            NotifyManager.getInstance().printOnlinePayRecordAsync(info, false);
                        }
                        if (adapter.getItemCount() == 0) {
                            mOnlinePayHandler.removeCallbacks(notifyOnlinePay);
                            hide();
                            NotifyManager.getInstance().startRepeatOnlinePay(SystemClock.elapsedRealtime() + ONLINE_PAY_INTERVAL);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单确认---失败:" + error);
                        if (error.contains("处理")) {
                            // FIXME 后台描述变更,则相应变更
                            error = "买单已被处理，详情请查看在线买单列表";
                        }
                        adapter.setNormalStatus(position, error);
                    }
                });
            }

            @Override
            public void onUnpass(final OnlinePayInfo info, final int position) {
                adapter.setLoadingStatus(position);
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单请到前台:" + info.id);
                NotifyManager.getInstance().unPassOnlinePay(info.id, AppConstants.ONLINE_PAY_STATUS_UNPASS, new Callback<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean o) {
                        XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单请到前台---成功:" + info.id);
                        XToast.show("已通知请到前台");
                        SPManager.getInstance().updateFastPayPushTag();
                        adapter.removeItem(position);
                        info.status = AppConstants.ONLINE_PAY_STATUS_UNPASS;
                        info.operatorName = AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")";
                        if (SPManager.getInstance().getOnlineUnpassSwitch()) {
                            NotifyManager.getInstance().printOnlinePayRecordAsync(info, false);
                        }
                        if (adapter.getItemCount() == 0) {
                            mOnlinePayHandler.removeCallbacks(notifyOnlinePay);
                            hide();
                            NotifyManager.getInstance().startRepeatOnlinePay(SystemClock.elapsedRealtime() + ONLINE_PAY_INTERVAL);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单请到前台---失败:" + error);
                        if (error.contains("处理")) {
                            // FIXME 后台描述变更,则相应变更
                            error = "买单已被处理，详情请查看在线买单列表";
                        }
                        adapter.setNormalStatus(position, error);
                    }
                });
            }

            @Override
            public void onClose(int position) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单 on close");
                adapter.removeItem(position);
                if (adapter.getItemCount() == 0) {
                    mOnlinePayHandler.removeCallbacks(notifyOnlinePay);
                    hide();
                    NotifyManager.getInstance().startRepeatOnlinePay(SystemClock.elapsedRealtime() + ONLINE_PAY_INTERVAL);
                }
            }

            @Override
            public void onReturn(int position) {
                adapter.setNormalStatus(position);
            }

            @Override
            public void onDetail(OnlinePayInfo.OnlinePayDiscountInfo info, final int position) {
                adapter.setLoadingStatus(position);
                XLogger.i(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单查看优惠详情:" + info.verifyCode);
                Observable<OnlinePayCouponResult> observable = XmdNetwork.getInstance().getService(SpaService.class)
                        .getDiscountCoupon(AccountManager.getInstance().getToken(), info.verifyCode);
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<OnlinePayCouponResult>() {
                    @Override
                    public void onCallbackSuccess(OnlinePayCouponResult result) {
                        adapter.setDetailStatus(position, result.getRespData());
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        XLogger.e(TAG, AppConstants.LOG_BIZ_NORMAL_CASHIER + "在线买单查看优惠详情---失败:" + e.getLocalizedMessage());
                        XToast.show("查看详情失败:" + e.getLocalizedMessage());
                        adapter.setNormalStatus(position);
                    }
                });
            }
        });
        rv.setLayoutManager(new CustomNotifyLayoutManager());

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(500);
        animator.setRemoveDuration(500);
        rv.setItemAnimator(animator);

        rv.setAdapter(adapter);
    }

    // 显示内网订单弹框
    public void showInnerOrderNotify(final InnerRecordInfo recordInfo) {
        wakeupScreen();
        textToSound("您有一笔新结账订单待处理");
        if (isShow) {
            XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单弹框提醒：当前已有弹框显示 " + recordInfo.payId);
            return;
        }
        mLayout = (PercentRelativeLayout) LayoutInflater.from(MainApplication.getInstance().getApplicationContext()).inflate(R.layout.layout_inner_notify, null);
        mWindowManager.addView(mLayout, mLayoutParams);
        isShow = true;
        XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "内网订单弹框提醒：" + recordInfo.payId);

        ImageView mCloseImg = (ImageView) mLayout.findViewById(R.id.notify_inner_off);
        RecyclerView mInnerList = (RecyclerView) mLayout.findViewById(R.id.item_inner_list);
        if (recordInfo.details != null && !recordInfo.details.isEmpty()) {
            InnerRecordDetailAdapter detailAdapter = new InnerRecordDetailAdapter(MainApplication.getInstance().getApplicationContext(), true);
            detailAdapter.setData(recordInfo.details);
            mInnerList.setLayoutManager(new LinearLayoutManager(MainApplication.getInstance().getApplicationContext()));
            mInnerList.setAdapter(detailAdapter);
        }
        TextView mOrigin = (TextView) mLayout.findViewById(R.id.notify_inner_origin_amount);
        TextView mNeed = (TextView) mLayout.findViewById(R.id.notify_inner_need_amount);
        Button mPayBtn = (Button) mLayout.findViewById(R.id.btn_notify_inner_pay);
        mCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "关闭内网订单弹框：" + recordInfo.payId);
                hide();
            }
        });
        mOrigin.setText("￥" + Utils.moneyToStringEx(recordInfo.originalAmount));
        mNeed.setText("￥" + Utils.moneyToStringEx(recordInfo.payAmount));
        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogger.i(TAG, AppConstants.LOG_BIZ_NATIVE_CASHIER + "选择支付内网订单：" + recordInfo.payId);
                hide();
                InnerManager.getInstance().initTradeByRecord(recordInfo);
                if (recordInfo.paidAmount > 0) {
                    Intent intent = new Intent(MainApplication.getInstance().getApplicationContext(), InnerModifyActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    MainApplication.getInstance().getApplicationContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(MainApplication.getInstance().getApplicationContext(), InnerMethodActivity.class);
                    intent.putExtra(AppConstants.EXTRA_INNER_METHOD_SOURCE, AppConstants.INNER_METHOD_SOURCE_PUSH);
                    intent.putExtra(AppConstants.EXTRA_INNER_RECORD_DETAIL, recordInfo);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    MainApplication.getInstance().getApplicationContext().startActivity(intent);
                }
            }
        });
    }

    // 关闭弹框
    public void hide() {
        if (isShow) {
            if (mLayout != null) {
                mWindowManager.removeViewImmediate(mLayout);
                mLayout = null;
            }
            isShow = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InnerRecordInfo innerRecordInfo) {
        showInnerOrderNotify(innerRecordInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OnlinePayListResult onlinePayListResult) {
        showOnlinePayNotify(onlinePayListResult.getRespData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OrderRecordListResult orderRecordListResult) {
        showOrderRecordNotify(orderRecordListResult.getRespData());
    }
}
