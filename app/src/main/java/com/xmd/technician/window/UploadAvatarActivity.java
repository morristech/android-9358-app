package com.xmd.technician.window;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.xmd.technician.R;
import com.xmd.technician.common.ActivityHelper;
import com.xmd.technician.common.Util;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionGrant;
import com.zhy.m.permission.ShowRequestPermissionRationale;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.Subscription;

public class UploadAvatarActivity extends BaseActivity {
    private static final int REQUEST_CODE_LOCAL_PICTURE_AVATAR = 1;
    private static final int REQUEST_CODE_CROP_AVATAR = 2;

    private static final int REQUEST_CODE_PERMISSION_SDCARD = 10;

    @Bind(R.id.confirm)
    Button mConfirm;
    @Bind(R.id.avatar)
    ImageView mAvatar;

    private String mImageFile;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_avatar);

        ButterKnife.bind(this);
        setTitle(R.string.register);
        setBackVisible(true);

        mSubscription = RxBus.getInstance().toObservable(AvatarResult.class).subscribe(result -> {
            ActivityHelper.getInstance().removeAllActivities();
            startActivity(new Intent(UploadAvatarActivity.this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mSubscription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCAL_PICTURE_AVATAR) {
                if (data != null) {
                    // 获取的选中的图片
                    ArrayList<String> result = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (result.size() == 0) {
                        Toast.makeText(UploadAvatarActivity.this, "图片选择器没有返回数据!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Uri fromUri = new Uri.Builder().scheme("file").path(result.get(0)).build();
                    Uri toUri = new Uri.Builder().scheme("file").path(getCacheDir().getPath() + File.separator + "crop-file").build();
                    Crop.of(fromUri, toUri).start(this, REQUEST_CODE_CROP_AVATAR);
                }
            } else if (requestCode == REQUEST_CODE_CROP_AVATAR) {
                Bitmap mPhotoTake = null;
                if (data != null) {
                    Uri uri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                    File file = new File(uri.getPath());
                    if (!file.exists()) {
                        Toast.makeText(UploadAvatarActivity.this, "图片裁剪失败!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    mPhotoTake = BitmapFactory.decodeFile(file.getAbsolutePath());
                    file.delete();
                }
                if (mPhotoTake != null) {
                    mAvatar.setImageBitmap(mPhotoTake);
                    mImageFile = Util.bitmap2base64(mPhotoTake, false);
                    mConfirm.setEnabled(true);
                }
            }
        }
    }

    @OnClick(R.id.avatar)
    public void selectLocalPic() {
        checkPermission();
    }

    @OnClick(R.id.confirm)
    public void updloadAvatar() {
        showProgressDialog("正在上传...");
        // 上传照片
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_IMG_FILE, mImageFile);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_AVATAR, params);

        Utils.reportRegisterEvent(this, "上传头像");
    }


    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!MPermissions.shouldShowRequestPermissionRationale(UploadAvatarActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_SDCARD)) {
                new AlertDialog.Builder(this)
                        .setMessage("需要存储访问权限才能选择照片，如果未能弹出权限申请框，请自行在设置中打开APP存储访问权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MPermissions.requestPermissions(UploadAvatarActivity.this, REQUEST_CODE_PERMISSION_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        })
                        .create()
                        .show();
            } else {
                MPermissions.requestPermissions(UploadAvatarActivity.this, REQUEST_CODE_PERMISSION_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);
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
                .start(this, REQUEST_CODE_LOCAL_PICTURE_AVATAR);
    }
}
