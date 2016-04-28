package com.xmd.technician.common;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xmd.technician.http.RequestConstant;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sdcm on 15-12-15.
 */
public class ImageLoader {

    /**
     * @param imagePath
     * @return
     */
    public static Bitmap readBitmapFromFile(String imagePath) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inPreferredConfig = Bitmap.Config.ARGB_8888;
        op.inDither = false;
        op.inScaled = false;
        return BitmapFactory.decodeFile(imagePath, op);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap readBigBitmapFromFile(String imagePath, int reqWidth) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = false;
        options.inScaled = false;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * @param imgUrl
     * @param target
     */
    public static void saveImageFile(String imgUrl, String target) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(RequestConstant.REQUEST_TIMEOUT);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                FileUtils.writeStream(is, target);
            }
        } catch (MalformedURLException e) {
            Logger.e("saveImageFile" + e.getLocalizedMessage());
        } catch (IOException e) {
            Logger.e("saveImageFile" + e.getLocalizedMessage());
        }
    }

    public static Bitmap readBitmapFromImgUrl(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(RequestConstant.REQUEST_TIMEOUT);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                return BitmapFactory.decodeStream(is);
            }
        } catch (MalformedURLException e) {
            Logger.e("readBitmapFromImgUrl" + e.getLocalizedMessage());
        } catch (IOException e) {
            Logger.e("readBitmapFromImgUrl" + e.getLocalizedMessage());
        }
        return null;
    }
}
