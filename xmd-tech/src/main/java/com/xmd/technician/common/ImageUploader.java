package com.xmd.technician.common;

import android.graphics.Bitmap;

import com.xmd.technician.Constant;
import com.xmd.technician.bean.PaidCouponUserDetail;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heyangya on 17-4-27.
 */

public class ImageUploader {
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

    public void uploadByUrl(int type,String url){
        ThreadPoolManager.run(new Runnable() {
            @Override
            public void run() {
                switch (type){
                    case TYPE_TECH_POSTER:
                        try {
                            String baseString = DESede.base64Encode(url);
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
}
