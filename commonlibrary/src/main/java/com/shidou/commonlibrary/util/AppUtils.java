package com.shidou.commonlibrary.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by heyangya on 16-4-20.
 */
public class AppUtils {
    public static boolean startApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        return true;
    }

    public static void installApp(Context context, Uri uri) {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(installIntent);
    }
}
