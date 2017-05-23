package com.xmd.manager.journal.model;

import android.text.TextUtils;

/**
 * Created by heyangya on 16-11-30.
 */

public class JournalItemVideo extends JournalItemBase {
    private MicroVideo microVideo;
    private boolean uploading;

    public JournalItemVideo() {
        super(null);
        microVideo = new MicroVideo();
    }

    public JournalItemVideo(String data) {
        super(data);
        microVideo = new MicroVideo();
        microVideo.setResourcePath(data);
    }

    @Override
    public String contentToString() {
        if (microVideo != null) {
            return microVideo.getResourcePath();
        }
        return null;
    }

    public MicroVideo getMicroVideo() {
        return microVideo;
    }

    @Override
    public String isDataReady() {
        if (microVideo != null && (!TextUtils.isEmpty(microVideo.getLocalUrl()) && TextUtils.isEmpty(microVideo.getAccessUrl()))) {
            return "视频还没有上传";
        }
        if (isUploading()) {
            return "视频正在上传，请稍等";
        }
        return super.isDataReady();
    }

    public boolean isUploading() {
        return uploading;
    }

    public void setUploading(boolean uploading) {
        this.uploading = uploading;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (microVideo != null) {
            hashCode += microVideo.hashCode();
        }
        hashCode += uploading ? 1 : 0;
        return hashCode;
    }

    @Override
    public JournalItemBase clone() throws CloneNotSupportedException {
        JournalItemVideo copy = (JournalItemVideo) super.clone();
        copy.microVideo = microVideo.clone();
        return copy;
    }
}
