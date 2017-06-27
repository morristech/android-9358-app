package com.xmd.m.notify.display;

import android.app.PendingIntent;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Vibrator;

import com.shidou.commonlibrary.util.AppUtils;
import com.xmd.app.EventBusSafeRegister;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mo on 17-6-27.
 * 处理消息的显示
 */

public class XmdDisplayManager {

    private static final XmdDisplayManager ourInstance = new XmdDisplayManager();

    public static XmdDisplayManager getInstance() {
        return ourInstance;
    }

    private XmdDisplayManager() {
    }

    private Context context;
    private XmdActionFactory xmdActionFactory;

    public void init(Context context, XmdActionFactory xmdActionFactory) {
        this.context = context;
        this.xmdActionFactory = xmdActionFactory;
        NotificationManager.getInstance().init(context);
        EventBusSafeRegister.register(this);
    }

    @Subscribe
    public void show(XmdDisplay display) {
        if (AppUtils.isBackground(context)) {
            if ((display.getScene() & XmdDisplay.SCENE_BG) == 0) {
                return;
            }
        } else {
            if ((display.getScene() & XmdDisplay.SCENE_FG) == 0) {
                return;
            }
        }
        switch (display.getStyle()) {
            case XmdDisplay.STYLE_NONE:
                showNoneUi(display);
                break;
            case XmdDisplay.STYLE_NOTIFICATION:
                showNotification(display);
                break;
        }
    }

    public PendingIntent createPendingIntent(XmdDisplay display) {
        if (xmdActionFactory != null) {
            return xmdActionFactory.create(display);
        }
        return null;
    }

    //不显示UI
    private void showNoneUi(XmdDisplay display) {
        if ((display.getFlags() & XmdDisplay.FLAG_RING) != 0) {
            Ringtone rt = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            rt.play();
        }
        if ((display.getFlags() & XmdDisplay.FLAG_VIBRATE) != 0) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{100, 400, 100, 400}, 1);
        }
    }

    //显示通知栏
    private void showNotification(XmdDisplay display) {
        NotificationManager.getInstance().show(display);
    }

    //显示toast消息

}
