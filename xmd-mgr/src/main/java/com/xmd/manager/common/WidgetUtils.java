package com.xmd.manager.common;

import android.view.View;

/**
 * Created by linms@xiaomodo.com on 16-8-18.
 */
public class WidgetUtils {

    public static void setViewVisibleOrGone(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public static void setViewVisibleOrGone(View view, boolean visible, boolean invisible) {
        if (invisible) {
            view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        } else {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

    }

    public static boolean isVisible(View view) {
        return View.VISIBLE == view.getVisibility();
    }
}
