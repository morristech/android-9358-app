package com.xmd.m.notify.display;

import android.app.PendingIntent;

/**
 * Created by mo on 17-6-27.
 * 行为工厂
 */

public interface XmdActionFactory {
    PendingIntent create(XmdDisplay display);
}
