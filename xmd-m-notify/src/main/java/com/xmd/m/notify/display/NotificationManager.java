package com.xmd.m.notify.display;

import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.xmd.app.IFunctionInit;
import com.xmd.m.notify.R;

/**
 * Created by heyangya on 17-1-22.
 * 通知模块
 */
public class NotificationManager implements IFunctionInit {
    private static NotificationManager ourInstance = new NotificationManager();

    public static NotificationManager getInstance() {
        return ourInstance;
    }

    private NotificationManager() {

    }

    private static Context sContext;
    private static android.app.NotificationManager mNotificationManager;

    public void init(Context context) {
        sContext = context.getApplicationContext();
        mNotificationManager = (android.app.NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public void show(XmdDisplay display) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(sContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        int defaults = 0;
        if ((display.getFlags() & XmdDisplay.FLAG_RING) != 0) {
            defaults |= NotificationCompat.DEFAULT_SOUND;
        }
        if ((display.getFlags() & XmdDisplay.FLAG_VIBRATE) != 0) {
            defaults |= NotificationCompat.DEFAULT_VIBRATE;
        }
        if ((display.getFlags() & XmdDisplay.FLAG_LIGHT) != 0) {
            defaults |= NotificationCompat.DEFAULT_LIGHTS;
        }
        builder.setDefaults(defaults);
        builder.setContentTitle(display.getTitle());
        builder.setContentText(display.getMessage());
        builder.setTicker(display.getMessage());
        builder.setAutoCancel(true);
        PendingIntent pendingIntent = XmdDisplayManager.getInstance().createPendingIntent(display);
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        mNotificationManager.notify(display.getBusinessType().hashCode(), builder.build());
    }
}
