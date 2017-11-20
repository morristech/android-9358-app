package com.xmd.manager.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.widget.DateTimePickDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-10-17.
 */

public class CouponFilterActivity extends BaseActivity {
    @BindView(R.id.button_total)
    RadioButton buttonTotal;
    @BindView(R.id.button_user)
    RadioButton buttonUser;
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


    public static String COUPON_FILTER_SELECTED_COUPON = "coupon_selected";
    private String mTotalStartTime;
    private String mTotalEndTime;
    private String mUserStartTime;
    private String mUserEndTime;
    private int mCurrentTimeFilterType; //0:全部，1：自定义时间
    private SelectCouponFragment scf;
    private CouponBean mCouponSelectedBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_filter);
        ButterKnife.bind(this);
        getIntentData();
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.coupon_filter_activity_title));
        setRightVisible(true, ResourceUtils.getString(R.string.text_clear), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFilter();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button_total:
                        llFilterTimeTotal.setVisibility(View.VISIBLE);
                        llUserTime.setVisibility(View.GONE);
                        mCurrentTimeFilterType = Constant.COUPON_FILTER_TIME_TYPE_ALL;
                        break;
                    case R.id.button_user:
                        llFilterTimeTotal.setVisibility(View.GONE);
                        llUserTime.setVisibility(View.VISIBLE);
                        mCurrentTimeFilterType = Constant.COUPON_FILTER_TIME_TYPE_USER;
                        break;
                }
            }
        });
        mTotalStartTime = SharedPreferenceHelper.getCurrentClubCreateTime();
        mTotalEndTime = DateUtil.getCurrentDate();
        mUserStartTime = TextUtils.isEmpty(mUserStartTime) ? DateUtil.getFirstDayOfMonth() : mUserStartTime;
        mUserEndTime = TextUtils.isEmpty(mUserEndTime) ? DateUtil.getCurrentDate() : mUserEndTime;
        tvFilterTimeAllStart.setText(mTotalStartTime);
        tvFilterTimeAllEnd.setText(mTotalEndTime);
        tvUserStartTime.setText(TextUtils.isEmpty(mUserStartTime) ? DateUtil.getFirstDayOfMonth() : mUserStartTime);
        tvUserEndTime.setText(TextUtils.isEmpty(mUserEndTime) ? DateUtil.getCurrentDate() : mUserEndTime);
        if (mCurrentTimeFilterType == Constant.COUPON_FILTER_TIME_TYPE_ALL) {
            buttonTotal.setChecked(true);
        } else {
            buttonUser.setChecked(true);
        }
        initSelectCouponFragment();
    }

    public void getIntentData() {
        Intent data = getIntent();
        mUserStartTime = data.getStringExtra(Constant.FILTER_COUPON_TIME_START_TIME);
        mUserEndTime = data.getStringExtra(Constant.FILTER_COUPON_TIME_END_TIME);
        mCurrentTimeFilterType = data.getIntExtra(Constant.FILTER_COUPON_TIME_TYPE, 0);
        mCouponSelectedBean = (CouponBean) data.getSerializableExtra(Constant.KEY_INTENT_COUPON_BEAN);
    }

    private void initSelectCouponFragment() {
        scf = new SelectCouponFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable(COUPON_FILTER_SELECTED_COUPON, mCouponSelectedBean);
        scf.setArguments(bundle);
        ft.replace(R.id.fm_coupon_select, scf);
        ft.commit();
    }


    @OnClick(R.id.btn_filter_coupon)
    public void onBtnFilterCouponClicked() {
        mUserStartTime = tvUserStartTime.getText().toString();
        mUserEndTime = tvUserEndTime.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(Constant.FILTER_COUPON_TIME_TYPE, mCurrentTimeFilterType);
        intent.putExtra(Constant.FILTER_COUPON_TIME_START_TIME, mCurrentTimeFilterType == Constant.COUPON_FILTER_TIME_TYPE_ALL ? mTotalStartTime : mUserStartTime);
        intent.putExtra(Constant.FILTER_COUPON_TIME_END_TIME, mCurrentTimeFilterType == Constant.COUPON_FILTER_TIME_TYPE_ALL ? mTotalEndTime : mUserEndTime);
        intent.putExtra(Constant.KEY_INTENT_COUPON_BEAN, getSelectBean());
        this.setResult(Activity.RESULT_OK, intent);
        this.finish();
    }


    @OnClick({R.id.tv_user_start_time, R.id.tv_user_end_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_user_start_time:
                DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(CouponFilterActivity.this, tvUserStartTime.getText().toString());
                dataPickDialogStr.dateTimePicKDialog(tvUserStartTime);
                break;
            case R.id.tv_user_end_time:
                DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(CouponFilterActivity.this, tvUserEndTime.getText().toString());
                dataPickDialogEnd.dateTimePicKDialog(tvUserEndTime);
                break;
        }
    }

    private void clearAllFilter() {
        buttonTotal.setChecked(true);
        scf.setAllCouponSelected();
    }

    private CouponBean getSelectBean() {
        return scf.getSelectedCouponOperate();
    }


}
