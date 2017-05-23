package com.xmd.manager.journal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.xmd.manager.R;

/**
 * Created by heyangya on 16-11-25.
 */

public class JournalEditButton extends TextView {
    public JournalEditButton(Context context) {
        super(context);
    }

    public JournalEditButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JournalEditButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JournalEditButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_journal_edit_light_red));
                setTextColor(getResources().getColor(R.color.white));
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button_journal_edit_white));
                setTextColor(getResources().getColor(R.color.light_red));
                break;
        }
        return super.onTouchEvent(event);
    }
}
