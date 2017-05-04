package com.xmd.technician.notify;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.SparseArray;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.R;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.event.EventReceiveMessage;
import com.xmd.technician.common.AppUtils;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.event.EventJoinedClub;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.window.MainActivity;

import java.util.List;

/**
 * Created by heyangya on 17-1-22.
 */
public class NotificationCenter {
    private static Context sContext;
    private static android.app.NotificationManager mNotificationManager;
    private static NotificationCenter ourInstance = new NotificationCenter();
    private static SparseArray<NotifySetting> settingMap = new SparseArray<>();


    public static final int TYPE_DEFAULT = 0x7601;
    public static final int TYPE_PAY_NOTIFY = 0x7602; //客户买单
    public static final int TYPE_CLUB_VERIFY = 0x7603; // 申请加入会所审核
    public static final int TYPE_CHAT_MESSAGE = 0x7604; //聊天
    public static final int TYPE_ORDER = 0x7605; //订单

    public static NotificationCenter getInstance() {
        return ourInstance;
    }

    private NotificationCenter() {
        //通过会所审核
        RxBus.getInstance().toObservable(EventJoinedClub.class).subscribe(this::onEventJoinedClub);

        //聊天消息
        RxBus.getInstance().toObservable(EventReceiveMessage.class).subscribe(this::onEventChatMessage);
    }

    public static void init(Context context) {
        sContext = context;
        mNotificationManager = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);

        settingMap.put(TYPE_DEFAULT, new NotifySetting(TYPE_DEFAULT, NotificationCompat.PRIORITY_DEFAULT, true, true, true, "", "", MainActivity.class));
        settingMap.put(TYPE_PAY_NOTIFY, new NotifySetting(TYPE_PAY_NOTIFY, NotificationCompat.PRIORITY_MAX, true, true, true, "买单通知", "您有新的买单通知，请点击查看", MainActivity.class));
        settingMap.put(TYPE_CLUB_VERIFY, new NotifySetting(TYPE_CLUB_VERIFY, NotificationCompat.PRIORITY_MAX, true, true, true, "审核", "", MainActivity.class));

        NotifySetting messageSetting = new NotifySetting(TYPE_CHAT_MESSAGE, NotificationCompat.PRIORITY_HIGH, true, true, false, "新消息", "", MainActivity.class);
        settingMap.put(TYPE_CHAT_MESSAGE, messageSetting);

        NotifySetting orderSetting = new NotifySetting(TYPE_CHAT_MESSAGE, NotificationCompat.PRIORITY_MAX, true, true, true, "新订单", "", MainActivity.class);
        orderSetting.setSoundUri(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.order));
        settingMap.put(TYPE_ORDER, orderSetting);
    }

    /**
     * 从配置创建一个builder
     *
     * @param setting 各种通知的设置
     * @return
     */
    private NotificationCompat.Builder createFromSetting(NotifySetting setting, Bundle extraData) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(sContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
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

        if (extraData == null) {
            extraData = new Bundle();
        }
        extraData.putInt(UINavigation.EXTRA_NOTIFY_ID, setting.getNotifyId());
        Intent intent = new Intent(sContext, setting.getTargetActivity());
        intent.putExtras(extraData);
        PendingIntent pendingIntent = PendingIntent.getActivity(sContext, setting.getNotifyId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.setContentTitle(setting.getTitle());

        //点击消失
        builder.setAutoCancel(true);

        return builder;
    }

    private void notify(int type, CharSequence title, CharSequence message, Bundle extraData) {
        NotifySetting setting = settingMap.get(type);
        if (setting == null) {
            return;
        }
        NotificationCompat.Builder builder = createFromSetting(setting, extraData);
        if (!TextUtils.isEmpty(title)) {
            builder.setContentTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setContentText(message);
            builder.setTicker(message);
        }
        mNotificationManager.notify(setting.getNotifyId(), builder.build());
    }

    private void notifyWhenBackground(int type, CharSequence title, CharSequence message, Bundle extraData) {
        if (AppUtils.isBackground()) {
            notify(type, title, message, extraData);
        }
    }

    /*****************************处理各种消息**************************************/

    //有新的买单通知
    public void notifyPayNotify(String title, String message) {
        notify(TYPE_PAY_NOTIFY, title, message, null);
    }

    //会所审核通过
    private void onEventJoinedClub(EventJoinedClub event) {
        notify(TYPE_CLUB_VERIFY, null, "恭喜您，成功加入" + event.clubName, null);
    }

    //聊天消息
    private void onEventChatMessage(EventReceiveMessage eventReceiveMessage) {
        List<EMMessage> list = eventReceiveMessage.getList();
        if (list != null && list.size() > 0) {
            for (EMMessage message : list) {
                String userName = "客户";
                try {
                    userName = message.getStringAttribute(ChatConstant.KEY_NAME);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                Bundle bundle = new Bundle();
                bundle.putString(ChatConstant.EMCHAT_ID, message.getFrom());
                bundle.putString(ChatConstant.EMCHAT_NICKNAME, CommonUtils.getMessageStringAttribute(message, ChatConstant.EMCHAT_NICKNAME));
                bundle.putString(ChatConstant.EMCHAT_AVATAR, CommonUtils.getMessageStringAttribute(message, ChatConstant.EMCHAT_AVATAR));
                if (CommonUtils.getCustomChatType(message) == ChatConstant.MESSAGE_TYPE_RECV_ORDER) {
                    //订单消息
                    notify(TYPE_ORDER, null, userName + ":" + CommonUtils.getMessageDigest(message, sContext), bundle);
                } else {
                    //其他消息
                    if (AppUtils.isBackground()) {
                        notify(TYPE_CHAT_MESSAGE, null, userName + ":" + CommonUtils.getMessageDigest(message, sContext), bundle);
                    } else {
                        Ringtone rt = RingtoneManager.getRingtone(sContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        rt.play();
                    }
                }
            }
        }
    }
}
