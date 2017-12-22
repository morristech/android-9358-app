package com.xmd.technician.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.XmdActivityManager;
import com.xmd.image_tool.ImageTool;
import com.xmd.technician.Constant;
import com.xmd.technician.common.ImageUploader;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.contract.CompleteRegisterInfoContract;
import com.xmd.technician.databinding.ActivityCompleteRegisterInfoBinding;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.AlertDialogBuilder;

import rx.Subscription;

/**
 * Created by heyangya on 16-12-20.
 */

public class CompleteRegisterInfoPresenter extends BasePresenter<CompleteRegisterInfoContract.View> implements CompleteRegisterInfoContract.Presenter {
    public ObservableBoolean mCanUpdate = new ObservableBoolean();
    private ActivityCompleteRegisterInfoBinding mBinding;
    private LoginTechnician mTech = LoginTechnician.getInstance();
    private String mNickName;
    private boolean mFemale;
    private Subscription mSubscription;
    private Subscription mUploadAvatarSubscription;

    private ImageTool mImageTool = new ImageTool();
    private Bitmap mAvatar;

    private boolean mJoinClub;

    public CompleteRegisterInfoPresenter(Context context, CompleteRegisterInfoContract.View view, ActivityCompleteRegisterInfoBinding binding) {
        super(context, view);
        mBinding = binding;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFemale = true;
        mBinding.setPresenter(this);

        mSubscription = RxBus.getInstance().toObservable(UpdateTechInfoResult.class)
                .subscribe(this::handleUpdateNickNameAndGenderResult);

        mUploadAvatarSubscription = RxBus.getInstance().toObservable(AvatarResult.class)
                .subscribe(this::handleUploadAvatar);

        mJoinClub = !mTech.hasClub(); //需要进入加入会所界面
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
        mUploadAvatarSubscription.unsubscribe();
    }


    @Override
    public void setNickName(Editable s) {
        mNickName = s.toString();
        checkCanUpdate();
    }

    @Override
    public void setGender(boolean female) {
        mFemale = female;
    }

    @Override
    public void onClickAvatar() {
        mImageTool.maxSize(Constant.AVATAR_MAX_SIZE).setAspectX_Y(1, 1).start((Activity) mContext, new ImageTool.ResultListener() {
            @Override
            public void onResult(String s, Uri uri, Bitmap bitmap) {
                if (s != null) {
                    XToast.show(s);
                } else if (bitmap != null) {
                    mAvatar = bitmap;
                    mBinding.avatar.setImageBitmap(mAvatar);
                    checkCanUpdate();
                }
            }
        });
    }

    @Override
    public void onClickFinish() {
        mView.showLoading("正在更新数据...");
        //更新昵称和性别
        mTech.updateTechNickNameAndGender(mNickName, mFemale ? LoginTechnician.GENDER_FEMALE : LoginTechnician.GENDER_MALE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageTool.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBack() {
        new AlertDialogBuilder(mContext)
                .setMessage("您还有资料没有填写，确定以后填写吗?")
                .setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setNegativeButton("以后再填写", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishSelf();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void checkCanUpdate() {
        mCanUpdate.set(!TextUtils.isEmpty(mNickName) && mAvatar != null);
    }

    private void handleUpdateNickNameAndGenderResult(UpdateTechInfoResult result) {
        mView.hideLoading();
        if (result.statusCode < 200 || result.statusCode > 299) {
            mView.showAlertDialog(result.msg);
        } else {
            mTech.onUpdateTechNickNameAndGender(mNickName, mFemale, result);
            if (mAvatar != null) {
                mView.showLoading("正在上传头像...");
                ImageUploader.getInstance().upload(ImageUploader.TYPE_AVATAR, mAvatar);
            } else {
                finishSelf();
            }
        }
    }

    private void handleUploadAvatar(AvatarResult result) {
        mView.hideLoading();
        if (result.statusCode < 200 || result.statusCode > 299) {
            mView.showAlertDialog(result.msg);
        } else {
            mTech.onUploadAvatarResult(result);
            finishSelf();
        }
    }

    private void finishSelf() {
        if (mJoinClub) {
            UINavigation.gotoJoinClubFrom(mContext, UINavigation.OPEN_JOIN_CLUB_FROM_START);
        } else {
            XmdActivityManager.getInstance().finishAll();
            UINavigation.gotoMainActivityFromStart(mContext);
        }
        mView.finishSelf();
    }
}
