package com.xmd.manager.journal.widget;

import android.app.DialogFragment;
import android.databinding.BindingAdapter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.manager.BR;
import com.xmd.manager.R;
import com.xmd.manager.common.CommonRecyclerViewAdapter;
import com.xmd.manager.common.ImageTool;
import com.xmd.manager.common.ScreenUtils;
import com.xmd.manager.databinding.JournalFragmentImageArticleTemplateBinding;
import com.xmd.manager.journal.contract.JournalContentImageArticleContract;
import com.xmd.manager.beans.JournalTemplateImageArticleBean;
import com.xmd.manager.journal.presenter.JournalContentImageArticlePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 17-1-3.
 */

public class JournalEditImageArticleTemplateDialogFragment extends DialogFragment
        implements JournalContentImageArticleContract.TemplateView {
    private JournalContentImageArticleContract.Presenter mPresenter;
    private JournalFragmentImageArticleTemplateBinding mBinding;
    private CommonRecyclerViewAdapter<ImageArticleTemplateWrapper> mAdapter;
    private int mTitleHeight = 0;
    private String mTemplateId;
    public static JournalEditImageArticleTemplateDialogFragment newInstance(String templateId) {
        JournalEditImageArticleTemplateDialogFragment fragment = new JournalEditImageArticleTemplateDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("templateId", templateId);
        fragment.setArguments(bundle);
        XLogger.i(">>>","FragmentTemplateId>>"+templateId);
        return fragment;
    }

    public JournalEditImageArticleTemplateDialogFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTemplateId = getArguments().getString("templateId");
        mBinding = JournalFragmentImageArticleTemplateBinding.inflate(inflater, container, false);
        mBinding.recycleview.setLayoutManager(new GridLayoutManager(inflater.getContext(), 2));
        mBinding.recycleview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(4, 4, 4, 4);
            }
        });
        mAdapter = new CommonRecyclerViewAdapter<>();
        mBinding.recycleview.setAdapter(mAdapter);
        mBinding.setPresenter((JournalContentImageArticlePresenter) mPresenter);
        mAdapter.setHandler(BR.presenter, mPresenter);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onTemplateViewCreate(mTemplateId);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        ScreenUtils.initScreenSize(window.getWindowManager());
        window.setLayout(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight() * 3 / 4);
    }

    @Override
    public void setPresenter(JournalContentImageArticleContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoadTemplateSuccess(List<JournalTemplateImageArticleBean> data, JournalTemplateImageArticleBean selected) {
        List<ImageArticleTemplateWrapper> showData = new ArrayList<>();
        for (JournalTemplateImageArticleBean template : data) {
            showData.add(new ImageArticleTemplateWrapper(template));
        }
        mAdapter.setData(R.layout.journal_item_image_article_template, BR.templateWrapper, showData);
        mAdapter.setViewInflatedListener(new CommonRecyclerViewAdapter.ViewInflatedListener() {
            @Override
            public void onViewInflated(int viewType, View view) {
                if (mTitleHeight == 0) {
                    int noLimitSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE / 2, View.MeasureSpec.AT_MOST);
                    mBinding.title.measure(noLimitSpec, noLimitSpec);
                    mTitleHeight = mBinding.title.getMeasuredWidth();
                }
                view.getLayoutParams().height =
                        (ScreenUtils.getScreenHeight() * 3 / 4 - mTitleHeight - 16) / 2;
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    public static class ImageArticleTemplateWrapper {
        public JournalTemplateImageArticleBean template;

        public ImageArticleTemplateWrapper(JournalTemplateImageArticleBean template) {
            this.template = template;
        }

        @BindingAdapter({"imageUrl"})
        public static void loadImage(ImageView view, String url) {
            ImageTool.loadImage(view.getContext(), url, view);
        }
    }
}
