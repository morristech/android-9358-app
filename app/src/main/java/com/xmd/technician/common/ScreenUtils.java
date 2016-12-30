package com.xmd.technician.common;

import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by heyangya on 16-11-9.
 */

public class ScreenUtils {
    public static void getScreenSize(WindowManager windowManager, int[] size) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        size[0] = displayMetrics.widthPixels;
        size[1] = displayMetrics.heightPixels;
    }
}
