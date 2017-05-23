package com.xmd.manager.journal.model;

import android.text.TextUtils;

/**
 * Created by heyangya on 16-11-17.
 */

public class MicroVideo implements Cloneable {
    private String localUrl; //本地路径
    private String accessUrl; //下载URL
    private String resourcePath;//资源路径，服务器需要
    private String videoCoverUrl;//视频封面

    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        if (!TextUtils.equals(this.localUrl, localUrl)) {
            this.localUrl = localUrl;
            accessUrl = null;
            resourcePath = null;
        }
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }


    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getVideoCoverUrl() {
        return videoCoverUrl;
    }

    public void setVideoCoverUrl(String videoCoverUrl) {
        this.videoCoverUrl = videoCoverUrl;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (resourcePath != null) {
            hashCode += resourcePath.hashCode();
        }
        return hashCode;
    }

    @Override
    protected MicroVideo clone() throws CloneNotSupportedException {
        return (MicroVideo) super.clone();
    }
}
