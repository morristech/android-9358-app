package com.xmd.technician.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

/**
 * Created by triman on 15-1-14.
 */
public class SmoothProgressBar extends ProgressBar {

    private int mTargetProgress;

    private ObjectAnimator mObjectAnimator;

    public SmoothProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
        }
        mTargetProgress = 0;
        super.setProgress(progress);
    }

    public void setInnerProgress(int progress) {
        super.setProgress(progress);
    }

    public int getInnerProgress() {
        return super.getProgress();
    }

    public void setTargetProgress(int progress) {
        if (progress < mTargetProgress) {
            return;
        }
        int time;
        int tarProgress;
        final int max = getMax();
        if (progress == max) {
            time = 300;
            tarProgress = progress;
        } else {
            if (progress > max / 2) {
                time = 10000;
            } else {
                time = 20000;
            }
            tarProgress = progress / 3 + max * 2 / 3;
        }
//        int curProgress = getProgress();
//        AndroidLog.d("progress", "new progress: " + progress);
//        AndroidLog.d("progress", "cur progress: " + curProgress);
//        AndroidLog.d("progress", "tar progress: " + tarProgress);
//        AndroidLog.d("progress", "duration: " + time);
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
        }
        mObjectAnimator = ObjectAnimator.ofInt(this, "innerProgress",
                tarProgress);
        mObjectAnimator.setInterpolator(new DecelerateInterpolator(3));
        mObjectAnimator.setDuration(time);
        mObjectAnimator.start();
        mTargetProgress = tarProgress;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (getProgress() == getMax()) {
            return;
        }
        super.onDraw(canvas);
    }
}
