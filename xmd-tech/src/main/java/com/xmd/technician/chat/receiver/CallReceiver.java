package com.xmd.technician.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.util.EMLog;
import com.xmd.technician.chat.ChatHelper;

/**
 * Created by Lhj on 17-4-6.
 */

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ChatHelper.getInstance().isLoggedIn())
            return;
        //username
        String from = intent.getStringExtra("from");
        //call type
        String type = intent.getStringExtra("type");

        EMLog.d("CallReceiver", "app received a incoming call");
    }
}
