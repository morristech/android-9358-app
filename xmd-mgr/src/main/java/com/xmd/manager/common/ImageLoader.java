package com.xmd.manager.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.xmd.manager.service.RequestConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sdcm on 15-12-15.
 */
public class ImageLoader {
    private static final int MAX_IMAGE_WIDTH = 1120;
    private static final int MAX_IMAGE_HEIGHT = 1448;

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
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
//        int options = 100;
//        while (output.toByteArray().length > maxkb&& options != 10) {
//            output.reset(); //清空output
//            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
//            options -= 10;
//        }
      //  return BitmapFactory.decodeByteArray(bitmap2Bytes(BitmapFactory.decodeFile(imagePath, op),32),0,0);
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
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inPreferredConfig = Bitmap.Config.ARGB_8888;
        op.inDither = false;
        op.inScaled = false;
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(RequestConstant.REQUEST_TIMEOUT);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                FileUtils.writeStream(is, target);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String encodeFileToBase64(String path) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = 1;
        while (options.outWidth / options.inSampleSize > MAX_IMAGE_WIDTH || options.outHeight / options.inSampleSize > MAX_IMAGE_HEIGHT) {
            options.inSampleSize <<= 1;
        }
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bitmap.recycle();
        String imgFile = "data:image/jpg;base64," + Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
        Runtime.getRuntime().gc(); //强制释放内存
        return imgFile;
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb&& options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }

}
