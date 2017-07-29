package com.xmd.m.notify.display;

import android.app.PendingIntent;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.view.View;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.EventBusSafeRegister;
import com.xmd.app.XmdActivityManager;

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
        XLogger.d("current is " + (XmdActivityManager.getInstance().isForeground() ? "bg" : "fg"));
        if (!XmdActivityManager.getInstance().isForeground()) {
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
            case XmdDisplay.STYLE_FLOAT_TOAST:
                showFloatToast(display);
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
            vibrator.vibrate(new long[]{100, 400, 100, 400}, -1);
        }
    }

    //显示通知栏
    private void showNotification(XmdDisplay display) {
        NotificationManager.getInstance().show(display);
    }

    //显示浮动消息
    private void showFloatToast(final XmdDisplay display) {
        FloatNotifyManager.getInstance()
                .setMessage(display.getMessage(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PendingIntent intent = XmdDisplayManager.getInstance().createPendingIntent(display);
                        if (intent != null) {
                            try {
                                intent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .show(display.getX(), display.getY(), display.getDuration());
    }
}
