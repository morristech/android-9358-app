package com.xmd.technician.window;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.TechInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.gson.ActivityListResult;
import com.xmd.technician.http.gson.CardShareListResult;
import com.xmd.technician.http.gson.PropagandaListResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.TechPosterListResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;
import com.xmd.technician.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;


/**
 * Created by Lhj on 2017/1/22.
 */

public class ShareCouponFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar_back)
    ImageView mToolbarBack;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.pay_tickets_name)
    TextView mPayTicketsName;
    @BindView(R.id.pay_tickets_title)
    TextView mPayTicketsTitle;
    @BindView(R.id.rl_paid_coupon)
    RelativeLayout mRlPaidCoupon;
    @BindView(R.id.coupon_name)
    TextView mCouponName;
    @BindView(R.id.coupon_title)
    TextView mCouponTitle;
    @BindView(R.id.rl_normal_coupon)
    RelativeLayout mRlNormalCoupon;
    @BindView(R.id.once_card_name)
    TextView mOnceCardName;
    @BindView(R.id.once_card_title)
    TextView mOnceCardTitle;
    @BindView(R.id.rl_once_card)
    RelativeLayout mRlOnceCard;
    @BindView(R.id.layout_coupon)
    LinearLayout mLayoutCoupon;
    @BindView(R.id.limit_name)
    TextView mLimitName;
    @BindView(R.id.limit_grab_total)
    TextView mLimitGrabTotal;
    @BindView(R.id.rl_limit_grab)
    RelativeLayout mRlLimitGrab;
    @BindView(R.id.pay_for_me_name)
    TextView mPayForMeName;
    @BindView(R.id.pay_for_me_total)
    TextView mPayForMeTotal;
    @BindView(R.id.rl_pay_for_me)
    RelativeLayout mRlPayForMe;
    @BindView(R.id.reward_name)
    TextView mRewardName;
    @BindView(R.id.reward_total)
    TextView mRewardTotal;
    @BindView(R.id.rl_reward)
    RelativeLayout mRlReward;
    @BindView(R.id.layout_activity)
    LinearLayout mLayoutActivity;
    @BindView(R.id.publication_name)
    TextView mPublicationName;
    @BindView(R.id.publication_total)
    TextView mPublicationTotal;
    @BindView(R.id.rl_publication)
    RelativeLayout mRlPublication;
    @BindView(R.id.layout_publicity)
    LinearLayout mLayoutPublicity;
    @BindView(R.id.tv_bottom_text)
    TextView mTvBottomText;
    @BindView(R.id.sv_share_view)
    ScrollView mSvShareView;
    @BindView(R.id.share_empty)
    EmptyView mShareEmpty;
    @BindView(R.id.ll_share_view)
    LinearLayout mShareView;
    @BindView(R.id.ll_share_tech_card)
    LinearLayout mShareTechCard;
    @BindView(R.id.img_tech_head)
    RoundImageView mImgTechHead;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.tv_poster_total)
    TextView mTvPosterTotal;
    @BindView(R.id.invitation_reward_name)
    TextView mInvitationRewardName;
    @BindView(R.id.invitation_reward_total)
    TextView mInvitationRewardTotal;
    @BindView(R.id.rl_invitation_reward)
    RelativeLayout rlInvitationReward;
