package com.xmd.manager.journal.widget;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.activity.ArticleDialogFragment;
import com.xmd.manager.journal.contract.JournalContentEditContract;
import com.xmd.manager.journal.manager.ArticleManager;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalItemArticle;

import java.util.List;

/**
 * Created by heyangya on 16-12-7.
 */

public class JournalEditArticlesView extends LinearLayout {
    protected JournalContent mContent;
    private JournalContentEditContract.Presenter mPresenter;

    private int mCurrentPage; //从0开始
    private int mCurrentCheckedIndex;
    private List<JournalItemArticle> mArticleList;
    private static final int PAGE_SIZE = 5;

    private ViewHolder[] mItemViews;

    private boolean mIsInitData;

    private int INVISIBLE_TYPE;

    public JournalEditArticlesView(Context context, JournalContent content, JournalContentEditContract.Presenter presenter) {
        super(context);
        mContent = content;
        mPresenter = presenter;
        mContent.setView(mViewUpdater);

        mItemViews = new ViewHolder[PAGE_SIZE];
    }

    //presenter 使用它来通知view更新
    protected BaseContentView mViewUpdater = new BaseContentView() {

        @Override
        public View getView() {
            return JournalEditArticlesView.this;
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mIsInitData) {
            mIsInitData = true;
            loadData();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private OnClickListener mOnClickPrev = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentPage--;
            if (mCurrentPage < 0) {
                mCurrentPage = (mArticleList.size() + PAGE_SIZE - 1) / PAGE_SIZE - 1;
            }
            showData();
        }
    };

    private OnClickListener mOnClickNext = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentPage++;
            if (mCurrentPage > (mArticleList.size() + PAGE_SIZE - 1) / PAGE_SIZE - 1) {
                mCurrentPage = 0;
            }
            showData();
        }
    };

    private OnClickListener mOnClickReload = new OnClickListener() {
        @Override
        public void onClick(View v) {
            loadData();
        }
    };


    private void loadData() {
        TextView textView = new TextView(getContext());
        addView(textView);
        textView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        textView.setText("正在加载中...");
        textView.setGravity(Gravity.CENTER);
        ArticleManager.getInstance().loadData(new Callback<List<JournalItemArticle>>() {
            @Override
            public void onResult(Throwable error, List<JournalItemArticle> result) {
                if (error != null) {
                    textView.setText("加载数据错误：" + error.getLocalizedMessage() + "\n点击重新加载");
                    textView.setOnClickListener(mOnClickReload);
                    return;
                }
                removeAllViews();
                mArticleList = result;
                ViewGroup view = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.journal_edit_articles, null);
                addView(view);
                LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                layoutParams.width = LayoutParams.MATCH_PARENT;
                layoutParams.height = LayoutParams.WRAP_CONTENT;
                View prevButton = view.findViewById(R.id.btn_prev);
                View nextButton = view.findViewById(R.id.btn_next);
                if (mArticleList.size() > PAGE_SIZE) {
                    prevButton.setOnClickListener(mOnClickPrev);
                    nextButton.setOnClickListener(mOnClickNext);
                    INVISIBLE_TYPE = INVISIBLE;
                } else {
                    prevButton.setVisibility(INVISIBLE);
                    nextButton.setVisibility(INVISIBLE);
                    INVISIBLE_TYPE = GONE;
                }

                ViewGroup articleViewGroup = (ViewGroup) view.findViewById(R.id.article_content);
                for (int i = 0; i < PAGE_SIZE; i++) {
                    View itemView = LayoutInflater.from(getContext()).inflate(R.layout.journal_edit_aticle_item, articleViewGroup, false);
                    articleViewGroup.addView(itemView);
                    mItemViews[i] = new ViewHolder(itemView);
                }

                mCurrentPage = 0;
                mCurrentCheckedIndex = -1;
                if (mContent.getDataSize() > 0) {
                    JournalItemArticle saveArticle = (JournalItemArticle) mContent.getData(0);
                    for (JournalItemArticle article : mArticleList) {
                        if (article.getArticleId() == saveArticle.getArticleId()) {
                            mCurrentCheckedIndex = mArticleList.indexOf(article);
                            break;
                        }
                    }
                }

                showData();
            }
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private RadioButton mRadioButton;
        private JournalItemArticle mData;
        private boolean mChecked;
        private int mIndex;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.tv_title);
            mRadioButton = (RadioButton) itemView.findViewById(R.id.rb_select);
            mRadioButton.setOnClickListener(mOnClickSelect);
            itemView.findViewById(R.id.btn_view).setOnClickListener(mOnClickView);
        }

        public void bindData(JournalItemArticle data, int index, boolean checked) {
            mData = data;
            mChecked = checked;
            mIndex = index;
            mTitleView.setText(String.valueOf(index + 1) + "." + data.getArticleTitle());
            mRadioButton.setChecked(mChecked);
        }

        private OnClickListener mOnClickView = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) getContext();
                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                Fragment prev = activity.getFragmentManager().findFragmentByTag("article");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                DialogFragment newFragment = ArticleDialogFragment.newInstance(mData.getArticleId());
                newFragment.show(ft, "article");
            }
        };

        private OnClickListener mOnClickSelect = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChecked) {
                    mCurrentCheckedIndex = mIndex;
                    JournalItemArticle article = mArticleList.get(mIndex);
                    if (mContent.getDataSize() == 0) {
                        mContent.addData(new JournalItemArticle(article.getArticleId(), article.getArticleTitle(), null));
                    } else {
                        JournalItemArticle saveArticle = (JournalItemArticle) mContent.getData(0);
                        saveArticle.setArticleId(article.getArticleId());
                        saveArticle.setArticleTitle(article.getArticleTitle());
                    }
                    showData();
                }
            }
        };
    }

    private void showData() {
        int start = mCurrentPage * PAGE_SIZE;
        final int end = start + PAGE_SIZE > mArticleList.size() ? mArticleList.size() : start + PAGE_SIZE;
        for (int i = start; i < end; i++) {
            mItemViews[i % PAGE_SIZE].itemView.setVisibility(VISIBLE);
            mItemViews[i % PAGE_SIZE].bindData(mArticleList.get(i), i, mCurrentCheckedIndex == i);
        }
        if (end - start < PAGE_SIZE) {
            //不满一页，那么隐藏其他需要隐藏的
            for (int i = end % PAGE_SIZE; i < PAGE_SIZE; i++) {
                mItemViews[i].itemView.setVisibility(INVISIBLE_TYPE);
            }
        }
    }
}
