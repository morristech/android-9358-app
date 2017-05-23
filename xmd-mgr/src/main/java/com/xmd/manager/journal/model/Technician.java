package com.xmd.manager.journal.model;

/**
 * Created by heyangya on 16-10-31.
 */

public class Technician {
    private String mId;
    private String mNo;
    private String mName;
    private String mIconUrl;

    public Technician(String id, String techNo, String name, String iconUrl) {
        mId = id;
        mNo = techNo;
        mName = name;
        mIconUrl = iconUrl;
    }

    public String getId() {
        return mId;
    }

    public String getNo() {
        return mNo;
    }

    public String getName() {
        return mName;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public void setIconUrl(String url) {
        mIconUrl = url;
    }
}
