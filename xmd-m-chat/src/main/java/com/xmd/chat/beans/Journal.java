package com.xmd.chat.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mo on 17-7-10.
 * 电子期刊
 */

public class Journal implements Parcelable {
    public String journalId;
    public int sequenceNo;
    public String title;
    public String subTitle;
    public String modifyDate;
    public int templateId;
    public String image;
    public String shareUrl;

    protected Journal(Parcel in) {
        journalId = in.readString();
        sequenceNo = in.readInt();
        title = in.readString();
        subTitle = in.readString();
        modifyDate = in.readString();
        templateId = in.readInt();
        image = in.readString();
        shareUrl = in.readString();
    }

    public static final Creator<Journal> CREATOR = new Creator<Journal>() {
        @Override
        public Journal createFromParcel(Parcel in) {
            return new Journal(in);
        }

        @Override
        public Journal[] newArray(int size) {
            return new Journal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(journalId);
        dest.writeInt(sequenceNo);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(modifyDate);
        dest.writeInt(templateId);
        dest.writeString(image);
        dest.writeString(shareUrl);
    }
}
