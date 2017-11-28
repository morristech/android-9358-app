package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.journal.activity.JournalListActivity;
import com.xmd.manager.journal.manager.JournalManager;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.MarketingResult;
import com.xmd.manager.service.response.SendGroupMessageResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Administrator on 2016/11/8.
 */
public class MarketingFragment extends BaseFragment {

    @BindView(R.id.marketing_journal_text)
    TextView marketingJournalText;
    @BindView(R.id.marketing_journal)
    RelativeLayout marketingJournal;
    @BindView(R.id.marketing_group_message_text)
    TextView marketingGroupMessageText;
    @BindView(R.id.marketing_group_message)
    RelativeLayout marketingGroupMessage;
    @BindView(R.id.marketing_reward_text)
    TextView marketingRewardText;
    @BindView(R.id.marketing_reward)
    RelativeLayout marketingReward;
    @BindView(R.id.marketing_deprive_text)
    TextView marketingDepriveText;
    @BindView(R.id.marketing_deprive)
    RelativeLayout marketingDeprive;
    @BindView(R.id.marketing_paid_coupon_text)
    TextView marketingPaidCouponText;
    @BindView(R.id.marketing_purchase_text)
    TextView marketingPurchaseText;
    @BindView(R.id.marketing_purchase)
    RelativeLayout marketingPurchase;
    @BindView(R.id.marketing_credit_shopping_text)
    TextView marketingCreditShoppingText;
    @BindView(R.id.marketing_credit_shopping)
    RelativeLayout marketingCreditShopping;
    @BindView(R.id.marketing_coupon_text)
    TextView marketingCouponText;
    @BindView(R.id.marketing_coupon)
    RelativeLayout marketingCoupon;
    @BindView(R.id.marketing_bottom_text)
    RelativeLayout marketingBottomText;
    @BindView(R.id.marketing_paid_coupon)
    RelativeLayout marketPaidCoupon;

    private Subscription mMarketingItemsSubscription;
    private Subscription mSendGroupMessageResultSubscription;
    private View view;
    private int mPublishedJournalCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marketing, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void initView() {
        view.findViewById(R.id.toolbar_left).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText(ResourceUtils.getString(R.string.home_activity_marketing));
        mMarketingItemsSubscription = RxBus.getInstance().toObservable(MarketingResult.class).subscribe(
                result -> handleMarketingItemsView(result)
        );

        mSendGroupMessageResultSubscription = RxBus.getInstance().toObservable(SendGroupMessageResult.class).subscribe(
                sendResult -> handlerSendGroupMessageResult(sendResult));

        updateData();
    }

    private void handlerSendGroupMessageResult(SendGroupMessageResult sendResult) {
        if (sendResult.statusCode == 200) {
            updateData();
        }
    }

    public void updateData() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_MARKETING_ITEMS);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPublishedJournalCount != JournalManager.getInstance().getPublishedSize()) {
            updateData();
        }
    }

    private void handleMarketingItemsView(MarketingResult result) {
        if (result.respData == null) {
            return;
        }
        if (Utils.isNotEmpty(String.valueOf(result.respData.value01))) {
            String journalText = "累计已发布 " + result.respData.value01 + " 篇";
            mPublishedJournalCount = result.respData.value01;
            marketingJournalText.setText(Utils.changeColor(journalText, ResourceUtils.getColor(R.color.btn_pressed_bg), 5, journalText.length() - 1));
        } else {
            marketingJournal.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(String.valueOf(result.respData.value02))) {
            String groupMessageText = "本月还可使用 " + result.respData.value02 + " 次";
            marketingGroupMessageText.setText(Utils.changeColor(groupMessageText, ResourceUtils.getColor(R.color.btn_pressed_bg), 6, groupMessageText.length() - 1));
        } else {
            marketingGroupMessage.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(String.valueOf(result.respData.value03))) {
            String rewardText = "当前有 " + result.respData.value03 + " 个活动正在进行中";
            marketingRewardText.setText(Utils.changeColor(rewardText, ResourceUtils.getColor(R.color.btn_pressed_bg), 3, rewardText.length() - 8));
        } else {
            marketingReward.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(String.valueOf(result.respData.value04))) {
            String purchaseText = "当前有 " + result.respData.value04 + " 个活动正在进行中";
            marketingPurchaseText.setText(Utils.changeColor(purchaseText, ResourceUtils.getColor(R.color.btn_pressed_bg), 3, purchaseText.length() - 8));
        } else {
            marketingPurchase.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(String.valueOf(result.respData.value05))) {
            String depriveText = "当前有 " + result.respData.value05 + " 个活动正在进行中";
            marketingDepriveText.setText(Utils.changeColor(depriveText, ResourceUtils.getColor(R.color.btn_pressed_bg), 3, depriveText.length() - 8));
        } else {
            marketingDeprive.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(String.valueOf(result.respData.value06))) {
            String creditShopping = "当前有 " + result.respData.value06 + " 种商品可兑换";
            marketingCreditShoppingText.setText(Utils.changeColor(creditShopping, ResourceUtils.getColor(R.color.btn_pressed_bg), 3, creditShopping.length() - 6));
        } else {
            marketingCreditShopping.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(String.valueOf(result.respData.value09))) {
            String couponText = "当前有 " + result.respData.value09 + " 种优惠券在线";
            marketingCouponText.setText(Utils.changeColor(couponText, ResourceUtils.getColor(R.color.btn_pressed_bg), 3, couponText.length() - 6));
        } else {
            marketingCoupon.setVisibility(View.GONE);
        }

        if (Utils.isNotEmpty(String.valueOf(result.respData.value10))) {
            String paidCouponText = "当前有 " + result.respData.value10 + " 种点钟券在线";
            marketingPaidCouponText.setText(Utils.changeColor(paidCouponText, ResourceUtils.getColor(R.color.btn_pressed_bg), 3, paidCouponText.length() - 6));
        } else {
            marketPaidCoupon.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.marketing_journal, R.id.marketing_group_message, R.id.marketing_reward, R.id.marketing_purchase, R.id.marketing_deprive, R.id.marketing_credit_shopping,
            R.id.marketing_coupon, R.id.marketing_paid_coupon})
    public void marketingItemClicked(View view) {
        switch (view.getId()) {
            case R.id.marketing_journal:
                startActivity(new Intent(getActivity(), JournalListActivity.class));
                break;
            case R.id.marketing_group_message:
                startActivity(new Intent(getActivity(), SendCouponDetailActivity.class));
                break;
            case R.id.marketing_reward:

                break;
            case R.id.marketing_purchase:

                break;
            case R.id.marketing_deprive:

                break;
            case R.id.marketing_credit_shopping:

                break;
            case R.id.marketing_paid_coupon:
                startActivity(new Intent(getActivity(), PaidCouponListActivity.class));
                break;
            case R.id.marketing_coupon:
                startActivity(new Intent(getActivity(), OperateCouponListActivity.class));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mMarketingItemsSubscription, mSendGroupMessageResultSubscription);
    }


}
