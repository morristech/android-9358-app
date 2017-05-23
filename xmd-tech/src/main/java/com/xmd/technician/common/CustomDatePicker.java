package com.xmd.technician.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by heyangya on 17-1-18.
 */

public class CustomDatePicker {
    public static void showDatePickerView(Context context, long initTime, DatePicker.OnDateChangedListener listener) {
        DatePicker datePicker = new DatePicker(context);
        int y, m, d;
        Calendar calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        if (initTime > 0) {
            Date date = new Date(initTime);

            calendar.setTime(date);
            y = calendar.get(Calendar.YEAR);
            m = calendar.get(Calendar.MONTH);
            d = calendar.get(Calendar.DAY_OF_MONTH);
        }
        datePicker.setCalendarViewShown(false);
        datePicker.init(y, m, d, null);
        new AlertDialog.Builder(context)
                .setView(datePicker)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDateChanged(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }
}
