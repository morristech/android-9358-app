package com.xmd.manager.window;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.adapter.StatisticsDataRecycleViewAdapter;
import com.xmd.manager.common.ChartUtils;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.ToastUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.LineChartDataResult;
import com.xmd.manager.widget.AutoRecyclerView;
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.StatisticsView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj on 2016/9/21.
 */
public class CouponReportActivity extends BaseActivity {
    @Bind(R.id.startTime)
    TextView mStartTime;
    @Bind(R.id.endTime)
    TextView mEndTime;
    @Bind(R.id.btnSubmit)
    Button mBtnSubmit;
    @Bind(R.id.statistics_chart_view)
    StatisticsView mStatisticsView;
    @Bind(R.id.tv_coupon_propagate)
    TextView tvCouponPropagate;
    @Bind(R.id.tv_coupon_delivery)
    TextView tvCouponDelivery;
    @Bind(R.id.tv_coupon_share)
    TextView tvCouponShare;
    @Bind(R.id.tv_coupon_personal)
    TextView tvCouponPersonal;
    @Bind(R.id.tv_coupon_total_income)
    TextView tvCouponTotalIncome;
    @Bind(R.id.tv_coupon_tech_income)
    TextView tvCouponTechIncome;
    @Bind(R.id.layout_order)
    LinearLayout layoutOrder;
    @Bind(R.id.coupon_detail)
    LinearLayout couponDetail;
    @Bind(R.id.scroll_view)
    ScrollView mScrollView;
    @Bind(R.id.coupon_head)
    LinearLayout couponHead;
    @Bind(R.id.recycleview)
    AutoRecyclerView mRecyclerView;


    private StatisticsDataRecycleViewAdapter mAdapter;
    private Subscription mVisitReportSubscription;

    private int headerId;
    private int ContentContainerId;
    private int lastScrollDelta = 0;


    private Map<String, String> params = new HashMap<>();
    private String initStartDateTime;
    private String initEndDateTime;
    private int mTotalData;
    private int mMaxData;

    private String startTime;
    private String endTime;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_report);
        ButterKnife.bind(this);
        setRightVisible(false, 0, null);
        title = getIntent().getStringExtra(RequestConstant.KEY_MAIN_TITLE);
        if (Utils.isNotEmpty(title)) {
            setTitle(title);
        } else {
            setTitle(ResourceUtils.getString(R.string.layout_statistics_coupon_get));
        }

        initView();
    }

    private void initView() {
        initStartDateTime = DateUtil.getDesignatedDate(DateUtil.MONTH);
        initEndDateTime = DateUtil.getCurrentDate();
        mStartTime.setText(initStartDateTime);
        mEndTime.setText(initEndDateTime);
        mStatisticsView.init(this, false);
        mVisitReportSubscription = RxBus.getInstance().toObservable(LineChartDataResult.class).subscribe(
                result -> handlerVisitResult(result)
        );

        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_START_DATE, initStartDateTime);
        params.put(RequestConstant.KEY_END_DATE, initEndDateTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_REPORT, params);

        final View view = mScrollView;
        view.post(() -> {
            ViewGroup.LayoutParams containerParams = mStatisticsView.getLayoutParams();
            containerParams.height = mScrollView.getHeight();
            mStatisticsView.setLayoutParams(containerParams);
        });
        mRecyclerView.parentScrollView = mScrollView;
        mRecyclerView.HeaderId = 200;
        mRecyclerView.ContentContainerId = 200;


    }

    private void handlerVisitResult(LineChartDataResult result) {
        if (result.statusCode == 200) {

            startTime = mStartTime.getText().toString();
            endTime = mEndTime.getText().toString();

            mTotalData = 0;
            for (int i = 0; i < result.respData.data.size(); i++) {
                mTotalData += result.respData.data.get(i);
            }
            mMaxData = Collections.max(result.respData.data);
            mStatisticsView.setTitles(ResourceUtils.getString(R.string.coupon_num), String.valueOf(mTotalData), "日期",
                    title);

            tvCouponTotalIncome.setText(result.respData.clubAmount);
            tvCouponTechIncome.setText(result.respData.techCommission);
            tvCouponDelivery.setText(result.respData.couponGetCount);
            tvCouponPropagate.setText(result.respData.couponOpenCount);
            tvCouponShare.setText(result.respData.couponShareCount);
            tvCouponPersonal.setText(result.respData.couponUseCount);
            loadData(result.respData.data, result.respData.dateTime);
        } else {
            makeShortToast(result.msg);
        }

    }

    private void loadData(List<Integer> data, List<Long> dataTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(SimpleTimeZone.getTimeZone("GMT+08"));

        try {
            float[] timeData = new float[data.size()];
            float[] numberData = new float[data.size()];
            String[] dateData = new String[data.size()];
            String[] numberDataString = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                dateData[i] = DateUtil.longToDate(dataTime.get(i));
                timeData[i] = sdf.parse(dateData[i]).getTime() / ChartUtils.DIV_BASE;
                numberData[i] = data.get(i);
                numberDataString[i] = String.valueOf((int) numberData[i]);
            }

            mStatisticsView.showData(timeData, numberData, dateData, numberDataString);
        } catch (ParseException e) {
            e.printStackTrace();

        }
    }

    @OnClick({R.id.startTime, R.id.endTime, R.id.btnSubmit, R.id.layout_order, R.id.coupon_detail})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTime:
                DateTimePickDialog dataPickDialogStr = new DateTimePickDialog(this, mStartTime.getText().toString());
                dataPickDialogStr.dateTimePicKDialog(mStartTime);
                break;
            case R.id.endTime:
                DateTimePickDialog dataPickDialogEnd = new DateTimePickDialog(this, mEndTime.getText().toString());
                dataPickDialogEnd.dateTimePicKDialog(mEndTime);
                break;
            case R.id.btnSubmit:
                String sT = mStartTime.getText().toString();
                String eT = mEndTime.getText().toString();
                int str = Utils.dateToInt(sT);
                int end = Utils.dateToInt(eT);
                if (end >= str) {
                    Map<String, String> params = new HashMap<>();
                    params.put(RequestConstant.KEY_START_DATE, sT);
                    params.put(RequestConstant.KEY_END_DATE, eT);
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_REPORT, params);
                } else {
                    ToastUtils.showToastShort(this, ResourceUtils.getString(R.string.time_select_alert));
                }
                break;
            case R.id.layout_order:
                Intent intentCoupon = new Intent(CouponReportActivity.this, CouponsDetailActivity.class);
                intentCoupon.putExtra(RequestConstant.KEY_SELECTED_START_TIME, startTime);
                intentCoupon.putExtra(RequestConstant.KEY_SELECTED_END_TIME, endTime);
                startActivity(intentCoupon);
                break;
            case R.id.coupon_detail:

                Intent intent = new Intent(CouponReportActivity.this, PaidCouponDetailActivity.class);
                intent.putExtra(RequestConstant.KEY_SELECTED_START_TIME, startTime);
                intent.putExtra(RequestConstant.KEY_SELECTED_END_TIME, endTime);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mVisitReportSubscription);
    }
}
