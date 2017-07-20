package com.xmd.m.notify.display;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.m.notify.R;

/**
 * Created by mo on 17-6-16.
 * 浮动通知
 */

public class FloatNotifyManager {
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;
    private View mLayout;

    private TextView messageView;
    private ImageView iconView;

    private boolean show;
    private Handler mHandler;

    private static FloatNotifyManager instance = new FloatNotifyManager();

    private FloatNotifyManager() {

    }

    public static FloatNotifyManager getInstance() {
        return instance;
    }

    public void init(Context context) {
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = 0;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        mLayout = inflater.inflate(R.layout.float_notify, null);

        iconView = (ImageView) mLayout.findViewById(com.xmd.app.R.id.icon);
        messageView = (TextView) mLayout.findViewById(com.xmd.app.R.id.message);

        mHandler = new Handler();
    }

    public FloatNotifyManager setIcon(int iconRes) {
        iconView.setVisibility(View.VISIBLE);
        iconView.setImageResource(iconRes);
        return this;
    }

    public FloatNotifyManager setMessage(CharSequence message) {
        messageView.setTag(null);
        messageView.setOnClickListener(null);
        messageView.setMovementMethod(null);
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(message);
        return this;
    }


    /**
     * 显示
     *
     * @param x             x
     * @param y             y
     * @param dismissTimeMs 显示时间
     */
    public void show(int x, int y, int dismissTimeMs) {
        mWindowParams.x = x;
        mWindowParams.y = y;
        if (!show) {
            show = true;
            mWindowManager.addView(mLayout, mWindowParams);
        } else {
            mHandler.removeCallbacksAndMessages(null);
            mWindowManager.updateViewLayout(mLayout, mWindowParams);
        }

        if (dismissTimeMs > 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hide();
                }
            }, dismissTimeMs);
        }
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (show) {
            mWindowManager.removeView(mLayout);
            show = false;
        }
        mHandler.removeCallbacksAndMessages(null);
        messageView.setTag(null);
        messageView.setMovementMethod(null);
    }

    public FloatNotifyManager setClickableMessage(String pre, String post, final String link, final Object params, final View.OnClickListener listener) {
        String message = "";
        message += pre != null ? pre : "";
        message += link != null ? link : "";
        message += post != null ? post : "";
        if (TextUtils.isEmpty(link)) {
            return setMessage(message);
        }
        SpannableString spannableString = new SpannableString(message);
        int start = pre != null ? pre.length() : 0;
        int end = start + link.length();
        int flag = Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xffff9800);
        messageView.setTag(params);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                listener.onClick(widget);
            }
        };
        spannableString.setSpan(clickableSpan, start, end, flag);
        spannableString.setSpan(colorSpan, start, end, flag);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(spannableString);
        return this;
    }

    public FloatNotifyManager setMessage(String message, View.OnClickListener listener) {
        messageView.setTag(null);
        messageView.setMovementMethod(null);
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(message);
        messageView.setOnClickListener(listener);
        return this;
    }
}
