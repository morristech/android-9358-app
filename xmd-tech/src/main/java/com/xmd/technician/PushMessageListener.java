package com.xmd.technician;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.app.XmdActivityManager;
import com.xmd.m.notify.push.XmdPushMessage;
import com.xmd.m.notify.push.XmdPushMessageListener;
import com.xmd.technician.clubinvite.ClubInviteDialogFragment;
import com.xmd.technician.clubinvite.beans.ClubInvite;
import com.xmd.technician.model.LoginTechnician;

/**
 * Created by mo on 17-6-29.
 * 处理消息推送
 */

public class PushMessageListener implements XmdPushMessageListener {
    @Override
    public void onMessage(XmdPushMessage message) {
        switch (message.getBusinessType()) {
            case XmdPushMessage.BUSINESS_TYPE_JOIN_CLUB:
                LoginTechnician.getInstance().loadTechInfo();
                break;
            case XmdPushMessage.BUSINESS_TYPE_POSITION_INVITE:
                AppCompatActivity activity = XmdActivityManager.getInstance().getCurrentActivity();
                if (activity != null) {
                    ClubInvite clubInvite = new Gson().fromJson(message.getData(), ClubInvite.class);
                    ClubInviteDialogFragment fragment = ClubInviteDialogFragment.newInstance(clubInvite);
                    FragmentManager fm = activity.getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment prev = fm.findFragmentByTag(ClubInviteDialogFragment.class.getName());
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    fragment.show(fm, ClubInviteDialogFragment.class.getName());
                    ft.commitAllowingStateLoss();
                }
                break;
        }
    }

    @Override
    public void onRawMessage(String message) {

    }
}
