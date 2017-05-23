package com.xmd.cashier.widget;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

/**
 * Created by heyangya on 16-8-26.
 */

public class CustomMoneyPhoneText extends CustomEditText {

    public CustomMoneyPhoneText(Context context) {
        super(context);
        init(context);
    }

    public CustomMoneyPhoneText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomMoneyPhoneText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomMoneyPhoneText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setFilters(new InputFilter[]{new MoneyInputFilter()});
    }

    /**
     * 字符按顺序单个输入
     */
    private class MoneyInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //必须以1开头
            if (dest.length() == 0 && !source.toString().startsWith("1")) {
                return "";
            }
            int keep = 13 - (dest.length() - (dend - dstart));
            //长度限制
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                String originText = dest.toString().replace(" ", "");
                if (!source.equals("") && (originText.length() == 3 || originText.length() == 7)) {
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
