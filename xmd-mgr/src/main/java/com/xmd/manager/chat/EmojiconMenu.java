package com.xmd.manager.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.xmd.manager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-6-8.
 */
public class EmojiconMenu extends RelativeLayout {

    @BindView(R.id.pager_view)
    ViewPager mViewPager;
    @BindView(R.id.indicator_view)
    LinearLayout mIndicatorView;

    private Bitmap mSelectedBitmap;
    private Bitmap mUnselectedBitmap;
    private int mDotPadding = 10;
    private int mEmojiconRows = 2;
    private int mEmojiconColumns = 7;

    private List<Emojicon> mTotalEmojiconList = new ArrayList<Emojicon>();
    private List<View> mViewPages;
    private List<ImageView> mDotViews;
    private int mLastPoint;

    private EmojiconMenuListener mEmojiconMenuListener;

    public EmojiconMenu(Context context) {
        super(context);
        initView(context, null);
    }

    public EmojiconMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public EmojiconMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.emojicon_menu_view, this);
        ButterKnife.bind(this);

        mSelectedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot_emojicon_selected);
        mUnselectedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot_emojicon_unselected);
    }

    public void init(List<Emojicon> emojiconList) {
        if (emojiconList == null) {
            throw new RuntimeException("emojiconList is null");
        }

        mTotalEmojiconList.addAll(emojiconList);
        mViewPages = getGridViews(emojiconList);
        mViewPager.setAdapter(new EmojiconPagerAdapter(mViewPages));
        mViewPager.addOnPageChangeListener(new EmojiconPageChangeListener());

        initDotViews(mViewPages.size());
    }

    public void setEmojiconMenuListener(EmojiconMenuListener listener) {
        this.mEmojiconMenuListener = listener;
    }

    private List<View> getGridViews(List<Emojicon> emojiconList) {
        int totalSize = emojiconList.size();
        int itemSize = mEmojiconRows * mEmojiconColumns;
        int pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < pageSize; i++) {
            GridView gv = (GridView) View.inflate(getContext(), R.layout.emojicon_grid_view, null);
            //GridView gv = (GridView) view.findViewById(R.id.grid_view);
            gv.setNumColumns(mEmojiconColumns);

            List<Map<String, Object>> list = new ArrayList<>();

            int size = Math.min((i + 1) * itemSize, totalSize);
            for (int j = i * itemSize; j < size; j++) {
                Map<String, Object> item = new HashMap<>();
                item.put("icon", emojiconList.get(j).getIcon());
                item.put("item", emojiconList.get(j));
                list.add(item);
            }
            gv.setAdapter(new SimpleAdapter(getContext(), list, android.R.layout.activity_list_item, new String[]{"icon"}, new int[]{android.R.id.icon}));
            gv.setOnItemClickListener((parent, v, position, id) -> {
                if (mEmojiconMenuListener != null) {
                    Map<String, Object> item = (Map<String, Object>) parent.getAdapter().getItem(position);
                    mEmojiconMenuListener.onEmojiconClicked((Emojicon) item.get("item"));
                }
            });

            views.add(gv);
        }

        return views;
    }

    private void initDotViews(int count) {
        mDotViews = new ArrayList<ImageView>();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(mDotPadding / 2, 0, mDotPadding / 2, 0);

        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(lp);
            imageView.setImageBitmap(mUnselectedBitmap);

            mIndicatorView.addView(imageView);
            mDotViews.add(imageView);
        }
    }

    class EmojiconPagerAdapter extends PagerAdapter {

        private List<View> views;

        public EmojiconPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }
    }

    class EmojiconPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mDotViews.get(mLastPoint).setImageBitmap(mUnselectedBitmap);
            mDotViews.get(position).setImageBitmap(mSelectedBitmap);
            mLastPoint = position;
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public interface EmojiconMenuListener {
        void onEmojiconClicked(Emojicon emojicon);
    }
}
