package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.CouponRecordBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CouponRecordResult;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;

/**
 * Created by Lhj on 17-10-31.
 */

public class CouponRecordSearchListFragment extends BaseListFragment<CouponRecordBean> {

    public static String KEY_INTENT_COUPON_RECORD = "intentCoupon";

    private Map<String, String> mParams;
    private Subscription mCouponRecordSubscription;
    private String mSearchText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_coupon_record_list, container, false);
        mCouponRecordSubscription = RxBus.getInstance().toObservable(CouponRecordResult.class).subscribe(
                result -> handleCouponRecordResult(result));
        return view;
    }

    private void handleCouponRecordResult(CouponRecordResult result) {
        if (result.isSearch) {
            if (result.statusCode == 200 && result.respData != null) {
                onGetListSucceeded(result.pageCount, result.respData);
                if (result.respData.size() == 0) {
                    XToast.show(ResourceUtils.getString(R.string.search_coupon_is_empty));
                }
            } else {
                onGetListFailed(result.msg);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void dispatchRequest() {
        if (mParams == null) {
            mParams = new HashMap<>();
        } else {
            mParams.clear();
        }
        mParams.put(RequestConstant.KEY_COUPON_ID, "");
        mParams.put(RequestConstant.KEY_COUPON_START_TIME, SharedPreferenceHelper.getCurrentClubCreateTime());
        mParams.put(RequestConstant.KEY_COUPON_END_TIME, DateUtil.getCurrentDate());
        mParams.put(RequestConstant.KEY_COUPON_STATUS, "");
        mParams.put(RequestConstant.KEY_COUPON_TIME_TYPE, "");
        mParams.put(RequestConstant.KEY_COUPON_PHONE_NUM_OR_COUPON_NO, mSearchText);
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(1));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(100));
        mParams.put(RequestConstant.KEY_COUPON_SEARCH_MARK, "searchMark");
        if (!TextUtils.isEmpty(mSearchText)) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_RECORD_DATA, mParams);
        }
    }

    public void searchText(String searchText) {
        this.mSearchText = searchText;
        onRefresh();
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCouponRecordSubscription);
    }

    @Override
    public void onItemClicked(CouponRecordBean bean) {
        super.onItemClicked(bean);
        Intent intent = new Intent(getActivity(), CouponReceiveAndUseDetailActivity.class);
        intent.putExtra(KEY_INTENT_COUPON_RECORD, bean);
        startActivity(intent);
    }
}
