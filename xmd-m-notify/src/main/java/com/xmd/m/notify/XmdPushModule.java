package com.xmd.m.notify;

import android.content.Context;

import com.xmd.m.notify.display.XmdActionFactory;
import com.xmd.m.notify.display.XmdDisplayManager;
import com.xmd.m.notify.push.XmdPushManager;
import com.xmd.m.notify.push.XmdPushMessageListener;

/**
 * Created by mo on 17-6-23.
 * 小摩豆推送
 */

public class XmdPushModule {
    public final static String TAG = "XmdPush";
    private static final XmdPushModule ourInstance = new XmdPushModule();

    public static XmdPushModule getInstance() {
        return ourInstance;
    }

    private XmdPushModule() {
    }

    public void init(Context context, String userType, XmdActionFactory xmdActionFactory, XmdPushMessageListener listener) {
        XmdDisplayManager.getInstance().init(context, xmdActionFactory);
        XmdPushManager.getInstance().init(context, userType, listener);
    }
}
