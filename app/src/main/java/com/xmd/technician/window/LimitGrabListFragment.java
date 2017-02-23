package com.xmd.technician.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.exceptions.HyphenateException;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.LimitGrabBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.gson.LimitGrabResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.EmptyView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class LimitGrabListFragment extends BaseListFragment<LimitGrabBean> {

    @Bind(R.id.empty_view_widget)
    EmptyView mEmptyViewWidget;
    private Subscription mLimitGrabListSubscription;
    private int mTotalAmount;

    public static LimitGrabListFragment getInstance(int totalAmount) {
        LimitGrabListFragment lf = new LimitGrabListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT, totalAmount);
        lf.setArguments(bundle);
        return lf;
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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST_DETAIL);
    }

    @Override
    protected void initView() {
        mLimitGrabListSubscription = RxBus.getInstance().toObservable(LimitGrabResult.class).subscribe(
                limitGrabResult -> handleLimitGrabResult(limitGrabResult)
        );
    }

    private void handleLimitGrabResult(LimitGrabResult limitGrabResult) {
        if (limitGrabResult.statusCode == 200) {
            if (limitGrabResult.respData == null) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mEmptyViewWidget.setEmptyViewWithDescription(R.drawable.ic_failed, "活动已下线");
            } else {
                if (limitGrabResult.respData.size() != mTotalAmount) {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                }
                onGetListSucceeded(1, limitGrabResult.respData);
            }
        } else {
            onGetListFailed(limitGrabResult.msg);
        }
    }

    @Override
    public void onShareClicked(LimitGrabBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.image, bean.shareUrl, SharedPreferenceHelper.getUserClubName() + "-" + bean.itemName+"限时抢购就等你来",
                ResourceUtils.getString(R.string.limit_grab_share_description), Constant.SHARE_COUPON, "");
    }

    @Override
    public void onItemClicked(LimitGrabBean bean) throws HyphenateException {
        super.onItemClicked(bean);
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mLimitGrabListSubscription);
        ButterKnife.unbind(this);
    }


}
