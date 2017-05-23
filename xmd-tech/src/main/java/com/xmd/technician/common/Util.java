package com.xmd.technician.common;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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

        return Pattern.matches("^\\d{1,6}$", text);
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
        /*ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baoStream);
        byte[] bytes = baoStream.toByteArray();*/

        byte[] bytes = bmpToByteArray(bitmap, true);
        imgFile = "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
        return imgFile;
    }

    public static String bitmap2base64(Bitmap bitmap, boolean needRecycle) {
        String imgFile = null;
        /*ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baoStream);
        byte[] bytes = baoStream.toByteArray();*/

        byte[] bytes = bmpToByteArray(bitmap, needRecycle);
        imgFile = "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
        return imgFile;
    }

    public static String bytes2base64(byte[] bytes) {
        return "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] bmpToByteArray(Bitmap bmp, boolean needRecycle){
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    public static String uriToRealPath(Activity activity,Uri uri) {
        Cursor cursor = activity.managedQuery(uri,
                new String[] {MediaStore.Images.Media.DATA},
                null,
                null,
                null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        return path;
    }
}
