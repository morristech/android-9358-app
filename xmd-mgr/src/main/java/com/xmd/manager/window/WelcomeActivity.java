package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hyphenate.chat.EMClient;
import com.shidou.commonlibrary.network.OkHttpUtil;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.Utils;

/**
 * Created by sdcm on 15-10-26.
 */
public class WelcomeActivity extends AppCompatActivity {

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        // User Token is null, meaning no user logged in
        if (Utils.isEmpty(SharedPreferenceHelper.getUserToken())) {
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, mTask, 500);

        } else {

//            EMClient.getInstance().groupManager().loadAllGroups();
//            EmchatManager.getInstance().loadConversationList();
            // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
            OkHttpUtil.getInstance().setCommonHeader("token", SharedPreferenceHelper.getUserToken());
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, new Runnable() {
                @Override
                public void run() {
                    if (!EMClient.getInstance().updateCurrentUserNick(SharedPreferenceHelper.getUserName())) {
                        Logger.e("LoginActivity", "update current user nick fail");
                    }
                }
            });

            // Switch to MainActivity
            if (Constant.MULTI_CLUB_ROLE.equals(SharedPreferenceHelper.getUserRole())) {
                startActivity(new Intent(WelcomeActivity.this, ClubListActivity.class));
                finish();
            } else {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}
