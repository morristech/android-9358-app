package com.xmd.app.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;

import com.shidou.commonlibrary.helper.XLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
     * shorten the string with ... to express if it's too long
     *
     * @param text
     * @param length
     * @return
     */
    public static String briefString(String text, int length) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }

        if (text.length() <= length) {
            return text;
        }

        int startIndex = length / 2;
        String prefix = text.substring(0, startIndex);
        String subfix = text.substring(text.length() - startIndex, text.length());

        return prefix + "..." + subfix;

    }

    public static List<String> StringToList(String textString, String delimiter) {
        List<String> strings = new ArrayList<>();
        strings.clear();
        if (TextUtils.isEmpty(textString)) {
            return strings;
        }
        String[] arrayString = textString.split(delimiter);
        return Arrays.asList(arrayString);


    }

    public static String list2String(List<String> list, String delimiter) {
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

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String StrSubstring(int length, String s, Boolean end) {
        if (TextUtils.isEmpty(s) || length <= 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        int sum = 0;
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (sum >= (length * 3)) {
                if (end) {
                    stringBuffer.append("...");
                }
                break;
            }
            char bt = chars[i];
            if (bt > 64 && bt < 123) {
                stringBuffer.append(String.valueOf(bt));
                sum += 2;
            } else {
                stringBuffer.append(String.valueOf(bt));
                sum += 3;
            }
        }
        return stringBuffer.toString();
    }

    public static boolean isAppRunningForeground(Context var0) {
        ActivityManager var1 = (ActivityManager) var0.getSystemService("activity");

        try {
            List var2 = var1.getRunningTasks(1);
            if (var2 != null && var2.size() >= 1) {
                boolean var3 = var0.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo) var2.get(0)).baseActivity.getPackageName());
                XLogger.d("utils", "app running in foregroud：" + var3);
                return var3;
            } else {
                return false;
            }
        } catch (SecurityException var4) {
            XLogger.d("EasyUtils", "Apk doesn\'t hold GET_TASKS permission");
            var4.printStackTrace();
            return false;
        }
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static String moneyToStringEx(int value) {
        int d1 = value / 100;
        int d2 = value % 100;
        return String.format(Locale.CHINA, "%d.%02d", d1, d2);
    }
}
