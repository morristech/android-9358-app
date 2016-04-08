package com.xmd.technician.common;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ruth on 15-5-28.
 */
public class Util {

    public static boolean matchPhoneNumFormat(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        return Pattern.matches("^1\\d{10}$", text);
    }

    public static boolean matchSecurityCodeFormat(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        return Pattern.matches("^\\d{6}$", text);
    }

    public static boolean matchPassWordFormat(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }

        if (password.length() < 6 || password.length() > 20) {
            return false;
        }

        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        Matcher m = p.matcher(password);
        return password.equals(m.replaceAll("".trim()));
    }

    /**
     * 从图库获取图片
     */
    public static void selectPicFromLocal(Activity activity, int requestCode) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * bitmap转换
     *
     * @param bitmap
     * @return
     */
    public static String bitmap2base64(Bitmap bitmap) {
        String imgFile = null;
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baoStream);
        byte[] bytes = baoStream.toByteArray();
        imgFile = "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
        return imgFile;
    }
}
