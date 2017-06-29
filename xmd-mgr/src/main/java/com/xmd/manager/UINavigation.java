package com.xmd.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.m.notify.display.XmdActionFactory;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.manager.chat.EmchatConstant;
import com.xmd.manager.window.ChatActivity;
import com.xmd.manager.window.MainActivity;
import com.xmd.manager.window.OnlinePayActivity;

/**
 * Created by mo on 17-6-29.
 * 界面跳转中心
 */

public class UINavigation {
    public static final int REQUEST_CODE_UI_ROUTE = 0x3300;
    public static final String EXTRA_XMD_DISPLAY = "extra_xmd_display";

    public static XmdActionFactory xmdActionFactory = new XmdActionFactory() {
        @Override
        public PendingIntent create(XmdDisplay display) {
            Intent intent = new Intent(ManagerApplication.getAppContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_XMD_DISPLAY, display);
            return PendingIntent.getActivity(ManagerApplication.getAppContext(), REQUEST_CODE_UI_ROUTE, intent, PendingIntent.FLAG_ONE_SHOT);
        }
    };

    public static void processXmdDisplay(Context context, XmdDisplay display) {
        XLogger.d("processXmdDisplay:" + display);
        if (display.getAction() == null) {
            return;
        }
        switch (display.getAction()) {
            case XmdDisplay.ACTION_CHAT_TO:
                gotoChatActivity(context, display.getActionData());
                break;
            case XmdDisplay.ACTION_VIEW_FAST_PAY:
                gotoOnlinePayNotifyList(context);
                break;
            default:
                break;
        }
    }


    public static void gotoOnlinePayNotifyList(Context context) {
        Intent intent = new Intent(context, OnlinePayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void gotoChatActivity(Context context, String chatId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EmchatConstant.EMCHAT_ID, chatId);
        context.startActivity(intent);
    }
}
