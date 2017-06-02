package com.shidou.commonlibrary.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Created by heyangya on 17-6-2.
 * 号码相关工具类
 */

public class CodeUtils {
    public static boolean matchPhoneNumFormat(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return Pattern.matches("^1\\d{10}$", text);
    }
}
