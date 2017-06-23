package com.xmd.m.notify;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.SparseArray;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.util.AppUtils;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.IFunctionInit;

import org.greenrobot.eventbus.Subscribe;

import static com.xmd.m.notify.NotifyEvent.TYPE_CHAT_MESSAGE;
import static com.xmd.m.notify.NotifyEvent.TYPE_JOIN_VERIFY;
import static com.xmd.m.notify.NotifyEvent.TYPE_DEFAULT;
import static com.xmd.m.notify.NotifyEvent.TYPE_ORDER;
import static com.xmd.m.notify.NotifyEvent.TYPE_PAY_NOTIFY;

/**
 * Created by heyangya on 17-1-22.
 * 通知模块
 */
public class NotificationManager implements IFunctionInit {
    private static Context sContext;
    private static android.app.NotificationManager mNotificationManager;
    private static NotificationManager ourInstance = new NotificationManager();
    private static SparseArray<NotifySetting> settingMap = new SparseArray<>();


    public static NotificationManager getInstance() {
        return ourInstance;
    }

    private NotificationManager() {
        EventBusSafeRegister.register(this);
    }

    public void init(Context context) {
        sContext = context.getApplicationContext();
        mNotificationManager = (android.app.NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //默认通知
        settingMap.put(TYPE_DEFAULT, new NotifySetting(TYPE_DEFAULT, "", ""));
        //买单通知
        settingMap.put(TYPE_PAY_NOTIFY, new NotifySetting(TYPE_PAY_NOTIFY, "买单通知", "您有新的买单通知，请点击查看"));
        //加入会所审核结果
        settingMap.put(TYPE_JOIN_VERIFY, new NotifySetting(TYPE_JOIN_VERIFY, "审核", ""));
        //聊天消息通知
        NotifySetting messageSetting = new NotifySetting(TYPE_CHAT_MESSAGE, "新消息", "");
        settingMap.put(TYPE_CHAT_MESSAGE, messageSetting);
        //订单通知
        NotifySetting orderSetting = new NotifySetting(TYPE_CHAT_MESSAGE, "新订单", "");
        orderSetting.setSoundUri(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.order));
        settingMap.put(TYPE_ORDER, orderSetting);
    }

    /**
     * 从配置创建一个builder
     */
    private NotificationCompat.Builder createFromSetting(NotifySetting setting) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(sContext);
        builder.setSmallIcon(setting.getNotifyIcon());
        builder.setPriority(setting.getPriority());
        int defaults = 0;
        if (setting.isSound()) {
            if (setting.getSoundUri() != null) {
                builder.setSound(setting.getSoundUri());
            } else {
                defaults |= NotificationCompat.DEFAULT_SOUND;
            }
        }
        if (setting.isLight()) {
            defaults |= NotificationCompat.DEFAULT_LIGHTS;
        }
        if (setting.isVibrate()) {
            defaults |= NotificationCompat.DEFAULT_VIBRATE;
        }
        builder.setDefaults(defaults);

        builder.setContentTitle(setting.getTitle());

        //点击消失
        builder.setAutoCancel(true);

        return builder;
    }

    @Subscribe
    public void onNotify(NotifyEvent event) {
        NotifySetting setting = settingMap.get(event.getType());
        if (setting == null) {
            XLogger.e("unknown notify type : " + event);
            return;
        }
        if (!AppUtils.isBackground(sContext) && (setting.getNotifyScene() & NotifySetting.NOTIFY_SCENE_BACKGROUND) != 0) {
            //当前处于前台，但通知要求在后台
            return;
        }
        if (AppUtils.isBackground(sContext) && (setting.getNotifyScene() & NotifySetting.NOTIFY_SCENE_FOREGROUND) != 0) {
            //当前处于后台，但通知要求在前台
            return;
        }

        NotificationCompat.Builder builder = createFromSetting(setting);
        if (!TextUtils.isEmpty(event.getTitle())) {
            builder.setContentTitle(event.getTitle());
        }
        if (!TextUtils.isEmpty(event.getMessage())) {
            builder.setContentText(event.getMessage());
            builder.setTicker(event.getMessage());
        }
        mNotificationManager.notify(setting.getNotifyId(), builder.build());
    }
}
