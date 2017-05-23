package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ClubCouponResult;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-17.
 */
public class CouponFragment extends BaseListFragment<CouponInfo> {

    private Subscription mGetClubCouponSubscription;
    private boolean mIsInited = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coupon, container, false);
    }

    @Override
    protected void initView() {
        mGetClubCouponSubscription = RxBus.getInstance().toObservable(ClubCouponResult.class).subscribe(
                clubAndCouponResult -> handleGetClubCouponListResult(clubAndCouponResult)
        );
    }

    private void handleGetClubCouponListResult(ClubCouponResult clubAndCouponResult) {
        if (!isResumed() && mIsInited) {
            return;
        }

        mIsInited = true;
        if (clubAndCouponResult.statusCode == 200) {
            onGetListSucceeded(clubAndCouponResult.pageCount, clubAndCouponResult.respData);
        } else {
            onGetListFailed(clubAndCouponResult.msg);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetClubCouponSubscription);
    }

    @Override
    public void onItemClicked(CouponInfo couponInfo) {
        Intent intent = new Intent(getActivity(), CouponActivity.class);
        intent.putExtra(Constant.PARAM_ACT_ID, couponInfo.actId);
        intent.putExtra(Constant.PARAM_COUPON_DISPLAY_TYPE, Constant.COUPON_DISPLAY_TYPE_CLUB);
        startActivity(intent);
    }

    @Override
    protected void dispatchRequest() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        params.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_COUPON_LIST, params);
    }
}
