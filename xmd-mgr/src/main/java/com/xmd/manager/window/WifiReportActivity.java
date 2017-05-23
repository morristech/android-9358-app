package com.xmd.manager.window;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.xmd.manager.widget.DateTimePickDialog;
import com.xmd.manager.widget.StatisticsView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by lhj 2016/9/21.
 */
public class WifiReportActivity extends BaseActivity {

    @Bind(R.id.startTime)
    TextView mStartTime;
    @Bind(R.id.endTime)
    TextView mEndTime;
    @Bind(R.id.btnSubmit)
    Button mBtnSubmit;
    @Bind(R.id.statistics_chart_view)
    StatisticsView mStatisticsView;

    private StatisticsDataRecycleViewAdapter mAdapter;
    private Subscription mVisitReportSubscription;


    private Map<String, String> params = new HashMap<>();
    private String initStartDateTime;
    private String initEndDateTime;
    private int mTotalData;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_report);
        setRightVisible(false, 0, null);
        title = getIntent().getStringExtra(RequestConstant.KEY_MAIN_TITLE);
        if (Utils.isNotEmpty(title)) {
            setTitle(title);
        } else {
            setTitle(ResourceUtils.getString(R.string.statistics_fragment_wifi_propagate));
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
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_WIFI_REPORT, params);
    }

    private void handlerVisitResult(LineChartDataResult result) {
        if (result.statusCode == 200) {
            mTotalData = 0;
            for (int i = 0; i < result.respData.data.size(); i++) {
                mTotalData += result.respData.data.get(i);
            }
            if (result.respData.data.size() == 0) {
                for (int i = 0; i < result.respData.dateTime.size(); i++) {
                    result.respData.data.add(0);
                }
            }

            mStatisticsView.setTitles(ResourceUtils.getString(R.string.statistics_fragment_wifi_propagate), String.valueOf(mTotalData), "日期",
                    title);

            loadData(result.respData.data, result.respData.dateTime);
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

    @OnClick({R.id.startTime, R.id.endTime, R.id.btnSubmit})
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
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_WIFI_REPORT, params);
                } else {
                    ToastUtils.showToastShort(this, ResourceUtils.getString(R.string.time_select_alert));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mVisitReportSubscription);
    }
}
