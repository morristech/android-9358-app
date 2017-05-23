package com.xmd.manager.journal.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by heyangya on 16-10-31.
 * 期刊模板，模板中包含了所有的内容项，并且指定了某些内容项的子内容个数，没有指定的话，默认数量为1
 */

public class JournalTemplate {
    private int mId;
    private String mName;
    private String mImageUrl;
    private List<JournalContentType> mRecommendContentTypes;
    private List<JournalContentType> mContentTypes;
    private Map<JournalContentType, Integer> mSubContentCountMap; //并且指定了内容项中子内容的数量
    private String mPreviewUrl;

    public JournalTemplate() {
        mRecommendContentTypes = new ArrayList<>();
        mContentTypes = new ArrayList<>();
        mSubContentCountMap = new HashMap<>();
    }

    public void addContentType(JournalContentType contentType, int subContentCount) {
        if (!mContentTypes.contains(contentType)) {
            mContentTypes.add(contentType);
            mSubContentCountMap.put(contentType, subContentCount);
        }
    }

    public JournalContentType getContentType(int position) {
        return mContentTypes.get(position);
    }

    public int getContentTypeSize() {
        return mContentTypes.size();
    }

    public Iterator<JournalContentType> getContentTypeIterator() {
        return mContentTypes.iterator();
    }

    public void setContentTypeSubContentCount(JournalContentType contentType, int subContentCount) {
        if (mContentTypes.contains(contentType)) {
            mSubContentCountMap.put(contentType, subContentCount);
        }
    }

    public int getSubContentCount(JournalContentType contentType) {
        return mSubContentCountMap.get(contentType);
    }


    public boolean isRecommendContentType(JournalContentType contentType) {
        return mRecommendContentTypes.contains(contentType);
    }

    public void addRecommendContentType(JournalContentType contentType) {
        if (mContentTypes.contains(contentType) && !mRecommendContentTypes.contains(contentType)) {
            mRecommendContentTypes.add(contentType);
        }
    }

    public int getRecommendContentTypeSize() {
        return mRecommendContentTypes.size();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.mPreviewUrl = previewUrl;
    }
}
