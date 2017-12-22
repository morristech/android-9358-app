package com.xmd.technician.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;

import butterknife.ButterKnife;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;


public class ClearableEditText extends EditText {

    /**
     * 删除按钮的图片
     */
    private Drawable delImg;

    // 判断是否获取焦点
    private boolean hasFoucs;

    private int mBgColorFilter;

    private CleanTextListener cleanText;

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

    public void setCleanTextListener(CleanTextListener cleanListener) {
        this.cleanText = cleanListener;
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && mBgColorFilter != -1) {
            getBackground().setColorFilter(mBgColorFilter, PorterDuff.Mode.SRC_ATOP);
        }

        ButterKnife.bind(this);

        delImg = getCompoundDrawables()[2];
        if (delImg == null) {
            // 获取删除的图片资源
            delImg = ResourceUtils.getDrawable(R.drawable.ic_delete);
        }
        delImg.setBounds(0, 0, delImg.getIntrinsicWidth(), delImg.getIntrinsicHeight());

        // 默认设置隐藏图标
        setClearIconVisible(false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                    this.setTag(null);
                    if (cleanText != null) {
                        cleanText.cleanText();
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

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @OnFocusChange
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @OnTextChanged
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
    }

    public interface CleanTextListener {
        void cleanText();
    }

}
