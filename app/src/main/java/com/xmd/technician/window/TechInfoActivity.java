package com.xmd.technician.window;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Adapter.AlbumAdapter;
import com.xmd.technician.Adapter.PhotoGridAdapter;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Util;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.bean.AlbumInfo;
import com.xmd.technician.bean.TechDetailInfo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ConfirmDialog;
import com.xmd.technician.widget.CustomAlertDialog;
import com.xmd.technician.widget.PhotoGridLayoutManager;
import com.xmd.technician.widget.PhotoGridView;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.RoundImageView;
import com.xmd.technician.widget.SelectPlaceDialog;
import com.xmd.technician.widget.SpaceItemDecoration;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class TechInfoActivity extends BaseActivity {
    public static final int REQUEST_CODE_LOCAL_PICTURE_AVATAR = 1;
    public static final int REQUEST_CODE_LOCAL_PICTURE_ALBUM = 2;
    public static final int REQUEST_CODE_CROP_AVATAR = 3;
    public static final int REQUEST_CODE_CROP_ALBUM = 4;

    @Bind(R.id.grid_view_container) PhotoGridView mAlbumContainer;
    @Bind(R.id.avatar) RoundImageView mAvatar;
    @Bind(R.id.user_name) TextView mUserName;
    @Bind(R.id.tech_number) TextView mSerialNo;
    @Bind(R.id.native_place) TextView mNativePlace;
    @Bind(R.id.phone_number) TextView mPhoneNumber;
    @Bind(R.id.introduce) TextView mDescription;
    @Bind(R.id.button_female) RadioButton mFemale;
    @Bind(R.id.button_male) RadioButton mMale;
    @Bind(R.id.album_container) RecyclerView mAlbumListView;

    private Subscription mGetTechInfoSubscription;
    private Subscription mUpdateTechInfoSubscription;
    private Subscription mUploadAvatarSubscription;
    private Subscription mUploadAlbumSubscription;
    private Subscription mDeleteAlbumSubscription;

    private List<AlbumInfo> mAlbums;
    private TechDetailInfo mTechInfo;
    private String mPhoneNum;

    // 籍贯
    private AlbumAdapter mAdapter;
    private PhotoGridAdapter mPhotoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_info);

        ButterKnife.bind(this);

        setTitle(R.string.title_activity_data_edit);
        setBackVisible(true);
        setRightVisible(true, R.string.save);

        mAdapter = new AlbumAdapter(this, new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onAddAlbum() {
                Util.selectPicFromLocal(TechInfoActivity.this, TechInfoActivity.REQUEST_CODE_LOCAL_PICTURE_ALBUM);
            }

            @Override
            public void onDeleteAlbum(int position) {
                new RewardConfirmDialog(TechInfoActivity.this, "",getString(R.string.edit_activity_delete_album)) {
                    @Override
                    public void onConfirmClick() {
                        AlbumInfo albumInfo = mAdapter.getItem(position);
                        if(albumInfo != null){
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
        mAlbumListView.addItemDecoration(new SpaceItemDecoration(spacingInPixels,SpaceItemDecoration.GRID_LAYOUT));
        mAlbumListView.setAdapter(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,0) {
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

        mPhotoAdapter = new PhotoGridAdapter(this);
        mAlbumContainer.setAdapter(mPhotoAdapter);
        mAlbumContainer.setOnItemClickListener((parent, view, position, id) -> {
            if(mPhotoAdapter.getItemViewType(position) == PhotoGridAdapter.TYPE_ADD){
                Util.selectPicFromLocal(TechInfoActivity.this, TechInfoActivity.REQUEST_CODE_LOCAL_PICTURE_ALBUM);
            }else {
                new ConfirmDialog(this, getString(R.string.edit_activity_delete_album)) {
                    @Override
                    public void onConfirmClick() {
                        AlbumInfo albumInfo = mPhotoAdapter.getItem(position);
                        if(albumInfo != null){
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(RequestConstant.KEY_ID, albumInfo.id);
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DELETE_ALBUM, params);
                        }
                    }
                }.show();
            }
        });

        mGetTechInfoSubscription = RxBus.getInstance().toObservable(TechEditResult.class).subscribe(
                techEditResult -> getTechInfoResult(techEditResult));

        mUpdateTechInfoSubscription = RxBus.getInstance().toObservable(UpdateTechInfoResult.class).subscribe(
                updateTechInfoResult -> finish());

        mUploadAlbumSubscription = RxBus.getInstance().toObservable(AlbumResult.class).subscribe(albumResult -> refresh());

        //mDeleteAlbumSubscription = RxBus.getInstance().toObservable(AlbumResult.class).subscribe(albumResult -> refresh());

        mUploadAvatarSubscription = RxBus.getInstance().toObservable(AvatarResult.class).subscribe(avatarResult -> refresh());

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetTechInfoSubscription);
        RxBus.getInstance().unsubscribe(mUpdateTechInfoSubscription);
        RxBus.getInstance().unsubscribe(mUploadAlbumSubscription);
        //RxBus.getInstance().unsubscribe(mDeleteAlbumSubscription);
        RxBus.getInstance().unsubscribe(mUploadAvatarSubscription);
    }

    private class MyLayoutManager extends GridLayoutManager{

        public MyLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
            View view = recycler.getViewForPosition(0);
            if(view != null){
                measureChild(view, widthSpec, heightSpec);
                int measuredWidth = View.MeasureSpec.getSize(widthSpec);
                int measuredHeight = view.getMeasuredHeight();
                setMeasuredDimension(measuredWidth, measuredHeight);
            }
        }
    }

    private void getTechInfoResult(TechEditResult result){
        if(result.respData != null){
            mAlbums = result.respData.albums;
            mTechInfo = result.respData.info;
            mPhoneNum = result.respData.phoneNum;

            mAdapter.refreshDataSet(mAlbums);
            mPhotoAdapter.refreshDataSet(mAlbums);
            initView();
        }
    }

    private void initView(){
        mUserName.setText(mTechInfo.name);
        mSerialNo.setText(mTechInfo.serialNo);
        mPhoneNumber.setText(mPhoneNum);
        mDescription.setText(mTechInfo.description);
        mNativePlace.setText(mTechInfo.province + " "+ mTechInfo.city);

        if (mTechInfo.gender.equals("male")){
            mMale.setChecked(true);
        }else {
            mFemale.setChecked(true);
        }

        Glide.with(this).load(mTechInfo.avatarUrl).into(mAvatar);
    }

    @OnClick(R.id.change_avatar_txt)
    public void changeAvatarClick(){
        Util.selectPicFromLocal(this, REQUEST_CODE_LOCAL_PICTURE_AVATAR);
    }

    @OnClick(R.id.native_place)
    public void nativePlaceClick(){
        if(mTechInfo == null){
            return;
        }

        Dialog dialog = new SelectPlaceDialog(this, R.style.default_dialog_style, mTechInfo.province, mTechInfo.city) {
            @Override
            public void onSelectConfirmMethod() {
                dismiss();
                mTechInfo.provinceCode = mCurrentProvinceCode;
                mTechInfo.province = mCurrentProvinceName;
                mTechInfo.cityCode = mCurrentCityCode;
                mTechInfo.city = mCurrentCityName;
                mNativePlace.setText(mTechInfo.province + " "+ mTechInfo.city);
            }
        };
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @OnClick(R.id.toolbar_right)
    public void updateTechInfo(){
        String cacheName = mUserName.getText().toString();
        if (TextUtils.isEmpty(cacheName)) {
            makeShortToast("姓名不能为空...");
            return;
        }

        mTechInfo.name = cacheName;
        mTechInfo.serialNo = mSerialNo.getText().toString();
        mTechInfo.description = mDescription.getText().toString();
        mTechInfo.gender = mFemale.isChecked()? "female" : "male";
        mTechInfo.phoneNum = mPhoneNumber.getText().toString();

        showProgressDialog("正在更新技师信息...");
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER, mTechInfo.toString());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_TECH_INFO, params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_LOCAL_PICTURE_ALBUM:
                case REQUEST_CODE_LOCAL_PICTURE_AVATAR:
                    if (data != null) {
                        // 获取的选中的图片
                        try {
                            Uri uri = data.getData();
                            Intent intent = new Intent(this, ClipPictureActivity.class);
                            intent.putExtra(ClipPictureActivity.EXTRA_INPUT, uri.toString());
                            if(requestCode == REQUEST_CODE_LOCAL_PICTURE_ALBUM) {
                                startActivityForResult(intent, REQUEST_CODE_CROP_ALBUM);
                            }else {
                                startActivityForResult(intent, REQUEST_CODE_CROP_AVATAR);
                            }
                        } catch (Exception e) {

                        }
                    }
                    break;
                case REQUEST_CODE_CROP_ALBUM:
                case REQUEST_CODE_CROP_AVATAR:
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
                        showProgressDialog("正在上传...");
                        String imgFile = Util.bitmap2base64(mPhotoTake);
                        // 上传照片
                        Map<String, String> params = new HashMap<>();
                        params.put(RequestConstant.KEY_IMG_FILE, imgFile);
                        if(requestCode == REQUEST_CODE_CROP_ALBUM){
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_ALBUM, params);
                        }else {
                            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPLOAD_AVATAR,params);
                        }
                    }
                    break;
            }
        }
    }

    private void refresh(){
        dismissProgressDialogIfShowing();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);
    }
}
