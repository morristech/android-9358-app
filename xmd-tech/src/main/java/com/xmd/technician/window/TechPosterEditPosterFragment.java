package com.xmd.technician.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.image_tool.ImageTool;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.PosterBean;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.widget.ClearableEditText;
import com.xmd.technician.widget.TechPosterDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-6-20.
 */

public class TechPosterEditPosterFragment extends BaseFragment implements TechPosterDialog.PosterShareOrSaveListener {

    @Bind(R.id.img_poster_select_img)
    ImageView imgPosterSelectImg;
    @Bind(R.id.img_poster_preview)
    ImageView imgPosterPreview;
    @Bind(R.id.rl_poster_preview)
    RelativeLayout rlPosterPreview;
    @Bind(R.id.iv_poster_primary_title)
    View ivPosterPrimaryTitle;
    @Bind(R.id.edit_poster_primary_title)
    ClearableEditText editPosterPrimaryTitle;
    @Bind(R.id.iv_poster_minor_title)
    View ivPosterMinorTitle;
    @Bind(R.id.edit_poster_minor_title)
    ClearableEditText editPosterMinorTitle;
    @Bind(R.id.iv_poster_tech_name)
    View ivPosterTechName;
    @Bind(R.id.edit_poster_tech_name)
    ClearableEditText editPosterTechName;
    @Bind(R.id.iv_poster_tech_number)
    View ivPosterTechNumber;
    @Bind(R.id.edit_poster_tech_number)
    TextView editPosterTechNumber;
    @Bind(R.id.iv_poster_club_name)
    View ivPosterClubName;
    @Bind(R.id.edit_poster_club_name)
    TextView editPosterClubName;

    private boolean primaryTitleIsSelected, minorTitleIsSelected, nickNameIsSelected, techNumberIsSelected, clubNameIsSelect;
    private String mPrimaryTitle, mMinorTitle, mNickName, mTechNumber, mClubName, mPosterImageUrl;
    private ImageTool mImageTool = new ImageTool();
    private String imageUrl;
    private String posterStyle;
    private PosterBean mPosterBean;
    private int mCurrentModel;
    private TechPosterDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tech_poster_edit_poster, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mPosterBean = (PosterBean) getArguments().get(Constant.KEY_CURRENT_POSTER);
        if (mPosterBean.id > 0) {//编辑
            if (Utils.isNotEmpty(mPosterBean.title)) {
                editPosterPrimaryTitle.setText(mPosterBean.title);
                ivPosterPrimaryTitle.setSelected(true);
                primaryTitleIsSelected = true;
            }
            if (Utils.isNotEmpty(mPosterBean.subTitle)) {
                editPosterMinorTitle.setText(mPosterBean.subTitle);
                ivPosterMinorTitle.setSelected(true);
                minorTitleIsSelected = true;
            }
            if (Utils.isNotEmpty(mPosterBean.name)) {
                editPosterTechName.setText(mPosterBean.name);
                ivPosterTechName.setSelected(true);
                nickNameIsSelected = true;
            }

            if (Utils.isNotEmpty(mPosterBean.techNo)) {
                ivPosterTechNumber.setSelected(true);
                techNumberIsSelected = true;
            }

            if (Utils.isNotEmpty(mPosterBean.clubName)) {
                ivPosterClubName.setSelected(true);
                clubNameIsSelect = true;
            }

            if (Utils.isNotEmpty(mPosterBean.imageUrl)) {
                Glide.with(getActivity()).load(mPosterBean.imageUrl).into(imgPosterSelectImg);
            }

            if (mPosterBean.style.equals(Constant.TECH_POSTER_TYPE_SQUARE)) {
                setModel(Constant.TECH_POSTER_SQUARE_MODEL);
            } else if (mPosterBean.style.equals(Constant.TECH_POSTER_TYPE_CIRCULAR)) {
                setModel(Constant.TECH_POSTER_CIRCULAR_MODEL);
            } else {
                setModel(Constant.TECH_POSTER_FLOWER_MODEL);
            }
        } else {//新建
            ivPosterPrimaryTitle.setSelected(true);
            primaryTitleIsSelected = true;
            ivPosterMinorTitle.setSelected(true);
            minorTitleIsSelected = true;
            ivPosterTechName.setSelected(true);
            nickNameIsSelected = true;
            ivPosterTechNumber.setSelected(true);
            techNumberIsSelected = true;
            ivPosterClubName.setSelected(true);
            clubNameIsSelect = true;
        }

