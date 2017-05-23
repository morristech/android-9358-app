package com.xmd.manager.journal.manager;

import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.model.JournalItemArticle;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.JournalArticlesResult;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-12-7.
 */
public class ArticleManager {
    private static ArticleManager ourInstance = new ArticleManager();
    private List<JournalItemArticle> mArticleList;
    private Subscription mSubscription;
    private boolean mDataLoaded;

    public static ArticleManager getInstance() {
        return ourInstance;
    }

    private ArticleManager() {
        mArticleList = new ArrayList<>();
    }

    public void loadData(Callback<List<JournalItemArticle>> callback) {
        if (mDataLoaded) {
            if (callback != null) {
                callback.onResult(null, mArticleList);
            }
            return;
        }
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = RetrofitServiceFactory
                .getSpaService()
                .getJournalArticles(SharedPreferenceHelper.getUserToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<JournalArticlesResult>() {
                    @Override
                    public void onResult(Throwable error, JournalArticlesResult result) {
                        mSubscription = null;
                        if (error == null) {
                            mDataLoaded = true;
                            mArticleList.clear();
                            if (result.respData != null) {
                                for (JournalArticlesResult.DATA item : result.respData) {
                                    mArticleList.add(new JournalItemArticle(item.value, item.desc, null));
                                }
                            }
                        }
                        if (callback != null) {
                            callback.onResult(error, mArticleList);
                        }
                    }
                }));
    }

    public void clear() {
        mDataLoaded = false;
        mArticleList.clear();
    }
}
