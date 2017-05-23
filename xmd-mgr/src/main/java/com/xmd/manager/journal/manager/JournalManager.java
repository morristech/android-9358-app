package com.xmd.manager.journal.manager;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.journal.Callback;
import com.xmd.manager.journal.model.Journal;
import com.xmd.manager.journal.model.JournalContent;
import com.xmd.manager.journal.model.JournalContentType;
import com.xmd.manager.journal.model.JournalItemPhoto;
import com.xmd.manager.journal.model.JournalItemVideo;
import com.xmd.manager.journal.model.JournalTemplate;
import com.xmd.manager.journal.net.NetworkSubscriber;
import com.xmd.manager.service.RetrofitServiceFactory;
import com.xmd.manager.service.response.BaseListResult;
import com.xmd.manager.service.response.JournalContentResult;
import com.xmd.manager.service.response.JournalContentTypeListResult;
import com.xmd.manager.service.response.JournalListResult;
import com.xmd.manager.service.response.JournalSaveResult;
import com.xmd.manager.service.response.JournalTemplateListResult;
import com.xmd.manager.service.response.JournalUpdateStatusResult;
import com.xmd.manager.service.response.JournalVideoDetailResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-10-31.
 * <p>
 * 管理电子相册列表，电子相册模块，项目类型
 */

public class JournalManager extends Observable {
    private static JournalManager instance = new JournalManager();
    private List<Journal> mJournals;
    private List<JournalTemplate> mJournalTemplates;
    private List<JournalContentType> mJournalContentTypes;
    private Journal mOriginJournal; //编辑状态下，原始的期刊数据，作为修改的时候用
    private Journal mTemporaryJournal; //临时期刊缓存

    private JournalManager() {
        mJournals = new ArrayList<>();
        mJournalTemplates = new ArrayList<>();
        mJournalContentTypes = new ArrayList<>();
    }

    public static JournalManager getInstance() {
        return instance;
    }

    public List<Journal> getJournals() {
        return mJournals;
    }

    public List<JournalTemplate> getJournalTemplates() {
        return mJournalTemplates;
    }

    //使用key获取内容类型
    private JournalContentType getJournalContentTypeByKey(String key) {
        for (int i = 0; i < mJournalContentTypes.size(); i++) {
            if (mJournalContentTypes.get(i).getKey().equals(key)) {
                return mJournalContentTypes.get(i);
            }
        }
        return null;
    }

    private JournalTemplate getTemplateById(int id) {
        for (JournalTemplate template : mJournalTemplates) {
            if (template.getId() == id) {
                return template;
            }
        }
        return null;
    }

    public String getUserToken() {
        return SharedPreferenceHelper.getUserToken();
    }

    //获取期刊列表
    public Subscription loadJournals(Callback<Void> callback) {
        return RetrofitServiceFactory.getSpaService().getJournalList(getUserToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<JournalListResult>(new Callback<JournalListResult>() {
                    @Override
                    public void onResult(Throwable error, JournalListResult result) {
                        if (error == null) {
                            //获取列表成功
                            mJournals.clear();
                            for (JournalListResult.JournalListItem item : result.respData) {
                                Journal journal = new Journal(item.id);
                                journal.setJournalNo(item.sequenceNo);
                                journal.setTitle(item.title);
                                journal.setSubTitle(item.subTitle);
                                journal.setViewCount(item.viewCount);
                                journal.setPraisedCount(item.likeCount);
                                journal.setSharedCount(item.shareCount);
                                journal.setPreviewUrl("/spa-manager" + item.previewUrl);
                                journal.setStatus(item.status);
                                journal.setLastModifiedTime(item.modifyDate);

                                mJournals.add(journal);
                            }
                        }
                        //sort data
                        sortJournals();
                        callback.onResult(error, null);
                    }
                }));
    }

    private void sortJournals() {
        if (mJournals.size() > 0) {
            Collections.sort(mJournals, new Comparator<Journal>() {
                @Override
                public int compare(Journal lhs, Journal rhs) {
                    if (lhs.getStatus() == Journal.STATUS_DRAFT && rhs.getStatus() == Journal.STATUS_DRAFT) {
                        return rhs.getLastModifiedTime().compareTo(lhs.getLastModifiedTime());
                    } else if (lhs.getStatus() == Journal.STATUS_DRAFT) {
                        return -1;
                    } else if (rhs.getStatus() == Journal.STATUS_DRAFT) {
                        return 1;
                    } else {
                        return rhs.getJournalNo() - lhs.getJournalNo();
                    }
                }
            });
        }
    }

    //加载所有内容类型
    public Subscription loadJournalContentTypes(Callback<Void> callback) {
        return RetrofitServiceFactory.getSpaService().getJournalContentTypes(getUserToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<JournalContentTypeListResult>(new Callback<JournalContentTypeListResult>() {
                    @Override
                    public void onResult(Throwable error, JournalContentTypeListResult result) {
                        if (error == null) {
                            mJournalContentTypes.clear();
                            for (JournalContentTypeListResult.Item item : result.respData) {
                                mJournalContentTypes.add(new JournalContentType(item.id, item.constKey, item.constValue));
                            }
                        }
                        callback.onResult(error, null);
                    }
                }));
    }

