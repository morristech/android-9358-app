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
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;
import com.xmd.technician.Adapter.AlbumAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.AlbumInfo;
import com.xmd.technician.bean.TechDetailInfo;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.PhotoGridLayoutManager;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.RoundImageView;
import com.xmd.technician.widget.SelectPlaceDialog;
import com.xmd.technician.widget.SpaceItemDecoration;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionGrant;
import com.zhy.m.permission.ShowRequestPermissionRationale;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import rx.Subscription;

public class TechInfoActivity extends BaseActivity {
    public static final int REQUEST_CODE_LOCAL_PICTURE_AVATAR = 1;
    public static final int REQUEST_CODE_LOCAL_PICTURE_ALBUM = 2;
    public static final int REQUEST_CODE_CROP_AVATAR = 3;
    public static final int REQUEST_CODE_CROP_ALBUM = 4;

    public static final int REQUEST_CODE_PERMISSION_SDCARD = 10;

    @Bind(R.id.avatar)
    RoundImageView mAvatar;
    @Bind(R.id.user_name)
    TextView mUserName;
    @Bind(R.id.native_place)
    TextView mNativePlace;
    @Bind(R.id.phone_number)
    TextView mPhoneNumber;
    @Bind(R.id.introduce)
    EditText mDescription;
    @Bind(R.id.button_female)
    RadioButton mFemale;
    @Bind(R.id.button_male)
    RadioButton mMale;
    @Bind(R.id.album_container)
    RecyclerView mAlbumListView;

    private Subscription mGetTechInfoSubscription;
    private Subscription mUpdateTechInfoSubscription;
    private Subscription mUploadAvatarSubscription;
    private Subscription mUploadAlbumSubscription;

    private List<AlbumInfo> mAlbums;
    private TechDetailInfo mTechInfo;
    private String techJoinClub;

    // 籍贯
    private AlbumAdapter mAdapter;
    private boolean mViewInitialized = false;
    private SelectPlaceDialog mSelectPlaceDialog;

