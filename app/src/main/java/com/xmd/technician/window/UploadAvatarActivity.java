package com.xmd.technician.window;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.xmd.technician.R;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import internal.org.apache.http.entity.mime.MinimalField;
import rx.Subscription;

public class UploadAvatarActivity extends BaseActivity {
    private static final int REQUEST_CODE_LOCAL_PICTURE_AVATAR = 1;
    private static final int REQUEST_CODE_CROP_AVATAR = 2;

    @Bind(R.id.confirm) Button mConfirm;
    @Bind(R.id.avatar) ImageView mAvatar;

    private String mImageFile;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_avatar);

        ButterKnife.bind(this);
        setTitle(R.string.register);
        setBackVisible(true);

        mSubscription = RxBus.getInstance().toObservable(AvatarResult.class).subscribe(
                result -> {startActivity(new Intent(UploadAvatarActivity.this, MainActivity.class)); finish();});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mSubscription);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE_LOCAL_PICTURE_AVATAR){
                if (data != null) {
                    // 获取的选中的图片
                    try {
                        Uri uri = data.getData();
                        Intent intent = new Intent(this, ClipPictureActivity.class);
                        intent.putExtra(ClipPictureActivity.EXTRA_INPUT, uri.toString());
                        startActivityForResult(intent, REQUEST_CODE_CROP_AVATAR);
                    } catch (Exception e) {

                    }
                }
            }else if(requestCode == REQUEST_CODE_CROP_AVATAR){
                Bitmap mPhotoTake = null;
                if (data != null) {
                    try {
                        Uri uri = Uri.parse(data.getStringExtra(ClipPictureActivity.EXTRA_OUTPUT));
                        mPhotoTake = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        ContentResolver resolver = getContentResolver();
                        resolver.delete(uri, null, null);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                if (mPhotoTake != null) {
                    mAvatar.setImageBitmap(mPhotoTake);
                    mImageFile = Util.bitmap2base64(mPhotoTake);
                    mConfirm.setEnabled(true);
                }
            }
        }
    }

    @OnClick(R.id.avatar)
    public void selectLocalPic(){
        Util.selectPicFromLocal(this, REQUEST_CODE_LOCAL_PICTURE_AVATAR);
    }

    @OnClick(R.id.confirm)
    public void updloadAvatar(){
        showProgressDialog("正在上传...");
        // 上传照片
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_IMG_FILE, mImageFile);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_AVATAR,params);
    }
}
