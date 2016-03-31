package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.CommentOrderRedPkResutlt;
import com.xmd.technician.http.gson.TechCurrentResult;
import com.xmd.technician.model.TechSummaryInfo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.QRDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by sdcm on 16-3-24.
 */
public class PersonalFragment extends BaseFragment{

    @Bind(R.id.avatar) CircleImageView mAvatar;
    @Bind(R.id.description) TextView mDescription;
    @Bind(R.id.user_name) TextView mTechName;
    @Bind(R.id.club_name) TextView mClubName;
    @Bind(R.id.status) ImageView mWorkStatus;

    private Subscription mTechInfoSubscription;
    private Subscription mCommentOrderSubscription;
    private TechSummaryInfo mTechInfo;
    private QRDialog mQRDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.personal_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        ((TextView)getView().findViewById(R.id.toolbar_title)).setText(R.string.personal_fragment_title);

        mTechInfoSubscription = RxBus.getInstance().toObservable(TechCurrentResult.class).subscribe(
                techCurrentResult -> handleTechCurrentResult(techCurrentResult));

        mCommentOrderSubscription = RxBus.getInstance().toObservable(CommentOrderRedPkResutlt.class).subscribe(
                commentOrderRedPkResult -> handleCommentOrderRedPk(commentOrderRedPkResult));
    }

    @Override
    public void onResume() {
        super.onResume();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_CURRENT_INFO);
        //MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mTechInfoSubscription);
        RxBus.getInstance().unsubscribe(mCommentOrderSubscription);
    }

    private void initView(){
        mDescription.setText(mTechInfo.description);
        mTechName.setText(mTechInfo.userName);

        Glide.with(this).load(mTechInfo.imageUrl).into(mAvatar);
    }

    private void handleTechCurrentResult(TechCurrentResult result){
        if(result.respData != null){
            mTechInfo = result.respData;
            initView();
        }
    }

    private void handleCommentOrderRedPk(CommentOrderRedPkResutlt commentOrderRedPkResutlt){

    }

    @OnClick(R.id.info_item)
    public void onPersonInfoItemClick(){
        Intent intent = new Intent(getActivity(), TechInfoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.account_item)
    public void onAccountItemClick(){
        Intent intent = new Intent(getActivity(), MyAccountActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.time_item)
    public void onWorkTimeItemClick(){
        Intent intent = new Intent(getActivity(), WorkTimeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.service_item)
    public void onServiceItemClick(){
        Intent intent = new Intent(getActivity(), TechInfoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.comment_item)
    public void onCommentItemClick(){
        Intent intent = new Intent(getActivity(), CommentActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.setting_item)
    public void onSettingItemClick(){
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.qrcode)
    public void showQR(){
        if(mTechInfo == null || TextUtils.isEmpty(mTechInfo.qrCodeUrl)) {
            return;
        }

        if (mQRDialog == null) {
            mQRDialog = new QRDialog(getActivity(), mTechInfo.qrCodeUrl);
        } else {
            mQRDialog.updateQR(mTechInfo.qrCodeUrl);
        }
        mQRDialog.show();
    }
}
