package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.FavourableActivityBean;
import com.xmd.manager.common.FileSizeUtil;
import com.xmd.manager.common.ImageLoader;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ThreadManager;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.AddGroupResult;
import com.xmd.manager.service.response.AlbumUploadResult;
import com.xmd.manager.service.response.FavourableActivityListResult;
import com.xmd.manager.service.response.GroupInfoResult;
import com.xmd.manager.service.response.GroupListResult;
import com.xmd.manager.service.response.GroupTagListResult;
import com.xmd.manager.service.response.SendGroupMessageResult;
import com.xmd.manager.service.response.TechListResult;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.LoadingDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by lhj on 2016/9/26.
 */
public class GroupMessageCustomerActivity extends BaseActivity {

    private final static int CUSTOMER_FRAGMENT = 0;
    private final static int COUPON_FRAGMENT = 1;
    private final static int EDIT_CONTENT_FRAGMENT = 2;
    private final static int CONFIRM_FRAGMENT = 3;

    private GMessageSelectCustomerFragment mCustomerFragment;
    private GMessageSelectCouponFragment mCouponFragment;
    private GMessageEditContentFragment mEditContentFragment;
    private GMessageConfirmFragment mConfirmFragment;

    private Subscription mGroupSaveEditSubscription;
    private Subscription mGetGroupInfoSubscription;
    private Subscription mGetCouponListSubscription;
    private Subscription mGroupMessageAlbumUploadSubscription;
    private Subscription mSendGroupMessageResultSubscription;
    private Subscription mGetGroupTagListSubscription;

