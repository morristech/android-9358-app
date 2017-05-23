package com.xmd.cashier.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-8-26.
 */

public class CustomMoneyEditText extends CustomEditText {
    private List<TextWatcher> mWatchList = new ArrayList<>();

    public CustomMoneyEditText(Context context) {
        super(context);
        init(context);
    }

    public CustomMoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomMoneyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomMoneyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setFilters(new InputFilter[]{new MoneyInputFilter()});
        super.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    String text = s.toString();
                    String startTag = "0";
                    if (text.length() > startTag.length() && text.startsWith(startTag) && text.charAt(startTag.length()) != '.') {
                        setTextInner(text.substring(startTag.length(), text.length()));
                    }
                }
                for (TextWatcher w : mWatchList) {
                    w.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void setMoney(int money) {
        setTextInner(Utils.moneyToString(money));
    }


    public int getMoney() {
        return Utils.stringToMoney(getText().toString());
    }

    private void setTextInner(CharSequence s) {
        setText(s);
        setSelection(getText().toString().length());
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        mWatchList.add(watcher);
    }


    /**
     * 字符按顺序单个输入
     */
    private class MoneyInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //清除数据时，返回0
            if (TextUtils.isEmpty(source) && dstart == 0 && dest.length() == (dend - dstart)) {
                return "0";
            }
            //首位输入.时，默认增加0
            if (source.equals(".") && dest.length() == 0) {
                return "0.";
            }

            //阻止输入多个.
            if (source.equals(".") && dest != null && dest.toString().contains(".")) {
                return "";
            }

            //阻止输入多个0开头的数值
            if (dest != null && dest.length() == 1 && dest.charAt(0) == '0' && source.equals("0")) {
                return "";
            }
            //只保留两位小数，
            if (!source.equals("") && dest != null && dest.length() >= 3 && dest.charAt(dstart - 3) == '.') {
                return "";
            }
            int keep = AppConstants.MAX_MONEY_INT_BIT - (dest.length() - (dend - dstart));
            //达到最大长度时，仍然可以输入小数点
            if (!TextUtils.isEmpty(source) && source.charAt(0) == '.') {
                keep++;
            }
            //当包含小数点时，可以再多2位数,小数点占一位
            if (dest.toString().contains(".") || source.toString().contains(".")) {
                keep += 3;
            }

            //长度限制
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
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
