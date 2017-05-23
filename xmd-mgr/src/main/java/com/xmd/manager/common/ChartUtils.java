package com.xmd.manager.common;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.xmd.manager.R;

import java.util.Date;

/**
 * Created by heyangya on 16-8-17.
 */

public class ChartUtils {
    public static int DIV_BASE = 3600 * 1000;

    public static void initLineChart(Context context, LineChart lineChart) {
        lineChart.setDrawGridBackground(false);
        int axisColor = context.getResources().getColor(R.color.colorChartAxis);
        int gridColor = context.getResources().getColor(R.color.colorChartGrid);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(axisColor);
        xAxis.setTextColor(axisColor);
        xAxis.setGridColor(gridColor);
        xAxis.setValueFormatter(new DayAxisValueFormatter(lineChart));
        xAxis.setLabelCount(1, true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setXOffset(-10);


        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setTextSize(14);

        leftAxis.setLabelCount(4, false);
        leftAxis.setTextColor(axisColor);
        leftAxis.setGridColor(gridColor);
        leftAxis.setAxisLineColor(axisColor);
        leftAxis.setAxisMinValue(0);
        lineChart.setDescription("");


    }

    public static void initLineChartDataSet(Context context, LineDataSet lineDataSet) {

        lineDataSet.setColor(context.getResources().getColor(R.color.colorChartLine));
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillAlpha(80);
        lineDataSet.setFillColor(context.getResources().getColor(R.color.colorChartFill));

        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);

    }

    private static class DayAxisValueFormatter implements AxisValueFormatter {
        private LineChart chart;

        DayAxisValueFormatter(LineChart chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String data = DateUtil.getSdf("yy-MM-dd").format(new Date(((long) value) * DIV_BASE));

            return data;
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}
