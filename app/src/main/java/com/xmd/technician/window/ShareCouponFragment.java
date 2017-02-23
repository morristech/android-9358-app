package com.xmd.technician.window;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.gson.ActivityListResult;
import com.xmd.technician.http.gson.CardShareListResult;
import com.xmd.technician.http.gson.PropagandaListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;


/**
 * Created by Lhj on 2017/1/22.
 */

public class ShareCouponFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.toolbar_back)
    ImageView mToolbarBack;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.pay_tickets_name)
    TextView mPayTicketsName;
    @Bind(R.id.pay_tickets_title)
    TextView mPayTicketsTitle;
    @Bind(R.id.rl_paid_coupon)
    RelativeLayout mRlPaidCoupon;
    @Bind(R.id.coupon_name)
    TextView mCouponName;
    @Bind(R.id.coupon_title)
    TextView mCouponTitle;
    @Bind(R.id.rl_normal_coupon)
    RelativeLayout mRlNormalCoupon;
    @Bind(R.id.once_card_name)
    TextView mOnceCardName;
    @Bind(R.id.once_card_title)
    TextView mOnceCardTitle;
    @Bind(R.id.rl_once_card)
    RelativeLayout mRlOnceCard;
    @Bind(R.id.layout_coupon)
    LinearLayout mLayoutCoupon;
    @Bind(R.id.limit_name)
    TextView mLimitName;
    @Bind(R.id.limit_grab_total)
    TextView mLimitGrabTotal;
    @Bind(R.id.rl_limit_grab)
    RelativeLayout mRlLimitGrab;
    @Bind(R.id.pay_for_me_name)
    TextView mPayForMeName;
    @Bind(R.id.pay_for_me_total)
    TextView mPayForMeTotal;
    @Bind(R.id.rl_pay_for_me)
    RelativeLayout mRlPayForMe;
    @Bind(R.id.reward_name)
    TextView mRewardName;
    @Bind(R.id.reward_total)
    TextView mRewardTotal;
    @Bind(R.id.rl_reward)
    RelativeLayout mRlReward;
    @Bind(R.id.layout_activity)
    LinearLayout mLayoutActivity;
    @Bind(R.id.publication_name)
    TextView mPublicationName;
    @Bind(R.id.publication_total)
    TextView mPublicationTotal;
    @Bind(R.id.rl_publication)
    RelativeLayout mRlPublication;
    @Bind(R.id.layout_publicity)
    LinearLayout mLayoutPublicity;
    @Bind(R.id.tv_bottom_text)
    TextView mTvBottomText;
    @Bind(R.id.sv_share_view)
    ScrollView mSvShareView;
    @Bind(R.id.share_empty)
    EmptyView mShareEmpty;

    private Subscription mShareCouponViewSubscription;
    private Subscription mShareActivityViewSubscription;
    private Subscription mSharePropagandaViewSubscription;
    private List<String> mCards;
    private List<String> mAction;
    private int mPaidAmount, mNormalCouponAmount, mOnceCardAmount, mLimitGrabAmount, mPayForMeAmount, mClubJournalAmount, mRewardActivityAmount;
    private boolean mCardIsNull, mActivityIsNull, mPropagandaIsNull;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_coupon, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        initTitleView(ResourceUtils.getString(R.string.copuon_fragment_title));
        mShareCouponViewSubscription = RxBus.getInstance().toObservable(CardShareListResult.class).subscribe(
                cardShareListResult -> handleCardList(cardShareListResult)
        );
        mShareActivityViewSubscription = RxBus.getInstance().toObservable(ActivityListResult.class).subscribe(
                activityListResult -> handleActivityList(activityListResult)
        );
        mSharePropagandaViewSubscription = RxBus.getInstance().toObservable(PropagandaListResult.class).subscribe(
                propagandaListResult -> handlePropagandaList(propagandaListResult)
        );
        mSwipeRefreshLayout.setColorSchemeColors(ResourceUtils.getColor(R.color.colorMainBtn));
        onRefresh();
        mSwipeRefreshLayout.setOnRefreshListener(this);


    }

    @Override
    public void onRefresh() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CARD_SHARE_LIST);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PROPAGANDA_LIST);
    }

    private void handleCardList(CardShareListResult cardShareListResult) {

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
                            mOnceCardName.setText(cardShareListResult.respData.get(i).couponName);
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



    }


    @OnClick({R.id.rl_paid_coupon, R.id.rl_normal_coupon, R.id.rl_once_card, R.id.rl_limit_grab, R.id.rl_pay_for_me, R.id.rl_reward, R.id.rl_publication})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_paid_coupon:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.PAID_COUPON, mPayTicketsName.getText().toString(), mPaidAmount);
                break;
            case R.id.rl_normal_coupon:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.NORMAL_COUPON, mCouponName.getText().toString(), mNormalCouponAmount);
                break;
            case R.id.rl_once_card:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.ONCE_CARD, mOnceCardName.getText().toString(), mOnceCardAmount);
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
            case R.id.rl_publication:
                ShareDetailListActivity.startShareDetailListActivity(getActivity(), ShareDetailListActivity.CLUB_JOURNAL, mPublicationName.getText().toString(), mClubJournalAmount);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        RxBus.getInstance().unsubscribe(mShareActivityViewSubscription, mShareCouponViewSubscription, mSharePropagandaViewSubscription);
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
    }

    private void setViewSate() {
        if (mCardIsNull && mActivityIsNull && mPropagandaIsNull) {
            mSvShareView.setVisibility(View.GONE);
            mShareEmpty.setStatus(EmptyView.Status.Empty);
            mShareEmpty.setEmptyPic(R.drawable.ic_failed);
            mShareEmpty.setEmptyTip("暂无数据，下拉刷新试试");
        } else {
            if (mShareEmpty.getVisibility() == View.VISIBLE) {
                mShareEmpty.setVisibility(View.GONE);
            }
            if (mSvShareView.getVisibility() == View.GONE) {
                mSvShareView.setVisibility(View.VISIBLE);
            }

        }
    }


}
