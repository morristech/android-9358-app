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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.CreditStatusResult;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
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

import java.util.HashMap;
import java.util.Map;

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
    @Bind(R.id.total_credit)TextView mCredit;
    @Bind(R.id.credit_center)RelativeLayout mCreditCenter;

    private Subscription mTechInfoSubscription;
    private Subscription mCommentOrderSubscription;
    private Subscription mSubmitInviteSubscription;
    private Subscription mCreditStatusSubscription;

    private TechSummaryInfo mTechInfo;
    private QRDialog mQRDialog;
    private  String mClubId;
    private Boolean isCreditCanExchange =false;
    private Map<String,String> param = new HashMap<>();

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
        RxBus.getInstance().unsubscribe(mTechInfoSubscription,mCommentOrderSubscription,mSubmitInviteSubscription,mCreditStatusSubscription);
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
        if(!Constant.TECH_STATUS_UNCERT.equals(mTechInfo.status)){
            mClubName.setText(mTechInfo.clubName);
        }
        mClubId = mTechInfo.clubId;
        Glide.with(this).load(mTechInfo.imageUrl).into(mAvatar);
        mCreditStatusSubscription = RxBus.getInstance().toObservable(CreditStatusResult.class).subscribe(
                result -> {
                    if(result.statusCode==200){
                        if(Utils.isNotEmpty(result.respData.systemSwitch)&&result.respData.systemSwitch.equals("on")){
                            mCreditCenter.setVisibility(View.VISIBLE);
                            if(result.respData.clubSwitch.equals("on")){
                                isCreditCanExchange = true;
                            }
                        }else{
                            mCreditCenter.setVisibility(View.GONE);
                        }

                    }
                }

        );

    }

    private void handleTechCurrentResult(TechCurrentResult result){
        if(result.respData != null){
            mTechInfo = result.respData;
            /*SharedPreferenceHelper.setUserName(mTechInfo.userName);
            SharedPreferenceHelper.setUserAvatar(mTechInfo.imageUrl);*/
            UserProfileProvider.getInstance().updateCurrentUserInfo(mTechInfo.userName, mTechInfo.imageUrl);
            if(Utils.isNotEmpty(result.respData.clubId)){
                SharedPreferenceHelper.setUserClubId(result.respData.clubId);
                param.put(RequestConstant.KEY_USER_CLUB_ID,result.respData.clubId);
                //      MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SWITCH_STATUS,param);
            }
            initView();
        }
    }

    private void handleCommentOrderRedPk(CommentOrderRedPkResutlt result){
        if(result.respData != null){
            if(result.respData.accountAmount > 0){
                mAccountMoney.setText(String.format("%1.2f元", result.respData.accountAmount));
            }else {
                mAccountMoney.setText("");
            }
            if(Utils.isNotEmpty(String.valueOf(result.respData.credits))&&result.respData.credits > 0){
                mCredit.setText(String.format("(%d)",result.respData.credits));
            }else {
                mCredit.setText("");
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

            if(Constant.TECH_STATUS_VALID.equals(result.respData.techStatus)||Constant.TECH_STATUS_REJECT.equals(result.respData.techStatus)){
                mClubName.setVisibility(View.GONE);
                mInviteBtn.setVisibility(View.VISIBLE);
            }else{
                mClubName.setVisibility(View.VISIBLE);
                mInviteBtn.setVisibility(View.GONE);

                if(Constant.TECH_STATUS_UNCERT.equals(result.respData.techStatus)){
                    mClubName.setText(result.respData.techStatusDesc);
                }
            }

            if(mTechInfo != null) mTechInfo.status = result.respData.techStatus;

            if(Constant.TECH_STATUS_BUSY.equals(result.respData.techStatus)){
                mWorkStatus.setImageResource(R.drawable.icon_busy);
            }else {
                mWorkStatus.setImageResource(R.drawable.icon_free);
            }
        }
    }
    private void handlerCreditStatus(CreditStatusResult result){

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
    @OnClick(R.id.credit_center)
    public void onCreditCenterClick(){
        if(isCreditCanExchange){
            Intent intent = new Intent(getActivity(),UserCreditCenterActivity.class);
            startActivity(intent);
        }else{
            ((BaseFragmentActivity)getActivity()).makeShortToast(getString(R.string.personal_fragment_status_check));
        }
    }


    @OnClick(R.id.time_item)
    public void onWorkTimeItemClick(){
        if(mTechInfo == null) return;
        if(Constant.TECH_STATUS_VALID.equals(mTechInfo.status)||Constant.TECH_STATUS_REJECT.equals(mTechInfo.status)){
            ((BaseFragmentActivity)getActivity()).makeShortToast(getString(R.string.personal_fragment_join_club));
            return;
        }else if(Constant.TECH_STATUS_UNCERT.equals(mTechInfo.status)){
            ((BaseFragmentActivity)getActivity()).makeShortToast(getString(R.string.personal_fragment_wait_check));
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
        if(mTechInfo != null){
            intent.putExtra(SettingActivity.EXTRA_JIONED_CLUB, !Constant.TECH_STATUS_VALID.equals(mTechInfo.status)&&!Constant.TECH_STATUS_REJECT.equals(mTechInfo.status));
        }
        startActivity(intent);
    }

    @OnClick(R.id.user_card_share)
    public void shareUserCard(){
        if(mTechInfo == null || TextUtils.isEmpty(mTechInfo.qrCodeUrl)) {
            return;
        }
        boolean canShare = true;
        if(Constant.TECH_STATUS_VALID.equals(mTechInfo.status)||Constant.TECH_STATUS_REJECT.equals(mTechInfo.status) || Constant.TECH_STATUS_UNCERT.equals(mTechInfo.status)){
            canShare = false;
        }
        if(Utils.isNotEmpty(mTechInfo.clubId)){
            Intent intent = new Intent(getActivity(),shareCardActivity.class);
            StringBuilder url = new StringBuilder(SharedPreferenceHelper.getServerHost());
            url.append(String.format("/spa-manager/spa2/?club=%s#technicianDetail&id=%s&techInviteCode=%s", mTechInfo.clubId, mTechInfo.id,mTechInfo.inviteCode));
            intent.putExtra(Constant.TECH_USER_HEAD_URL,mTechInfo.imageUrl);
            intent.putExtra(Constant.TECH_USER_NAME,mTechInfo.userName);
            intent.putExtra(Constant.TECH_USER_TECH_NUM,mTechInfo.serialNo);
            intent.putExtra(Constant.TECH_USER_CLUB_NAME,mTechInfo.clubName);
            intent.putExtra(Constant.TECH_SHARE_URL,url.toString());
            intent.putExtra(Constant.TECH_ShARE_CODE_IMG,mTechInfo.qrCodeUrl);
            intent.putExtra(Constant.TECH_CAN_SHARE,canShare);

            startActivity(intent);
        }else {
            ((BaseFragmentActivity)getActivity()).makeShortToast(getString(R.string.personal_fragment_join_club));
        }

    }

    @OnClick(R.id.user_center_ranking)
    public void showRankingView(){
        if(TextUtils.isEmpty(mClubId)){
            ((BaseFragmentActivity)getActivity()).makeShortToast(getString(R.string.personal_fragment_join_club));
            return;
        }else{
            String url = SharedPreferenceHelper.getServerHost()+String.format(RequestConstant.URL_RANKING,System.currentTimeMillis(), RequestConstant.USER_TYPE_TECH,
                    RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken()
            );
            Intent intent = new Intent(getActivity(),BrowserActivity.class);
            intent.putExtra(BrowserActivity.EXTRA_SHOW_MENU,false);
            intent.putExtra(BrowserActivity.EXTRA_URL,url);
            startActivity(intent);
        }


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
