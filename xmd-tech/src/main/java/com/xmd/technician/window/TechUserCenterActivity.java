package com.xmd.technician.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.utils.Utils;
import com.xmd.image_tool.ImageTool;
import com.xmd.technician.Adapter.AlbumListAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.AlbumInfo;
import com.xmd.technician.bean.TechDetailInfo;
import com.xmd.technician.common.ImageUploader;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.ClearableEditText;
import com.xmd.technician.widget.PhotoGridLayoutManager;
import com.xmd.technician.widget.RewardConfirmDialog;
import com.xmd.technician.widget.SelectPlaceDialog;
import com.xmd.technician.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on AlbumListAdapter.ALBUM_STATUS_NORMAL7-AlbumListAdapter.ALBUM_STATUS_NORMALAlbumListAdapter.ALBUM_STATUS_NORMAL-4.
 */

public class TechUserCenterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.divide_line)
    View divideLine;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    @BindView(R.id.img_tech_center_head)
    CircleImageView imgTechCenterHead;
    @BindView(R.id.rl_edit_tech_head)
    RelativeLayout rlEditTechHead;
    @BindView(R.id.edit_tech_head_mask)
    View editTechHeadMask;
    @BindView(R.id.img_tech_nick_name)
    ImageView imgTechNickName;
    @BindView(R.id.tv_tech_nick_name)
    TextView tvTechNickName;
    @BindView(R.id.edit_tech_nick_name)
    ClearableEditText editTechNickName;
    @BindView(R.id.img_tech_gender)
    ImageView imgTechGender;
    @BindView(R.id.tv_tech_gender)
    TextView tvTechGender;
    @BindView(R.id.rb_tech_gender_man)
    RadioButton rbTechGenderMan;
    @BindView(R.id.rb_tech_gender_women)
    RadioButton rbTechGenderWomen;
    @BindView(R.id.edit_rg_tech_gender)
    RadioGroup editRgTechGender;
    @BindView(R.id.img_tech_origin)
    ImageView imgTechOrigin;
    @BindView(R.id.tv_tech_origin)
    TextView tvTechOrigin;
    @BindView(R.id.tv_tech_origin_edit)
    TextView tvTechOriginEdit;
    @BindView(R.id.tv_tech_origin_select)
    TextView tvTechOriginSelect;
    @BindView(R.id.edit_ll_tech_origin_select)
    LinearLayout editLlTechOriginSelect;
    @BindView(R.id.img_tech_autograph)
    ImageView imgTechAutograph;
    @BindView(R.id.tv_tech_autograph)
    TextView tvTechAutograph;
    @BindView(R.id.edit_tech_autograph)
    ClearableEditText editTechAutograph;
    @BindView(R.id.tv_tech_autograph_length)
    TextView tvTechAutographLength;
    @BindView(R.id.edit_ll_tech_autograph)
    LinearLayout editLlTechAutograph;
    @BindView(R.id.tv_tech_alarm_number)
    TextView tvTechAlarmNumber;
    @BindView(R.id.recycler_view_alarm)
    RecyclerView recyclerViewAlarm;
    @BindView(R.id.tv_album_authority)
    TextView tvAlbumAuthority;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_part)
    RadioButton rbPart;
    @BindView(R.id.rg_authority_select)
    RadioGroup rgAuthoritySelect;
    @BindView(R.id.ll_authority_change)
    LinearLayout llAuthorityChange;
    @BindView(R.id.edit_ll_tech_center_edit_save)
    LinearLayout editLlTechCenterEditSave;
    @BindView(R.id.edit_ll_tech_nick_name)
    LinearLayout editLlTechNickName;
    @BindView(R.id.tv_tech_nick_name_length)
    TextView tvTechNickNameLength;
    @BindView(R.id.ll_album_list)
    LinearLayout llAlbumList;
    @BindView(R.id.albums_is_null)
    TextView albumsIsNull;

    private int mCurrentInfoState;//状态：0,编辑状态，AlbumListAdapter.ALBUM_STATUS_NORMAL：正常状态

    private Subscription mGetTechInfoSubscription;
    private Subscription mUpdateTechInfoSubscription;
    private Subscription mUploadAvatarSubscription;
    private Subscription mUploadAlbumSubscription;

    private List<AlbumInfo> mAlbums; //相册
    private TechDetailInfo mTechInfo; //个人信息
    private Bitmap mAvatarBitmap;//头像bitmap
    private AlbumListAdapter mAdapter;
    private SelectPlaceDialog mSelectPlaceDialog;
    private ImageTool mImageTool = new ImageTool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_user_center);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mCurrentInfoState = AlbumListAdapter.ALBUM_STATUS_NORMAL;
        setTitle(ResourceUtils.getString(R.string.tech_info_edit_title));
        setBackVisible(true);
        divideLine.setVisibility(View.GONE);
        toolbarRight.setVisibility(View.VISIBLE);
        toolbarRight.setText(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? ResourceUtils.getString(R.string.tech_center_info_edit) : ResourceUtils.getString(R.string.tech_center_info_save));
        toolbarRight.setOnClickListener(this);
        rlEditTechHead.setOnClickListener(this);
        tvTechOriginSelect.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        initRecyclerView();
        editTechAutograph.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTechAutographLength.setText(String.format("%s/200", editTechAutograph.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTechNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTechNickNameLength.setText(String.format("%s/10", editTechNickName.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTechNickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });
        mGetTechInfoSubscription = RxBus.getInstance().toObservable(TechEditResult.class).subscribe(
                techEditResult -> handleGetTechEditResult(techEditResult));
        mUpdateTechInfoSubscription = RxBus.getInstance().toObservable(UpdateTechInfoResult.class).subscribe(
                updateTechInfoResult -> handleUpdateTechInfoResult(updateTechInfoResult));
        mUploadAvatarSubscription = RxBus.getInstance().toObservable(AvatarResult.class).subscribe(
                avatarResult -> handleAvatarResult(avatarResult));
        mUploadAlbumSubscription = RxBus.getInstance().toObservable(AlbumResult.class).subscribe(
                albumResult -> handleAlbumResult(albumResult));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);
    }

    private void initRecyclerView() {
        mAlbums = new ArrayList<>();
        mAdapter = new AlbumListAdapter(TechUserCenterActivity.this, mAlbumManagerListener);
        recyclerViewAlarm.setHasFixedSize(true);
        recyclerViewAlarm.setLayoutManager(new PhotoGridLayoutManager(this, 4));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.app_default_padding);
        recyclerViewAlarm.addItemDecoration(new SpaceItemDecoration(spacingInPixels, SpaceItemDecoration.GRID_LAYOUT));
        recyclerViewAlarm.setAdapter(mAdapter);
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
        helper.attachToRecyclerView(recyclerViewAlarm);
    }


    private void handleAvatarResult(AvatarResult result) {
        hideLoading();
        if (result.statusCode >= 200 && result.statusCode <= 299) {
            setResult(RESULT_OK);
            //加载本地图片会比较快一些
            imgTechCenterHead.setImageBitmap(mAvatarBitmap);
            editTechHeadMask.setBackgroundResource(R.drawable.img_upload_head);
        } else {
            Toast.makeText(this, "头像上传失败：" + (TextUtils.isEmpty(result.msg) ? "网络异常" : result.msg), Toast.LENGTH_LONG).show();
        }
    }

    private void handleUpdateTechInfoResult(UpdateTechInfoResult result) {
        hideLoading();
        if (result.statusCode == 200) {
            setResult(RESULT_OK);
            mCurrentInfoState = AlbumListAdapter.ALBUM_STATUS_NORMAL;
            makeShortToast("编辑成功");
            viewStateChanged();
        } else {
            makeShortToast("编辑失败：" + result.msg);
        }

    }

    private void handleGetTechEditResult(TechEditResult result) {
        hideLoading();
        if (result.respData != null) {
            mAlbums = result.respData.albums;
            if (mAlbums.size() == 0 && mCurrentInfoState == mAdapter.ALBUM_STATUS_NORMAL) {
                llAlbumList.setVisibility(View.GONE);
                albumsIsNull.setVisibility(View.VISIBLE);
            } else {
                llAlbumList.setVisibility(View.VISIBLE);
                albumsIsNull.setVisibility(View.GONE);
                ;
            }
            if (mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_EDIT) {
                mAdapter.refreshDataSet(mAdapter.ALBUM_STATUS_EDIT, mAlbums);
            } else {
                mTechInfo = result.respData.info;
                mAdapter.refreshDataSet(mAdapter.ALBUM_STATUS_NORMAL, mAlbums);
                initViewData();
            }

        }
    }

    private void handleAlbumResult(AlbumResult result) {
        hideLoading();
        if (result.statusCode >= 200 && result.statusCode <= 299) {
            setResult(RESULT_OK);
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_EDIT_INFO);
        } else {
            if (!TextUtils.isEmpty(result.msg)) {
                XToast.show("照片上传失败：" + result.msg);
            }

        }
    }


    private void initViewData() {
        if (TextUtils.isEmpty(mTechInfo.avatarUrl)) {
            editTechHeadMask.setBackgroundResource(R.drawable.img_upload_head);
        } else {
            editTechHeadMask.setBackgroundResource(R.drawable.img_select_head);
        }
        Glide.with(this).load(mTechInfo.avatarUrl).error(R.drawable.img_default_avatar).into(imgTechCenterHead);
        tvTechNickName.setText(mTechInfo.getName());
        tvTechGender.setText(mTechInfo.getGender());
        tvTechOrigin.setText(String.format("%s %s", mTechInfo.getProvince(), mTechInfo.getCity()));
        tvTechAutograph.setText(mTechInfo.getDescription());
        if (mTechInfo.getViewAlbumType() == AlbumListAdapter.ALBUM_STATUS_NORMAL || mTechInfo.getViewAlbumType() == 0) {
            rbAll.setChecked(true);
        } else if (mTechInfo.getViewAlbumType() == 2) {
            rbPart.setChecked(true);
        }
        tvAlbumAuthority.setText(mTechInfo.getViewAlbumType() == 2 ? ResourceUtils.getString(R.string.tech_center_info_album_jurisdiction_vip)
                : ResourceUtils.getString(R.string.tech_center_info_album_jurisdiction_all));

    }

    @OnClick(R.id.edit_ll_tech_origin_select)
    public void onTvTechOriginSelectClicked() {
        if (mSelectPlaceDialog == null) {
            mSelectPlaceDialog = new SelectPlaceDialog(this, R.style.default_dialog_style, mTechInfo.getProvince(), mTechInfo.getCity()) {
                @Override
                public void onSelectConfirmMethod() {
                    dismiss();
                    tvTechOriginEdit.setText(mCurrentProvinceName + " " + mCurrentCityName);
                    tvTechOriginSelect.setText(TextUtils.isEmpty(tvTechOriginEdit.getText().toString().trim()) ? "选择" : "修改");
                }
            };
            mSelectPlaceDialog.setCanceledOnTouchOutside(false);
        }

        mSelectPlaceDialog.show();
    }

    @OnClick(R.id.btn_edit_save)
    public void onBtnEditSaveClicked() {
        mTechInfo.setName(editTechNickName.getText().toString());
        mTechInfo.setGender(rbTechGenderMan.isChecked() ? "男" : "女");
        if (TextUtils.isEmpty(mTechInfo.getName())) {
            XToast.show("技师昵称不能为空");
            return;
        }
        mTechInfo.setDescription(Utils.replaceBlank(editTechAutograph.getText().toString().trim()));
        mTechInfo.setViewAlbumType(rbAll.isChecked() ? AlbumListAdapter.ALBUM_STATUS_NORMAL : 2);
        if (mSelectPlaceDialog != null) {
            mTechInfo.setProvinceCode(mSelectPlaceDialog.mCurrentProvinceCode);
            mTechInfo.setProvince(mSelectPlaceDialog.mCurrentProvinceName);
            mTechInfo.setCityCode(mSelectPlaceDialog.mCurrentCityCode);
            mTechInfo.setCity(mSelectPlaceDialog.mCurrentCityName);
        }
        showLoading("正在更新技师信息...", true);
        mAdapter.refreshDataSet(AlbumListAdapter.ALBUM_STATUS_NORMAL, mAlbums);
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_USER, mTechInfo.toString());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_UPDATE_TECH_INFO, params);

    }

    private void viewStateChanged() {

        editTechHeadMask.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        toolbarRight.setText(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? ResourceUtils.getString(R.string.tech_center_info_edit) : ResourceUtils.getString(R.string.tech_center_info_save));

        imgTechNickName.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        tvTechNickName.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        editLlTechNickName.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        //   editTechNickName.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        tvTechNickName.setText(mTechInfo.getName());
        editTechNickName.setText(mTechInfo.getName());

        imgTechGender.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        tvTechGender.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        editRgTechGender.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        tvTechGender.setText(mTechInfo.getGender());
        rbTechGenderMan.setChecked(mTechInfo.getGender().equals("男"));
        rbTechGenderWomen.setChecked(mTechInfo.getGender().equals("女"));

        imgTechOrigin.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        tvTechOrigin.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        editLlTechOriginSelect.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        tvTechOrigin.setText(String.format("%s %s", mTechInfo.getProvince(), mTechInfo.getCity()));
        tvTechOriginEdit.setText(String.format("%s %s", mTechInfo.getProvince(), mTechInfo.getCity()));
        tvTechOriginSelect.setText(TextUtils.isEmpty(tvTechOriginEdit.getText().toString().trim()) ? "选择" : "修改");


        imgTechAutograph.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        tvTechAutograph.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        editLlTechAutograph.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        tvTechAutograph.setText(mTechInfo.getDescription());
        editTechAutograph.setText("-".equals(mTechInfo.getDescription()) ? "" : mTechInfo.getDescription());

        tvTechAlarmNumber.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        tvAlbumAuthority.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.VISIBLE : View.GONE);
        tvAlbumAuthority.setText(mTechInfo.getViewAlbumType() == 2 ? ResourceUtils.getString(R.string.tech_center_info_album_jurisdiction_vip)
                : ResourceUtils.getString(R.string.tech_center_info_album_jurisdiction_all));

        llAuthorityChange.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        editLlTechCenterEditSave.setVisibility(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? View.GONE : View.VISIBLE);
        mAdapter.refreshDataSet(mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL ? mAdapter.ALBUM_STATUS_NORMAL : mAdapter.ALBUM_STATUS_EDIT, mAlbums);

        if (mCurrentInfoState == mAdapter.ALBUM_STATUS_NORMAL && mAlbums.size() == 0) {
            llAlbumList.setVisibility(View.GONE);
            albumsIsNull.setVisibility(View.VISIBLE);
        } else {
            llAlbumList.setVisibility(View.VISIBLE);
            albumsIsNull.setVisibility(View.GONE);
        }

    }

    private AlbumListAdapter.OnItemManagerClickListener mAlbumManagerListener = new AlbumListAdapter.OnItemManagerClickListener() {
        @Override
        public void onAddAlbum() {
            mImageTool.reset().maxSize(Constant.ALBUM_MAX_SIZE).setAspectX_Y(5, 7).start(TechUserCenterActivity.this, new ImageTool.ResultListener() {
                @Override
                public void onResult(String s, Uri uri, Bitmap bitmap) {
                    if (s != null) {
                        XToast.show(s);
                    } else if (bitmap != null) {
                        showLoading("正在上传照片...", false);
                        ImageUploader.getInstance().upload(ImageUploader.TYPE_ALBUM, bitmap);
                    }
                }
            });
        }

        @Override
        public void onDeleteAlbum(int position) {
            new RewardConfirmDialog(TechUserCenterActivity.this, "", getString(R.string.edit_activity_delete_album), "") {
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
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageTool.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        RxBus.getInstance().unsubscribe(mGetTechInfoSubscription, mUpdateTechInfoSubscription, mUploadAlbumSubscription, mUploadAvatarSubscription);
        hideLoading();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_edit_tech_head:
                if (mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL) {
                    return;
                }
                mImageTool.reset().maxSize(Constant.AVATAR_MAX_SIZE).setAspectX_Y(1, 1).start(TechUserCenterActivity.this, new ImageTool.ResultListener() {
                    @Override
                    public void onResult(String s, Uri uri, Bitmap bitmap) {
                        if (s != null) {
                            XToast.show(s);
                        } else if (bitmap != null) {
                            showLoading("正在上传头像...", false);
                            mAvatarBitmap = bitmap;
                            ImageUploader.getInstance().upload(ImageUploader.TYPE_AVATAR, bitmap);
                        }
                    }
                });
                break;
            case R.id.toolbar_right:
                if (mCurrentInfoState == AlbumListAdapter.ALBUM_STATUS_NORMAL) {
                    mCurrentInfoState = AlbumListAdapter.ALBUM_STATUS_EDIT;
                    viewStateChanged();
                } else {
                    onBtnEditSaveClicked();
                }

                break;
        }
    }


}
