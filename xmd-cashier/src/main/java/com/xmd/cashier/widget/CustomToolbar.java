package com.xmd.cashier.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xmd.cashier.R;

/**
 * Created by heyangya on 16-9-1.
 */

public class CustomToolbar extends Toolbar {
    public CustomToolbar(Context context) {
        super(context);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(@StringRes int resId) {
        super.setTitle("");
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
    }

    public void setTitleString(CharSequence title) {
        ((TextView) findViewById(R.id.tv_title)).setText(title);
    }
}