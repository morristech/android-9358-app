package com.xmd.technician.window;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionGrant;
import com.zhy.m.permission.ShowRequestPermissionRationale;

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
    private static final int REQUEST_CODE_PERMISSION_SDCARD = 10;
    private String mCropFilePath;
    private boolean mOnlyPick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();

        mOnlyPick = getIntent().getIntExtra(EXTRA_COMMAND, EXTRA_COMMAND_CROP) == EXTRA_COMMAND_PICK;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK) {
                if (data != null) {
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
                        Crop.of(fromUri, toUri).start(this, REQUEST_CODE_CROP);
                    }
                }
            } else if (requestCode == REQUEST_CODE_CROP) {
                if (data != null) {
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
        } else {
            finish();
        }
    }


    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!MPermissions.shouldShowRequestPermissionRationale(ImageSelectAndCropActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_SDCARD)) {
                new AlertDialog.Builder(this)
                        .setMessage("需要存储访问权限才能选择照片，如果未能弹出权限申请框，请自行在设置中打开APP存储访问权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MPermissions.requestPermissions(ImageSelectAndCropActivity.this, REQUEST_CODE_PERMISSION_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        })
                        .create()
                        .show();
            } else {
                MPermissions.requestPermissions(ImageSelectAndCropActivity.this, REQUEST_CODE_PERMISSION_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            gotoSelectImage();
        }
    }

    @ShowRequestPermissionRationale(REQUEST_CODE_PERMISSION_SDCARD)
    public void showNeedPermisstion() {

    }

    @PermissionGrant(REQUEST_CODE_PERMISSION_SDCARD)
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
