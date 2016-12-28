package com.xmd.technician.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.window.CompleteRegisterInfoActivity;
import com.xmd.technician.window.JoinClubActivity;
import com.xmd.technician.window.LoginActivity;
import com.xmd.technician.window.MainActivity;
import com.xmd.technician.window.RegisterActivity;

/**
 * Created by heyangya on 16-12-22.
 */

public class UINavigation {
    public static final String EXTRA_JOIN_CLUB = "extra_join_club";
    public static final String EXTRA_OPEN_JOIN_CLUB_FROM = "extra_join_club_from";

    public static final int OPEN_JOIN_CLUB_FROM_REGISTER = 1;
    public static final int OPEN_JOIN_CLUB_FROM_LOGIN = 2;
    public static final int OPEN_JOIN_CLUB_FROM_MAIN = 3;

    //登录
    public static void gotoLogin(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    //注册
    public static void gotoRegister(Context context, boolean joinClub) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra(EXTRA_JOIN_CLUB, joinClub);
        context.startActivity(intent);
    }

    //从主页点击加入会所，只显示返回按钮
    //从注册流程加入所会，需要显示跳过按钮，并跳转到信息完善界面
    //从登录流程加入会所，只显示跳过按钮
    public static void gotoJoinClubFrom(Context context, int openFrom) {
        Intent intent = new Intent(context, JoinClubActivity.class);
        intent.putExtra(EXTRA_OPEN_JOIN_CLUB_FROM, openFrom);
        context.startActivity(intent);
    }

    public static void gotoJoinClubForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, JoinClubActivity.class);
        intent.putExtra(EXTRA_OPEN_JOIN_CLUB_FROM, OPEN_JOIN_CLUB_FROM_MAIN);
        activity.startActivityForResult(intent, requestCode);
    }

    //完善注册资料
    public static void gotoCompleteRegisterInfo(Context context) {
        context.startActivity(new Intent(context, CompleteRegisterInfoActivity.class));
    }

    public static void gotoMainActivityFromRegisterOrLogin(Context context) {
        LoginTechnician technician = LoginTechnician.getInstance();
        ActivityHelper.getInstance().removeAllActivities();
        UserProfileProvider.getInstance().updateCurrentUserInfo(technician.getNickName(), technician.getAvatarUrl());
        technician.loginEmChatAccount();
        context.startActivity(new Intent(context, MainActivity.class));
    }
}