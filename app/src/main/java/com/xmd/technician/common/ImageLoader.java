package com.xmd.technician.common;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.xmd.technician.http.RequestConstant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    //从文件中读取图片，如果超过限制，则按长宽比进行压缩
    public static Bitmap getBitmapFromFile(String imagePath, int maxSize) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        boolean needScale = false;
        if (options.outWidth > maxSize || options.outHeight > maxSize) {
            needScale = true;
            int sampleSize = 1;
            do {
                sampleSize <<= 1;
            }
            while (options.outHeight / sampleSize > maxSize && options.outWidth / sampleSize > maxSize);
            sampleSize >>= 1;
            options.inSampleSize = sampleSize;
        }
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = false;
        options.inScaled = false;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        if (bitmap == null) {
            return null;
        }
        if (needScale) {
            float scale1 = ((float) maxSize) / bitmap.getWidth();
            float scale2 = ((float) maxSize) / bitmap.getHeight();
            float scale = scale1 > scale2 ? scale1 : scale2;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
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


    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView, int placeHolder) {
        Glide.with(context).load(url).placeholder(placeHolder).into(imageView);
    }

    public static void loadCircleImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).transform(circleTransformation(context)).into(imageView);
    }

    public static void loadCircleImage(Context context, String url, ImageView imageView, int placeHolder) {
        Glide.with(context).load(url).transform(new CircleBitmapTransformation(context)).
                placeholder(placeHolder).into(imageView);
    }

    public static BitmapTransformation circleTransformation(Context context) {
        return new CircleBitmapTransformation(context);
    }

    private static class CircleBitmapTransformation extends BitmapTransformation {
        public CircleBitmapTransformation(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int size = Math.min(toTransform.getWidth(), toTransform.getHeight());

            Bitmap bitmap = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(toTransform,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            int w = (toTransform.getWidth() - size) / 2;
            int h = (toTransform.getHeight() - size) / 2;
            if (w != 0 || h != 0) {
                Matrix matrix = new Matrix();
                matrix.setTranslate(-w, -h);
                shader.setLocalMatrix(matrix);
            }
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return bitmap;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

    public static void saveBitmapToLocal(Context context,Bitmap mBitmap,String bitmapName){
        File file;
        file = new File(Environment.getExternalStorageDirectory()+"/"+bitmapName+".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveImageToGallery(context,file);
    }

    public static void saveImageToGallery(Context context,File file){
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),"code",null);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+file)));
            Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(context,"保存失败",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
