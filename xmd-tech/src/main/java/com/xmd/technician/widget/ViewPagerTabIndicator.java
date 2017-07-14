package com.xmd.technician.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */
public class ViewPagerTabIndicator extends FrameLayout {
    public interface IOnPageChangeListener {
        void onPageSelected(int position);
    }

    /**
     * Called by the outside
     */
    public interface IOnTabClickListener {
        void onTabClick(int position);
    }

    /**
     * @hide
     */
    @IntDef({INDICATOR_TOP, INDICATOR_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorGravityMode {
    }

    @IntDef({DRAWABLE_LEFT, DRAWABLE_TOP, DRAWABLE_RIGHT, DRAWABLE_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DrawableDirectionMode {
    }

    private IOnTabClickListener mOnTabclickListener;
    private IOnPageChangeListener mOnPageChangeListener;

    public static final int INDICATOR_TOP = 0;
    public static final int INDICATOR_BOTTOM = 1;

    public static final int DRAWABLE_LEFT = 0;
    public static final int DRAWABLE_TOP = 1;
    public static final int DRAWABLE_RIGHT = 2;
    public static final int DRAWABLE_BOTTOM = 3;

    private List<TextView> mTextViewList = new ArrayList<>();

    private ViewPager mViewPager;

    private float mTranslationX;
    private Paint mPaint;

    private int mTabCount;

    private int mIndicatorGravity;
    private int mDrawableDirection;

    private String[] mTabTexts;
    private Drawable[] mTabIcons;

    private int mTabHeight;
    private int mTabTextBg;
    private int mTextPadding;
    private int mTextPaddingBottom;
    private float mTextSize;
    private ColorStateList mTextColor;

    private int mLineWidth;
    private int mLineMarginTopBottom;
    private int mLineMarginBottom;
    private int mLineColor;

    private int mNoticeSize;
    private int mNoticeTextSize;
    private int mNoticeTextColor;
    private Drawable mNoticeBg;
    private int mNoticeMarginTop;
    private int mNoticeMarginRight;

    private int mIndicatorHeight;

    private boolean mWithDivider = true;
    private boolean mWithIndicator = false;
    private Context mContext;

    public ViewPagerTabIndicator(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public ViewPagerTabIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        this.mContext = context;
    }

    public ViewPagerTabIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {

        mTabHeight = ResourceUtils.getDimenInt(R.dimen.tab_height);

        mTabTextBg = ResourceUtils.getColor(R.color.tab_bg);
        mTextPadding = ResourceUtils.getDimenInt(R.dimen.tab_indicator_text_padding);
        mTextPaddingBottom = ResourceUtils.getDimenInt(R.dimen.tab_indicator_text_padding_bottom);
        mTextSize = ResourceUtils.getDimenFloat(R.dimen.small_text_size);
        mTextColor = ResourceUtils.getColorStateList(R.color.selector_tab_text);

        mLineWidth = ResourceUtils.getDimenInt(R.dimen.line_width);
        mLineMarginTopBottom = ResourceUtils.getDimenInt(R.dimen.line_margin_bottom);
        mLineMarginBottom = ResourceUtils.getDimenInt(R.dimen.line_margin_bottom);
        mLineColor = ResourceUtils.getColor(R.color.divider_color);

        mNoticeSize = ResourceUtils.getDimenInt(R.dimen.tab_notice_size);
        mNoticeTextSize = ResourceUtils.getDimenInt(R.dimen.tab_notice_text_size);
        mNoticeTextColor = ResourceUtils.getColor(R.color.color_white);
        //  mNoticeBg = ResourceUtils.getDrawable(R.drawable.shape_tab_unread_msg);
        mNoticeMarginTop = ResourceUtils.getDimenInt(R.dimen.tab_notice_margin_top);
        mNoticeMarginRight = ResourceUtils.getDimenInt(R.dimen.tab_notice_margin_right);

        mIndicatorHeight = ResourceUtils.getDimenInt(R.dimen.tab_indicator_indicator_height);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ResourceUtils.getColor(R.color.primary_color));
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTranslationX = getWidth() / mTabCount * (position + positionOffset);
                invalidate();
            }

            @Override
            public void onPageSelected(int position) {
                updateSelectedPosition(position);
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(position);
                }
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mWithIndicator) {
            canvas.save();
            if (getIndicatorGravity() == INDICATOR_BOTTOM) {
                canvas.translate(mTranslationX, getHeight() - mIndicatorHeight);
            } else {
                canvas.translate(mTranslationX, 0);
            }
            canvas.drawRect((float) 0, (float) 0, (float) (getWidth() / mTabCount), (float) mIndicatorHeight, mPaint);
            canvas.restore();
        }
    }

    public void setWithDivider(boolean withDivider) {
        this.mWithDivider = withDivider;
    }

    public void setOnPageChangeListener(IOnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    public void setOnTabclickListener(IOnTabClickListener onTabclickLitener) {
        this.mOnTabclickListener = onTabclickLitener;
    }

    public void setIndicatorGravity(@IndicatorGravityMode int indicatorGravity) {
        mWithIndicator = true;
        if (mIndicatorGravity != indicatorGravity) {
            mIndicatorGravity = indicatorGravity;
            requestLayout();
        }
    }

    public void setDrawbleDirection(@DrawableDirectionMode int drawbleDirection) {
        if (mDrawableDirection != drawbleDirection) {
            mDrawableDirection = drawbleDirection;
            requestLayout();
        }
    }

    /**
     * Returns the current indicator gravity.
     *
     * @return either {@link #INDICATOR_TOP} or {@link #INDICATOR_BOTTOM}
     */
    @IndicatorGravityMode
    public int getIndicatorGravity() {
        return mIndicatorGravity;
    }

    public int getTabCount() {
        return mTabCount;
    }

    public void setTabTexts(String[] tabTexts) {
        this.mTabTexts = tabTexts;
    }

    public void setTabTextSize(int textSize) {
        this.mTextSize = Utils.dip2px(mContext, textSize);
    }

    public void setWithIndicator(boolean withIndicator) {
        mWithIndicator = withIndicator;
    }

    public void setTabIcons(Drawable[] icons) {
        this.mTabIcons = icons;
    }

    public void setup() {
        if (mTabTexts == null) {
            throw new RuntimeException("Should set the tab texts first.");
        }

        if (mTabIcons != null && mTabIcons.length != mTabTexts.length) {
            throw new RuntimeException("The tab titles' size should equal to tab icons' size");
        }

        mTabCount = mTabTexts.length;

        //LinearLayout
        createTabContent();

        updateSelectedPosition(0);

    }

    private void createTabContent() {
        LinearLayout tabContainer = new LinearLayout(getContext());
        FrameLayout.LayoutParams tabContainerParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mTabHeight);
        addView(tabContainer, tabContainerParams);

        for (int i = 0; i < mTabCount; i++) {
            //LinearLayout >> FrameLayout
            tabContainer.addView(createTabItem(i), createTabItemParams());
            //Linearlayout >> View (line)
            if (i < mTabCount - 1 && mWithDivider) {
                tabContainer.addView(createVerticalLine(), createVerticalLineParams());
            }
        }
    }

    private LinearLayout.LayoutParams createTabItemParams() {
        LinearLayout.LayoutParams tabItemParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        tabItemParams.weight = 1;
        return tabItemParams;
    }

    /**
     * @param index the position of the item
     * @return the FrameLayout containing the text and notices
     */
    private FrameLayout createTabItem(int index) {
        //LinearLayout >> FrameLayout
        FrameLayout tabItem = new FrameLayout(getContext());
        //LinearLayout >> FrameLayout >> TextView (content)
        TextView textView = new TextView(getContext());
        textView.setBackgroundColor(mTabTextBg);
        textView.setPadding(mTextPadding, mTextPadding, mTextPadding, mTextPaddingBottom);
        textView.setGravity(Gravity.CENTER);
        textView.setText(mTabTexts[index]);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        textView.setTextColor(mTextColor);

        textView.setClickable(true);
        textView.setOnClickListener(v -> {
            updateSelectedPosition(index);
            if (mOnTabclickListener != null) {
                mOnTabclickListener.onTabClick(index);
            }
        });

        if (mTabIcons != null && mTabIcons.length > 0) {
            mTabIcons[index].setBounds(0, 0, mTabIcons[index].getIntrinsicWidth(), mTabIcons[index].getIntrinsicHeight());
            switch (mDrawableDirection) {
                case DRAWABLE_LEFT:
                    textView.setCompoundDrawables(mTabIcons[index], null, null, null);
                    break;
                case DRAWABLE_TOP:
                    textView.setCompoundDrawables(null, mTabIcons[index], null, null);
                    break;
                case DRAWABLE_RIGHT:
                    textView.setCompoundDrawables(null, null, mTabIcons[index], null);
                    break;
                case DRAWABLE_BOTTOM:
                    textView.setCompoundDrawables(null, null, null, mTabIcons[index]);
                    break;
            }
        }

        FrameLayout.LayoutParams textViewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        textViewParams.gravity = Gravity.CENTER;
        tabItem.addView(textView, textViewParams);

        mTextViewList.add(textView);

        return tabItem;
    }

    /**
     * @param position
     * @param count
     */
    public void setNotice(int position, int count) {
        FrameLayout tabItem = getTabItemByPosition(position);
        if (tabItem != null) {
            int childCount = tabItem.getChildCount();
            if (count > 0) {
                if (childCount == 1) {
                    //Add LinearLayout >> FrameLayout >> 2. TextView (notice)
                    FrameLayout.LayoutParams noticeParams = new FrameLayout.LayoutParams(mNoticeSize, mNoticeSize);
                    noticeParams.gravity = Gravity.TOP | Gravity.RIGHT;
                    noticeParams.setMargins(0, mNoticeMarginTop, mNoticeMarginRight, 0);
                    TextView notice = new TextView(getContext());
                    notice.setGravity(Gravity.CENTER);
                    notice.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNoticeTextSize);
                    notice.setTextColor(mNoticeTextColor);
                    notice.setBackgroundDrawable(mNoticeBg);
                    notice.setText(String.valueOf(count));
                    tabItem.addView(notice, noticeParams);
                } else {
                    TextView notice = (TextView) tabItem.getChildAt(1);
                    notice.setText(String.valueOf(count));
                }
            } else {
                if (childCount == 2) {
                    tabItem.removeViewAt(1);
                }
            }
        }
    }

    private LinearLayout.LayoutParams createVerticalLineParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mLineWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        params.bottomMargin = mLineMarginBottom;
        params.topMargin = mLineMarginTopBottom;
        return params;
    }

    private View createVerticalLine() {
        View line = new View(getContext());
        line.setBackgroundColor(mLineColor);
        return line;
    }

    /**
     * update the selected text view
     *
     * @param position the position
     */
    public void updateSelectedPosition(int position) {
        mViewPager.setCurrentItem(position);
        for (int i = 0; i < mTextViewList.size(); i++) {
            TextView textView = mTextViewList.get(i);
            textView.setSelected(i == position);
        }
    }

    /**
     * @param position
     * @return the FrameLayout tabItem
     */
    private FrameLayout getTabItemByPosition(int position) {
        if (position >= mTabCount) {
            return null;
        }
        LinearLayout tabContainer = (LinearLayout) getChildAt(0);
        int childCount = tabContainer.getChildCount();
        int index = 0;
        for (int i = 0; i < childCount; i++) {
            View view = tabContainer.getChildAt(i);
            if (view instanceof FrameLayout) {
                if (index == position) {
                    return (FrameLayout) view;
                }
                index++;
                continue;
            }
        }
        return null;
    }
}