    private int mCurrentRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_info);

        ButterKnife.bind(this);

        setTitle(R.string.title_activity_data_edit);
        setBackVisible(true);
        setRightVisible(true, R.string.save);
        techJoinClub = getIntent().getStringExtra(Constant.TECH_STATUS);
        mAdapter = new AlbumAdapter(this, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onAddAlbum() {
                mCurrentRequestCode = REQUEST_CODE_LOCAL_PICTURE_ALBUM;
                checkPermission();
            }

            @Override
            public void onDeleteAlbum(int position) {
                new RewardConfirmDialog(TechInfoActivity.this, "", getString(R.string.edit_activity_delete_album), "") {
                    @Override
                    public void onConfirmClick() {
                        AlbumInfo albumInfo = mAdapter.getItem(position);
                        if (albumInfo != null) {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(RequestConstant.KEY_ID, albumInfo.id);
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DELETE_ALBUM, params);
                        }
                        dismiss();
                    }
                }.show();
            }
        });
        mAlbumListView.setHasFixedSize(true);
        mAlbumListView.setLayoutManager(new PhotoGridLayoutManager(this, 4));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.app_default_padding);
        mAlbumListView.addItemDecoration(new SpaceItemDecoration(spacingInPixels, SpaceItemDecoration.GRID_LAYOUT));
        mAlbumListView.setAdapter(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();
                mAdapter.onItemSwap(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(mAlbumListView);

        mGetTechInfoSubscription = RxBus.getInstance().toObservable(TechEditResult.class).subscribe(
                techEditResult -> getTechInfoResult(techEditResult));

        mUpdateTechInfoSubscription = RxBus.getInstance().toObservable(UpdateTechInfoResult.class).subscribe(
                updateTechInfoResult -> {
                    if (updateTechInfoResult.statusCode == 200) {
                        finish();
                    } else {
                        dismissProgressDialogIfShowing();
                        makeShortToast(updateTechInfoResult.msg);
                    }
                });

        mUploadAlbumSubscription = RxBus.getInstance().toObservable(AlbumResult.class).subscribe(albumResult -> refresh());

        mUploadAvatarSubscription = RxBus.getInstance().toObservable(AvatarResult.class).subscribe(avatarResult -> refresh());

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetTechInfoSubscription, mUpdateTechInfoSubscription, mUploadAlbumSubscription, mUploadAvatarSubscription);
    }

    private void getTechInfoResult(TechEditResult result) {
        if (result.respData != null) {
            mAlbums = result.respData.albums;
            mTechInfo = result.respData.info;
            mAdapter.refreshDataSet(mAlbums);
            initView();
        }
    }

    private void initView() {
        if (!mViewInitialized) {
            mViewInitialized = true;
            mUserName.setText(mTechInfo.name);
            mPhoneNumber.setText(mTechInfo.phoneNum);
            mDescription.setText(mTechInfo.description);

            if (TextUtils.isEmpty(mTechInfo.province) || mTechInfo.province.equals("null")) {
                mNativePlace.setText(getString(R.string.edit_activity_select_native_place));
            } else {
                mNativePlace.setText(mTechInfo.province + " " + mTechInfo.city);
            }

            if (mTechInfo.gender.equals("male")) {
                mMale.setChecked(true);
            } else {
                mFemale.setChecked(true);
            }
        }

        Glide.with(this).load(mTechInfo.avatarUrl).into(mAvatar);
    }

    //检查权限
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!MPermissions.shouldShowRequestPermissionRationale(TechInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_PERMISSION_SDCARD)) {
                new AlertDialog.Builder(this)
                        .setMessage("需要存储访问权限才能选择照片，如果未能弹出权限申请框，请自行在设置中打开APP存储访问权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MPermissions.requestPermissions(TechInfoActivity.this, REQUEST_CODE_PERMISSION_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        })
                        .create()
                        .show();
            } else {
                MPermissions.requestPermissions(TechInfoActivity.this, REQUEST_CODE_PERMISSION_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);
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
                .start(this, mCurrentRequestCode);
    }

    @OnClick(R.id.change_avatar_txt)
    public void changeAvatarClick() {
        mCurrentRequestCode = REQUEST_CODE_LOCAL_PICTURE_AVATAR;
        checkPermission();
    }

    @OnClick(R.id.native_place)
    public void nativePlaceClick() {
        if (mTechInfo == null) {
            return;
        }

        if (mSelectPlaceDialog == null) {
            mSelectPlaceDialog = new SelectPlaceDialog(this, R.style.default_dialog_style, mTechInfo.province, mTechInfo.city) {
                @Override
                public void onSelectConfirmMethod() {
                    dismiss();
                    mNativePlace.setText(mCurrentProvinceName + " " + mCurrentCityName);
                }
            };
            mSelectPlaceDialog.setCanceledOnTouchOutside(false);
        }

        mSelectPlaceDialog.show();
    }

    @OnClick(R.id.toolbar_right)
    public void updateTechInfo() {
        if (mTechInfo == null) {
            return;
        }

        String cacheName = mUserName.getText().toString();
        if (TextUtils.isEmpty(cacheName)) {
            makeShortToast("姓名不能为空...");
            return;
        }

        mTechInfo.name = cacheName;
        mTechInfo.description = replaceBlank(mDescription.getText().toString());
        mTechInfo.gender = mFemale.isChecked() ? "female" : "male";
        //mTechInfo.phoneNum = mPhoneNumber.getText().toString();
        if (mSelectPlaceDialog != null) {
            mTechInfo.provinceCode = mSelectPlaceDialog.mCurrentProvinceCode;
            mTechInfo.province = mSelectPlaceDialog.mCurrentProvinceName;
            mTechInfo.cityCode = mSelectPlaceDialog.mCurrentCityCode;
            mTechInfo.city = mSelectPlaceDialog.mCurrentCityName;
        }

        showProgressDialog("正在更新技师信息...");
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER, mTechInfo.toString());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_TECH_INFO, params);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOCAL_PICTURE_ALBUM:
                case REQUEST_CODE_LOCAL_PICTURE_AVATAR:
                    if (data != null) {
                        // 获取的选中的图片
                        try {
                            ArrayList<String> result = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                            if (result.size() == 0) {
                                Toast.makeText(TechInfoActivity.this, "图片选择器没有返回数据!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Uri fromUri = new Uri.Builder().scheme("file").path(result.get(0)).build();
                            Uri toUri = new Uri.Builder().scheme("file").path(getCacheDir().getPath() + File.separator + "crop-file").build();
                            if (requestCode == REQUEST_CODE_LOCAL_PICTURE_ALBUM) {
                                Crop.of(fromUri, toUri).start(this, REQUEST_CODE_CROP_ALBUM);
                            } else {
                                Crop.of(fromUri, toUri).start(this, REQUEST_CODE_CROP_AVATAR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_CROP_ALBUM:
                case REQUEST_CODE_CROP_AVATAR:
                    Bitmap mPhotoTake = null;
                    if (data != null) {
                        Uri uri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                        File file = new File(uri.getPath());
                        if (!file.exists()) {
                            Toast.makeText(TechInfoActivity.this, "图片裁剪失败!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        mPhotoTake = BitmapFactory.decodeFile(file.getAbsolutePath());
                        file.delete();
                    }
                    if (mPhotoTake != null) {
                        showProgressDialog("正在上传...");
                        String imgFile = Util.bitmap2base64(mPhotoTake);
                        // 上传照片
                        Map<String, String> params = new HashMap<>();
                        params.put(RequestConstant.KEY_IMG_FILE, imgFile);
                        if (requestCode == REQUEST_CODE_CROP_ALBUM) {
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_ALBUM, params);
                        } else {
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_AVATAR, params);
                        }
                    }
                    break;
            }
        }
    }

    private void refresh() {
        dismissProgressDialogIfShowing();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
