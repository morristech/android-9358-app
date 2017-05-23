package com.xmd.manager.common;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.xmd.manager.ManagerApplication;

/**
 * Created by sdcm on 15-10-26.
 */
public class ResourceUtils {

    private static Resources getResources() {
        return ManagerApplication.getAppContext().getResources();
    }

    public static String getString(int stringId) {
        return getResources().getString(stringId);
    }

    public static float getDimenFloat(int dimenId) {
        return getResources().getDimension(dimenId);
    }

    public static int getDimenInt(int dimenId) {
        return (int) getResources().getDimension(dimenId);
    }

    public static Drawable getDrawable(int resId) {
        return getResources().getDrawable(resId);
    }

    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }

    public static int getTextSize(int dimenId) {
        int diment = getDimenInt(dimenId);
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, getDimenInt(dimenId), getResources().getDisplayMetrics());
        return px;
    }


}
