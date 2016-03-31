package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.xmd.technician.Adapter.MsgListAdapter;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.gson.ModifyPasswordResult;

public class WelcomeActivity extends BaseActivity {

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(WelcomeActivity.this , LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if(TextUtils.isEmpty(SharedPreferenceHelper.getUserToken())){
            ThreadManager.postDelayed(ThreadManager.THREAD_TYPE_MAIN, mTask, 500);
        } else {
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            // Switch to MainActivity
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            // it's single chat
            intent.putExtra(ChatConstant.EXTRA_USER_ID, "琵琶");
            startActivity(intent);
            finish();
        }

    }
}
