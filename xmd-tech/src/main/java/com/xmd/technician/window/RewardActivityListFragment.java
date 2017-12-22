package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.RewardBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.gson.RewardListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.EmptyView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class RewardActivityListFragment extends BaseListFragment<RewardBean> {


    @BindView(R.id.empty_view_widget)
    EmptyView mEmptyViewWidget;
    private Subscription mRewardActivityListSubscription;
    private int mTotalAmount;

    public static RewardActivityListFragment getInstance(int totalAmount) {
        RewardActivityListFragment rf = new RewardActivityListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT, totalAmount);
        rf.setArguments(bundle);
        return rf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTotalAmount = getArguments().getInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT);
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_REWARD_ACTIVITY_LIST);
    }

    @Override
    protected void initView() {
        mRewardActivityListSubscription = RxBus.getInstance().toObservable(RewardListResult.class).subscribe(
                rewardListResult -> handleRewardListResult(rewardListResult)
        );
    }

    private void handleRewardListResult(RewardListResult rewardListResult) {
        if (rewardListResult.statusCode == 200) {
            if (rewardListResult.respData == null || rewardListResult.respData.size() == 0) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mEmptyViewWidget.setEmptyViewWithDescription(R.drawable.ic_failed, "活动已过期或已下线");
            } else {
                if (rewardListResult.respData.size() != mTotalAmount) {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                }
                onGetListSucceeded(1, rewardListResult.respData);
            }
        } else {
            onGetListFailed(rewardListResult.msg);
        }
    }

    @Override
    public void onShareClicked(RewardBean bean) {
        super.onShareClicked(bean);

        ShareController.doShare(bean.image, bean.shareUrl, bean.actName,
                ResourceUtils.getString(R.string.reward_share_description), Constant.SHARE_TYPE_REWARD_ACTIVITY, "");
    }

    @Override
    public void onItemClicked(RewardBean bean) throws HyphenateException {
        super.onItemClicked(bean);
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mRewardActivityListSubscription);
    }
}
