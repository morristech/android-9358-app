package com.xmd.cashier.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.xmd.cashier.R;

/**
 * Created by heyangya on 16-9-6.
 */

public class CustomEditText extends EditText {
    private OnFocusChangeListener mOnFocusChangeListener;

    public CustomEditText(Context context) {
        super(context);
        init(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        super.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setTextColor(getResources().getColor(R.color.colorAccent));
                    setHintTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    setTextColor(getResources().getColor(R.color.colorText));
                    setHintTextColor(getResources().getColor(R.color.colorText3));
                }
                if (mOnFocusChangeListener != null) {
                    mOnFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mOnFocusChangeListener = l;
    }

    public void moveCursorToEnd() {
        setSelection(getText().length());
    }
}
