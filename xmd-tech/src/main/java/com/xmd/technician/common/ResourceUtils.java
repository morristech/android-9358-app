package com.xmd.technician.common;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.xmd.technician.TechApplication;

/**
 * Created by sdcm on 15-10-26.
 */
public class ResourceUtils {
    private static Resources getResources() {
        return TechApplication.getAppContext().getResources();
    }

    public static String getString(int stringId){
        return TechApplication.getAppContext().getResources().getString(stringId);
    }

    public static float getDimenFloat(int dimenId){
        return TechApplication.getAppContext().getResources().getDimension(dimenId);
    }

    public static int getDimenInt(int dimenId){
        return (int) TechApplication.getAppContext().getResources().getDimension(dimenId);
    }

    public static Drawable getDrawable(int resId) {
        return TechApplication.getAppContext().getResources().getDrawable(resId);
    }

    public static int getColor(int resId) {
        return TechApplication.getAppContext().getResources().getColor(resId);
    }
    public static ColorStateList getColorStateList(int resId) {
        return getResources().getColorStateList(resId);
    }

}
