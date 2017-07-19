package com.xmd.manager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.view.ChatActivity;
import com.xmd.m.comment.CommentDetailActivity;
import com.xmd.m.comment.CommentListActivity;
import com.xmd.m.notify.display.XmdActionFactory;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.manager.window.LoginActivity;
import com.xmd.manager.window.MainActivity;
import com.xmd.manager.window.OnlinePayActivity;
import com.xmd.manager.window.OrdersDetailActivity;

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
            case XmdDisplay.ACTION_VIEW_ORDER_DETAIL:
                gotoOrderDetail(context, display.getActionData());
                break;
            case XmdDisplay.ACTION_VIEW_COMMENT:
                gotoComment(context);
                break;
            case XmdDisplay.ACTION_VIEW_COMMENT_DETAIL:
                gotoCommentDetail(context, display.getActionData());
            default:
                break;
        }
    }

    public static void gotoOrderDetail(Context context, String orderId) {
        Intent intent = new Intent(context, OrdersDetailActivity.class);
//        intent.putExtra(OrderDetailActivity.KEY_ORDER_ID, orderId);
        context.startActivity(intent);
    }

    public static void gotoComment(Context context) {
        CommentListActivity.startCommentListActivity((Activity) context, true, null);
    }

    public static void gotoCommentDetail(Context context, String commentId) {
        CommentDetailActivity.startCommentDetailActivity((Activity) context, commentId, true);
    }

    public static void gotoOnlinePayNotifyList(Context context) {
        Intent intent = new Intent(context, OnlinePayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void gotoChatActivity(Context context, String chatId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ChatActivity.EXTRA_CHAT_ID, chatId);
        context.startActivity(intent);
    }

    public static void gotoLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
