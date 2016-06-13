package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.PaidCouponUserDetail;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-4-29.
 */
public class CouponFragment extends BaseListFragment<CouponInfo> {

    private Subscription mGetCouponListSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coupon, container, false);
    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetCouponListSubscription);
    }

    @Override
    protected void initView() {
        initTitleView(ResourceUtils.getString(R.string.copuon_fragment_title));
        mGetCouponListSubscription = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(
            result -> handleGetCopuonListResult(result)
        );
    }

    private void handleGetCopuonListResult(CouponListResult result) {
        if(!isVisible()){
            return;
        }
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);
        } else {
            if(result.respData.coupons == null || result.respData.coupons.isEmpty()){
                ((BaseActivity)getActivity()).makeShortToast(ResourceUtils.getString(R.string.coupon_fragment_coupon_empty_reason));
            }
            onGetListSucceeded(result.pageCount, result.respData.coupons);
        }
    }

    @Override
    public void onItemClicked(CouponInfo couponInfo) {
        if (Constant.COUPON_TYPE_PAID.equals(couponInfo.couponType)) {
            Intent intent = new Intent(getActivity(), PaidCouponDetailActivity.class);
            intent.putExtra(Constant.PARAM_ACT_ID, couponInfo.actId);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), NormalCouponDetailActivity.class);
            intent.putExtra(Constant.PARAM_ACT_ID, couponInfo.actId);
            startActivity(intent);
        }
    }

    @Override
    public boolean isSlideable() {
        return false;
    }

    @Override
    public boolean isPaged() {
        return false;
    }
}
