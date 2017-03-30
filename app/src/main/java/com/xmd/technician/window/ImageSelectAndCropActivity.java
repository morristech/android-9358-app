package com.xmd.technician.window;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.xmd.library.PermissionTool;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

//图片选择和裁剪窗口

public class ImageSelectAndCropActivity extends BaseActivity {
    public static final String EXTRA_COMMAND = "extra_command";
    public static final int EXTRA_COMMAND_PICK = 1;
    public static final int EXTRA_COMMAND_CROP = 2;

    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_CROP = 2;
    private static final int REQUEST_CODE_PERMISSION = 10;
    private String mCropFilePath;
    private boolean mOnlyPick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnlyPick = getIntent().getIntExtra(EXTRA_COMMAND, EXTRA_COMMAND_CROP) == EXTRA_COMMAND_PICK;
        PermissionTool.requestPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new String[]{"存取SD卡"}, REQUEST_CODE_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                gotoSelectImage();
            } else {
                finish();
                return;
            }
        }

        if (requestCode == REQUEST_CODE_PICK) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // 获取的选中的图片
                ArrayList<String> result = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (result.size() == 0) {
                    Toast.makeText(ImageSelectAndCropActivity.this, "图片选择器没有返回数据!", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                if (mOnlyPick) {
                    //只选择图片
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(result.get(0)));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    //需要裁剪
                    Uri fromUri = new Uri.Builder().scheme("file").path(result.get(0)).build();
                    mCropFilePath = getCacheDir().getPath() + File.separator + "crop-file";
                    Uri toUri = new Uri.Builder().scheme("file").path(mCropFilePath).build();
                    Crop.of(fromUri, toUri).asPng(true).start(this, REQUEST_CODE_CROP);
                }
            } else {
                finish();
            }
        } else if (requestCode == REQUEST_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                File file = new File(uri.getPath());
                if (!file.exists()) {
                    Toast.makeText(ImageSelectAndCropActivity.this, "图片裁剪失败!", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setData(uri);
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    }

    public void gotoSelectImage() {
        MultiImageSelector.create()
                .single()
                .start(this, REQUEST_CODE_PICK);
    }

    public static void onlyPick(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImageSelectAndCropActivity.class);
        intent.putExtra(EXTRA_COMMAND, EXTRA_COMMAND_PICK);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void pickAndCrop(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImageSelectAndCropActivity.class);
        intent.putExtra(EXTRA_COMMAND, EXTRA_COMMAND_CROP);
        activity.startActivityForResult(intent, requestCode);
    }
}
