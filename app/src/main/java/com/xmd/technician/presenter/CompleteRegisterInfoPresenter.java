package com.xmd.technician.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.UINavigation;
import com.xmd.technician.contract.CompleteRegisterInfoContract;
import com.xmd.technician.databinding.ActivityCompleteRegisterInfoBinding;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.AlertDialogBuilder;
import com.xmd.technician.window.ImageSelectAndCropActivity;

import rx.Subscription;

/**
 * Created by heyangya on 16-12-20.
 */

public class CompleteRegisterInfoPresenter extends BasePresenter<CompleteRegisterInfoContract.View> implements CompleteRegisterInfoContract.Presenter {
    private ActivityCompleteRegisterInfoBinding mBinding;
    private LoginTechnician mTech = LoginTechnician.getInstance();
    public ObservableBoolean mCanUpdate = new ObservableBoolean();
    private String mAvatarUrl;
    private String mNickName;
    private boolean mFemale;
    private static final int REQUEST_CODE_PICK_AVATAR = 1;
    private Subscription mSubscription;
    private Subscription mUploadAvatarSubscription;

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

        mJoinClub = TextUtils.isEmpty(mTech.getTechId()); //技师ID为空，需要进入加入会所界面
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
        ImageSelectAndCropActivity.pickAndCrop((Activity) mContext, REQUEST_CODE_PICK_AVATAR);
    }

    @Override
    public void onClickFinish() {
        mView.showLoading("正在更新数据...");
        //更新昵称和性别
        mTech.updateTechNickNameAndGender(mNickName, mFemale ? LoginTechnician.GENDER_FEMALE : LoginTechnician.GENDER_MALE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_AVATAR) {
            if (resultCode != Activity.RESULT_OK) {
                if (resultCode != Activity.RESULT_CANCELED) {
                    mView.showAlertDialog("选择图片失败！");
                }
            } else {
                mAvatarUrl = data.getData().getPath();
                Glide.with(mContext)
                        .load(mAvatarUrl)
                        .transform(ImageLoader.circleTransformation(mContext))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mBinding.avatar);
                checkCanUpdate();
            }
        }
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
        mCanUpdate.set(!TextUtils.isEmpty(mNickName) && !TextUtils.isEmpty(mAvatarUrl));
    }

    private void handleUpdateNickNameAndGenderResult(UpdateTechInfoResult result) {
        mView.hideLoading();
        if (result.statusCode < 200 || result.statusCode > 299) {
            mView.showAlertDialog(result.msg);
        } else {
            mTech.onUpdateTechNickNameAndGender(mNickName, mFemale, result);
            if (!TextUtils.isEmpty(mAvatarUrl)) {
                mView.showLoading("正在上传头像...");
                mTech.uploadAvatar(mAvatarUrl);
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
            UINavigation.gotoMainActivityFromStart(mContext);
        }
        mView.finishSelf();
    }
}
