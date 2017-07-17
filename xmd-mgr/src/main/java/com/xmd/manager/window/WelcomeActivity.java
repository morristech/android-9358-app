package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
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
