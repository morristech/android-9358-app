package com.xmd.app.utils;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.xmd.app.XmdApp;

/**
 * Created by Lhj on 17-6-30.
 */

public class ResourceUtils {
    private static Resources getResources() {
        return XmdApp.getInstance().getContext().getResources();
    }

    public static String getString(int stringId) {
        return XmdApp.getInstance().getContext().getResources().getString(stringId);
    }

    public static float getDimenFloat(int dimenId) {
        return XmdApp.getInstance().getContext().getResources().getDimension(dimenId);
    }

    public static int getDimenInt(int dimenId) {
        return (int) XmdApp.getInstance().getContext().getResources().getDimension(dimenId);
    }

    public static Drawable getDrawable(int resId) {
        return XmdApp.getInstance().getContext().getResources().getDrawable(resId);
    }

    public static int getColor(int resId) {
        return XmdApp.getInstance().getContext().getResources().getColor(resId);
    }

    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }

}
