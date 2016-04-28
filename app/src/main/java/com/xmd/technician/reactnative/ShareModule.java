package com.xmd.technician.reactnative;

import android.graphics.Bitmap;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.xmd.technician.Constant;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linms@xiaomodo.com on 16-4-22.
 */
public class ShareModule extends ReactContextBaseJavaModule {

    public ShareModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "Share";
    }

    @ReactMethod
    public void share(String title, String description, String imgUrl, String shareUrl) {
        Map<String, Object> params = new HashMap<>();

        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, () -> {
            final Bitmap thumbnail = ImageLoader.readBitmapFromImgUrl(imgUrl);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> {
                params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
                params.put(Constant.PARAM_SHARE_URL, shareUrl);
                params.put(Constant.PARAM_SHARE_TITLE, title);
                params.put(Constant.PARAM_SHARE_DESCRIPTION, description);
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
            });
        });
    }

}
