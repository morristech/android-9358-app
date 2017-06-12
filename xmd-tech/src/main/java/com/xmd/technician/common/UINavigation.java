package com.xmd.technician.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.technician.Constant;
import com.xmd.technician.TechApplication;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.utils.UserUtils;
import com.xmd.technician.notify.NotificationCenter;
import com.xmd.technician.window.CompleteRegisterInfoActivity;
import com.xmd.technician.window.JoinClubActivity;
import com.xmd.technician.window.LoginActivity;
import com.xmd.technician.window.MainActivity;
import com.xmd.technician.window.RegisterActivity;
import com.xmd.technician.window.TechChatActivity;

import java.util.Map;

/**
 * Created by heyangya on 16-12-22.
 */

public class UINavigation {
    public static final String EXTRA_JOIN_CLUB = "extra_join_club";
    public static final String EXTRA_OPEN_JOIN_CLUB_FROM = "extra_join_club_from";
    public static final String EXTRA_NOTIFY_ID = "extra_notify_id";

    public static final int OPEN_JOIN_CLUB_FROM_START = 1;
    public static final int OPEN_JOIN_CLUB_FROM_MAIN = 3;

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

    public static void gotoChatActivity(Context context, Map<String, Object> params) {
        String emchatId = (String) params.get(ChatConstant.EMCHAT_ID);
        String emchatNickname = (String) params.get(ChatConstant.EMCHAT_NICKNAME);
        String emchatAvatar = (String) params.get(ChatConstant.EMCHAT_AVATAR);
        String emchatIsTech = (String) params.get(ChatConstant.EMCHAT_IS_TECH);

        if (TextUtils.isEmpty(emchatId)) {
            return;
        }

        ChatUser chatUser = new ChatUser(emchatId);
        chatUser.setNickname(emchatNickname);
        chatUser.setAvatar(emchatAvatar);
        chatUser.setUserType(emchatIsTech);
        UserUtils.saveUser(chatUser);

        Intent intent = new Intent(context, TechChatActivity.class);
        intent.putExtra(ChatConstant.TO_CHAT_USER_ID, emchatId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TechApplication.getAppContext().startActivity(intent);
    }


    public static boolean routeNotify(Context context, int notifyId, Bundle extraData) {
        XLogger.d("routeNotify:" + notifyId);
        switch (notifyId) {
            case NotificationCenter.TYPE_ORDER:
            case NotificationCenter.TYPE_CHAT_MESSAGE:
                Intent intent = new Intent(context, TechChatActivity.class);
                intent.putExtras(extraData);
                context.startActivity(intent);
                return true;
            case NotificationCenter.TYPE_PAY_NOTIFY:
                gotoMainActivityIndexFragmentFromService(context, 0);
                return true;
            default:
                break;
        }
        return false;
    }
}
