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
import com.xmd.technician.bean.PayForMeBean;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.gson.PayForMeListResult;
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

public class PayForMeListFragment extends BaseListFragment<PayForMeBean> {

    @Bind(R.id.empty_view_widget)
    EmptyView mEmptyViewWidget;
    private Subscription mPayForMeListSubscription;
    private int mTotalAmount;

    public static PayForMeListFragment getInstance(int totalAmount) {
        PayForMeListFragment pf = new PayForMeListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT, totalAmount);
        pf.setArguments(bundle);
        return pf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        mTotalAmount = getArguments().getInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PAY_FOR_ME_LIST_DETAIL);
    }

    @Override
    protected void initView() {
        mPayForMeListSubscription = RxBus.getInstance().toObservable(PayForMeListResult.class).subscribe(
                payForMeListResult -> handlePayForMeListResult(payForMeListResult)
        );
    }

    private void handlePayForMeListResult(PayForMeListResult payForMeListResult) {
        if (payForMeListResult.statusCode == 200) {
            if (payForMeListResult.respData == null) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mEmptyViewWidget.setEmptyViewWithDescription(R.drawable.ic_failed, "活动已下线");
            } else {
                if (payForMeListResult.respData.size() != mTotalAmount) {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_ACTIVITY_LIST);
                }
                onGetListSucceeded(1, payForMeListResult.respData);
            }
        } else {
            onGetListFailed(payForMeListResult.msg);
        }
    }

    @Override
    public void onShareClicked(PayForMeBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare(bean.image, bean.shareUrl, bean.actName,
                ResourceUtils.getString(R.string.pay_for_me_share_description), Constant.SHARE_COUPON, "");
    }

    @Override
    public void onItemClicked(PayForMeBean bean) throws HyphenateException {
        super.onItemClicked(bean);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mPayForMeListSubscription);
        ButterKnife.unbind(this);
    }

    @Override
    public boolean isPaged() {
        return false;

    }
}
