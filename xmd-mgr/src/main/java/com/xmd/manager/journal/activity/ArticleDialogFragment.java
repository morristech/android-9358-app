package com.xmd.manager.journal.activity;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.ScreenUtils;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.JournalArticleDetailResult;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-12-8.
 */

public class ArticleDialogFragment extends DialogFragment implements View.OnClickListener {
    private int mArticleId;
    private static final String EXTRA_ARTICLE_ID = "extra_article_id";
    private TextView mArticleContentView;
    private TextView mArticleTitleView;
    private View mDividerView;
    private Subscription mSubscription;

    public static ArticleDialogFragment newInstance(int articleId) {
        ArticleDialogFragment fragment = new ArticleDialogFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mArticleId = getArguments().getInt(EXTRA_ARTICLE_ID);
        View view = inflater.inflate(R.layout.journal_edit_article_content, container, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.findViewById(R.id.btn_close).setOnClickListener(this);
        mArticleContentView = (TextView) view.findViewById(R.id.article_content);
        mArticleTitleView = (TextView) view.findViewById(R.id.article_title);
        mDividerView = view.findViewById(R.id.divider);
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawable(getDialog().getContext().getResources().getDrawable(R.color.transparent));
        loadData();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(ScreenUtils.getScreenWidth() - 64, ScreenUtils.getScreenHeight() * 4 / 5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
        }
    }

    public void loadData() {
        mArticleTitleView.setVisibility(View.GONE);
        mDividerView.setVisibility(View.GONE);
        mArticleContentView.setText("正在加载文章，请稍等...");
        mArticleContentView.setGravity(Gravity.CENTER);
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = RetrofitServiceFactory.getSpaService().getJournalArticleDetail(
                SharedPreferenceHelper.getServerHost() +
                        RequestConstant.URL_JOURNAL_ARTICLE_DETAIL + "/" + String.valueOf(mArticleId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<JournalArticleDetailResult>() {
                    @Override
                    public void onResult(Throwable error, JournalArticleDetailResult result) {
                        mSubscription = null;
                        if (error == null) {
                            mArticleContentView.setGravity(Gravity.LEFT);
                            mArticleTitleView.setVisibility(View.VISIBLE);
                            mDividerView.setVisibility(View.VISIBLE);
                            mArticleTitleView.setText(result.respData.title);
                            mArticleContentView.setText(Html.fromHtml(result.respData.content));
                        } else {
                            mArticleContentView.setText("加载失败：" + error.getLocalizedMessage());
                        }
                    }
                }));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }
}
