package com.xmd.cashier.widget;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

/**
 * Created by zr on 16-12-7.
 * 交易号输入框
 */

public class CustomTradeNoText extends CustomEditText {
    public CustomTradeNoText(Context context) {
        super(context);
        init(context);
    }

    public CustomTradeNoText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTradeNoText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomTradeNoText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setFilters(new InputFilter[]{new TradeNoInputFilter()});
    }

    /**
     * source:输入的字符串
     * start:source的start
     * end:source的end, start=0时,end即为source长度
     * dest:输入框原来的内容
     * dstart:替换或者添加的起始位置
     * dend:替换或者添加的结束位置
     */
    private class TradeNoInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // 长度限制:18 & 限制小数点输入
            int keep = 18 - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return (source.equals("") || source.equals(".")) ? "" : source;
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
