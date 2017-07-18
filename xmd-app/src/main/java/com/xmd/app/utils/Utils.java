package com.xmd.app.utils;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lhj on 17-7-4.
 */

public class Utils {
    /**
     * 遮罩效果
     *
     * @param activity
     * @param toMask
     */
    public static void maskScreen(Activity activity, boolean toMask) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (toMask) {
            lp.alpha = 0.4f;
        } else {
            lp.alpha = 1.0f;
        }
        activity.getWindow().setAttributes(lp);
    }

    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static String listToString(List<String> list, String delimiter) {
        if (list.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                buffer.append(list.get(i) + delimiter);
            }
            return buffer.toString().substring(0, buffer.length() - 1);
        } else {
            return "";
        }

    }

    /**
     * @param changeStr  要改变颜色的字符串
     * @param colorStr   要改变成的颜色的十六进制表示 如#ffffff
     * @param startIndex 开始下标
     * @param endIndex   结束下标
     * @return
     */
    public static Spannable changeColor(String changeStr, String colorStr,
                                        int startIndex, int endIndex) {
        if (changeStr == null) {
            return new SpannableString("");
        }
        Spannable ss = new SpannableString(changeStr);
        //make sure the endIndex is less than the length of the change string.
        int len = changeStr.length();
        if (endIndex >= len) {
            endIndex = len;
        }
        ss.setSpan(new ForegroundColorSpan(Color.parseColor(colorStr)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * @param text
     * @return
     */
    public static boolean matchPhoneNumFormat(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        Pattern p = Pattern
                .compile("^((13[0-9])|(14[0-9])|(17[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");

        Matcher m = p.matcher(text);

        return m.matches();
    }
}
