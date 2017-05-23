package com.xmd.manager.journal.manager;

import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.model.ImageArticleTemplate;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.JournalImageArticleTemplateList;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 17-1-3.
 */
public class ImageArticleManager {
    private static ImageArticleManager ourInstance = new ImageArticleManager();

    public static ImageArticleManager getInstance() {
        return ourInstance;
    }

    private ImageArticleManager() {
    }

    List<ImageArticleTemplate> mTemplates;

    public Subscription getTemplates(Callback<List<ImageArticleTemplate>> callback) {
        if (mTemplates != null) {
            if (callback != null) {
                callback.onResult(null, mTemplates);
            }
            return null;
        } else {
            return RetrofitServiceFactory.getSpaService().getJournalImageArticleTemplateList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetworkSubscriber<>(new Callback<JournalImageArticleTemplateList>() {
                        @Override
                        public void onResult(Throwable error, JournalImageArticleTemplateList result) {
                            if (error == null) {
                                mTemplates = new ArrayList<>();
                                for (JournalImageArticleTemplateList.Item item : result.respData) {
                                    ImageArticleTemplate template = new ImageArticleTemplate();
                                    template.id = item.templateId;
                                    template.imageCount = item.templateImageCount;
                                    template.coverUrl = item.templateUrl;
                                    if (item.templateArticles != null) {
                                        for (String article : item.templateArticles) {
                                            ImageArticleTemplate.Article a = new ImageArticleTemplate.Article();
                                            a.wordsLimit = Integer.parseInt(article);
                                            template.articles.add(a);
                                        }
                                    }
                                    mTemplates.add(template);
                                }
                            }
                            if (callback != null) {
                                callback.onResult(error, mTemplates);
                            }
                        }
                    }));
        }
    }

    public void clear() {
        mTemplates = null;
    }

    public ImageArticleTemplate getTemplateById(String templateId) {
        if (mTemplates != null) {
            for (ImageArticleTemplate template : mTemplates) {
                if (template.id.equals(templateId)) {
                    return template;
                }
            }
        }
        return null;
    }
}
