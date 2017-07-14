package com.xmd.technician.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.shidou.commonlibrary.helper.ThreadPoolManager;
import com.xmd.technician.Constant;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyangya on 17-4-27.
 */

public class ImageUploader {
    private static final int MAX_IMAGE_WIDTH = 1120;
    private static final int MAX_IMAGE_HEIGHT = 1448;
    private static final ImageUploader ourInstance = new ImageUploader();

    public static ImageUploader getInstance() {
        return ourInstance;
    }

    private ImageUploader() {
    }

    public static final int TYPE_AVATAR = 1;
    public static final int TYPE_ALBUM = 2;
    public static final int TYPE_HELLO = 3;
    public static final int TYPE_TECH_POSTER = 4;

    public void upload(int type, Bitmap bitmap) {
        ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                String mImageFile = Util.bytes2base64(baos.toByteArray());
                Map<String, String> params = new HashMap<>();
                params.put(RequestConstant.KEY_IMG_FILE, mImageFile);

                switch (type) {
                    case TYPE_AVATAR:
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_AVATAR, params);
                        break;
                    case TYPE_ALBUM:
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_ALBUM, params);
                        break;
                    case TYPE_HELLO:
                        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_HELLO_TEMPLATE_IMG, params);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void uploadByUrl(int type, String url) {
        ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case TYPE_TECH_POSTER:
                        try {
                            String baseString = encodeFileToBase64(url);
                            Map<String, String> params = new HashMap<>();
                            params.put(RequestConstant.KEY_POSTER_IMAGE_CATEGORY, Constant.TECH_POSTER_CATEGORY_TYPE);
                            params.put(RequestConstant.KEY_POSTER_IMAGE_IMG_FILE, baseString);
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_IMAGE_UPLOAD, params);
                        } catch (Exception e) {
                            e.printStackTrace();
                            RxBus.getInstance().post(new Throwable("图片解析异常，请重新上传"));
                        }

                        break;
                }
            }
        });
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
        Logger.i(">>>", "image size:" + bitmap.getWidth() + "x" + bitmap.getHeight());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bitmap.recycle();
        String imgFile = "data:image/jpg;base64," + Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
        Runtime.getRuntime().gc(); //强制释放内存
        return imgFile;
    }
}
