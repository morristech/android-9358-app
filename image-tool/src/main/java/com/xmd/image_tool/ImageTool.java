package com.xmd.image_tool;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.crazyman.library.PermissionTool;
import com.soundcloud.android.crop.Crop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by heyangya on 17-4-26.
 */

public class ImageTool {
    private ResultListener listener;
//    String ASPECT_X = "aspect_x";
//    String ASPECT_Y = "aspect_y";
    private int maxWidth = 0;
    private int maxHeight = 0;
    private int maxSize = 0;
    private int mAspect_X = 5;
    private int mAspect_Y = 7;
    private boolean onlyPick = false;
    private static final int REQUEST_CODE_PERMISSION = 0x8531;
    private static final int REQUEST_CODE_PICK = 0x8532;
    private static final int REQUEST_CODE_CROP = 0x8533;
    private Activity activity;
    private Fragment fragment;

    public ImageTool onlyPick(boolean onlyPick) {
        this.onlyPick = onlyPick;
        return this;
    }

    public ImageTool reset() {
        maxWidth = 0;
        maxHeight = 0;
        maxSize = 0;
        onlyPick = false;
        return this;
    }

    public ImageTool maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        maxSize = maxSize < maxWidth ? maxWidth : maxSize;
        return this;
    }

    public ImageTool maxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        maxSize = maxSize < maxHeight ? maxHeight : maxSize;
        return this;
    }

    public ImageTool maxSize(int maxSize) {
        this.maxSize = maxSize;
        this.maxWidth = maxSize;
        this.maxHeight = maxSize;
        return this;
    }
    public ImageTool setAspectX_Y(int aspectX,int aspect_y){
        this.mAspect_X = aspectX;
        this.mAspect_Y = aspect_y;
        return this;
    }

    public void start(@NonNull Activity activity, @NonNull ResultListener listener) {
        this.listener = listener;
        this.activity = activity;
        this.fragment = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PermissionTool.requestPermission(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new String[]{"存取SD卡"}, REQUEST_CODE_PERMISSION);
        } else {
            doPickImage();
        }
    }

    public void start(@NonNull Fragment fragment, @NonNull ResultListener listener) {
        this.listener = listener;
        this.fragment = fragment;
        this.activity = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PermissionTool.requestPermission(fragment, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new String[]{"存取SD卡"}, REQUEST_CODE_PERMISSION);
        } else {
            doPickImage();
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                //权限申请成功
                doPickImage();
            } else {
                //权限申请失败
                finish("权限申请失败！", null, null);
            }
            return true;
        }

        if (requestCode == REQUEST_CODE_PICK) {
            if (resultCode == Activity.RESULT_OK) {
                processPickResult(data);
            } else {
                finish(null, null, null);
            }
            return true;
        }
        return false;
    }

    //选择图片
    private void doPickImage() {
        if (activity != null) {
            MultiImageSelector.create()
                    .single()
                    .start(activity, REQUEST_CODE_PICK);
        } else if (fragment != null) {
            MultiImageSelector.create()
                    .single()
                    .start(fragment, REQUEST_CODE_PICK);
        }
    }

    private void processPickResult(Intent data) {
        if (data == null) {
            finish("选择图片失败！", null, null);
            return;
        }
        ArrayList<String> result = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
        if (result.size() == 0) {
            finish("选择图片失败！", null, null);
            return;
        }
        Uri uri = new Uri.Builder().scheme("file").path(result.get(0)).build();
        if (onlyPick) {
            doCompress(uri);
        } else {
            doCropImage(uri);
        }
    }

    //裁剪图片
    private void doCropImage(Uri uri) {
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        Crop crop = Crop.of(uri, null).onlyBitmap(true).withAspect(mAspect_X,mAspect_Y);
        if (maxSize > 0) {
            crop.withMaxSize(maxWidth, maxHeight).onlyBitmap(true);
        }
        if (activity != null) {
            crop.start(activity, Crop.REQUEST_CROP);
        } else {
            crop.start(fragment.getContext(), fragment, Crop.REQUEST_CROP);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCropResult(Crop.ResultBitmap resultBitmap) {
        if (resultBitmap != null) {
            finish(null, null, resultBitmap.getBitmap());
        } else {
            finish("裁剪失败！", null, null);
        }
    }

    //压缩图片
    private void doCompress(Uri uri) {
        if (maxSize > 0) {

        }
        finish(null, uri, null);
    }

    private void finish(String error, Uri uri, Bitmap bitmap) {
        activity = null;
        if (!onlyPick) {
            EventBus.getDefault().unregister(this);
        }
        listener.onResult(error, uri, bitmap);
    }

    public interface ResultListener {
        void onResult(String error, Uri uri, Bitmap bitmap);
    }
}
