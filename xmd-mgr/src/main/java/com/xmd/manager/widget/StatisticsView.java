package com.xmd.manager.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.xmd.manager.R;
import com.xmd.manager.adapter.StatisticsDataRecycleViewAdapter;
import com.xmd.manager.beans.ItemBean;
import com.xmd.manager.common.ChartUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by heyangya on 16-8-18.
 */

public class StatisticsView extends LinearLayout {


    private Context context;
    private TextView titleTextView;
    private TextView numberTextView;
    private TextView listTitle1TextView;
    private TextView listTitle2TextView;
    private LineChart lineChart;
    private ItemClickInterface mInterface;
    private RecyclerView recyclerView;
    private boolean mIsShow;

    private StatisticsDataRecycleViewAdapter mAdapter;


    public StatisticsView(Context context) {
        super(context);
    }

    public StatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatisticsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface ItemClickInterface {
        void itemClicked(ItemBean item);
    }


    public void init(Context context, Boolean isShow) {
        this.mIsShow = isShow;
        this.context = context;
        titleTextView = (TextView) findViewById(R.id.tv_title);
        numberTextView = (TextView) findViewById(R.id.tv_number);
        listTitle1TextView = (TextView) findViewById(R.id.tv_list_title1);
        listTitle2TextView = (TextView) findViewById(R.id.tv_list_title2);

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new StatisticsDataRecycleViewAdapter(mIsShow);
        recyclerView.setAdapter(mAdapter);

        lineChart = (LineChart) findViewById(R.id.line_chart);
        ChartUtils.initLineChart(context, lineChart);
    }

    public void setTitles(String t1, String t2, String t3, String t4) {
        titleTextView.setText(t1);
        numberTextView.setText(t2);
        listTitle1TextView.setText(t3);
        listTitle2TextView.setText(t4);
    }

    public void setItemClickedListener(ItemClickInterface interfaceClick) {
        mInterface = interfaceClick;
    }

    public void showData(float[] x, float[] y, String[] c1, String[] c2) {
        List<Entry> entries = new ArrayList<>();

        if (x.length > 1) {
            for (int i = 0; i < x.length; i++) {
                entries.add(new Entry(x[i], y[i]));
            }
        } else {
            entries.add(new Entry(x[0], y[0]));
            entries.add(new Entry(x[0], y[0]));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "");

        ChartUtils.initLineChartDataSet(context, lineDataSet);
        lineDataSet.setDrawCircles(false);

        lineChart.setData(new LineData(lineDataSet));
        lineChart.getLegend().setEnabled(false);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        List<StatisticsDataRecycleViewAdapter.Item> datas = new ArrayList<>();
        for (int i = c1.length - 1; i >= 0; i--) {
            datas.add(new StatisticsDataRecycleViewAdapter.Item(c1[i], c2[i]));
        }
        if (mIsShow) {
            mAdapter.setData(datas, new StatisticsDataRecycleViewAdapter.CallbackItem() {
                @Override
                public void onItemClicked(Object bean) {
                    if (null != bean) {
                        mInterface.itemClicked((ItemBean) bean);
                    }

                }
            });
        } else {
            mAdapter.setData(datas);
        }

        mAdapter.notifyDataSetChanged();
    }


}
