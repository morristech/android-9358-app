package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.hyphenate.chat.EMClient;
import com.xmd.technician.R;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.ScreenUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.model.LoginTechnician;

public class WelcomeActivity extends BaseActivity {
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            UINavigation.gotoLogin(WelcomeActivity.this);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ScreenUtils.setScreenSize(getWindowManager());

        if (TextUtils.isEmpty(LoginTechnician.getInstance().getToken())) {
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, mTask, 500);
        } else {
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            UserProfileProvider.getInstance().initContactList();
            // Switch to MainActivity
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
