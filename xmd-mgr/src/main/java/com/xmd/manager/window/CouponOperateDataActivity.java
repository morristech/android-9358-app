package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.beans.CouponOperateDataBean;
import com.xmd.manager.common.CouponDataFilterManager;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.CouponOperateDataResult;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by Lhj on 17-10-17.
 */

public class CouponOperateDataActivity extends BaseActivity {

    @BindView(R.id.coupon_filter_time)
    TextView couponFilterTime;
    @BindView(R.id.tv_coupon_receive)
    TextView tvCouponReceive;
    @BindView(R.id.tv_coupon_verification)
    TextView tvCouponVerification;
    @BindView(R.id.tv_coupon_overtime)
    TextView tvCouponOvertime;
    @BindView(R.id.tv_coupon_name)
    TextView tvCouponName;
    @BindView(R.id.fm_coupon_operate_detail)
    FrameLayout fmCouponOperateDetail;

    public static final int FILTER_COUPON_REQUEST_CODE = 1;

    private CouponBean mFilterBean;
    private CouponOperateDataFragment mCouponOperateDataFragment;
    private String mFilterStartTime;
    private String mFilterEndTime;
    private Map<String, String> mParams;
    private Subscription mCouponOperateDataSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_operate_data);
        ButterKnife.bind(this);
        getIntentData();
        initView();
    }

    public void getIntentData() {
        mFilterBean = getIntent().getParcelableExtra(Constant.KEY_INTENT_COUPON_BEAN);
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.coupon_operate_date_title));
        setRightVisible(true, R.drawable.ic_record_filter, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CouponOperateDataActivity.this, CouponFilterActivity.class), FILTER_COUPON_REQUEST_CODE);
            }
        });
        mFilterStartTime = SharedPreferenceHelper.getCurrentClubCreateTime();
        mFilterEndTime = DateUtil.getCurrentDate();
        mCouponOperateDataSubscription = RxBus.getInstance().toObservable(CouponOperateDataResult.class).subscribe(
                result -> handleCouponOperateData(result)
        );
        initCouponDataFragment();
        getOperateTotalData();
    }

    private void handleCouponOperateData(CouponOperateDataResult result) {
        if (result.statusCode == 200) {
            setViewData(result.respData);
        } else {
            XToast.show(result.msg);
        }
    }

    private void initCouponDataFragment() {
        mCouponOperateDataFragment = new CouponOperateDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.KEY_INTENT_COUPON_BEAN, mFilterBean);
        mCouponOperateDataFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fm_coupon_operate_detail, mCouponOperateDataFragment);
        ft.commit();

    }

    private void setViewData(CouponOperateDataBean bean) {
        tvCouponName.setText(mFilterBean == null ? ResourceUtils.getString(R.string.coupon_data_all_coupon) : mFilterBean.actTitle);
        couponFilterTime.setText(String.format("%s - %s", mFilterStartTime, mFilterEndTime));
        tvCouponReceive.setText(bean.getTotal);
        tvCouponVerification.setText(bean.haveUseTotal);
        tvCouponOvertime.setText(bean.expireTotal);
    }

    private void getOperateTotalData() {
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.clear();
        mParams.put(RequestConstant.KEY_COUPON_ID, mFilterBean == null ? "" : mFilterBean.actId);
        mParams.put(RequestConstant.KEY_COUPON_START_DATE, mFilterStartTime);
        mParams.put(RequestConstant.KEY_COUPON_END_DATE, mFilterEndTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_COUPON_OPERATE_DATA_TOTAL, mParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_COUPON_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                mFilterBean = (CouponBean) data.getParcelableExtra(Constant.KEY_INTENT_COUPON_BEAN);
                mFilterStartTime = data.getStringExtra(Constant.FILTER_COUPON_TIME_START_TIME);
                mFilterEndTime = data.getStringExtra(Constant.FILTER_COUPON_TIME_END_TIME);
                getOperateTotalData();
                mCouponOperateDataFragment.notifyDataRefresh(mFilterBean == null ? "" : mFilterBean.actId, mFilterStartTime, mFilterEndTime);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mCouponOperateDataSubscription);
        CouponDataFilterManager.getCouponFilterManagerInstance().onDestroyManager();
    }

    @OnClick({R.id.ll_receive_record, R.id.ll_verification_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_receive_record:
                CouponRecordActivity.startCouponRecordActivity(this, mFilterBean, mFilterStartTime, mFilterEndTime, Constant.COUPON_STATUS_CAN_USE,Constant.COUPON_TIME_TYPE_ALL);
                break;
            case R.id.ll_verification_record:
                CouponRecordActivity.startCouponRecordActivity(this, mFilterBean, mFilterStartTime, mFilterEndTime, Constant.COUPON_STATUS_VERIFIED,Constant.COUPON_TIME_TYPE_ALL);
                break;

        }
    }
}
