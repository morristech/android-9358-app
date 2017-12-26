package com.xmd.manager.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xmd.manager.R;
import com.xmd.manager.common.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Lhj on 17-9-12.
 */

public class DateTimePickDialogUtil implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private AlertDialog mAlertDialog;
    private String mDateTime;
    private String mInitDateTime;
    private Activity mActivity;

    public DateTimePickDialogUtil(Activity activity, String initDateTime) {
        this.mActivity = activity;
        this.mInitDateTime = initDateTime;
    }

    public void init(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        if (Utils.isNotEmpty(mInitDateTime)) {
            calendar = this.getCalendarByInitDate(mInitDateTime);
        } else {
            mInitDateTime = calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "- "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }
        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);

        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(mDatePicker.getYear(), mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(), mTimePicker.getCurrentHour(),
                mTimePicker.getCurrentMinute());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        mDateTime = sdf.format(calendar.getTime());
        mAlertDialog.setTitle(mDateTime);
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public AlertDialog dateTimePicKDialog(final TextView inputDate) {
        LinearLayout dateTimeLayout = (LinearLayout) mActivity
                .getLayoutInflater().inflate(R.layout.dialog_common_datetime, null);
        mDatePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        mTimePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
        init(mDatePicker, mTimePicker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(this);
        mAlertDialog = new AlertDialog.Builder(mActivity)
                .setTitle(mInitDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inputDate.setText(mDateTime);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
        Button buttonPositive = mAlertDialog.getButton(DialogInterface.BUTTON1);
        Button buttonNegative = mAlertDialog.getButton(DialogInterface.BUTTON2);
        buttonPositive.setTextColor(Color.parseColor("#12DA10"));
        buttonNegative.setTextColor(Color.parseColor("#04b6b6"));
        onDateChanged(null, 0, 0, 0);
        return mAlertDialog;
    }


    private Calendar getCalendarByInitDate(String initDateTime) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = Integer.valueOf(initDateTime.substring(0, 4)).intValue();
        int currentMonth = Integer.valueOf(initDateTime.substring(5, 7)).intValue() - 1;
        int currentDay = Integer.valueOf(initDateTime.substring(8, 10)).intValue();
        int currentHour = Integer.valueOf(initDateTime.substring(11, 13)).intValue();
        int currentMinute = Integer.valueOf(initDateTime.substring(14, 16)).intValue();

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

}
