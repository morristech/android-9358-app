package com.xmd.technician.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.xmd.technician.R;

/**
 * Created by sdcm on 16-4-12.
 */
public class StepView extends View {

    private static final int DEFAULT_TEXTSIZE = 14;
    private static final int DEFAULT_RADIUS = 16;
    private static final int DEFAULT_STEPS = 3;
    private static final int PADDING = 5;

    private Paint mTodoPaint;
    private Paint mDonePaint;
    private Paint mTextPaint;

    //default steps
    private int mTotolStep;
    private int mTotolPath;

    private int mStepRadius;
    private int mTodoColor;
    private int mDoneColor;
    private int mTextColor;
    private int mTextSize;

    /**
     * which step just done
     */
    private int mCurrentStep = -1;

    /**
     * the descriptions for each step
     */
    private String[] mDescriptions;

    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StepView);
        mStepRadius = a.getDimensionPixelSize(R.styleable.StepView_stepRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS, getResources().getDisplayMetrics()));
        mTextSize = a.getDimensionPixelSize(R.styleable.StepView_textSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXTSIZE, getResources().getDisplayMetrics()));
        mTodoColor = a.getColor(R.styleable.StepView_todoColor, Color.GRAY);
        mDoneColor = a.getColor(R.styleable.StepView_doneColor, Color.RED);
        mTextColor = a.getColor(R.styleable.StepView_textColor, Color.BLACK);
        mTotolStep = a.getInt(R.styleable.StepView_totalSteps, DEFAULT_STEPS);
        mTotolPath = mTotolStep - 1;
        a.recycle();

        mTodoPaint = new Paint();
        mTodoPaint.setColor(mTodoColor);
        mTodoPaint.setAntiAlias(true);
        mTodoPaint.setStrokeWidth(2);
        mTodoPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mDonePaint = new Paint();
        mDonePaint.setColor(mDoneColor);
        mDonePaint.setAntiAlias(true);
        mDonePaint.setStrokeWidth(2);
        mDonePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mDonePaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public void setCurrentStep(int step) {
        mCurrentStep = step;
        invalidate();
    }

    public void setStepDescriptions(String[] descriptions) {
        mDescriptions = descriptions;
        if (mDescriptions == null || mDescriptions.length != mTotolStep) {
            throw new RuntimeException("The descriptions doesn't match the steps");
        }
        invalidate();
    }

    public void setup(int step, String[] descriptions) {
        setCurrentStep(step);
        setStepDescriptions(descriptions);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        height -= paddingTop + paddingBottom;
        width -= paddingLeft + paddingRight;

        boolean withDesc = mDescriptions != null && mDescriptions.length == mTotolStep;
        float dotY = (withDesc ? height / 3 : height / 2) + paddingTop;
        float textY = dotY + mStepRadius + mTextSize + PADDING;

        if (mTotolStep == 1) {
            float dotX = width / 2 + paddingLeft;
            canvas.drawCircle(dotX, dotY, mStepRadius, mCurrentStep >= 0 ? mDonePaint : mTodoPaint);
            if (withDesc) {
                String text = mDescriptions[0];
                int textLen = text.length() * mTextSize;
                float textStartX = dotX - textLen / 2;
                mTodoPaint.setTextSize(mTextSize);
                canvas.drawText(text, textStartX, textY, mTextPaint);
            }
        } else {
            if (withDesc) {
                String firstStepDesc = mDescriptions[0];
                int leftMargin = firstStepDesc.length() * mTextSize / 2;
                leftMargin = leftMargin > mStepRadius ? leftMargin : 0;

                String lastStepDesc = mDescriptions[mTotolStep - 1];
                int rightMargin = lastStepDesc.length() * mTextSize / 2;
                rightMargin = rightMargin > mStepRadius ? rightMargin : 0;

                float dotX = leftMargin + paddingLeft;
                // we should draw the dot up in the center of the description
                // the vertical center where to draw
                int pathLength = (width - (mTotolStep * 2 * mStepRadius) - leftMargin - rightMargin) / mTotolPath;
                for (int i = 0; i < mTotolStep; i++) {
                    canvas.drawCircle(dotX, dotY, mStepRadius, mCurrentStep >= i ? mDonePaint : mTodoPaint);
                    String text = mDescriptions[i];
                    int textLen = text.length() * mTextSize;
                    float textStartX = dotX - textLen / 2;
                    mTodoPaint.setTextSize(mTextSize);
                    canvas.drawText(text, textStartX, textY, mTextPaint);
                    dotX += mStepRadius;
                    if (i < mTotolStep - 1) {
                        canvas.drawLine(dotX, dotY, dotX + pathLength, dotY, mCurrentStep > i ? mDonePaint : mTodoPaint);
                        dotX += pathLength + mStepRadius;
                    }
                }
            } else {
                float xPos = mStepRadius + paddingLeft;
                // compute the length of the path line
                int pathLength = (width - (mTotolStep * 2 * mStepRadius)) / mTotolPath;

                for (int i = 0; i < mTotolStep; i++) {
                    canvas.drawCircle(xPos, dotY, mStepRadius, mCurrentStep >= i ? mDonePaint : mTodoPaint);
                    xPos += mStepRadius;
                    if (i < mTotolStep - 1) {
                        canvas.drawLine(xPos, dotY, xPos + pathLength, dotY, mCurrentStep > i ? mDonePaint : mTodoPaint);
                        xPos += pathLength + mStepRadius;
                    }
                }
            }
        }
    }
}
