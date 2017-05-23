package com.xmd.technician.widget;


import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

public class ClearablePasswordEditText extends ClearableEditText {


    public ClearablePasswordEditText(Context context) {
        super(context);
        init();
    }

    public ClearablePasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearablePasswordEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if (!Character.isLetterOrDigit(source.charAt(i))) {
                                return "";
                            }
                        }
                        return null;
                    }
                },
                new InputFilter.LengthFilter(20)
        });
    }
}
