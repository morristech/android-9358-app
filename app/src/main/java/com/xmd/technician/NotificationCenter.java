package com.xmd.technician;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.xmd.technician.common.AppUtils;
import com.xmd.technician.event.EventJoinedClub;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.onlinepaynotify.event.PayNotifyNewDataEvent;
import com.xmd.technician.window.MainActivity;

/**
 * Created by heyangya on 17-1-22.
 */
public class NotificationCenter {
    private static Context sContext;
    private static android.app.NotificationManager mNotificationManager;
    private static NotificationCenter ourInstance = new NotificationCenter();

    public static NotificationCenter getInstance() {
        return ourInstance;
    }

    private NotificationCenter() {
        //通过会所审核
        RxBus.getInstance().toObservable(EventJoinedClub.class).subscribe(this::onEventJoinedClub);
        //买单通知
        RxBus.getInstance().toObservable(PayNotifyNewDataEvent.class).subscribe(this::onPayNotifyNewDataEvent);
    }

    public static void init(Context context) {
        sContext = context;
        mNotificationManager = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    public static final int TYPE_DEFAULT = 1;
    public static final int TYPE_PAY_NOTIFY = 2; //支付通知
    public static final int TYPE_ORDER = 3;//订单
    public static final int TYPE_JOINED_CLUB = 4;//通过会所审核

    public static final int ID_DEFAULT = 1;
    public static final int ID_PAY_NOTIFY = 2;
    public static final int ID_JOINED_CLUB = 3;

    public void notify(String title, String text, int type) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(sContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);

        PendingIntent pendingIntent = PendingIntent.getActivity(sContext, 1, new Intent(sContext, MainActivity.class), 0);
        builder.setContentIntent(pendingIntent);

        builder.setDefaults(Notification.DEFAULT_ALL);

        switch (type) {
            case TYPE_PAY_NOTIFY:
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                mNotificationManager.notify(ID_PAY_NOTIFY, builder.build());
                break;
            case TYPE_ORDER:
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                //TODO
                break;
            case TYPE_JOINED_CLUB:
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                mNotificationManager.notify(ID_JOINED_CLUB, builder.build());
                break;
            case TYPE_DEFAULT:
            default:
                mNotificationManager.notify(ID_DEFAULT, builder.build());
        }
    }


    //有新的买单通知
    public void onPayNotifyNewDataEvent(PayNotifyNewDataEvent event) {
        //检查APP是否处于后台，如果处于后台，发送通知到系统通知栏
        if (AppUtils.isBackground()) {
            NotificationCenter.getInstance().notify("支付通知", "您有新的支付通知，请点击查看", NotificationCenter.TYPE_PAY_NOTIFY);
        }
    }

    //会所审核通过
    private void onEventJoinedClub(EventJoinedClub event) {
        NotificationCenter.getInstance().notify("审核通过", "恭喜您，成功加入" + event.clubName, NotificationCenter.TYPE_JOINED_CLUB);
    }
}
