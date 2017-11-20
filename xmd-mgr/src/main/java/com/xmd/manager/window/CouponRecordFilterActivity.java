package com.xmd.manager.window;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.event.CouponRecordFilterEvent;
import com.xmd.manager.widget.DateTimePickDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-10-31.
 */

public class CouponRecordFilterActivity extends BaseActivity {
    @BindView(R.id.btn_all_time)
    RadioButton btnAllTime;
    @BindView(R.id.btn_receive_time)
    RadioButton btnReceiveTime;
    @BindView(R.id.btn_verification_time)
    RadioButton btnVerificationTime;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.tv_filter_time_all_start)
    TextView tvFilterTimeAllStart;
    @BindView(R.id.tv_filter_time_all_end)
    TextView tvFilterTimeAllEnd;
    @BindView(R.id.ll_filter_time_total)
    LinearLayout llFilterTimeTotal;
    @BindView(R.id.tv_user_start_time)
    TextView tvUserStartTime;
    @BindView(R.id.tv_user_end_time)
    TextView tvUserEndTime;
    @BindView(R.id.ll_user_time)
    LinearLayout llUserTime;
    @BindView(R.id.radio_group_type)
    RadioGroup radioGroupType;
    @BindView(R.id.rb_coupon_all)
    RadioButton rbCouponAll;
    @BindView(R.id.rb_coupon_usable)
    RadioButton rbCouponUsable;
    @BindView(R.id.rb_coupon_used)
    RadioButton rbCouponUsed;
    @BindView(R.id.rb_coupon_overtime)
    RadioButton rbCouponOvertime;
    @BindView(R.id.fm_coupon_select)
    FrameLayout fmCouponSelect;
    @BindView(R.id.btn_filter_coupon)
    Button btnFilterCoupon;

    private String mCurrentTimeFilterType; //选择时间类型：0:全部，1：领券时间，2：核销时间
    private String mCurrentStateType;//选择当前状态类型：0:全部，1：可用，2：已核销，3：已过期
    private String mStartTime;//开始时间
    private String mEndTime;//结束时间
    private String mUserStartTime;
    private String mUserEndTime;
    private SelectCouponFragment scf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_record_filter);
        ButterKnife.bind(this);
        getIntentData();
        initView();
    }

    public void getIntentData() {
        mUserStartTime = getIntent().getStringExtra(Constant.COUPON_RECORD_START_TIME);
        mUserEndTime = getIntent().getStringExtra(Constant.COUPON_RECORD_END_TIME);
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.coupon_record_activity_title));
        setRightVisible(true, ResourceUtils.getString(R.string.text_clear), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSetting();
            }
        });
        mCurrentTimeFilterType = Constant.COUPON_TIME_TYPE_ALL;
        llFilterTimeTotal.setVisibility(View.VISIBLE);
        llUserTime.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_all_time:
                        mCurrentTimeFilterType = Constant.COUPON_TIME_TYPE_ALL;
                        llFilterTimeTotal.setVisibility(View.VISIBLE);
                        llUserTime.setVisibility(View.GONE);
                        break;
                    case R.id.btn_receive_time:
                        mCurrentTimeFilterType = Constant.COUPON_TIME_TYPE_GET_TIME;
                        llFilterTimeTotal.setVisibility(View.GONE);
                        llUserTime.setVisibility(View.VISIBLE);
                        break;
                    case R.id.btn_verification_time:
                        mCurrentTimeFilterType = Constant.COUPON_TIME_TYPE_VERIFY_TIME;
                        llFilterTimeTotal.setVisibility(View.GONE);
                        llUserTime.setVisibility(View.VISIBLE);
                        rbCouponUsed.setChecked(true);
                        radioGroupType.setFocusable(false);
                        break;
                }
            }
        });
        mCurrentStateType = Constant.COUPON_STATUS_ALL;
        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != R.id.rb_coupon_used && mCurrentTimeFilterType.equals(Constant.COUPON_TIME_TYPE_VERIFY_TIME)) {
                    XToast.show(ResourceUtils.getString(R.string.change_status_alter_text));
                    rbCouponUsed.setChecked(true);
                    return;
                }
                switch (checkedId) {
                    case R.id.rb_coupon_all:
                        mCurrentStateType = Constant.COUPON_STATUS_ALL;
                        break;
                    case R.id.rb_coupon_usable:
                        mCurrentStateType = Constant.COUPON_STATUS_CAN_USE;
                        break;
                    case R.id.rb_coupon_used:
                        mCurrentStateType = Constant.COUPON_STATUS_VERIFIED;
                        break;
                    case R.id.rb_coupon_overtime:
                        mCurrentStateType = Constant.COUPON_STATUS_EXPIRED;
                        break;
                }
            }
        });
        initTimeView();
        initSelectCouponFragment();
    }

    private void initSelectCouponFragment() {
        scf = new SelectCouponFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fm_coupon_select, scf);
        ft.commit();

    }

    private void initTimeView() {
        mStartTime = SharedPreferenceHelper.getCurrentClubCreateTime();
        mEndTime = DateUtil.getCurrentDate();
        tvFilterTimeAllStart.setText(mStartTime);
        tvFilterTimeAllEnd.setText(mEndTime);
        tvUserStartTime.setText(TextUtils.isEmpty(mUserStartTime) ? mStartTime : mUserStartTime);
        tvUserEndTime.setText(TextUtils.isEmpty(mUserEndTime) ? mEndTime : mUserEndTime);
    }

    //清空
    private void clearAllSetting() {
        btnAllTime.setChecked(true);
        rbCouponAll.setChecked(true);
        scf.setAllCouponSelected();
    }

    @OnClick(R.id.btn_filter_coupon)
    public void onViewClicked() {
        String filterStartTime;
        String filterEndTime;
        CouponBean couponBean = getSelectBean();
        if (btnAllTime.isChecked()) {
            filterStartTime = mStartTime;
            filterEndTime = mEndTime;
        } else {
            filterStartTime = tvUserStartTime.getText().toString();
            filterEndTime = tvUserEndTime.getText().toString();
        }
        if (couponBean == null) {
            EventBus.getDefault().post(new CouponRecordFilterEvent(filterStartTime, filterEndTime, "", ResourceUtils.getString(R.string.coupon_data_all_coupon), mCurrentStateType, mCurrentTimeFilterType));
        } else {
            EventBus.getDefault().post(new CouponRecordFilterEvent(filterStartTime, filterEndTime, couponBean.actId, couponBean.actTitle, mCurrentStateType, mCurrentTimeFilterType));
        }
        this.finish();
    }

    @OnClick({R.id.tv_user_start_time, R.id.tv_user_end_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_user_start_time:
                DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(CouponRecordFilterActivity.this, tvUserStartTime.getText().toString());
                dataPickDialogStr.dateTimePicKDialog(tvUserStartTime);
                break;
            case R.id.tv_user_end_time:
                DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(CouponRecordFilterActivity.this, tvUserEndTime.getText().toString());
                dataPickDialogEnd.dateTimePicKDialog(tvUserEndTime);
                break;
        }
    }

    private CouponBean getSelectBean() {
        return scf.getSelectedCouponOperate();
    }


}