    //加载所有模板
    public Subscription loadJournalTemplates(Callback<Void> callback) {
        return RetrofitServiceFactory.getSpaService().getJournalTemplateList(getUserToken(), null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<JournalTemplateListResult>(new Callback<JournalTemplateListResult>() {
                    @Override
                    public void onResult(Throwable error, JournalTemplateListResult result) {
                        if (error == null) {
                            mJournalTemplates.clear();
                            for (JournalTemplateListResult.Item item : result.respData) {
                                JournalTemplate journalTemplate = new JournalTemplate();
                                journalTemplate.setId(item.id);
                                journalTemplate.setName(item.name);
                                journalTemplate.setImageUrl(item.coverImageUrl);
                                journalTemplate.setPreviewUrl("/spa-manager" + item.previewUrl);
                                //默认模板都拥有所有内容类型
                                for (JournalContentType type : mJournalContentTypes) {
                                    if (type.getKey().equals(JournalContentType.CONTENT_KEY_PHOTO_ALBUM)) {
                                        journalTemplate.addContentType(type, 9); //相册默认为9个
                                    } else if (type.getKey().equals(JournalContentType.CONTENT_KEY_TECHNICIAN_RANKING)) {
                                        journalTemplate.addContentType(type, 3); //服务之星默认为3个
                                    } else {
                                        journalTemplate.addContentType(type, 1);
                                    }
                                }
                                //添加推荐内容类型
                                try {
                                    JSONObject jsonObject = new JSONObject(new Gson().toJson(item.itemsConfiguration));
                                    Iterator<String> iterator = jsonObject.keys();
                                    while (iterator.hasNext()) {
                                        String key = iterator.next();
                                        JournalContentType contentType = getJournalContentTypeByKey(key);
                                        if (contentType != null) {
                                            journalTemplate.addRecommendContentType(contentType);
                                            journalTemplate.setContentTypeSubContentCount(contentType, jsonObject.getInt(key));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mJournalTemplates.add(journalTemplate);
                            }
                        }
                        callback.onResult(error, null);
                    }
                }));
    }

    //获取期刊数据
    public Subscription getJournalContent(Journal journal, Callback<Void> callback) {
        return RetrofitServiceFactory.getSpaService().getJournalContent(getUserToken(), journal.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<JournalContentResult>() {
                    @Override
                    public void onResult(Throwable error, JournalContentResult result) {
                        if (error == null) {
                            journal.clearContent();
                            JournalTemplate template = getTemplateById(result.respData.templateId);
                            if (template == null) {
                                callback.onResult(new RuntimeException("无法找到此期刊对应的模板！"), null);
                                return;
                            }
                            journal.setTemplate(template);
                            journal.setTitle(result.respData.title);
                            journal.setSubTitle(result.respData.subTitle);
                            for (JournalContentResult.JournalItem item : result.respData.journalItemData) {
                                JournalContentType contentType = getJournalContentTypeByKey(item.itemKey);
                                JournalContent content = new JournalContent(contentType, journal.getTemplate().getSubContentCount(contentType));
                                content.setStringData(item.content);
                                content.setTitle(item.title);
                                journal.addContent(content, item.itemOrder);
                            }
                        }
                        callback.onResult(error, null);
                    }
                }));
    }

    private Subscription changeJournalStatus(final Journal journal, final Callback<Void> callback, final int status) {
        int oldStatus = journal.getStatus();
        return RetrofitServiceFactory.getSpaService().updateJournalStatus(getUserToken(), journal.getId(), status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<JournalUpdateStatusResult>() {
                    @Override
                    public void onResult(Throwable error, JournalUpdateStatusResult result) {
                        if (error == null) {
                            if (status != Journal.STATUS_DELETED) {
                                if (result.respData.sequenceNo != 0) {
                                    journal.setJournalNo(result.respData.sequenceNo);
                                }
                                journal.setStatus(status);
                                journal.setLastModifiedTime(result.respData.modifyDate);
                                setChanged();
                                if (oldStatus == Journal.STATUS_DRAFT) {//草稿修改为发布才需要排序
                                    sortJournals();
                                    notifyObservers();
                                } else {
                                    notifyObservers(journal); //不需要排序，只需要刷新自己
                                }
                            }
                        }
                        if (callback != null) {
                            callback.onResult(error, null);
                        }
                    }
                }));
    }

    //发布期刊
    public Subscription publishJournal(Journal journal, Callback<Void> callback) {
        return changeJournalStatus(journal, callback, Journal.STATUS_PUBLISHED);
    }

    //下架期刊
    public Subscription offlineJournal(Journal journal, Callback<Void> callback) {
        return changeJournalStatus(journal, callback, Journal.STATUS_OFFLINE);
    }

    //删除期刊
    public Subscription deleteJournal(Journal journal, Callback<Void> callback) {
        mJournals.remove(journal);
        setChanged(); //快速通知删除，避免网络原因导致删除过慢
        notifyObservers();
        return changeJournalStatus(journal, callback, Journal.STATUS_DELETED);
    }

    //新增期刊
    public Subscription addOrUpdateJournal(Journal journal, Callback<Void> callback) {
        return addOrUpdate(journal, new Callback<JournalSaveResult>() {
            @Override
            public void onResult(Throwable error, JournalSaveResult result) {
                if (error == null) {
                    //保存到服务器成功，将数据新增到列表
                    if (journal.getId() == Journal.CREATE_JOURNAL_ID) {
                        journal.setId(result.respData.journalId);
                        mJournals.add(0, journal);
                    }
                    journal.setPreviewUrl("/spa-manager" + result.respData.previewUrl);
                    journal.setLastModifiedTime(DateUtil.dateToStringMinite(new Date()));

                    sortJournals();//重新排序
                    setChanged();
                    notifyObservers();
                }
                if (callback != null) {
                    callback.onResult(error, null);
                }
            }
        });
    }

    private Subscription addOrUpdate(Journal journal, Callback<JournalSaveResult> callback) {
        //构造上传数据
        List<JournalContentResult.JournalItem> items = new ArrayList<>();
        for (int i = 0; i < journal.getContentSize(); i++) {
            JournalContentResult.JournalItem item = new JournalContentResult.JournalItem();
            JournalContent content = journal.getContent(i);
            item.title = content.getTitle();
            if (TextUtils.isEmpty(item.title)) {
                item.title = content.getType().getName();
            }
            item.itemKey = content.getType().getKey();
            item.itemOrder = i;
            item.content = content.getStringData();
            items.add(item);
        }
        String stringData = new Gson().toJson(items);
        return RetrofitServiceFactory.getSpaService().addOrUpdateJournal(
                getUserToken(),
                journal.getId(),
                journal.getTemplate().getId(),
                journal.getTitle(),
                journal.getSubTitle(),
                stringData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<JournalSaveResult>(callback));
    }

    public Subscription refresh(Callback<Void> callback) {
        return loadJournals(callback);
    }

    public Subscription getPhotoUrls(JournalContent content, Callback<Void> callback) {
        StringBuilder idsBuilder = new StringBuilder();
        for (int i = 0; i < content.getDataSize(); i++) {
            idsBuilder.append(((JournalItemPhoto) content.getData(i)).getPhoto().getImageId()).append(",");
        }
        if (idsBuilder.length() > 0) {
            idsBuilder.setLength(idsBuilder.length() - 1);
        }
        return RetrofitServiceFactory.getSpaService().getUrlByPhotoId(getUserToken(), idsBuilder.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<BaseListResult<String>>() {
                    @Override
                    public void onResult(Throwable error, BaseListResult<String> result) {
                        if (error == null) {
                            int i = 0;
                            for (String url : result.respData) {
                                ((JournalItemPhoto) content.getData(i)).getPhoto().setRemoteUrl(url);
                                i++;
                            }
                        }
                        callback.onResult(error, null);
                    }
                }));
    }

