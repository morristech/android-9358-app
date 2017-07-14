package com.xmd.technician.common;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.xmd_m_comment.CustomerInfoDetailActivity;
import com.example.xmd_m_comment.httprequest.ConstantResources;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.m.notify.display.XmdActionFactory;
import com.xmd.m.notify.display.XmdDisplay;
import com.xmd.technician.Constant;
import com.xmd.technician.TechApplication;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.onlinepaynotify.view.OnlinePayNotifyActivity;
import com.xmd.technician.window.CompleteRegisterInfoActivity;
import com.xmd.technician.window.ContactInformationDetailActivity;
import com.xmd.technician.window.JoinClubActivity;
import com.xmd.technician.window.LoginActivity;
import com.xmd.technician.window.MainActivity;
import com.xmd.technician.window.RegisterActivity;
import com.xmd.technician.window.TechChatActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by heyangya on 16-12-22.
 */

public class UINavigation {
    public static final String EXTRA_JOIN_CLUB = "extra_join_club";
    public static final String EXTRA_OPEN_JOIN_CLUB_FROM = "extra_join_club_from";
    public static final String EXTRA_NOTIFY_TYPE = "extra_notify_id";

    public static final int OPEN_JOIN_CLUB_FROM_START = 1;
    public static final int OPEN_JOIN_CLUB_FROM_MAIN = 3;

    public static final int REQUEST_CODE_UI_ROUTE = 0x3300;
    public static final String EXTRA_XMD_DISPLAY = "extra_xmd_display";

    //登录
    public static void gotoLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //注册
    public static void gotoRegister(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    //从主页点击加入会所，只显示返回按钮
    //从注册流程加入所会，需要显示跳过按钮，并跳转到信息完善界面
    //从登录流程加入会所，只显示跳过按钮
    public static void gotoJoinClubFrom(Context context, int openFrom) {
        Logger.i("---gotoJoinClubFrom---" + openFrom);
        Intent intent = new Intent(context, JoinClubActivity.class);
        intent.putExtra(EXTRA_OPEN_JOIN_CLUB_FROM, openFrom);
        context.startActivity(intent);
    }

    public static void gotoJoinClubForResult(Activity activity, int requestCode) {
        Logger.i("---gotoJoinClubForResult---");
        Intent intent = new Intent(activity, JoinClubActivity.class);
        intent.putExtra(EXTRA_OPEN_JOIN_CLUB_FROM, OPEN_JOIN_CLUB_FROM_MAIN);
        activity.startActivityForResult(intent, requestCode);
    }

    //完善注册资料，后续可能需要加入会所
    public static void gotoCompleteRegisterInfo(Context context) {
        Intent intent = new Intent(context, CompleteRegisterInfoActivity.class);
        context.startActivity(intent);
    }

    public static void gotoMainActivityFromStart(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void gotoMainActivityIndexFragmentFromService(Context context, int index) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.EXTRA_FRAGMENT_SWITCH, index);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void gotoChatActivityFromService(Context context, String emChatId) {
        Intent intent = new Intent(context, TechChatActivity.class);
        intent.putExtra(ChatConstant.TO_CHAT_USER_ID, emChatId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void gotoOnlinePayNotifyList(Context context) {
        Intent intent = new Intent(context, OnlinePayNotifyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void gotoChatActivity(Context context, String remoteChatId) {
        EventBus.getDefault().post(new EventStartChatActivity(remoteChatId));
    }


    public static void gotoCustomerDetailActivity(Activity activity, String customerId,String appType,boolean isTech) {
        CustomerInfoDetailActivity.StartCustomerInfoDetailActivity(activity, customerId, appType,isTech);
    }


    public static XmdActionFactory xmdActionFactory = new XmdActionFactory() {
        @Override
        public PendingIntent create(XmdDisplay display) {
            Intent intent = new Intent(TechApplication.getAppContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_XMD_DISPLAY, display);
            return PendingIntent.getActivity(TechApplication.getAppContext(), REQUEST_CODE_UI_ROUTE, intent, PendingIntent.FLAG_ONE_SHOT);
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
}