        if (Utils.isNotEmpty(LoginTechnician.getInstance().getTechNo())) {
            editPosterTechNumber.setText(LoginTechnician.getInstance().getTechNo());
        } else {
            editPosterTechNumber.setText("");
        }

        if (Utils.isNotEmpty(LoginTechnician.getInstance().getClubName())) {
            editPosterClubName.setText(LoginTechnician.getInstance().getClubName());
        } else {
            editPosterClubName.setText("");
        }


    }


    public void setModel(int model) {

        mCurrentModel = model;
        posterStyle = Constant.TECH_POSTER_TYPE_FLOWER;
        if (null != imgPosterPreview) {
            switch (model) {
                case Constant.TECH_POSTER_CIRCULAR_MODEL:
                    Glide.with(getActivity()).load(R.drawable.img_poster_circular_small).into(imgPosterPreview);
                    posterStyle = Constant.TECH_POSTER_TYPE_CIRCULAR;
                    break;
                case Constant.TECH_POSTER_FLOWER_MODEL:
                    posterStyle = Constant.TECH_POSTER_TYPE_FLOWER;
                    Glide.with(getActivity()).load(R.drawable.img_poster_flower_small).into(imgPosterPreview);
                    break;
                case Constant.TECH_POSTER_SQUARE_MODEL:
                    posterStyle = Constant.TECH_POSTER_TYPE_SQUARE;
                    Glide.with(getActivity()).load(R.drawable.img_poster_square_small).into(imgPosterPreview);
                    break;
                default:
                    posterStyle = Constant.TECH_POSTER_TYPE_FLOWER;
                    Glide.with(getActivity()).load(R.drawable.img_poster_flower_small).into(imgPosterPreview);
                    break;
            }

        }
    }


    @OnClick({R.id.img_poster_select_img, R.id.rl_poster_preview, R.id.iv_poster_primary_title, R.id.iv_poster_minor_title, R.id.iv_poster_tech_name, R.id.iv_poster_tech_number, R.id.iv_poster_club_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_poster_select_img:
                mImageTool.reset().maxSize(Constant.POSTER_MAX_SIZE).onlyPick(true).start(this, (s, uri, bitmap) -> {
                    if (s == null && uri != null) {
                        imageUrl = uri.getPath();
                        Glide.with(getActivity()).load(imageUrl).into(imgPosterSelectImg);
                    }
                });

                break;
            case R.id.rl_poster_preview:

                if (primaryTitleIsSelected) {
                    mPrimaryTitle = editPosterPrimaryTitle.getText().toString();
                } else {
                    mPrimaryTitle = "";
                }
                if (minorTitleIsSelected) {
                    mMinorTitle = editPosterMinorTitle.getText().toString();
                } else {
                    mMinorTitle = "";
                }
                if (nickNameIsSelected) {
                    mNickName = editPosterTechName.getText().toString();
                } else {
                    mNickName = "";
                }
                if (techNumberIsSelected) {
                    mTechNumber = LoginTechnician.getInstance().getTechNo();
                } else {
                    mTechNumber = "";
                }
                if (clubNameIsSelect) {
                    mClubName = LoginTechnician.getInstance().getClubName();
                } else {
                    mClubName = "";
                }
                if (mPosterBean != null && Utils.isNotEmpty(mPosterBean.imageId)) {
                    mPosterImageUrl = mPosterBean.imageUrl;
                } else {
                    mPosterImageUrl = "";
                }
                if (mDialog == null) {
                    mDialog = new TechPosterDialog(getActivity(), mCurrentModel, true);
                }

                mDialog.show();
                mDialog.setViewDate(mPrimaryTitle, mMinorTitle, mNickName, mTechNumber, mClubName, imageUrl, mPosterImageUrl);
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.setPosterListener(this);
                break;
            case R.id.iv_poster_primary_title:
                primaryTitleIsSelected = changeViewState(ivPosterPrimaryTitle);
                break;
            case R.id.iv_poster_minor_title:
                minorTitleIsSelected = changeViewState(ivPosterMinorTitle);
                break;
            case R.id.iv_poster_tech_name:
                nickNameIsSelected = changeViewState(ivPosterTechName);
                break;
            case R.id.iv_poster_tech_number:
                techNumberIsSelected = changeViewState(ivPosterTechNumber);
                break;
            case R.id.iv_poster_club_name:
                clubNameIsSelect = changeViewState(ivPosterClubName);
                break;
        }
    }

    private boolean changeViewState(View view) {
        if (view.isSelected()) {
            view.setSelected(false);
            return false;
        } else {
            view.setSelected(true);
            return true;
        }
    }

    public String getImageUrl() {
        if (Utils.isNotEmpty(imageUrl)) {
            return imageUrl;
        } else {
            return "";
        }
    }

    public Map<String, String> getPosterInfo() {
        Map<String, String> params = new HashMap<>();
        if (primaryTitleIsSelected) {//大标题
            if (Utils.isNotEmpty(editPosterPrimaryTitle.getText().toString())) {
                params.put(RequestConstant.KEY_POSTER_TITLE, editPosterPrimaryTitle.getText().toString());
            } else {
                Utils.makeShortToast(getActivity(), "请输入大标题,或取消勾选");
                return null;
            }
        } else {
            params.put(RequestConstant.KEY_POSTER_TITLE, "");
        }
        if (minorTitleIsSelected) {//小标题
            if (Utils.isNotEmpty(editPosterMinorTitle.getText().toString())) {
                params.put(RequestConstant.KEY_POSTER_SUB_TITLE, editPosterMinorTitle.getText().toString());
            } else {
                Utils.makeShortToast(getActivity(), "请输入小标题,或取消勾选");
                return null;
            }
        } else {
            params.put(RequestConstant.KEY_POSTER_SUB_TITLE, "");
        }

        if (nickNameIsSelected) {//昵称
            if (Utils.isNotEmpty(editPosterTechName.getText().toString())) {
                params.put(RequestConstant.KEY_POSTER_NAME, editPosterTechName.getText().toString());
            } else {
                Utils.makeShortToast(getActivity(), "请输入昵称,或取消勾选");
                return null;
            }
        } else {
            params.put(RequestConstant.KEY_POSTER_NAME, "");
        }
        if (techNumberIsSelected) {//技师编号
            params.put(RequestConstant.KEY_POSTER_TECH_NO, LoginTechnician.getInstance().getTechNo());
        } else {
            params.put(RequestConstant.KEY_POSTER_TECH_NO, "");
        }
        if (clubNameIsSelect) {//会所名称
            params.put(RequestConstant.KEY_POSTER_CLUB_NAME, LoginTechnician.getInstance().getClubName());
        } else {
            params.put(RequestConstant.KEY_POSTER_CLUB_NAME, "");

        }
        params.put(RequestConstant.KEY_POSTER_STYLE, posterStyle);
        if (mPosterBean.id > 0) {
            params.put(RequestConstant.KEY_POSTER_ID, String.valueOf(mPosterBean.id));
        }
        return params;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageTool.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private String saveImage(View v) {
        Bitmap bitmap;
        String name = "jietu.png";
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + "jietu.png");
        if (!file.exists()) {
            file.mkdir();
        }
        View view = mDialog.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        mDialog.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int[] loacation = new int[2];
        v.getLocationOnScreen(loacation);
        File picFile = new File(file, name);
        try {
            bitmap = Bitmap.createBitmap(bitmap, loacation[0], loacation[1], view.getWidth(), view.getHeight() - Utils.dip2px(getActivity(), 60));
            FileOutputStream fout = new FileOutputStream(picFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            saveImageToGallery(picFile);
            return file.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            view.destroyDrawingCache();
        }

        return null;
    }

    public void saveImageToGallery(File file) {
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), "code", null);
            // 最后通知图库更新
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + file)));
            Utils.makeShortToast(getActivity(), "保存成功，可在图库查看");

        } catch (FileNotFoundException e) {
            Utils.makeShortToast(getActivity(), "保存失败");
            e.printStackTrace();
        }
    }


    @Override
    public void posterSave(View view) {
        saveImage(view);
    }

    @Override
    public void posterEdit() {

    }

    @Override
    public void posterShare() {

    }
}
