package com.xmd.technician.common;

import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by heyangya on 16-11-9.
 */

public class ScreenUtils {
    public static int mScreenWidth;
    public static int mScreenHeight;

    public static void setScreenSize(WindowManager windowManager) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }

    public static int getScreenWidth() {
        return mScreenWidth;
    }

    public static int getScreenHeight() {
        return mScreenHeight;
    }
}
