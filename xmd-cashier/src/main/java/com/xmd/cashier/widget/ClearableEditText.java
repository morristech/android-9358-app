package com.xmd.cashier.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;

public class ClearableEditText extends EditText {
    // 删除按钮的图片
    private Drawable delImg;
    // 判断是否获取焦点
    private boolean hasFocus;
    private int mBgColorFilter;
    private ClearEditText mClearEditText;

    public interface ClearEditText {
        void clearListener();
    }

    public void setClearEditTextListener(ClearEditText clearEditText) {
        this.mClearEditText = clearEditText;
    }

    public ClearableEditText(Context context) {
        super(context);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearableEditText);
        mBgColorFilter = typedArray.getColor(R.styleable.ClearableEditText_bg_color_filter, -1);
        typedArray.recycle();
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && mBgColorFilter != -1) {
            getBackground().setColorFilter(mBgColorFilter, PorterDuff.Mode.SRC_ATOP);
        }
        delImg = getCompoundDrawables()[2];
        if (delImg == null) {
            // 获取删除的图片资源
            delImg = MainApplication.getInstance().getResources().getDrawable(R.drawable.bg_icon_delete);
        }
        delImg.setBounds(0, 0, delImg.getIntrinsicWidth(), delImg.getIntrinsicHeight());

        // 默认设置隐藏图标
        setClearIconVisible(false);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ClearableEditText.this.hasFocus = hasFocus;
                if (hasFocus) {
                    setClearIconVisible(getText().length() > 0);
                } else {
                    setClearIconVisible(false);
                }
            }
        });
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hasFocus) {
                    setClearIconVisible(s.length() > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                    this.setTag(null);
                    if (null != mClearEditText) {
                        mClearEditText.clearListener();
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? delImg : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }
}
