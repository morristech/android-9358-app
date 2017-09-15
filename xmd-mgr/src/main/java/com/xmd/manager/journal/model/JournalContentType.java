package com.xmd.manager.journal.model;

import android.support.annotation.NonNull;

/**
 * Created by heyangya on 16-10-31.
 */

public class JournalContentType {

    public static final String CONTENT_KEY_TECHNICIAN = "01"; //技师
    public static final String CONTENT_KEY_SERVICE = "02"; //项目
    public static final String CONTENT_KEY_PHOTO_ALBUM = "03";//相册
    public static final String CONTENT_KEY_TECHNICIAN_RANKING = "04";//服务之星
    public static final String CONTENT_KEY_VIDEO = "05";//小视频
    public static final String CONTENT_KEY_COUPON = "06";//优惠活动
    public static final String CONTENT_KEY_ARTICLE = "07";//养生专栏
    public static final String CONTENT_KEY_ACTIVITY = "08";//活动
    public static final String CONTENT_KEY_IMAGE_ARTICLE = "09";//图文

    private int mId;
    private String mKey;
    private String mName;

    public JournalContentType(int id, @NonNull String key, @NonNull String name) {
        mId = id;
        mKey = key;
        mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof JournalContentType)) {
            return false;
        }
        JournalContentType that = (JournalContentType) o;
        return mKey.equals(that.getKey());
    }

    @Override
    public int hashCode() {
        return mKey.hashCode();
    }
}
