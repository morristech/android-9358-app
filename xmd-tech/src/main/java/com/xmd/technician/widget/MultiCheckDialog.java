package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xmd.technician.R;

/**
 * 上班时间复选星期的对话框
 * Created by SD_ZR on 15-6-2.
 */
public abstract class MultiCheckDialog extends Dialog implements CompoundButton.OnCheckedChangeListener {

    private Context context;

    private Button mCancelButton;
    private Button mConfirmButton;

    private CheckBox mCheckMon;
    private CheckBox mCheckTue;
    private CheckBox mCheckWed;
    private CheckBox mCheckThu;
    private CheckBox mCheckFri;
    private CheckBox mCheckSat;
    private CheckBox mCheckSun;

    private Boolean[] checked = new Boolean[7];

    public MultiCheckDialog(Context context) {
        super(context);
        this.context = context;
    }

    public MultiCheckDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public MultiCheckDialog(Context context, int theme, String dayRange) {
        this(context, theme);
        if (dayRange == null) {
            return;
        }
        for (int i = 0; i < checked.length; i++) {
            checked[i] = (dayRange.contains(Integer.toString(i))) ? true : false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_multi_check);

        mCheckMon = (CheckBox) findViewById(R.id.check_box_monday);
        mCheckMon.setChecked(checked[0]);
        mCheckMon.setOnCheckedChangeListener(this);

        mCheckTue = (CheckBox) findViewById(R.id.check_box_tuesday);
        mCheckTue.setChecked(checked[1]);
        mCheckTue.setOnCheckedChangeListener(this);

        mCheckWed = (CheckBox) findViewById(R.id.check_box_wednesday);
        mCheckWed.setChecked(checked[2]);
        mCheckWed.setOnCheckedChangeListener(this);

        mCheckThu = (CheckBox) findViewById(R.id.check_box_thursday);
        mCheckThu.setChecked(checked[3]);
        mCheckThu.setOnCheckedChangeListener(this);

        mCheckFri = (CheckBox) findViewById(R.id.check_box_friday);
        mCheckFri.setChecked(checked[4]);
        mCheckFri.setOnCheckedChangeListener(this);

        mCheckSat = (CheckBox) findViewById(R.id.check_box_saturday);
        mCheckSat.setChecked(checked[5]);
        mCheckSat.setOnCheckedChangeListener(this);

        mCheckSun = (CheckBox) findViewById(R.id.check_box_sunday);
        mCheckSun.setChecked(checked[6]);
        mCheckSun.setOnCheckedChangeListener(this);

        mCancelButton = (Button) findViewById(R.id.input_dialog_button_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mConfirmButton = (Button) findViewById(R.id.input_dialog_button_confirm);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDaysConfirmMethod();
            }
        });

    }

    protected abstract void onSelectDaysConfirmMethod();

    protected String onGetDayNameResult() {

        StringBuilder sbDayName = new StringBuilder();
        if (mCheckMon.isChecked()) {
            sbDayName.append(mCheckMon.getText().toString() + ",");
        }
        if (mCheckTue.isChecked()) {
            sbDayName.append(mCheckTue.getText().toString() + ",");
        }
        if (mCheckWed.isChecked()) {
            sbDayName.append(mCheckWed.getText().toString() + ",");
        }
        if (mCheckThu.isChecked()) {
            sbDayName.append(mCheckThu.getText().toString() + ",");
        }
        if (mCheckFri.isChecked()) {
            sbDayName.append(mCheckFri.getText().toString() + ",");
        }
        if (mCheckSat.isChecked()) {
            sbDayName.append(mCheckSat.getText().toString() + ",");
        }
        if (mCheckSun.isChecked()) {
            sbDayName.append(mCheckSun.getText().toString() + ",");
        }
        return (sbDayName.length() == 0) ? sbDayName.toString() : sbDayName.substring(0, sbDayName.length() - 1);
    }

    protected String onGetDayIdResult() {
        StringBuilder sbDayId = new StringBuilder();
        for (int i = 0; i < checked.length; i++) {
            if (checked[i]) {
                sbDayId.append(i + ",");
            }
        }
        return (sbDayId.length() == 0) ? sbDayId.toString() : sbDayId.substring(0, sbDayId.length() - 1);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check_box_monday:
                checked[0] = isChecked;
                break;
            case R.id.check_box_tuesday:
                checked[1] = isChecked;
                break;
            case R.id.check_box_wednesday:
                checked[2] = isChecked;
                break;
            case R.id.check_box_thursday:
                checked[3] = isChecked;
                break;
            case R.id.check_box_friday:
                checked[4] = isChecked;
                break;
            case R.id.check_box_saturday:
                checked[5] = isChecked;
                break;
            case R.id.check_box_sunday:
                checked[6] = isChecked;
                break;
            default:
                break;
        }
    }
}
