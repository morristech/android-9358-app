package com.xmd.manager.journal.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by heyangya on 16-12-12.
 */

public class JournalEditActivityView extends LinearLayout {
    private static final int MAX_LIMIT = 200;
    protected JournalContent mContent;
    private JournalItemActivity mJournalItemActivity;
    private JournalContentEditContract.Presenter mPresenter;
    private SimpleDateFormat mSDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private EditText mContentView;
    private TextView mLimitTextView;
    private TextView mStartTimeView;
    private TextView mEndTimeView;

    public JournalEditActivityView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter) {
        super(context);
        mContent = content;
        mContent.setView(mViewUpdater);
        mPresenter = presenter;
        setOrientation(VERTICAL);

        initView();
    }

    private void initView() {
        View contentLayout = LayoutInflater.from(getContext()).inflate(R.layout.journal_edit_activity, this, false);
        addView(contentLayout);
        mContentView = (EditText) contentLayout.findViewById(R.id.edt_content);
        mContentView.setOnFocusChangeListener(mOnActivityContentFocusChange);
        mContentView.addTextChangedListener(mActivityTextWatcher);
        mLimitTextView = (TextView) contentLayout.findViewById(R.id.tv_limit);
        mStartTimeView = (TextView) contentLayout.findViewById(R.id.tv_start_time);
        mEndTimeView = (TextView) contentLayout.findViewById(R.id.tv_end_time);

        if (mContent.getDataSize() == 0) {
            mContent.addData(new JournalItemActivity());
        }
        mJournalItemActivity = (JournalItemActivity) mContent.getData(0);

        if (!TextUtils.isEmpty(mJournalItemActivity.getStartTime())) {
            mStartTimeView.setText(mJournalItemActivity.getStartTime());
        } else {
            mStartTimeView.setText("开始时间");
        }
        mStartTimeView.setOnClickListener(mOnClickStartTime);

        if (!TextUtils.isEmpty(mJournalItemActivity.getEndTime())) {
            mEndTimeView.setText(mJournalItemActivity.getEndTime());
        } else {
            mEndTimeView.setText("结束时间");
        }
        mEndTimeView.setOnClickListener(mOnClickEndTime);

        mContentView.setText(mJournalItemActivity.getContent());
        mLimitTextView.setText(mJournalItemActivity.getContent().length() + "/" + MAX_LIMIT + "字");
    }

    //presenter 使用它来通知view更新
    protected BaseContentView mViewUpdater = new BaseContentView() {

        @Override
        public View getView() {
            return JournalEditActivityView.this;
        }
    };


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private OnFocusChangeListener mOnActivityContentFocusChange = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
//            scrollTo((int) mLimitTextView.getX(), (int) mLimitTextView.getY());
        }
    };

    private TextWatcher mActivityTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mLimitTextView.setText(s.toString().length() + "/" + MAX_LIMIT + "字");
            mJournalItemActivity.setContent(s.toString());
        }
    };

    private OnClickListener mOnClickStartTime = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showDatePickerView(mStartTimeView.getText().toString(), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mStartTimeView.setText(mSDF.format(calendar.getTime()));
                    mJournalItemActivity.setStartTime(mStartTimeView.getText().toString());
                }
            });
        }
    };

    private OnClickListener mOnClickEndTime = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showDatePickerView(mEndTimeView.getText().toString(), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mEndTimeView.setText(mSDF.format(calendar.getTime()));
                    mJournalItemActivity.setEndTime(mEndTimeView.getText().toString());
                }
            });
        }
    };

    private void showDatePickerView(String initTime, DatePicker.OnDateChangedListener listener) {
        DatePicker datePicker = new DatePicker(getContext());
        AlertDialog dialog;
        int y, m, d;
        Calendar calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        if (!TextUtils.isEmpty(initTime)) {
            try {
                Date date = mSDF.parse(initTime);

                calendar.setTime(date);
                y = calendar.get(Calendar.YEAR);
                m = calendar.get(Calendar.MONTH);
                d = calendar.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException ignore) {

            }
        }
        datePicker.setCalendarViewShown(false);
        datePicker.init(y, m, d, null);
        dialog = new AlertDialog.Builder(getContext())
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
                }).show();
        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON1);
        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON2);
        buttonPositive.setTextColor(Color.parseColor("#12DA10"));
        buttonNegative.setTextColor(Color.parseColor("#04b6b6"));
                //.create()

    }
}
