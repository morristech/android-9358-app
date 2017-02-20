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
import com.xmd.technician.http.gson.ShareCouponResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;

import rx.Subscription;

/**
 * Created by Lhj on 2017/2/9.
 */

public class PaidCouponListFragment extends BaseListFragment<ShareCouponBean> {

    private Subscription mGetPaidCouponListSubscription;

    public static PaidCouponListFragment getInstance() {
        return new PaidCouponListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CARD_LIST_DETAIL, Constant.PAID_TYPE);
    }

    @Override
    protected void initView() {
        mGetPaidCouponListSubscription = RxBus.getInstance().toObservable(ShareCouponResult.class).subscribe(
                couponListResult -> handleCouponList(couponListResult)
        );
    }

    private void handleCouponList(ShareCouponResult couponListResult) {
        if (couponListResult.statusCode == 200) {
            onGetListSucceeded(1, couponListResult.respData);
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
        if (Constant.COUPON_TYPE_PAID.equals(couponInfo.couponType)) {
            Intent intent = new Intent(getActivity(), PaidCouponDetailActivity.class);
            intent.putExtra(Constant.PARAM_ACT_ID, couponInfo.actId);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetPaidCouponListSubscription);
    }
}
