package com.xmd.manager.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;

/**
 * Created by Administrator on 2016/12/5.
 */

public class SideBar extends View {
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private Drawable mDrawable;
    public static String[] b = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
    };
    private int choose = -1;
    private Paint paint = new Paint();
    private TextView mTextDialog;
    private Context mContext;

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

    public SideBar(Context context) {
        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mDrawable = ResourceUtils.getDrawable(R.drawable.ic_search);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = (height - Utils.dip2px(mContext, ResourceUtils.getDimenInt(R.dimen.side_bar_text_size)) * 2) / b.length;
        DrawSearchIcon(canvas, width);

        for (int i = 0; i < b.length; i++) {

            paint.setColor(Color.BLACK);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            paint.setTextSize(Utils.dip2px(mContext, ResourceUtils.getDimenInt(R.dimen.side_bar_text_size)));
            if (i == choose) {
                paint.setColor(ResourceUtils.getColor(R.color.sideBarColor));
                paint.setFakeBoldText(true);
            }
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight + width / 2;

            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();
        }
    }

    private void DrawSearchIcon(Canvas canvas, int width) {
        if (mDrawable != null) {
            mDrawable.setBounds((width - Utils.dip2px(mContext, ResourceUtils.getDimenInt(R.dimen.side_bar_text_size))) / 2, (width - Utils.dip2px(mContext, ResourceUtils.getDimenInt(R.dimen.side_bar_text_size))) / 2,
                    width / 2 + ResourceUtils.getDimenInt(R.dimen.side_bar_text_size), width / 2 + ResourceUtils.getDimenInt(R.dimen.side_bar_text_size));

            mDrawable.draw(canvas);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener listener) {
        this.onTouchingLetterChangedListener = listener;
    }

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

}
