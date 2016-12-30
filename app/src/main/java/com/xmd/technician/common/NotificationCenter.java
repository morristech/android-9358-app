package com.xmd.technician.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by heyangya on 16-12-29.
 */
public class NotificationCenter {
    private static NotificationCenter ourInstance = new NotificationCenter();
    private NotificationManager mManager;
    public static final int NOTIFICATION_ORDER = 1;
    public static final int NOTIFICATION_CHAT_MESSAGE = 2;

    public static NotificationCenter getInstance() {
        return ourInstance;
    }

    private NotificationCenter() {

    }

    public synchronized void post(Context context, String message, int id) {
        if (mManager == null) {
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentText(message);
        builder.build();

    }
}
