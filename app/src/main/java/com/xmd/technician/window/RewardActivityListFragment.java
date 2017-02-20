package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.RewardBean;
import com.xmd.technician.http.gson.RewardListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;

import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class RewardActivityListFragment extends BaseListFragment<RewardBean> {


    private Subscription mRewardActivityListSubscription;

    public static RewardActivityListFragment getInstance(){
        return  new RewardActivityListFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
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
        if(rewardListResult.statusCode == 200){
            onGetListSucceeded(1,rewardListResult.respData);
        }else {
            onGetListFailed(rewardListResult.msg);
        }
    }

    @Override
    public void onShareClicked(RewardBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.image, bean.shareUrl, SharedPreferenceHelper.getUserClubName() + "-" + bean.actName,
                String.format("赶紧来抽%s啦",bean.firstPrizeName), Constant.SHARE_COUPON, "");
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
