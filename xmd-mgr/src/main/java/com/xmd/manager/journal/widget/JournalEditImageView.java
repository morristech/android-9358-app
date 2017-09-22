package com.xmd.manager.journal.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xmd.manager.R;
import com.xmd.manager.journal.model.JournalContent;

/**
 * Created by heyangya on 16-11-9.
 */

public abstract class JournalEditImageView extends LinearLayout {
    protected int IMAGE_LINE_SIZE = 3;
    protected int IMAGE_VIEW_MARGIN = 16;

    protected Context mContext;
    protected JournalContent mContent;

    private boolean mIsInitSubView;
    private LinearLayout mCurrentViewGroup;
    private int mImageSize;

    public JournalEditImageView(Context context, JournalContent content) {
        super(context);
        mContent = content;
        mContext = context;
        mContent.setView(mViewUpdater);
        mCurrentViewGroup = this;

        if (mContent.getSubContentCount() > IMAGE_LINE_SIZE) {
            setOrientation(VERTICAL);
        } else {
            setGravity(Gravity.CENTER);
            setOrientation(HORIZONTAL);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mIsInitSubView) {
            mIsInitSubView = true;
            mImageSize = (MeasureSpec.getSize(widthMeasureSpec) - IMAGE_LINE_SIZE * 2 * IMAGE_VIEW_MARGIN) / IMAGE_LINE_SIZE;
            notifyDataChanged();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public CustomCombineImageView addSubView(int childIndex) {
        if (mContent.getSubContentCount() > IMAGE_LINE_SIZE) {
            //有多行,需要插入一个LinearLayout
            if (childIndex % IMAGE_LINE_SIZE == 0) {
                mCurrentViewGroup = new LinearLayout(mContext);
                addView(mCurrentViewGroup, childIndex / IMAGE_LINE_SIZE);
                if (childIndex < IMAGE_LINE_SIZE) {
                    mCurrentViewGroup.setGravity(Gravity.CENTER);
                } else {
                    mCurrentViewGroup.setGravity(Gravity.LEFT);
                }
            }
        }
        CustomCombineImageView view = (CustomCombineImageView) LayoutInflater.from(mContext).inflate(R.layout.journal_combine_image_view, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getViewWidth(), getViewHeight());
        layoutParams.leftMargin = IMAGE_VIEW_MARGIN;
        layoutParams.rightMargin = IMAGE_VIEW_MARGIN;
        if (mContent.getSubContentCount() > IMAGE_LINE_SIZE) {
            layoutParams.topMargin = IMAGE_VIEW_MARGIN;
            layoutParams.bottomMargin = IMAGE_VIEW_MARGIN;
        }
        view.setLayoutParams(layoutParams);
        mCurrentViewGroup.addView(view);

        view.setOnClickDeleteViewListener(new CustomCombineImageView.OnClickDeleteViewListener() {
            @Override
            public void onClick(View v) {
                onClickDeleteItem(childIndex);
            }
        });
        view.setOnClickImageListener(new CustomCombineImageView.OnClickImageListener() {
            @Override
            public void onClick(View v) {
                onClickReplaceItem(childIndex);
            }
        });
        return view;
    }

    public void notifyDataChanged() {
        removeAllViews();
        for (int i = 0; i < mContent.getDataSize(); i++) {
            CustomCombineImageView view = addSubView(i);
            bindData(view, mContent.getData(i));
        }
        if (showAddImageView() && mContent.getDataSize() < mContent.getSubContentCount()) {
            //数据未满，放一个空白项
            CustomCombineImageView view = addSubView(mContent.getDataSize());
            view.setOnClickImageListener(new CustomCombineImageView.OnClickImageListener() {
                @Override
                public void onClick(View v) {
                    onClickAddItem();
                }
            });
        }
    }

    protected boolean showAddImageView() {
        return true;
    }


    public void notifyItemChanged(int index) {
        bindData(getCustomCombineImageView(index), mContent.getData(index));
    }

    protected CustomCombineImageView getCustomCombineImageView(int index) {
        CustomCombineImageView view;
        if (IMAGE_LINE_SIZE < mContent.getSubContentCount()) {
            //有多行
            LinearLayout linearLayout = (LinearLayout) getChildAt(index / IMAGE_LINE_SIZE);
            view = (CustomCombineImageView) linearLayout.getChildAt(index % IMAGE_LINE_SIZE);
        } else {
            view = (CustomCombineImageView) getChildAt(index);
        }
        return view;
    }

    //显示最后的+号
    protected void showAddView(boolean visible) {
        if (mContent.getDataSize() < mContent.getSubContentCount()) {
            getCustomCombineImageView(mContent.getDataSize()).setVisibility(visible ? VISIBLE : GONE);
        }
    }

    //presenter 使用它来通知view更新
    protected BaseContentView mViewUpdater = new BaseContentView() {
        @Override
        public void notifyDataChanged() {
            JournalEditImageView.this.notifyDataChanged();
        }

        @Override
        public void notifyItemChanged(int index) {
            JournalEditImageView.this.notifyItemChanged(index);
        }

        @Override
        public View getView() {
            return JournalEditImageView.this;
        }

        @Override
        public void onDestroy() {
            onViewDestroy();
        }

        @Override
        public void clearData() {
            onClearData();
        }
    };

    public void onViewDestroy() {

    }

    public void onClearData() {

    }

    public int getViewHeight() {
        return mImageSize;
    }

    public int getViewWidth() {
        return mImageSize;
    }

    public abstract void bindData(CustomCombineImageView view, Object data);

    public abstract void onClickAddItem();

    public abstract void onClickDeleteItem(int index);

    public abstract void onClickReplaceItem(int index);
}
