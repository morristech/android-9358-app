package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ShareCouponBean;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.ShareCouponResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;
import com.xmd.technician.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class NormalCouponListFragment extends BaseListFragment<ShareCouponBean> {


    @Bind(R.id.empty_view_widget)
    EmptyView mEmptyViewWidget;
    private Subscription mNormalCouponListSubscription;
    private int mTotalAmount;

    public static NormalCouponListFragment getInstance(int totalAmount) {
        NormalCouponListFragment nf = new NormalCouponListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT, totalAmount);
        nf.setArguments(bundle);
        return nf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTotalAmount = getArguments().getInt(ShareDetailListActivity.SHARE_TOTAL_AMOUNT);
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        mEmptyViewWidget.setStatus(EmptyView.Status.Loading);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        params.put(RequestConstant.KEY_COUPON_TYPE,Constant.COUPON_TYPE);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CARD_LIST_DETAIL, params);
    }

    @Override
    protected void initView() {
        mNormalCouponListSubscription = RxBus.getInstance().toObservable(ShareCouponResult.class).subscribe(
                couponListResult -> handleCouponList(couponListResult)
        );
    }

    private void handleCouponList(ShareCouponResult couponListResult) {
        if (couponListResult.statusCode == 200) {
            if (couponListResult.respData == null) {
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CARD_SHARE_LIST);
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mEmptyViewWidget.setEmptyViewWithDescription(R.drawable.ic_failed, "优惠券已下线");
            } else {
                mEmptyViewWidget.setStatus(EmptyView.Status.Gone);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                if (couponListResult.respData.size() != mTotalAmount) {
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CARD_SHARE_LIST);
                }
                onGetListSucceeded(couponListResult.pageCount, couponListResult.respData);

            }
        } else {
            onGetListFailed(couponListResult.msg);
        }
    }

    @Override
    public void onShareClicked(ShareCouponBean bean) {
        super.onShareClicked(bean);
        ShareController.doShare("", bean.shareUrl, SharedPreferenceHelper.getUserClubName() + "-" + bean.actTitle,
                bean.consumeMoneyDescption + "，超值优惠，超值享受。快来约我。", Constant.SHARE_COUPON, bean.actId);
    }

    @Override
    public void onItemClicked(ShareCouponBean couponInfo) {
        if (!Constant.COUPON_TYPE_PAID.equals(couponInfo.couponType)) {
            Intent intent = new Intent(getActivity(), NormalCouponDetailActivity.class);
            intent.putExtra(Constant.PARAM_ACT_ID, couponInfo.actId);
            startActivity(intent);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mNormalCouponListSubscription);
        ButterKnife.unbind(this);
    }
}
