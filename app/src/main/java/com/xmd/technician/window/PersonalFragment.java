package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.Logger;
import com.xmd.technician.http.gson.CommentOrderRedPkResutlt;
import com.xmd.technician.http.gson.InviteCodeResult;
import com.xmd.technician.http.gson.TechCurrentResult;
import com.xmd.technician.bean.TechSummaryInfo;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.InviteDialog;
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
    @Bind(R.id.invite_btn) Button mInviteBtn;
    @Bind(R.id.status) ImageView mWorkStatus;
    @Bind(R.id.account_balance) TextView mAccountMoney;
    @Bind(R.id.unread_count) TextView mUnreadCommentCount;
    @Bind(R.id.comment_count) TextView mCommentCount;
    @Bind(R.id.description_line) View mDesLine;

    private Subscription mTechInfoSubscription;
    private Subscription mCommentOrderSubscription;
    private Subscription mSubmitInviteSubscription;

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

        mSubmitInviteSubscription = RxBus.getInstance().toObservable(InviteCodeResult.class).subscribe(inviteCodeResult -> submitInviteResult(inviteCodeResult));
    }

    @Override
    public void onResume() {
        super.onResume();
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_TECH_CURRENT_INFO);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mTechInfoSubscription,mCommentOrderSubscription,mSubmitInviteSubscription);
    }

    private void initView(){
        if(TextUtils.isEmpty(mTechInfo.description)){
            mDescription.setVisibility(View.GONE);
            mDesLine.setVisibility(View.INVISIBLE);
        }else {
            mDescription.setVisibility(View.VISIBLE);
            mDesLine.setVisibility(View.VISIBLE);
            mDescription.setText(mTechInfo.description);
        }

        mTechName.setText(mTechInfo.userName);
        if(!mTechInfo.status.equals("uncert")){
            mClubName.setText(mTechInfo.clubName);
        }
        Glide.with(this).load(mTechInfo.imageUrl).into(mAvatar);
    }

    private void handleTechCurrentResult(TechCurrentResult result){
        if(result.respData != null){
            mTechInfo = result.respData;
            SharedPreferenceHelper.setUserName(mTechInfo.userName);
            SharedPreferenceHelper.setUserAvatar(mTechInfo.imageUrl);
            SharedPreferenceHelper.setSerialNo(mTechInfo.serialNo);
            initView();
        }
    }

    private void handleCommentOrderRedPk(CommentOrderRedPkResutlt result){
        if(result.respData != null){
            if(result.respData.accountAmount > 0){
                mAccountMoney.setText(String.format("%d元", result.respData.accountAmount));
            }else {
                mAccountMoney.setText("");
            }

            if(result.respData.unreadCommentCount > 0){
                mUnreadCommentCount.setText(String.format("+%d",result.respData.unreadCommentCount));
            }else {
                mUnreadCommentCount.setText("");
            }

            if(result.respData.allCommentCount > 0){
                mCommentCount.setText(String.format("(%d)",result.respData.allCommentCount));
            }else {
                mCommentCount.setText("");
            }

            if(result.respData.techStatus.equals("valid")||result.respData.techStatus.equals("reject")){
                mClubName.setVisibility(View.GONE);
                mInviteBtn.setVisibility(View.VISIBLE);
            }else{
                mClubName.setVisibility(View.VISIBLE);
                mInviteBtn.setVisibility(View.GONE);

                if(result.respData.techStatus.equals("uncert")){
                    mClubName.setText(result.respData.techStatusDesc);
                }
            }

            if(mTechInfo != null) mTechInfo.status = result.respData.techStatus;

            if(result.respData.techStatus.equals("busy")){
                mWorkStatus.setImageResource(R.drawable.icon_busy);
            }else {
                mWorkStatus.setImageResource(R.drawable.icon_free);
            }
        }
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
        if(mTechInfo == null) return;
        if(mTechInfo.status.equals("valid")||mTechInfo.status.equals("reject")){
            ((BaseActivity)getActivity()).makeShortToast(getString(R.string.personal_fragment_join_club));
            return;
        }else if(mTechInfo.status.equals("uncert")){
            ((BaseActivity)getActivity()).makeShortToast(getString(R.string.personal_fragment_wait_check));
            return;
        }

        Intent intent = new Intent(getActivity(), WorkTimeActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.service_item)
    public void onServiceItemClick(){
        Intent intent = new Intent(getActivity(), ServiceItemActivity.class);
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
            mQRDialog = new QRDialog(getActivity(), mTechInfo.qrCodeUrl, true);
        } else {
            mQRDialog.updateQR(mTechInfo.qrCodeUrl);
        }
        mQRDialog.show();
    }

    @OnClick(R.id.invite_btn)
    public void showInviteDialog(){
        new InviteDialog(getActivity(), R.style.default_dialog_style).show();
    }

    private void submitInviteResult(InviteCodeResult result){
        if(result.statusCode == 200){
            mTechInfo.clubId = result.id;
            mTechInfo.clubName = result.name;
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT);
        }
    }
}
