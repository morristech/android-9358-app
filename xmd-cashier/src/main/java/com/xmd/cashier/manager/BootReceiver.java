package com.xmd.cashier.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xmd.cashier.activity.LoginActivity;

/**
 * Created by zr on 18-1-18.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_ACTION.equals(intent.getAction())) {
            Intent bootIntent = new Intent(context, LoginActivity.class);
            bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(bootIntent);
        }
    }
}
