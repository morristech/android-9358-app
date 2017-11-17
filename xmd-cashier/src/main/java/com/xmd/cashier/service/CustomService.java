package com.xmd.cashier.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
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
import android.widget.Toast;

import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.activity.InnerMethodActivity;
import com.xmd.cashier.adapter.InnerRecordDetailAdapter;
import com.xmd.cashier.adapter.OnlinePayNotifyAdapter;
import com.xmd.cashier.adapter.OrderRecordNotifyAdapter;
import com.xmd.cashier.cashier.PosFactory;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.InnerRecordInfo;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.dal.net.RequestConstant;
import com.xmd.cashier.dal.net.SpaService;
import com.xmd.cashier.dal.net.response.OnlinePayCouponResult;
import com.xmd.cashier.dal.sp.SPManager;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.manager.NotifyManager;
import com.xmd.cashier.widget.CustomNotifyLayoutManager;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.ServerException;
import com.xmd.m.network.XmdNetwork;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zr on 17-5-4.
 */

public class CustomService extends Service {
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    private CustomNotifyReceiver mReceiver;         // 广播:监听接单买单提醒

    public class CustomNotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(AppConstants.EXTRA_NOTIFY_TYPE);
            switch (type) {
                case AppConstants.EXTRA_NOTIFY_TYPE_ORDER_RECORD:
                    showOrderRecordNotify((List<OrderRecordInfo>) intent.getSerializableExtra(AppConstants.EXTRA_NOTIFY_DATA));
                    break;
                case AppConstants.EXTRA_NOTIFY_TYPE_ONLINE_PAY:
                    showOnlinePayNotify((List<OnlinePayInfo>) intent.getSerializableExtra(AppConstants.EXTRA_NOTIFY_DATA));
                    break;
                case AppConstants.EXTRA_NOTIFY_TYPE_INNER_PAY:
                    showInnerOrderNotify((InnerRecordInfo) intent.getSerializableExtra(AppConstants.EXTRA_NOTIFY_DATA));
                    break;
                default:
                    break;
            }
        }
    }

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private PercentRelativeLayout mLayout;  // 布局容器

    private boolean isShow;     // 当前是否有弹框显示

    private final static String EXTRA_CMD = "cmd";
    private final static String EXTRA_CMD_DATA = "cmd_data";

    private final static int ONGOING_NOTIFICATION = 2017;
    private final static int CMD_REFRESH_ONLINE_PAY_NOTIFY = 1;
    private final static int CMD_REFRESH_ORDER_RECORD_NOTIFY = 2;

    // 轮询间隔
    private static int ONLINE_PAY_INTERVAL = 15 * 1000;
    private static int ORDER_RECORD_INTERVAL = 15 * 1000;

    private Handler mOrderRecordHandler;
    private Handler mOnlinePayHandler;

    private Thread workOnlinePayThread;
    private Thread workOrderRecordThread;

    private class WorKOnlinePayThread extends Thread {
        @Override
        public void run() {
            while (mRefreshOnlinePayNotify) {
                try {
                    Thread.sleep(ONLINE_PAY_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                if (SPManager.getInstance().getFastPayPushTag() > 0) {
                    NotifyManager.getInstance().refreshOnlinePayNotify();
                }
            }
        }
    }

    private class WorkOrderRecordThread extends Thread {
        @Override
        public void run() {
            while (mRefreshOrderRecordNotify) {
                try {
                    Thread.sleep(ORDER_RECORD_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                if (SPManager.getInstance().getOrderPushTag() > 0) {
                    NotifyManager.getInstance().refreshOrderRecordNotify();
                }
            }
        }
    }

    private boolean mRefreshOnlinePayNotify;
    private boolean mRefreshOrderRecordNotify;

    private Runnable notifyOnlinePay = new Runnable() {
        @Override
        public void run() {
            textToSound("客户已买单,请尽快处理");
            wakeupScreen();
            mOnlinePayHandler.postDelayed(this, ONLINE_PAY_INTERVAL);
        }
    };
    private Runnable notifyOrderRecord = new Runnable() {
        @Override
        public void run() {
            textToSound("您有新的小摩豆预约订单");
            wakeupScreen();
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

        mPowerManager = (PowerManager) MainApplication.getInstance().getApplicationContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CustomService.class.getName());
        mWakeLock.acquire();

        mOrderRecordHandler = new Handler();
        mOnlinePayHandler = new Handler();

        mReceiver = new CustomNotifyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_CUSTOM_NOTIFY_RECEIVER);
        registerReceiver(mReceiver, filter);

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
            int cmd = intent.getIntExtra(EXTRA_CMD, -1);
            switch (cmd) {
                case CMD_REFRESH_ONLINE_PAY_NOTIFY:
                    // 刷新在线买单
                    mRefreshOnlinePayNotify = intent.getBooleanExtra(EXTRA_CMD_DATA, false);
                    if (mRefreshOnlinePayNotify) {
                        if (workOnlinePayThread == null) {
                            workOnlinePayThread = new WorKOnlinePayThread();
                            workOnlinePayThread.start();
                        }
                    } else {
                        if (workOnlinePayThread != null) {
                            workOnlinePayThread.interrupt();
                            workOnlinePayThread = null;
                        }
                    }
                    break;
                case CMD_REFRESH_ORDER_RECORD_NOTIFY:
                    // 刷新预约订单
                    mRefreshOrderRecordNotify = intent.getBooleanExtra(EXTRA_CMD_DATA, false);
                    if (mRefreshOrderRecordNotify) {
                        if (workOrderRecordThread == null) {
                            workOrderRecordThread = new WorkOrderRecordThread();
                            workOrderRecordThread.start();
                        }
                    } else {
                        if (workOrderRecordThread != null) {
                            workOrderRecordThread.interrupt();
                            workOrderRecordThread = null;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return START_STICKY;
    }

    public static void refreshOnlinePayNotify(boolean on) {
        Context context = MainApplication.getInstance().getApplicationContext();
        Intent intent = new Intent(context, CustomService.class);
        intent.putExtra(EXTRA_CMD, CustomService.CMD_REFRESH_ONLINE_PAY_NOTIFY);
        intent.putExtra(EXTRA_CMD_DATA, on);
        context.startService(intent);
    }

    public static void refreshOrderRecordNotify(boolean on) {
        Context context = MainApplication.getInstance().getApplicationContext();
        Intent intent = new Intent(context, CustomService.class);
        intent.putExtra(EXTRA_CMD, CustomService.CMD_REFRESH_ORDER_RECORD_NOTIFY);
        intent.putExtra(EXTRA_CMD_DATA, on);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }

        if (workOnlinePayThread != null) {
            workOnlinePayThread.interrupt();
            workOnlinePayThread = null;
        }
        if (workOrderRecordThread != null) {
            workOrderRecordThread.interrupt();
            workOrderRecordThread = null;
        }

        if (mLayout != null && isShow) {
            mWindowManager.removeView(mLayout);
        }
        unregisterReceiver(mReceiver);
        mOnlinePayHandler.removeCallbacks(notifyOnlinePay);
        mOrderRecordHandler.removeCallbacks(notifyOrderRecord);

        stopForeground(true);
    }

    // 打印小票
    private void posPrint(final String type, final Object object) {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        switch (type) {
                            case AppConstants.EXTRA_NOTIFY_TYPE_ONLINE_PAY:
                                NotifyManager.getInstance().printOnlinePayRecord((OnlinePayInfo) object, false, true);
                                NotifyManager.getInstance().printOnlinePayRecord((OnlinePayInfo) object, false, false);
                                break;
                            case AppConstants.EXTRA_NOTIFY_TYPE_ORDER_RECORD:
                                NotifyManager.getInstance().printOrderRecord((OrderRecordInfo) object, false);
                                break;
                            default:
                                break;
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    // 语音播放
    private void textToSound(String text) {
        if (!TextUtils.isEmpty(text)) {
            PosFactory.getCurrentCashier().textToSound(text);
        }
    }

    // 点亮屏幕
    private void wakeupScreen() {
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
            return;
        }
        refreshOrderRecordNotify(false);
        mLayout = (PercentRelativeLayout) LayoutInflater.from(MainApplication.getInstance().getApplicationContext()).inflate(R.layout.layout_custom_notify, null);
        mWindowManager.addView(mLayout, mLayoutParams);
        isShow = true;
        mOrderRecordHandler.post(notifyOrderRecord);
        RecyclerView rv = (RecyclerView) mLayout.findViewById(R.id.rv_custom_notify);
        final OrderRecordNotifyAdapter adapter = new OrderRecordNotifyAdapter();
        adapter.setData(list);
        adapter.setCallBack(new OrderRecordNotifyAdapter.OrderRecordNotifyCallBack() {
            @Override
            public void onAccept(final OrderRecordInfo info, final int position) {
                adapter.setLoadingStatus(position);    // 更新处理时的状态
                Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                        .updateOrderRecordStatus(AccountManager.getInstance().getToken(), AppConstants.SESSION_TYPE, AppConstants.ORDER_RECORD_STATUS_ACCEPT, info.id);
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        adapter.removeItem(position);
                        Toast.makeText(MainApplication.getInstance().getApplicationContext(), "接单成功", Toast.LENGTH_SHORT).show();
                        SPManager.getInstance().updateOrderPushTag();
                        info.status = AppConstants.ORDER_RECORD_STATUS_ACCEPT;
                        info.receiverName = AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")";
                        if (SPManager.getInstance().getOrderAcceptSwitch()) {
                            posPrint(AppConstants.EXTRA_NOTIFY_TYPE_ORDER_RECORD, info);
                        }
                        if (adapter.getItemCount() == 0) {
                            mOrderRecordHandler.removeCallbacks(notifyOrderRecord);
                            hide();
                            refreshOrderRecordNotify(true);
                        }
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_ERROR) {
                            SPManager.getInstance().updateOrderPushTag();
                            String tempStr = e.getLocalizedMessage();
                            if (tempStr.contains("处理")) {// FIXME 后台描述变更,则相应变更
                                tempStr = "订单已被处理，详情请查看付费预约列表";
                            }
                            adapter.setNormalStatus(position, tempStr);
                        } else {
                            adapter.setNormalStatus(position);
                            Toast.makeText(MainApplication.getInstance().getApplicationContext(), "接单失败:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onReject(final OrderRecordInfo info, final int position) {
                adapter.setLoadingStatus(position);    // 更新处理时的状态
                Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                        .updateOrderRecordStatus(AccountManager.getInstance().getToken(), AppConstants.SESSION_TYPE, AppConstants.ORDER_RECORD_STATUS_REJECT, info.id);
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        adapter.removeItem(position);
                        Toast.makeText(MainApplication.getInstance().getApplicationContext(), "拒绝成功", Toast.LENGTH_SHORT).show();
                        SPManager.getInstance().updateOrderPushTag();
                        info.status = AppConstants.ORDER_RECORD_STATUS_REJECT;
                        if (SPManager.getInstance().getOrderRejectSwitch()) {
                            posPrint(AppConstants.EXTRA_NOTIFY_TYPE_ORDER_RECORD, info);
                        }
                        if (adapter.getItemCount() == 0) {
                            mOrderRecordHandler.removeCallbacks(notifyOrderRecord);
                            hide();
                            refreshOrderRecordNotify(true);
                        }
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_ERROR) {
                            SPManager.getInstance().updateOrderPushTag();
                            // status=400
                            String tempStr = e.getLocalizedMessage();
                            if (tempStr.contains("处理")) {// FIXME 后台描述变更,则相应变更
                                tempStr = "订单已被处理，详情请查看付费预约列表";
                            }
                            adapter.setNormalStatus(position, tempStr);
                        } else {
                            adapter.setNormalStatus(position);
                            Toast.makeText(MainApplication.getInstance().getApplicationContext(), "拒绝失败:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onClose(int position) {
                adapter.removeItem(position);
                if (adapter.getItemCount() == 0) {
                    mOrderRecordHandler.removeCallbacks(notifyOrderRecord);
                    hide();
                    refreshOrderRecordNotify(true);
                }
            }
        });
        rv.setLayoutManager(new CustomNotifyLayoutManager());
        DefaultItemAnimator animator = new DefaultItemAnimator();
        //设置动画时间
        animator.setAddDuration(500);
        animator.setRemoveDuration(500);
        rv.setItemAnimator(animator);
        rv.setAdapter(adapter);
    }

    // 显示在线买单弹框
    public void showOnlinePayNotify(List<OnlinePayInfo> list) {
        if (isShow) {
            return;
        }
        refreshOnlinePayNotify(false);
        mLayout = (PercentRelativeLayout) LayoutInflater.from(MainApplication.getInstance().getApplicationContext()).inflate(R.layout.layout_custom_notify, null);
        mWindowManager.addView(mLayout, mLayoutParams);
        isShow = true;
        mOnlinePayHandler.post(notifyOnlinePay);
        RecyclerView rv = (RecyclerView) mLayout.findViewById(R.id.rv_custom_notify);
        final OnlinePayNotifyAdapter adapter = new OnlinePayNotifyAdapter();
        adapter.setData(list);
        adapter.setCallBack(new OnlinePayNotifyAdapter.OnlinePayNotifyCallBack() {
            @Override
            public void onPass(final OnlinePayInfo info, final int position) {
                adapter.setLoadingStatus(position);
                Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                        .updateOnlinePayStatus(AccountManager.getInstance().getToken(), info.id, AppConstants.ONLINE_PAY_STATUS_PASS);
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        adapter.removeItem(position);
                        Toast.makeText(MainApplication.getInstance().getApplicationContext(), "买单确认成功", Toast.LENGTH_SHORT).show();
                        SPManager.getInstance().updateFastPayPushTag();
                        info.status = AppConstants.ONLINE_PAY_STATUS_PASS;
                        info.operatorName = AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")";
                        if (SPManager.getInstance().getOnlinePassSwitch()) {
                            posPrint(AppConstants.EXTRA_NOTIFY_TYPE_ONLINE_PAY, info);
                        }
                        if (adapter.getItemCount() == 0) {
                            mOnlinePayHandler.removeCallbacks(notifyOnlinePay);
                            hide();
                            refreshOnlinePayNotify(true);
                        }
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_ERROR) {
                            SPManager.getInstance().updateFastPayPushTag();
                            // status = 400
                            String tempStr = e.getLocalizedMessage();
                            if (tempStr.contains("处理")) {// FIXME 后台描述变更,则相应变更
                                tempStr = "买单已被处理，详情请查看在线买单列表";
                            }
                            adapter.setNormalStatus(position, tempStr);
                        } else {
                            adapter.setNormalStatus(position);
                            Toast.makeText(MainApplication.getInstance().getApplicationContext(), "买单确认失败:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onUnpass(final OnlinePayInfo info, final int position) {
                adapter.setLoadingStatus(position);
                Observable<BaseBean> observable = XmdNetwork.getInstance().getService(SpaService.class)
                        .updateOnlinePayStatus(AccountManager.getInstance().getToken(), info.id, AppConstants.ONLINE_PAY_STATUS_UNPASS);
                XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
                    @Override
                    public void onCallbackSuccess(BaseBean result) {
                        adapter.removeItem(position);
                        Toast.makeText(MainApplication.getInstance().getApplicationContext(), "已通知请到前台", Toast.LENGTH_SHORT).show();
                        SPManager.getInstance().updateFastPayPushTag();
                        info.status = AppConstants.ONLINE_PAY_STATUS_UNPASS;
                        info.operatorName = AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")";
                        if (SPManager.getInstance().getOnlineUnpassSwitch()) {
                            posPrint(AppConstants.EXTRA_NOTIFY_TYPE_ONLINE_PAY, info);
                        }
                        if (adapter.getItemCount() == 0) {
                            mOnlinePayHandler.removeCallbacks(notifyOnlinePay);
                            hide();
                            refreshOnlinePayNotify(true);
                        }
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof ServerException && ((ServerException) e).statusCode == RequestConstant.RESP_ERROR) {
                            SPManager.getInstance().updateFastPayPushTag();
                            // status = 400
                            String tempStr = e.getLocalizedMessage();
                            if (tempStr.contains("处理")) {   // FIXME 后台描述变更,则相应变更
                                tempStr = "买单已被处理，详情请查看在线买单列表";
                            }
                            adapter.setNormalStatus(position, tempStr);
                        } else {
                            adapter.setNormalStatus(position);
                            Toast.makeText(MainApplication.getInstance().getApplicationContext(), "请到前台失败:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onClose(int position) {
                adapter.removeItem(position);
                if (adapter.getItemCount() == 0) {
                    mOnlinePayHandler.removeCallbacks(notifyOnlinePay);
                    hide();
                    refreshOnlinePayNotify(true);
                }
            }

            @Override
            public void onReturn(int position) {
                adapter.setNormalStatus(position);
            }

            @Override
            public void onDetail(OnlinePayInfo.OnlinePayDiscountInfo info, final int position) {
                adapter.setLoadingStatus(position);
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
                        Toast.makeText(MainApplication.getInstance().getApplicationContext(), "查看详情失败:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        adapter.setNormalStatus(position);
                    }
                });
            }
        });
        rv.setLayoutManager(new CustomNotifyLayoutManager());
        DefaultItemAnimator animator = new DefaultItemAnimator();
        //设置动画时间
        animator.setAddDuration(500);
        animator.setRemoveDuration(500);
        rv.setItemAnimator(animator);
        rv.setAdapter(adapter);
    }

    public void showInnerOrderNotify(final InnerRecordInfo recordInfo) {
        textToSound("您有一笔新结账订单待处理");
        wakeupScreen();
        if (isShow) {
            return;
        }
        mLayout = (PercentRelativeLayout) LayoutInflater.from(MainApplication.getInstance().getApplicationContext()).inflate(R.layout.layout_inner_notify, null);
        mWindowManager.addView(mLayout, mLayoutParams);
        isShow = true;

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
                hide();
            }
        });
        mOrigin.setText("￥" + Utils.moneyToStringEx(recordInfo.originalAmount));
        mNeed.setText("￥" + Utils.moneyToStringEx(recordInfo.payAmount));
        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                Intent intent = new Intent(MainApplication.getInstance().getApplicationContext(), InnerMethodActivity.class);
                intent.putExtra(AppConstants.EXTRA_INNER_METHOD_SOURCE, AppConstants.INNER_METHOD_SOURCE_PUSH);
                intent.putExtra(AppConstants.EXTRA_INNER_RECORD_DETAIL, recordInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                MainApplication.getInstance().getApplicationContext().startActivity(intent);
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
}
