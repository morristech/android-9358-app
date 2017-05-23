package com.xmd.manager.common;

import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by heyangya on 16-11-9.
 */

public class ScreenUtils {
    public static int mScreenWidth;
    public static int mScreenHeight;
    public static float mDensity;

    public static void initScreenSize(WindowManager windowManager) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        mDensity = displayMetrics.density;
    }

    public static int getScreenWidth() {
        return mScreenWidth;
    }

    public static int getScreenHeight() {
        return mScreenHeight;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * mDensity);
    }

    public static int[] getScreenSize(WindowManager windowManager) {
        int[] size = new int[2];
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        size[0] = displayMetrics.widthPixels;
        size[1] = displayMetrics.heightPixels;
        return size;
    }
}
