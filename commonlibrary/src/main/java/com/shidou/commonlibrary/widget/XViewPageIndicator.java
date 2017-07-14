package com.shidou.commonlibrary.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-4-11.
 * View pager 指示器
 */
public class XViewPageIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private Context mContext;
    private List<ImageView> mImageViews;
    private int mNormalImageId;
    private int mFocusImageId;

    public XViewPageIndicator(Context context) {
        super(context);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public XViewPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public XViewPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public XViewPageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mImageViews = new ArrayList<>();
    }

    public void setViewPager(ViewPager pager, int normalImageId, int focusImageId) {
        pager.addOnPageChangeListener(this);
        mNormalImageId = normalImageId;
        mFocusImageId = focusImageId;
    }

    public synchronized void drawIcons(int count) {
        mImageViews.clear();
        removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setPadding(8, 8, 8, 8);
            addView(imageView);
            imageView.getLayoutParams().width = ScreenUtils.dpToPx(16);
            imageView.getLayoutParams().height = ScreenUtils.dpToPx(16);
            mImageViews.add(imageView);
        }
        onPageSelected(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public synchronized void onPageSelected(int position) {
        if (mImageViews.size() > 0) {
            int realPosition = position % mImageViews.size();
            //选中某一个页面
            for (int i = 0; i < mImageViews.size(); i++) {
                if (i == realPosition) {
                    mImageViews.get(i).setImageResource(mFocusImageId);
                } else {
                    mImageViews.get(i).setImageResource(mNormalImageId);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