//    @BindView(R.id.btn_share_user_card)
//    Button mBtnShareUserCard;

    private Subscription mShareCouponViewSubscription;
    private Subscription mShareActivityViewSubscription;
    private Subscription mSharePropagandaViewSubscription;
    private Subscription mGetTechCurrentInfoSubscription;

    private List<String> mCards;
    private List<String> mAction;
    private int mPaidAmount, mNormalCouponAmount, mOnceCardAmount, mLimitGrabAmount, mPayForMeAmount, mClubJournalAmount, mRewardActivityAmount, mInvitationRewardActivityAmount;
    private boolean mCardIsNull, mActivityIsNull, mPropagandaIsNull;
    private boolean isFirst;
    private TechInfo mTechInfo;
    private Subscription mPosterListSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_coupon, container, false);
        ButterKnife.bind(this, view);
        isFirst = true;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        initTitleView("营销");

        mGetTechCurrentInfoSubscription = RxBus.getInstance().toObservable(TechInfoResult.class).subscribe(
                techCurrentResult -> handleTechCurrentResult(techCurrentResult));
        mShareCouponViewSubscription = RxBus.getInstance().toObservable(CardShareListResult.class).subscribe(
                cardShareListResult -> handleCardList(cardShareListResult)
        );
        mShareActivityViewSubscription = RxBus.getInstance().toObservable(ActivityListResult.class).subscribe(
                activityListResult -> handleActivityList(activityListResult)
        );
        mSharePropagandaViewSubscription = RxBus.getInstance().toObservable(PropagandaListResult.class).subscribe(
                propagandaListResult -> handlePropagandaList(propagandaListResult)
        );
        mPosterListSubscription = RxBus.getInstance().toObservable(TechPosterListResult.class).subscribe(
                result -> handlerTechPosterListResult(result)
        );
        LoginTechnician.getInstance().loadTechInfo();
        mSwipeRefreshLayout.setColorSchemeColors(ResourceUtils.getColor(R.color.color_main_btn));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_POSTER_LIST);
        onRefresh();

    }

    private void handleTechCurrentResult(TechInfoResult techCurrentResult) {
        if (techCurrentResult.respData != null) {
            Glide.with(getActivity()).load(techCurrentResult.respData.imageUrl).error(R.drawable.img_default_square).into(mImgTechHead);
            String userInfo;
            if (Utils.isNotEmpty(techCurrentResult.respData.serialNo)) {
                userInfo = techCurrentResult.respData.userName + "[" + techCurrentResult.respData.serialNo + "]";
                mUserName.setText(Utils.changeColor(userInfo, ResourceUtils.getColor(R.color.contact_marker), SharedPreferenceHelper.getUserName().length() + 1, userInfo.length() - 1));
            } else {
                userInfo = techCurrentResult.respData.userName;
                mUserName.setText(userInfo);
            }
            mTechInfo = techCurrentResult.respData;
            showAvatarInTitleBar(mTechInfo.imageUrl);
        }
    }

    private void handlerTechPosterListResult(TechPosterListResult result) {
        if (result.statusCode == 200) {
            if (null == result.respData || result.respData.list.size() == 0) {
                mTvPosterTotal.setText("0");
            } else {
                mTvPosterTotal.setText(String.valueOf(result.respData.list.size()));
            }
        }
    }


    @Override
    public void onRefresh() {
        if (isFirst) {
            isFirst = false;
        } else {
            mShareTechCard.setVisibility(View.VISIBLE);
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CARD_SHARE_LIST);
    }

    private void handleCardList(CardShareListResult cardShareListResult) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (cardShareListResult.statusCode == 200) {
            if (cardShareListResult.respData == null || cardShareListResult.respData.size() == 0) {
                mCardIsNull = true;
                mLayoutCoupon.setVisibility(View.GONE);
            } else if (cardShareListResult.respData.size() > 0) {
                mCardIsNull = false;
                mLayoutCoupon.setVisibility(View.VISIBLE);
                setCardViewSate(cardShareListResult);
                for (int i = 0; i < cardShareListResult.respData.size(); i++) {
                    if (Integer.parseInt(cardShareListResult.respData.get(i).count) > 0) {
                        if (cardShareListResult.respData.get(i).couponType.equals(Constant.NORMAL_COUPON_TYPE)) {
                            mCouponName.setText(cardShareListResult.respData.get(i).couponName);
                            mCouponTitle.setText(cardShareListResult.respData.get(i).count);
                            mNormalCouponAmount = Integer.parseInt(cardShareListResult.respData.get(i).count);
                        } else if (cardShareListResult.respData.get(i).couponType.equals(Constant.PAID_TYPE)) {
                            mPayTicketsName.setText(cardShareListResult.respData.get(i).couponName);
                            mPayTicketsTitle.setText(cardShareListResult.respData.get(i).count);
                            mPaidAmount = Integer.parseInt(cardShareListResult.respData.get(i).count);
                        } else if (cardShareListResult.respData.get(i).couponType.equals(Constant.ONCE_TYPE)) {
                            String onceCardName = cardShareListResult.respData.get(i).couponName;
                            SpannableString spannableString = new SpannableString(onceCardName);
                            spannableString.setSpan(new TextAppearanceSpan(getActivity(), R.style.text_credit), 4, onceCardName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mOnceCardName.setText(spannableString);
                            mOnceCardTitle.setText(cardShareListResult.respData.get(i).count);
                            mOnceCardAmount = Integer.parseInt(cardShareListResult.respData.get(i).count);
                        }
                    }
                }
            }

        } else {
            mLayoutCoupon.setVisibility(View.GONE);
        }
    }

    private void handleActivityList(ActivityListResult activityListResult) {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PROPAGANDA_LIST);
        if (activityListResult.statusCode == 200) {
            if (activityListResult.respData == null || activityListResult.respData.size() == 0) {
                mActivityIsNull = true;
                mLayoutActivity.setVisibility(View.GONE);
            } else if (activityListResult.respData.size() > 0) {
                mActivityIsNull = false;
                mLayoutActivity.setVisibility(View.VISIBLE);
                setActivityViewSate(activityListResult);
                for (int i = 0; i < activityListResult.respData.size(); i++) {
                    if (Integer.parseInt(activityListResult.respData.get(i).count) > 0) {
                        if (activityListResult.respData.get(i).actType.equals(Constant.ONE_YUAN_TYPE)) {
                            mPayForMeName.setText(activityListResult.respData.get(i).actName);
                            mPayForMeTotal.setText(activityListResult.respData.get(i).count);
                            mPayForMeAmount = Integer.parseInt(activityListResult.respData.get(i).count);
                        } else if (activityListResult.respData.get(i).actType.equals(Constant.PAID_ITEM_TYPE)) {
                            mLimitName.setText(activityListResult.respData.get(i).actName);
                            mLimitGrabTotal.setText(activityListResult.respData.get(i).count);
                            mLimitGrabAmount = Integer.parseInt(activityListResult.respData.get(i).count);
                        } else if (activityListResult.respData.get(i).actType.equals(Constant.DRAW_TYPE)) {
                            mRewardName.setText(activityListResult.respData.get(i).actName);
                            mRewardTotal.setText(activityListResult.respData.get(i).count);
                            mRewardActivityAmount = Integer.parseInt(activityListResult.respData.get(i).count);
                            mClubJournalAmount = Integer.parseInt(activityListResult.respData.get(i).count);
                        } else if (activityListResult.respData.get(i).actType.equals(Constant.INVITATION_TYPE)) {
                            mInvitationRewardName.setText(activityListResult.respData.get(i).actName);
                            mInvitationRewardTotal.setText(activityListResult.respData.get(i).count);
                        }

                    }
                }
            }

        } else {
            mLayoutCoupon.setVisibility(View.GONE);
        }

    }

    private void handlePropagandaList(PropagandaListResult propagandaListResult) {
        if (propagandaListResult.statusCode == 200) {
            if (propagandaListResult.respData == null || propagandaListResult.respData.size() == 0) {
                mLayoutPublicity.setVisibility(View.GONE);
                mPropagandaIsNull = true;
            } else if (propagandaListResult.respData.size() > 0) {
                mPropagandaIsNull = false;
                mLayoutPublicity.setVisibility(View.VISIBLE);
                for (int i = 0; i < propagandaListResult.respData.size(); i++) {
                    if (Integer.parseInt(propagandaListResult.respData.get(i).count) > 0) {
                        if (propagandaListResult.respData.get(i).proType.equals(Constant.JOURNAL_TYPE)) {
                            mRlPublication.setVisibility(View.VISIBLE);
                            mPublicationName.setText(propagandaListResult.respData.get(0).proName);
                            mPublicationTotal.setText(String.valueOf(propagandaListResult.respData.get(0).count));
                        }
                    }

                }
            }
        }
        updateViewSate();
    }


    @OnClick({R.id.rl_paid_coupon, R.id.rl_normal_coupon, R.id.rl_once_card, R.id.rl_limit_grab, R.id.rl_pay_for_me, R.id.rl_reward, R.id.rl_invitation_reward, R.id.rl_publication, R.id.ll_share_tech_card, R.id.ll_share_tech_poster})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_paid_coupon:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.PAID_COUPON, mPayTicketsName.getText().toString(), mPaidAmount);
                break;
            case R.id.rl_normal_coupon:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.NORMAL_COUPON, mCouponName.getText().toString(), mNormalCouponAmount);
                break;
            case R.id.rl_once_card:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.ONCE_CARD, "特惠商城", mOnceCardAmount);
                break;
            case R.id.rl_limit_grab:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.LIMIT_GRAB, mLimitName.getText().toString(), mLimitGrabAmount);
                break;
            case R.id.rl_pay_for_me:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.PAY_FOR_ME, mPayForMeName.getText().toString(), mPayForMeAmount);
                break;
            case R.id.rl_reward:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.REWARD_ACTIVITY, mRewardName.getText().toString(), mRewardActivityAmount);
                break;
            case R.id.rl_invitation_reward:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.INVITATION_REWARD_ACTIVITY, mInvitationRewardName.getText().toString(), mInvitationRewardActivityAmount);
                break;
            case R.id.rl_publication:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.CLUB_JOURNAL, mPublicationName.getText().toString(), mClubJournalAmount);
                break;

            case R.id.ll_share_tech_card:
                if (mTechInfo != null) {
                    if (TextUtils.isEmpty(mTechInfo.qrCodeUrl)) {
                        Toast.makeText(getContext(), "二维码为空！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Boolean canShare = true;
                    if (Constant.TECH_STATUS_VALID.equals(mTechInfo.status) || Constant.TECH_STATUS_REJECT.equals(mTechInfo.status) || Constant.TECH_STATUS_UNCERT.equals(mTechInfo.status)) {
                        canShare = false;
                    }
                    Intent intent = new Intent(getActivity(), TechShareCardActivity.class);
                    StringBuilder url;
                    if (Utils.isEmpty(mTechInfo.shareUrl)) {
                        url = new StringBuilder(SharedPreferenceHelper.getServerHost());
                        url.append(String.format("/spa-manager/spa2/?club=%s#technicianDetail&id=%s&techInviteCode=%s", mTechInfo.clubId, mTechInfo.id, mTechInfo.inviteCode));
                    } else {
                        url = new StringBuilder(mTechInfo.shareUrl);
                    }
                    intent.putExtra(Constant.TECH_USER_HEAD_URL, mTechInfo.imageUrl);
                    intent.putExtra(Constant.TECH_USER_NAME, mTechInfo.userName);
                    intent.putExtra(Constant.TECH_USER_TECH_NUM, mTechInfo.serialNo);
                    intent.putExtra(Constant.TECH_USER_CLUB_NAME, mTechInfo.clubName);
                    intent.putExtra(Constant.TECH_SHARE_URL, url.toString());
                    intent.putExtra(Constant.TECH_ShARE_CODE_IMG, mTechInfo.qrCodeUrl);
                    intent.putExtra(Constant.TECH_CAN_SHARE, canShare);
                    startActivity(intent);
                }
                break;
            case R.id.ll_share_tech_poster:
                Intent intentPoster = new Intent(getActivity(), TechPersonalPosterActivity.class);
                startActivity(intentPoster);
                break;


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mShareActivityViewSubscription, mShareCouponViewSubscription, mSharePropagandaViewSubscription, mGetTechCurrentInfoSubscription, mPosterListSubscription);
    }

    private void setCardViewSate(CardShareListResult cardShareListResult) {
        if (mCards == null) {
            mCards = new ArrayList<>();
        } else {
            mCards.clear();
        }
        for (int i = 0; i < cardShareListResult.respData.size(); i++) {
            mCards.add(cardShareListResult.respData.get(i).couponType);
        }
        if (mCards.contains(Constant.PAID_TYPE)) {
            mRlPaidCoupon.setVisibility(View.VISIBLE);
        } else {
            mRlPaidCoupon.setVisibility(View.GONE);
        }
        if (mCards.contains(Constant.NORMAL_COUPON_TYPE)) {
            mRlNormalCoupon.setVisibility(View.VISIBLE);
        } else {
            mRlNormalCoupon.setVisibility(View.GONE);
        }
        if (mCards.contains(Constant.ONCE_TYPE)) {
            mRlOnceCard.setVisibility(View.VISIBLE);
        } else {
            mRlOnceCard.setVisibility(View.GONE);
        }
    }

    private void setActivityViewSate(ActivityListResult activityListResult) {
        if (mAction == null) {
            mAction = new ArrayList<>();
        } else {
            mAction.clear();
        }
        for (int i = 0; i < activityListResult.respData.size(); i++) {
            mAction.add(activityListResult.respData.get(i).actType);
        }
        if (mAction.contains(Constant.ONE_YUAN_TYPE)) {
            mRlPayForMe.setVisibility(View.VISIBLE);
        } else {
            mRlPayForMe.setVisibility(View.GONE);
        }
        if (mAction.contains(Constant.PAID_ITEM_TYPE)) {
            mRlLimitGrab.setVisibility(View.VISIBLE);
        } else {
            mRlLimitGrab.setVisibility(View.GONE);
        }
        if (mAction.contains(Constant.DRAW_TYPE)) {
            mRlReward.setVisibility(View.VISIBLE);
        } else {
            mRlReward.setVisibility(View.GONE);
        }
        if (mAction.contains(Constant.INVITATION_TYPE)) {
            rlInvitationReward.setVisibility(View.VISIBLE);
        } else {
            rlInvitationReward.setVisibility(View.GONE);
        }

    }

    private void updateViewSate() {
        if (mCardIsNull && mActivityIsNull && mPropagandaIsNull) {
            mShareView.setVisibility(View.GONE);
            mShareEmpty.setStatus(EmptyView.Status.Empty);
            mShareEmpty.setEmptyPic(R.drawable.img_share_null);
            mShareEmpty.setEmptyTip("");
        } else {
            if (mShareEmpty.getVisibility() == View.VISIBLE) {
                mShareEmpty.setVisibility(View.GONE);
            }
            if (mShareView.getVisibility() == View.GONE) {
                mShareView.setVisibility(View.VISIBLE);
            }

        }
    }
}
