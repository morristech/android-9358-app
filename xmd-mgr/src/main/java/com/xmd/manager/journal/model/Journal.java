package com.xmd.manager.journal.model;

import android.text.TextUtils;

import com.xmd.manager.journal.manager.TechnicianManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by heyangya on 16-10-31.
 */

public class Journal implements Cloneable {
    public static final int CREATE_JOURNAL_ID = 0;

    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_OFFLINE = 2;
    public static final int STATUS_DELETED = 3;

    private int mId;
    private int mJournalNo;
    private JournalTemplate mTemplate;
    private String mTitle;
    private String mSubTitle;
    private List<JournalContent> mContents;
    private Map<JournalContent, Integer> mContentPositionMap;
    private int mStatus;
    private int mViewCount;
    private int mPraisedCount;
    private int mSharedCount;
    private String mLastModifiedTime;
    private String mPreviewUrl;

    public Journal(int id) {
        mId = id;
        mContents = new ArrayList<>();
        mContentPositionMap = new HashMap<>();
        mTitle = "";
        mSubTitle = "";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id:" + mId + "\n")
                .append("journalNo:" + mJournalNo + "\n")
                .append("title:" + mTitle + "\n")
                .append("subTitle:" + mSubTitle + "\n")
                .append("mStatus:" + mStatus + "\n")
                .append("mViewCount:" + mViewCount + "\n")
                .append("mPraisedCount:" + mPraisedCount + "\n")
                .append("mSharedCount:" + mSharedCount + "\n")
                .append("mLastModifiedTime:" + mLastModifiedTime + "\n");

        return builder.toString();
    }

    //增加一个content，并且按类型ID排序
    public void addContent(JournalContent content) {
        int i = 0;
        for (; i < mContents.size(); i++) {
            if (content.getType().getId() < mContents.get(i).getType().getId()) {
                break;
            }
        }
        mContents.add(i, content);
        mContentPositionMap.put(content, i);
    }

    //增加一个content，并且按指定位置排序
    public void addContent(JournalContent content, int position) {
        int i = 0;
        for (; i < mContents.size(); i++) {
            if (position < mContentPositionMap.get(mContents.get(i))) {
                break;
            }
        }
        mContents.add(i, content);
        mContentPositionMap.put(content, position);
    }

    //清除所有content
    public void clearContent() {
        mContents.clear();
        mContentPositionMap.clear();
    }


    //移除指定类型的content
    public void removeContentByType(JournalContentType type) {
        Iterator<JournalContent> iterator = mContents.iterator();
        while (iterator.hasNext()) {
            JournalContent content = iterator.next();
            if (content.getType().equals(type)) {
                iterator.remove();
                mContentPositionMap.remove(content);
            }
        }
    }

    //内容是否包含某一个内容类型
    public boolean isContentContainType(JournalContentType type) {
        for (JournalContent content : mContents) {
            if (content.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public int getContentSize() {
        return mContents.size();
    }

    //获取指定位置的content
    public JournalContent getContent(int position) {
        if (position > mContents.size() - 1) {
            return null;
        }
        return mContents.get(position);
    }

    //将content向前移动一个位置
    public void moveContentUp(JournalContent content) {
        int index = mContents.indexOf(content);
        if (index > 0) {
            mContents.remove(content);
            mContents.add(index - 1, content);
        }
    }

    //删除一个content
    public void removeContent(JournalContent content) {
        mContents.remove(content);
        mContentPositionMap.remove(content);
    }

    //复制一个content，并增加
    public void copyAndAddContent(JournalContent content) {
        JournalContent newContent = new JournalContent(content);
        mContents.add(mContents.indexOf(content) + 1, newContent);
    }

    //数据有没有准备好，如果准备好，返回"true"，否则返回没有准备好的原因
    public String AllDataIsReady() {
        if (TextUtils.isEmpty(mTitle) || TextUtils.isEmpty(mSubTitle)) {
            return "期刊的标题或子标题没有填写!";
        }
        return contentDataIsReady();
    }

    public String contentDataIsReady() {
        for (JournalContent content : mContents) {
            String result = content.dataIsReady();
            if (!TextUtils.equals(result, "true")) {
                return result;
            }
        }
        return "true";
    }

    public void setId(int id) {
        mId = id;
    }

    public void setTemplate(JournalTemplate template) {
        mTemplate = template;
    }

    public void initContentByTemplate(JournalTemplate template) {
        setTemplate(template);
        mContents.clear();
        mContentPositionMap.clear();
        Iterator<JournalContentType> iterator = mTemplate.getContentTypeIterator();
        while (iterator.hasNext()) {
            JournalContentType type = iterator.next();
            if (mTemplate.isRecommendContentType(type)) {
                JournalContent content = new JournalContent(type, mTemplate.getSubContentCount(type));
                addContent(content);
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
            }
        }
    }

    public Iterator<JournalContent> getContentIterator() {
        return mContents.iterator();
    }

    public void setJournalNo(int journalNo) {
        this.mJournalNo = journalNo;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setSubTitle(String subTitle) {
        this.mSubTitle = subTitle;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public void setViewCount(int viewCount) {
        this.mViewCount = viewCount;
    }

    public void setPraisedCount(int praisedCount) {
        this.mPraisedCount = praisedCount;
    }

    public void setSharedCount(int sharedCount) {
        this.mSharedCount = sharedCount;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.mLastModifiedTime = lastModifiedTime;
    }

    public void setPreviewUrl(String previewUrl) {
        this.mPreviewUrl = previewUrl;
    }

    public int getId() {
        return mId;
    }

    public int getJournalNo() {
        return mJournalNo;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public int getStatus() {
        return mStatus;
    }

    public int getViewCount() {
        return mViewCount;
    }

    public int getPraisedCount() {
        return mPraisedCount;
    }

    public int getSharedCount() {
        return mSharedCount;
    }

    public String getLastModifiedTime() {
        return mLastModifiedTime;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public JournalTemplate getTemplate() {
        return mTemplate;
    }

    //比较两个期刊要保存的数据是否相同，不相同则认为对期刊做了修改
    public boolean saveDataEquals(Journal that) {
        boolean result = that.mId == mId
                && that.mTemplate.getId() == mTemplate.getId()
                && TextUtils.equals(that.mTitle, mTitle)
                && TextUtils.equals(that.mSubTitle, mSubTitle);
        if (!result) {
            return false;
        }
        if (that.getContentSize() != getContentSize()) {
            return false;
        }
        for (int i = 0; i < getContentSize(); i++) {
            JournalContent thatContent = that.getContent(i);
            JournalContent thisContent = this.getContent(i);
            if (!TextUtils.equals(thatContent.getType().getKey(),
                    thisContent.getType().getKey())) {
                return false;
            }
            if (!TextUtils.equals(thatContent.getTitle(),
                    thisContent.getTitle())) {
                return false;
            }
            if (!TextUtils.equals(thatContent.getStringData(),
                    thisContent.getStringData())) {
                return false;
            }

        }
        return true;
    }

    @Override
    public Journal clone() {
        try {
            Journal copy = (Journal) super.clone();
            copy.mContents = new ArrayList<>();
            copy.mContentPositionMap = new HashMap<>();
            for (JournalContent content : mContents) {
                JournalContent newContent = content.clone();
                copy.mContents.add(newContent);
                copy.mContentPositionMap.put(newContent, mContentPositionMap.get(content));
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("could not clone journal!");
        }
    }
}
