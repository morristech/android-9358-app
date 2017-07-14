package com.shidou.commonlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by mo on 17-7-7.
 * webView强化
 */

public class XWebView extends WebView {
    private boolean blockTouch;

    public XWebView(Context context) {
        super(context);
    }

    public XWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public XWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public XWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (listener != null) {
            listener.onDrawFinish();
        }
    }

    private OnDrawFinishListener listener;

    public OnDrawFinishListener getListener() {
        return listener;
    }

    public void setOnDrawFinishListener(OnDrawFinishListener listener) {
        this.listener = listener;
    }

    public interface OnDrawFinishListener {
        void onDrawFinish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (blockTouch) {
            return false;
        } else {
            return super.onTouchEvent(event);
        }
    }

    public boolean isBlockTouch() {
        return blockTouch;
    }

    public void setBlockTouch(boolean blockTouch) {
        this.blockTouch = blockTouch;
    }
}
