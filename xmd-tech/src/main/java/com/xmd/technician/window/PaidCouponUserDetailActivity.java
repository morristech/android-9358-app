package com.xmd.technician.window;

import android.content.Intent;
import android.widget.TextView;

import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.Entry;
import com.xmd.technician.bean.PaidCouponUserDetail;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.PaidCouponUserDetailResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.ListPopupWindow;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-3.
 */
public class PaidCouponUserDetailActivity extends BaseListActivity<PaidCouponUserDetail> implements ListPopupWindow.Callback<Entry> {

    private String mActId;
    private Map<String, String> mParams = new HashMap<>();
    @Bind(R.id.tv_filter_status)
    TextView mTvFilterStatus;

    @Override
    protected void setContentViewLayout() {
        setContentView(R.layout.activity_paid_coupon_user_detail);
    }

    private Subscription mGetPaidCouponUserDetailSubscription;

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        mActId = intent.getStringExtra(Constant.PARAM_ACT_ID);
        setTitle(ResourceUtils.getString(R.string.paid_coupon_user_detail_activity_title));
        setBackVisible(true);

        mParams.put(RequestConstant.KEY_ACT_ID, mActId);
        mParams.put(RequestConstant.KEY_COUPON_STATUS, Constant.COUPON_STATUS_ALL);
        mParams.put(RequestConstant.KEY_PAGE, String.valueOf(mPages));
        mParams.put(RequestConstant.KEY_PAGE_SIZE, String.valueOf(PAGE_SIZE));
        mGetPaidCouponUserDetailSubscription = RxBus.getInstance().toObservable(PaidCouponUserDetailResult.class).subscribe(
                paidCouponUserDetailResult -> handleGetPaidCouponUserDetailResult(paidCouponUserDetailResult)
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetPaidCouponUserDetailSubscription);
    }

    private void handleGetPaidCouponUserDetailResult(PaidCouponUserDetailResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetListFailed(result.msg);
        } else {
            onGetListSucceeded(result.pageCount, result.respData);
        }
    }

    @OnClick(R.id.tv_filter_status)
    public void onFilterStatus() {
        ListPopupWindow<Entry> statusPopupWindow = new ListPopupWindow<>(mTvFilterStatus, Constant.COUPON_STATUS_DATA, this);
        statusPopupWindow.showAsDropDown();

    }

    @Override
    protected void dispatchRequest() {
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_PAID_COUPON_USER_DETAIL, mParams);
    }


    @Override
    public void onPopupWindowItemClicked(Entry entry) {
        mParams.put(RequestConstant.KEY_COUPON_STATUS, entry.key);
        dispatchRequest();
        mTvFilterStatus.setText(entry.value);
    }
}