    //获取视频的播放地址和封面链接
    public Subscription getVideoInfo(JournalItemVideo contentVideo, Callback<Void> callback) {
        return RetrofitServiceFactory.getSpaService().getJournalVideoDetail(getUserToken(), contentVideo.getMicroVideo().getResourcePath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<>(new Callback<JournalVideoDetailResult>() {
                    @Override
                    public void onResult(Throwable error, JournalVideoDetailResult result) {
                        if (error == null) {
                            contentVideo.getMicroVideo().setAccessUrl(result.respData.playUrl);
                            contentVideo.getMicroVideo().setVideoCoverUrl(result.respData.coverUrl);
                        }
                        callback.onResult(error, null);
                    }
                }));
    }

    //使用ID在现有的期刊列表中查找期刊,如果ID是用于创建期刊，那么返回创建期刊
    public Journal getJournalById(int journalId) {
        if (mJournals != null) {
            for (Journal journal : mJournals) {
                if (journal.getId() == journalId) {
                    return journal;
                }
            }
        }
        throw new RuntimeException("invalid journal id:" + journalId);
    }

    public int getPublishedSize() {
        int count = 0;
        for (Journal journal : mJournals) {
            if (journal.getStatus() == Journal.STATUS_PUBLISHED) {
                count++;
            }
        }
        return count;
    }

    public int getContentTypesSize() {
        return mJournalContentTypes.size();
    }

    public Journal getOriginJournal() {
        return mOriginJournal;
    }

    public void setOriginJournal(Journal originJournal) {
        this.mOriginJournal = originJournal;
    }

    public Journal getTemporaryJournal() {
        return mTemporaryJournal;
    }

    public void setTemporaryJournal(Journal temporaryJournal) {
        this.mTemporaryJournal = temporaryJournal;
    }

    public Journal createDefaultJournal() {
        Journal journal = new Journal(Journal.CREATE_JOURNAL_ID);
        journal.setTemplate(mJournalTemplates.get(0));
        return journal;
    }
}
