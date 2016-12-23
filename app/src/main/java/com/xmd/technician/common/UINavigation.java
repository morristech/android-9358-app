package com.xmd.technician.common;

import android.content.Context;
import android.content.Intent;

import com.xmd.technician.window.CompleteRegisterInfoActivity;
import com.xmd.technician.window.JoinClubActivity;
import com.xmd.technician.window.LoginActivity;
import com.xmd.technician.window.RegisterActivity;

/**
 * Created by heyangya on 16-12-22.
 */

public class UINavigation {
    public static final String EXTRA_JOIN_CLUB = "extra_join_club";
    public static final String EXTRA_SHOW_SKIP = "extra_show_skip";

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

    //加入会所
    public static void gotoJoinClub(Context context, boolean showSkipButton) {
        Intent intent = new Intent(context, JoinClubActivity.class);
        intent.putExtra(EXTRA_SHOW_SKIP, showSkipButton);
        context.startActivity(intent);
    }

    //完善注册资料
    public static void gotoCompleteRegisterInfo(Context context) {
        context.startActivity(new Intent(context, CompleteRegisterInfoActivity.class));
    }
}
