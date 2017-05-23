package com.xmd.manager.journal.model;

import android.text.TextUtils;

/**
 * Created by heyangya on 16-11-10.
 */

public class AlbumPhoto implements Cloneable {
    private String mLocalPath;
    private String mImageId;
    private boolean mNeedUpload;
    private String mRemoteUrl;

    public AlbumPhoto(String localPath) {
        mLocalPath = localPath;
        mNeedUpload = true;
    }

    public AlbumPhoto() {

    }

    public String getLocalPath() {
        return mLocalPath;
    }

    public void setLocalPath(String path) {
        if (!TextUtils.equals(mLocalPath, path)) {
            mNeedUpload = true;
            mImageId = null;
            mRemoteUrl = null;
            mLocalPath = path;
        }
    }

    public boolean isNeedUpload() {
        return mNeedUpload;
    }

    public void setNeedUpload(boolean needUpload) {
        mNeedUpload = needUpload;
    }

    public String getImageId() {
        return mImageId;
    }

    public void setImageId(String imageId) {
        this.mImageId = imageId;
        mNeedUpload = TextUtils.isEmpty(imageId);
    }

    public String getRemoteUrl() {
        return mRemoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        mRemoteUrl = remoteUrl;
    }

    @Override
    public AlbumPhoto clone() throws CloneNotSupportedException {
        return (AlbumPhoto) super.clone();
    }
}
