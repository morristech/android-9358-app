package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.Constants;
import com.xmd.chat.event.ChatUmengStatisticsEvent;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.InvitationRewardBean;
import com.xmd.technician.common.DateUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.gson.InvitationRewardResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.EmptyView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 2017/12/20.
 */

public class InvitationRewardActivityListFragment extends BaseFragment {
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    @BindView(R.id.invitation_reward_name)
    TextView invitationRewardName;
    @BindView(R.id.invitation_reward_time)
    TextView invitationRewardTime;

    private Subscription mInvitationRewardActivitySubscription;
    private InvitationRewardBean rewardBean;

    public static InvitationRewardActivityListFragment getInstance() {
        InvitationRewardActivityListFragment rf = new InvitationRewardActivityListFragment();
        return rf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitation_reward_activity, container, false);
        ButterKnife.bind(this, view);
        mInvitationRewardActivitySubscription = RxBus.getInstance().toObservable(InvitationRewardResult.class).subscribe(
                result -> handleRewardListResult(result));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_INVITATION_REWARD_ACTIVITY_LIST);
        return view;
    }


    private void handleRewardListResult(InvitationRewardResult result) {
        if (result.statusCode == 200) {
            if (result.respData == null) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                emptyView.setEmptyViewWithDescription(R.drawable.ic_failed, "活动已过期或已下线");
            } else {
                rewardBean = result.respData;
                invitationRewardName.setText(rewardBean.activityName);
                if (rewardBean.startTime > 0 && rewardBean.endTime > 0) {
                    invitationRewardTime.setText(String.format("活动时间：%s-%s", DateUtil.long2Date(rewardBean.startTime, "yyyy.MM.dd"), DateUtil.long2Date(rewardBean.endTime, "yyyy.MM.dd")));
                } else {
                    invitationRewardTime.setText("活动时间：不限");
                }
            }
        } else {
            XToast.show(result.msg);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mInvitationRewardActivitySubscription);
    }

    @OnClick(R.id.invitation_reward_share)
    public void onViewClicked() {
        EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_INVITATION));
        if (rewardBean != null) {
            ShareController.doShare(rewardBean.registerPrize.imageUrl, rewardBean.activityLink, rewardBean.activityName,
                    ResourceUtils.getString(R.string.invitation_reward_share_description), Constant.SHARE_TYPE_INVITATION_REWARD_ACTIVITY, rewardBean.activityId);
        }

    }
}
