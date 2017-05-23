package com.xmd.manager.journal.model;

/**
 * Created by heyangya on 16-11-18.
 */

public class JournalItemPhoto extends JournalItemBase {
    private AlbumPhoto mPhoto;

    public JournalItemPhoto(AlbumPhoto photo) {
        super(null);
        this.mPhoto = photo;
    }

    public JournalItemPhoto(String data) {
        super(data);
        mPhoto = new AlbumPhoto();
        mPhoto.setImageId(data);
        mPhoto.setNeedUpload(false);
    }

    @Override
    public String contentToString() {
        return mPhoto == null ? null : mPhoto.getImageId();
    }

    public AlbumPhoto getPhoto() {
        return mPhoto;
    }

    @Override
    public String isDataReady() {
        if (mPhoto.isNeedUpload()) {
            return "图片还没有上传";
        }
        return "true";
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (mPhoto != null) {
            hashCode += mPhoto.hashCode();
        } else {
            hashCode = super.hashCode();
        }
        return hashCode;
    }

    @Override
    public JournalItemBase clone() throws CloneNotSupportedException {
        JournalItemPhoto copy = (JournalItemPhoto) super.clone();
        copy.mPhoto = mPhoto.clone();
        return copy;
    }
}