    private int mLimitAmount;
    private int mLimitImageSize;
    private String imageId;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message_customer);
        setRightVisible(true, ResourceUtils.getString(R.string.cancel), view -> finish());
        ButterKnife.bind(this);

        initView();

        mGroupSaveEditSubscription = RxBus.getInstance().toObservable(AddGroupResult.class).subscribe(
                result -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_GROUP_TAG_LIST));
        mGetGroupInfoSubscription = RxBus.getInstance().toObservable(GroupInfoResult.class).subscribe(
                groupInfoResult -> {
                    if(groupInfoResult.statusCode == 200){
                        mLimitAmount = groupInfoResult.respData.limitNumber;
                        mLimitImageSize = groupInfoResult.respData.imageSize;
                    }

                    mCustomerFragment.handlerGroupInfoResult(groupInfoResult);
                });
        mGetCouponListSubscription = RxBus.getInstance().toObservable(FavourableActivityListResult.class).subscribe(
                activityResult -> mCouponFragment.handleFavourableActivityResult(activityResult));
        mGroupMessageAlbumUploadSubscription = RxBus.getInstance().toObservable(AlbumUploadResult.class).subscribe(
                uploadResult -> handleAlbumUploadResult(uploadResult));
        mSendGroupMessageResultSubscription = RxBus.getInstance().toObservable(SendGroupMessageResult.class).subscribe(
                sendResult -> handlerSendGroupMessageResult(sendResult));
        mGetGroupTagListSubscription = RxBus.getInstance().toObservable(GroupTagListResult.class).subscribe(
                result -> mCustomerFragment.handlerGroupTagList(result));
    }

    private void initView() {
        showFragment(CUSTOMER_FRAGMENT);
    }

    private void showFragment(int index) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (mCustomerFragment != null) {
            transaction.hide(mCustomerFragment);
        }
        if (mCouponFragment != null) {
            transaction.hide(mCouponFragment);
        }
        if (mEditContentFragment != null) {
            transaction.hide(mEditContentFragment);
        }
        if (mConfirmFragment != null) {
            transaction.hide(mConfirmFragment);
        }


        switch (index) {
            case CUSTOMER_FRAGMENT:
                if (mCustomerFragment != null) {
                    transaction.show(mCustomerFragment);
                } else {
                    mCustomerFragment = new GMessageSelectCustomerFragment();
                    transaction.add(R.id.fragment_message_step, mCustomerFragment);
                }
                break;
            case COUPON_FRAGMENT:
                if (mCouponFragment != null) {
                    transaction.show(mCouponFragment);
                } else {
                    mCouponFragment = new GMessageSelectCouponFragment();
                    transaction.add(R.id.fragment_message_step, mCouponFragment);
                }
                break;
            case EDIT_CONTENT_FRAGMENT:
                if (mEditContentFragment != null) {
                    transaction.show(mEditContentFragment);
                } else {
                    mEditContentFragment = new GMessageEditContentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(GMessageEditContentFragment.EXTRA_LIMIT_SIZE, mLimitImageSize);
                    mEditContentFragment.setArguments(bundle);
                    transaction.add(R.id.fragment_message_step, mEditContentFragment);
                }
                break;
            case CONFIRM_FRAGMENT:
                if (mConfirmFragment != null) {
                    transaction.show(mConfirmFragment);
                } else {
                    mConfirmFragment = new GMessageConfirmFragment();
                    transaction.add(R.id.fragment_message_step, mConfirmFragment);
                }
                Map<String, Object> params = new HashMap<>();
                params.put(RequestConstant.KEY_GROUP_IMAGE_ID, mEditContentFragment.getImageUrl());
                params.put(RequestConstant.KEY_GROUP_MESSAGE_CONTENT, mEditContentFragment.getMessageContent());
                params.put(RequestConstant.KEY_GROUP_SEND_COUNT, mCustomerFragment.getSelectCustomerCount());
                params.put(RequestConstant.KEY_GROUP_COUPON_CONTENT, mCouponFragment.getCouponInfo());
                params.put(RequestConstant.KEY_GROUP_USER_GROUP_TYPE, mCustomerFragment.getSelectCustomerGroupType());
                params.put(RequestConstant.KEY_GROUP_NAME, mCustomerFragment.getSelectCustomerGroupNames());
                mConfirmFragment.initData(params);
                break;
        }
        transaction.commit();
    }

    private void handleAlbumUploadResult(AlbumUploadResult uploadResult) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (uploadResult.statusCode == 200) {
            imageId = uploadResult.respData.imageId;
            sendGroupMessage();
        }
    }

    private void handlerSendGroupMessageResult(SendGroupMessageResult sendResult) {
        if (sendResult.statusCode == 200) {
            makeShortToast(ResourceUtils.getString(R.string.send_group_message_success));
            this.finish();
        } else {
            makeShortToast(sendResult.msg);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGroupSaveEditSubscription, mGetGroupInfoSubscription,mGetGroupTagListSubscription,
                 mGetCouponListSubscription, mGroupMessageAlbumUploadSubscription,mSendGroupMessageResultSubscription);
    }

    public void gotoEditContentFragment() {
        showFragment(EDIT_CONTENT_FRAGMENT);
        //setFragment(mEditContentFragment);
    }

    public void gotoCustomerFragment() {
        showFragment(CUSTOMER_FRAGMENT);
        //setFragment(mCustomerFragment);
    }

    public void gotoCouponFragment() {
        showFragment(COUPON_FRAGMENT);
        //setFragment(mCouponFragment);
    }

    public void gotoConfirmFragment() {
        showFragment(CONFIRM_FRAGMENT);
        //setFragment(mCouponFragment);
    }

    public void sendGroupMessage() {
        FavourableActivityBean couponInfo = mCouponFragment.getCouponInfo();
        String selectedCouponActId;
        String selectedCouponName;
        String couponContent;
        String currentMessageType;
        if (couponInfo == null) {
            selectedCouponActId = "-1";
            selectedCouponName = "";
            couponContent = "";
            currentMessageType = "";
        }else {
            selectedCouponActId = couponInfo.actId;
            selectedCouponName = couponInfo.actName;
            couponContent = couponInfo.msg;
            currentMessageType = couponInfo.msgType;
        }

        if (Utils.isEmpty(imageId)) {
            imageId = "";
        }

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_GROUP_ACT_ID, selectedCouponActId);
        params.put(RequestConstant.KEY_GROUP_ACT_NAME, selectedCouponName);
        params.put(RequestConstant.KEY_GROUP_COUPON_CONTENT, couponContent);
        params.put(RequestConstant.KEY_GROUP_MESSAGE_TYEP, currentMessageType);
        params.put(RequestConstant.KEY_GROUP_IMAGE_ID, imageId);
        params.put(RequestConstant.KEY_GROUP_MESSAGE_CONTENT, mEditContentFragment.getMessageContent());
        params.put(RequestConstant.KEY_GROUP_USER_GROUP_TYPE, mCustomerFragment.getSelectCustomerGroupType());
        params.put(RequestConstant.KEY_GROUP_IDS, mCustomerFragment.getSelectCustomerGroupIds());
        params.put(RequestConstant.KEY_GROUP_SUB_GROUP_LABELS, mCustomerFragment.getSelectCustomerGroupNames());
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GROUP_MESSAGE_SEND, params);
    }

    public void send(){
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> {
            if (mLimitAmount > 0) {
                if (Utils.isEmpty(mEditContentFragment.getImageUrl())) {
                    sendGroupMessage();
                } else {
                    try {

                        if (FileSizeUtil.getFileOrFilesSize(mEditContentFragment.getImageUrl(), FileSizeUtil.SIZE_TYPE_KB) > 1024 * mLimitImageSize) {
                            new AlertDialogBuilder(GroupMessageCustomerActivity.this).setTitle("温馨提示").setMessage("图片大小超过" + mLimitImageSize + "M,继续发送将压缩该图片发送。是否确认发送？").setCancelable(true).setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).setPositiveButton("发送", v -> {
                                mDialog = new LoadingDialog(GroupMessageCustomerActivity.this);
                                mDialog.show("正在上传图片");
                                try {
                                    uploadImage(mEditContentFragment.getImageUrl());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    makeShortToast("图片上传失败，请重新选择");
                                    mDialog.dismiss();
                                    e.printStackTrace();
                                }
                            }).show();
                        } else {
                            mDialog = new LoadingDialog(GroupMessageCustomerActivity.this);
                            mDialog.show("正在上传图片");
                            uploadImage(mEditContentFragment.getImageUrl());
                        }

                    } catch (IOException e) {
                        makeShortToast("图片上传失败，请重新选择");
                        mDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            } else {
                Utils.makeShortToast(GroupMessageCustomerActivity.this, ResourceUtils.getString(R.string.send_group_message_alert_enough));
            }
        });
    }

    private void uploadImage(String imageUrl) throws IOException {
        String base = ImageLoader.encodeFileToBase64(imageUrl);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_DO_GROUP_MESSAGE_ALBUM_UPLOAD, base);
    }
}
