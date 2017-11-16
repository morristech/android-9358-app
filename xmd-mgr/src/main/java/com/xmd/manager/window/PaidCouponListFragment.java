package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CouponListResult;
import com.xmd.manager.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Lhj on 17-11-13.
 */

public class PaidCouponListFragment extends BaseListFragment<CouponBean> {

    public static String COUPON_LIST_STATUS_TYPE = "listType";
    public static String COUPON_LIST_TYPE_ONLINE = "online";
    public static String COUPON_LIST_TYPE_OFFLINE = "offline";
    public static String KEY_INTENT_COUPON_BEAN = "couponBean";

    private String mCouponListType;//在线，不在线
    private String mCouponType; //点钟券“paid”，
    private Map<String, String> mParams;
    private Subscription mCouponListSubscribe;

    private EmptyView mEmptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paid_coupon_list, container, false);
        mCouponType = Constant.COUPON_PAID_TYPE;
        getArgumentsData();
        mEmptyView = (EmptyView) view.findViewById(R.id.empty_view);
        return view;
    }

    @Override
    protected void dispatchRequest() {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_COUPON_TYPE, mCouponType);
        mParams.put(RequestConstant.KEY_COUPON_ONLINE, mCouponListType.equals(COUPON_LIST_TYPE_ONLINE) ? Constant.COUPON_ONLINE_TRUE : Constant.COUPON_ONLINE_FALSE);
        mParams.put(RequestConstant.KEY_COUPON_LIST_TYPE, mCouponListType);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_LIST_DATA, mParams);
    }

    @Override
    protected void initView() {
        mEmptyView.setStatus(EmptyView.Status.Loading);
        mCouponListSubscribe = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(
                result -> handleCouponListResult(result)
        );
    }

    private void handleCouponListResult(CouponListResult result) {
        mEmptyView.setStatus(EmptyView.Status.Gone);
        if (result.onLineType == null || !result.onLineType.equals(mCouponListType)) {
            return;
        }
        if (result.statusCode == 200) {
            for (CouponBean bean : result.respData) {
                bean.online = mCouponListType.equals(COUPON_LIST_TYPE_ONLINE) ? Constant.COUPON_ONLINE_TRUE : Constant.COUPON_ONLINE_FALSE;
            }
            onGetListSucceeded(result.pageCount, result.respData);
        } else {
            onGetListFailed(result.msg);
        }
    }

    public void getArgumentsData() {
        mCouponListType = getArguments().getString(COUPON_LIST_STATUS_TYPE);
    }

    @Override
    public void onItemClicked(CouponBean bean) {
        super.onItemClicked(bean);
        Intent intent = new Intent(getActivity(), CouponInfoDetailActivity.class);
        intent.putExtra(KEY_INTENT_COUPON_BEAN, bean);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCouponListSubscribe);
    }
}
