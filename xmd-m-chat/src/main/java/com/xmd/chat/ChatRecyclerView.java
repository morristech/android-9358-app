package com.xmd.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by mo on 17-7-12.
 * 聊天recycler view
 */

public class ChatRecyclerView extends RecyclerView {
    private int maxHeight;

    public ChatRecyclerView(Context context) {
        super(context);
    }

    public ChatRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h > maxHeight) {
            maxHeight = h;
        }
        if (onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }
    }

    public boolean isMaxHeight() {
        return getHeight() == maxHeight;
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    private OnSizeChangedListener onSizeChangedListener;

    public OnSizeChangedListener getOnSizeChangedListener() {
        return onSizeChangedListener;
    }

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }
}
