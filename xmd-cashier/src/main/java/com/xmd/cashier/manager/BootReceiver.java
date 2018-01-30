package com.xmd.cashier.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.cashier.activity.LoginActivity;
import com.xmd.cashier.common.AppConstants;

/**
 * Created by zr on 18-1-18.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        XLogger.i(TAG, AppConstants.LOG_BIZ_LOCAL_CONFIG + BOOT_ACTION);
        if (BOOT_ACTION.equals(intent.getAction())) {
            Intent bootIntent = new Intent(context, LoginActivity.class);
            bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(bootIntent);
        }
    }
}
