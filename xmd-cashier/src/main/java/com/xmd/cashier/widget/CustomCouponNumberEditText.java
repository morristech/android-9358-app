package com.xmd.cashier.widget;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

import com.xmd.cashier.common.AppConstants;

/**
 * Created by heyangya on 16-8-26.
 */

public class CustomCouponNumberEditText extends CustomEditText {

    public CustomCouponNumberEditText(Context context) {
        super(context);
        init(context);
    }

    public CustomCouponNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomCouponNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomCouponNumberEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setFilters(new InputFilter[]{new MoneyInputFilter()});
    }

    private void setTextInner(CharSequence s) {
        setText(s);
        setSelection(getText().toString().length());
    }


    /**
     * 字符按顺序单个输入
     */
    private class MoneyInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            //长度限制
            int keep = AppConstants.MAX_COUPON_NUMBER_LENGTH - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                String originText = dest.toString().replace(" ", "");
                if (!source.equals("") && originText.length() != 0 && originText.length() % 4 == 0) {
                    return " " + source;
                } else {
                    return null; // keep original
                }
            } else {
                keep += start;
                if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                    --keep;
                    if (keep == start) {
                        return "";
                    }
                }
                return source.subSequence(start, keep);
            }
        }
    }
}
