package com.xmd.manager.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.xmd.manager.ManagerApplication;
import com.xmd.manager.R;
import com.xmd.manager.beans.GetuiPayload;
import com.xmd.manager.window.WelcomeActivity;

import java.security.SecureRandom;

/**
 * Created by sdcm on 15-12-7.
 */
public class WrapperNotificationManager {

    private static final int ORDER_NOTIFICATION_ID = 0x0001;

    private static class NotificationManagerHolder {
        private static WrapperNotificationManager sInstance = new WrapperNotificationManager();
    }

    private WrapperNotificationManager() {

    }

    public static WrapperNotificationManager getInstance() {

        return NotificationManagerHolder.sInstance;
    }

    /**
     * @param wrapperMsg
     */
    public void postOrderNotification(GetuiPayload wrapperMsg, String title) {

        PendingIntent clickIntent = createPendingIntent(new Intent(ManagerApplication.getAppContext(), WelcomeActivity.class));

        Notification notification = null;

        Notification.Builder builder = new Notification.Builder(ManagerApplication.getAppContext())
                .setSmallIcon(R.drawable.push)
                .setContentTitle(title)
                .setContentText(wrapperMsg.msgContent)
                .setTicker(wrapperMsg.msgContent)
                .setAutoCancel(true)
                .setContentIntent(clickIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }

        if (notification != null) {

            /*if (wrapperMsg.ring) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }

            if (wrapperMsg.vibrate) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }*/

            NotificationManager notificationManager = (NotificationManager) ManagerApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(new SecureRandom().nextInt(), notification);
        }

    }

    private PendingIntent createPendingIntent(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(ManagerApplication.getAppContext(), 0, intent, 0);
        return pendingIntent;
    }

}
