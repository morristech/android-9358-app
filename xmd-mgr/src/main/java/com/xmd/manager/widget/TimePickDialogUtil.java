package com.xmd.manager.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xmd.manager.R;
import com.xmd.manager.common.Utils;


import java.util.Calendar;

/**
 * Created by Lhj on 17-9-12.
 */

public class TimePickDialogUtil implements TimePicker.OnTimeChangedListener {

    private TimePicker mTimePicker;
    private AlertDialog mAlertDialog;
    private String mDateTime;
    private String mInitDateTime;
    private Activity mActivity;

    public TimePickDialogUtil(Activity activity, String initDateTime) {
        this.mActivity = activity;
        this.mInitDateTime = initDateTime;
    }

    public void init(TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();

        if (Utils.isNotEmpty(mInitDateTime)) {
            timePicker.setCurrentHour(Integer.parseInt(mInitDateTime.substring(0, 2)));
            timePicker.setCurrentMinute(Integer.parseInt(mInitDateTime.substring(3, 5)));
        } else {
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        mAlertDialog.setTitle(mDateTime);
        mDateTime = getTime(hourOfDay, minute);
    }

    private String getTime(int hourOfDay, int minute) {
        String hour = "00";
        String mine = "00";
        if (String.valueOf(hourOfDay).length() == 1) {
            hour = "0" + String.valueOf(hourOfDay);
        } else {
            hour = String.valueOf(hourOfDay);
        }
        if (String.valueOf(minute).length() == 1) {
            mine = "0" + String.valueOf(minute);
        } else {
            mine = String.valueOf(minute);
        }
        return hour + ":" + mine;
    }

    public AlertDialog dateTimePicKDialog(final TextView inputDate) {
        LinearLayout dateTimeLayout = (LinearLayout) mActivity
                .getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        mTimePicker = (TimePicker) dateTimeLayout.findViewById(R.id.time_picker);
        init(mTimePicker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(this);
        mAlertDialog = new AlertDialog.Builder(mActivity)
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inputDate.setText(mDateTime);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                     //   inputDate.setText("");
                    }
                }).show();
        Button buttonPositive = mAlertDialog.getButton(DialogInterface.BUTTON1);
        Button buttonNegative = mAlertDialog.getButton(DialogInterface.BUTTON2);
        buttonPositive.setTextColor(Color.parseColor("#12DA10"));
        buttonNegative.setTextColor(Color.parseColor("#04b6b6"));
        return mAlertDialog;
    }


}
