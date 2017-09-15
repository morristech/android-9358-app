package com.xmd.technician.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.image_tool.ImageTool;
import com.xmd.technician.Adapter.AlbumAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.AlbumInfo;
import com.xmd.technician.bean.TechDetailInfo;
import com.xmd.technician.common.ImageUploader;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class TechInfoActivity extends BaseActivity {
    public static final int REQUEST_CODE_LOCAL_PICTURE_AVATAR = 1;
    public static final int REQUEST_CODE_LOCAL_PICTURE_ALBUM = 2;

    @BindView(R.id.avatar)
    RoundImageView mAvatar;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.native_place)
    TextView mNativePlace;
    @BindView(R.id.phone_number)
    TextView mPhoneNumber;
    @BindView(R.id.introduce)
    EditText mDescription;
    @BindView(R.id.button_female)
    RadioButton mFemale;
    @BindView(R.id.button_male)
    RadioButton mMale;
    @BindView(R.id.album_container)
    RecyclerView mAlbumListView;

    @BindView(R.id.button_album_all)
    RadioButton mAlbumAllAccess;
    @BindView(R.id.button_album_vip)
    RadioButton mAlbumVipAccess;

    private Subscription mGetTechInfoSubscription;
    private Subscription mUpdateTechInfoSubscription;
    private Subscription mUploadAvatarSubscription;
    private Subscription mUploadAlbumSubscription;

    private List<AlbumInfo> mAlbums;
    private TechDetailInfo mTechInfo;
    private Bitmap mAvatarBitmap;

    // 籍贯
    private AlbumAdapter mAdapter;
    private boolean mViewInitialized = false;
    private SelectPlaceDialog mSelectPlaceDialog;
    private ImageTool mImageTool = new ImageTool();

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
                mImageTool.reset().maxSize(Constant.ALBUM_MAX_SIZE).setAspectX_Y(5, 7).start(TechInfoActivity.this, new ImageTool.ResultListener() {
                    @Override
                    public void onResult(String s, Uri uri, Bitmap bitmap) {
                        if (s != null) {
                            XToast.show(s);
                        } else if (bitmap != null) {
                            showLoading("正在上传照片...");
                            ImageUploader.getInstance().upload(ImageUploader.TYPE_ALBUM, bitmap);
                        }
                    }
                });
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
                techEditResult -> handleGetTechEditResult(techEditResult));

        mUpdateTechInfoSubscription = RxBus.getInstance().toObservable(UpdateTechInfoResult.class).subscribe(
                updateTechInfoResult -> {
                    if (updateTechInfoResult.statusCode == 200) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        dismissProgressDialogIfShowing();
                        makeShortToast(updateTechInfoResult.msg);
                    }
                });

        mUploadAlbumSubscription = RxBus.getInstance().toObservable(AlbumResult.class).subscribe(this::handleAlbumResult);

        mUploadAvatarSubscription = RxBus.getInstance().toObservable(AvatarResult.class).subscribe(this::handleAvatarResult);

        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetTechInfoSubscription, mUpdateTechInfoSubscription, mUploadAlbumSubscription, mUploadAvatarSubscription);
        hideLoading();
    }

    private void handleGetTechEditResult(TechEditResult result) {
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
            mPhoneNumber.setText(String.format("%s (仅做登录帐号，不对客户显示)", mTechInfo.phoneNum));
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

            if (mTechInfo.viewAlbumType == 1 || mTechInfo.viewAlbumType == 0) {
                mAlbumAllAccess.setChecked(true);
            } else if (mTechInfo.viewAlbumType == 2) {
                mAlbumVipAccess.setChecked(true);
            }
        }

        Glide.with(this).load(mTechInfo.avatarUrl).into(mAvatar);
    }

    @OnClick(R.id.change_avatar_txt)
    public void changeAvatarClick() {
        mImageTool.reset().maxSize(Constant.AVATAR_MAX_SIZE).setAspectX_Y(1, 1).start(this, new ImageTool.ResultListener() {
            @Override
            public void onResult(String s, Uri uri, Bitmap bitmap) {
                if (s != null) {
                    XToast.show(s);
                } else if (bitmap != null) {
                    showProgressDialog("正在上传头像...");
                    mAvatarBitmap = bitmap;
                    ImageUploader.getInstance().upload(ImageUploader.TYPE_AVATAR, bitmap);
                }
            }
        });
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
        mTechInfo.viewAlbumType = mAlbumAllAccess.isChecked() ? 1 : 2;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageTool.onActivityResult(requestCode, resultCode, data);
    }

    private void handleAvatarResult(AvatarResult result) {
        dismissProgressDialogIfShowing();
        if (result.statusCode >= 200 && result.statusCode <= 299) {
            setResult(RESULT_OK);
            //加载本地图片会比较快一些
            mAvatar.setImageBitmap(mAvatarBitmap);
        } else {
            Toast.makeText(this, "头像上传失败：" + result.msg, Toast.LENGTH_LONG).show();
        }
    }

    private void handleAlbumResult(AlbumResult result) {
        // dismissProgressDialogIfShowing();
        hideLoading();
        if (result.statusCode >= 200 && result.statusCode <= 299) {
            setResult(RESULT_OK);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);
        } else {
            Toast.makeText(this, "照片上传失败：" + result.msg, Toast.LENGTH_LONG).show();
        }
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
