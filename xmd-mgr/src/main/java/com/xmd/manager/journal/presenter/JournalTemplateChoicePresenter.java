package com.xmd.manager.journal.presenter;

import android.content.Context;

import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.contract.JournalTemplateChoiceContract;
import com.xmd.manager.journal.manager.JournalManager;
import com.xmd.manager.journal.manager.TechnicianManager;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalContentType;
import com.xmd.manager.journal.model.JournalItemTechnician;
import com.xmd.manager.journal.model.JournalTemplate;
import com.xmd.manager.journal.model.TechnicianRanking;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created by heyangya on 16-10-31.
 */

public class JournalTemplateChoicePresenter implements JournalTemplateChoiceContract.Presenter {
    private JournalManager mJournalManager = JournalManager.getInstance();

    private Context mContext;
    private JournalTemplateChoiceContract.View mView;
    private Journal mJournal;

    private Subscription mLoadTemplatesSubscription;
    private Subscription mLoadJournalContentTypesSubscription;
    private Subscription mLoadJournalContentSubscription;

    public JournalTemplateChoicePresenter(Context context, JournalTemplateChoiceContract.View view) {
        mView = view;
        mContext = context;
    }

    @Override
    public void onCreate() {
        reloadData();
    }


    @Override
    public void onDestroy() {
        mView.hideLoadingAnimation();
        cancelAllNetworkAccess();
    }

    @Override
    public void reloadData() {
        loadContentTypes();
    }

    private void cancelAllNetworkAccess() {
        if (mLoadTemplatesSubscription != null) {
            mLoadTemplatesSubscription.unsubscribe();
        }
        if (mLoadJournalContentTypesSubscription != null) {
            mLoadJournalContentTypesSubscription.unsubscribe();
        }
        if (mLoadJournalContentSubscription != null) {
            mLoadJournalContentSubscription.unsubscribe();
        }
    }

    private void loadContentTypes() {
        //1.加载所有内容类型
        if (mLoadJournalContentTypesSubscription != null) {
            mLoadJournalContentTypesSubscription.unsubscribe();
        }
        mView.showLoadingTemplates();
        mLoadJournalContentTypesSubscription = mJournalManager.loadJournalContentTypes(new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                mLoadJournalContentTypesSubscription = null;
                if (error == null) {
                    if (mJournalManager.getContentTypesSize() == 0) {
                        onLoadDataError("内容类型为空！");
                    } else {
                        loadTemplates();
                    }
                } else {
                    mView.hideLoadingAnimation();
                    onLoadDataError("无法加载内容类型数据，" + error.getLocalizedMessage());
                }
            }
        });
    }

    private void loadTemplates() {
        //2.加载所有模板
        if (mLoadTemplatesSubscription != null) {
            mLoadTemplatesSubscription.unsubscribe();
        }
        mLoadTemplatesSubscription = mJournalManager.loadJournalTemplates(new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                mLoadTemplatesSubscription = null;
                if (error == null) {
                    if (mJournalManager.getJournalTemplates().size() == 0) {
                        onLoadDataError("模板列表为空！");
                    } else {
                        //3.模板已加载，可以开始创建期刊
                        if (mView.getJournalIdFromIntent() == Journal.CREATE_JOURNAL_ID) {
                            //新建期刊,设置临时数据
                            mJournal = mJournalManager.createDefaultJournal();
                            mView.hideLoadingAnimation();
                            mJournal.initContentByTemplate(mJournal.getTemplate());
                            mJournalManager.setTemporaryJournal(mJournal);
                            mView.showTemplates(mJournalManager.getJournalTemplates(), mJournal, null);
                        } else {
                            //编辑期刊
                            loadJournalContent();
                        }
                    }
                } else {
                    mView.hideLoadingAnimation();
                    onLoadDataError("无法加载模板数据，" + error.getLocalizedMessage());
                }
            }
        });
    }

    //加载期刊详细数据
    private void loadJournalContent() {
        if (mLoadJournalContentSubscription != null) {
            mLoadJournalContentSubscription.unsubscribe();
        }
        Journal journal = mJournalManager.getJournalById(mView.getJournalIdFromIntent());
        mLoadJournalContentSubscription = mJournalManager.getJournalContent(journal, new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                mLoadJournalContentSubscription = null;
                mView.hideLoadingAnimation();
                if (error == null) {
                    List<JournalTemplate> templateList = new ArrayList<>();
                    templateList.add(journal.getTemplate());
                    List<JournalContentType> unCancelContentTypes = new ArrayList<>();
                    for (int i = 0; i < journal.getContentSize(); i++) {
                        JournalContentType contentType = journal.getContent(i).getType();
                        if (!unCancelContentTypes.contains(contentType)) {
                            unCancelContentTypes.add(contentType);
                        }
                    }
                    mView.showTemplates(templateList, journal, unCancelContentTypes);

                    //设置原始期刊数据
                    mJournalManager.setOriginJournal(journal);

                    //设置临时期刊数据
                    mJournal = journal.clone();
                    mJournalManager.setTemporaryJournal(mJournal);
                } else {
                    onLoadDataError("无法加载期刊数据，" + error.getLocalizedMessage());
                }
            }
        });
    }

    private void setSelectedTemplate(JournalTemplate template) {
        mJournal.initContentByTemplate(template);
    }


    @Override
    public void onViewTemplate(JournalTemplate template) {
        UINavigation.gotoWebBrowse(mContext, SharedPreferenceHelper.getServerHost() + template.getPreviewUrl());
    }

    @Override
    public void onSelectTemplate(JournalTemplate template) {
        if (mJournal.getTemplate().getId() != template.getId()) {
            setSelectedTemplate(template);
            mView.setNextButtonEnable(template.getRecommendContentTypeSize() > 0);
            mView.showSelectedTemplate(mJournal);
        }
    }

    @Override
    public void onSelectTemplateItem(JournalContentType contentType, boolean selected) {
        if (selected) {
            if (mJournal.getContentSize() == 0) {
                mView.setNextButtonEnable(true);
            }
            JournalContent content = new JournalContent(contentType, mJournal.getTemplate().getSubContentCount(contentType));
            mJournal.addContent(content);
            //服务之星需要特别处理，因为它的内容是默认选中的
            if (content.getType().getKey().equals(JournalContentType.CONTENT_KEY_TECHNICIAN_RANKING)) {
                if (content.getDataSize() == 0) {
                    TechnicianRanking ranking = TechnicianManager.getInstance().getCommentRanking();
                    if (ranking != null && ranking.getData() != null && ranking.getData().size() > 0) {
                        for (int i = 0; i < content.getSubContentCount() && i < ranking.getData().size(); i++) {
                            content.addData(new JournalItemTechnician(ranking.getData().get(i)));
                        }
                    }
                }
            }
        } else {
            mJournal.removeContentByType(contentType);
            if (mJournal.getContentSize() == 0) {
                mView.setNextButtonEnable(false);
            }
        }
    }

    @Override
    public void onEditContent() {
        //编辑内容
        if (mJournal.getId() == Journal.CREATE_JOURNAL_ID) {
            //新建期刊,设置原始期刊数据
            mJournalManager.setOriginJournal(mJournal.clone());
        }
        UINavigation.gotoContentEditActivity(mContext, mJournal.getId(), mJournal.getTemplate().getId());
        mView.finishSelf();
    }

    @Override
    public void onKeyBack() {
        mView.finishSelf();
    }

    private void onLoadDataError(String error) {
        mView.showLoadingTemplatesError();
        mView.showToast(error);
    }
}
