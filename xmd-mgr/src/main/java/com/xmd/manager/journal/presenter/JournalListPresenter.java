package com.xmd.manager.journal.presenter;

import android.content.Context;
import android.view.View;

import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.UINavigation;
import com.xmd.manager.journal.contract.JournalListContract;
import com.xmd.manager.journal.manager.ArticleManager;
import com.xmd.manager.journal.manager.ClubServiceManager;
import com.xmd.manager.journal.manager.CouponActivityManager;
import com.xmd.manager.journal.manager.ImageArticleManager;
import com.xmd.manager.journal.manager.JournalManager;
import com.xmd.manager.journal.manager.TechnicianManager;
import com.xmd.manager.journal.manager.VideoManager;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.AlertDialogBuilder;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import rx.Subscription;

/**
 * Created by heyangya on 16-10-31.
 */

public class JournalListPresenter implements JournalListContract.Presenter, Observer {
    private JournalManager mJournalManager = JournalManager.getInstance();

    private Context mContext;
    private JournalListContract.View mView;

    private Subscription loadJournals;
    private Subscription publishJournal;
    private Subscription offlineJournal;
    private Subscription deleteJournal;

    public JournalListPresenter(Context context, JournalListContract.View view) {
        mView = view;
        mContext = context;
    }

    @Override
    public void onCreate() {
        if (loadJournals != null) {
            loadJournals.unsubscribe();
        }
        mView.showLoadingJournalList();
        loadJournals = mJournalManager.loadJournals(new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                if (error == null) {
                    mView.showJournalList(mJournalManager.getJournals());
                } else {
                    mView.showLoadJournalListError(error.getLocalizedMessage());
                }
            }
        });

        loadExtraData();

        buildTempResources();

        mJournalManager.addObserver(this);
    }


    @Override
    public void onDestroy() {
        mView.hideLoadingAnimation();
        if (loadJournals != null) {
            loadJournals.unsubscribe();
        }
        if (publishJournal != null) {
            publishJournal.unsubscribe();
        }
        if (offlineJournal != null) {
            offlineJournal.unsubscribe();
        }
        if (deleteJournal != null) {
            deleteJournal.unsubscribe();
        }

        cleanTempResources();

        mJournalManager.deleteObserver(this);
    }

    @Override
    public void refreshJournal() {
        mJournalManager.refresh(new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                if (error == null) {
                    mView.showJournalList(mJournalManager.getJournals());
                } else {
                    mView.showLoadJournalListError(error.getLocalizedMessage());
                }
            }
        });

        loadExtraData();
    }

    private void loadExtraData() {
        //加载技师信息
        TechnicianManager.getInstance().clearData();
        TechnicianManager.getInstance().loadTechnicians(null);
        //加载技师好评排行榜
        TechnicianManager.getInstance().loadTechnicianRanking(RequestConstant.TECHNICIAN_RANKING_TYPE_COMMENT, null);

        //加载项目信息
        ClubServiceManager.getInstance().clear();
        ClubServiceManager.getInstance().loadService(null);

        //加载活动信息
        CouponActivityManager.getInstance().clear();
        CouponActivityManager.getInstance().loadData(null);

        //加载养生文章
        ArticleManager.getInstance().clear();
        ArticleManager.getInstance().loadData(null);

        //加载视频参数
        VideoManager.getInstance().loadVideoConfig();

        //加载图文样式
        ImageArticleManager.getInstance().clear();
        //ImageArticleManager.getInstance().getTemplates(1);
    }

    @Override
    public void publishJournal(Journal journal, int viewPosition) {
        if (publishJournal != null) {
            publishJournal.unsubscribe();
        }
        mView.showLoadingAnimation();
        publishJournal = mJournalManager.publishJournal(journal, new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                mView.hideLoadingAnimation();
                if (error == null) {
                    mView.showToast(mContext.getString(R.string.journal_publish_success));
                } else {
                    mView.showAlertDialog(mContext.getString(R.string.journal_publish_failed) + error.getLocalizedMessage());
                }
                publishJournal = null;
            }
        });
    }

    @Override
    public void offlineJournal(Journal journal, int viewPosition) {
        if (offlineJournal != null) {
            offlineJournal.unsubscribe();
        }
        mView.showLoadingAnimation();
        mJournalManager.offlineJournal(journal, new Callback<Void>() {
            @Override
            public void onResult(Throwable error, Void result) {
                mView.hideLoadingAnimation();
                if (error == null) {
                    mView.showToast(mContext.getString(R.string.journal_offline_success));
                } else {
                    mView.showAlertDialog(mContext.getString(R.string.journal_offline_failed) + error.getLocalizedMessage());
                }
                offlineJournal = null;
            }
        });
    }

    @Override
    public void deleteJournal(Journal journal) {
        new AlertDialogBuilder(mContext)
                .setMessage("确定删除 " + journal.getTitle() + " ?")
                .setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deleteJournal != null) {
                            deleteJournal.unsubscribe();
                        }
                        mView.showLoadingAnimation();
                        mJournalManager.deleteJournal(journal, new Callback<Void>() {
                            @Override
                            public void onResult(Throwable error, Void result) {
                                mView.hideLoadingAnimation();
                                if (error == null) {
                                    mView.showToast(mContext.getString(R.string.journal_delete_success));
                                    mView.updateJournalList();
                                } else {
                                    mView.showAlertDialog(mContext.getString(R.string.journal_delete_failed) + error.getLocalizedMessage());
                                }
                                deleteJournal = null;
                            }
                        });
                    }
                })
                .setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();

    }

    @Override
    public void editJournal(Journal journal) {
        UINavigation.gotoTemplateChoiceActivity(mContext, journal.getId());
    }

    @Override
    public void viewJournal(Journal journal) {
        UINavigation.gotoWebBrowse(mContext, SharedPreferenceHelper.getServerHost() + journal.getPreviewUrl());
    }

    @Override
    public void newJournal() {
        UINavigation.gotoTemplateChoiceActivity(mContext, Journal.CREATE_JOURNAL_ID);
    }


    private void buildTempResources() {
        File file = new File(mContext.getCacheDir().getPath() + File.separator + "crop-images");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private void cleanTempResources() {
        //删除旧的图片文件
        File file = new File(mContext.getCacheDir().getPath() + File.separator + "crop-images");
        if (file.exists()) {
            file.delete();
        }
    }

    //需要刷新列表
    @Override
    public void update(Observable observable, Object data) {
        if (data != null && data instanceof Journal) {
            int changeIndex = mJournalManager.getJournals().indexOf(data);
            if (changeIndex > 0) {
                mView.updateJournalListItem(changeIndex);
                return;
            }
        }
        mView.updateJournalList();
    }
}
