package com.example.xmd_m_comment.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 17-7-6.
 */

public class CommentRateBean implements Parcelable{
    /**
     * id : 26
     * userId : 700881129107886080
     * userName : Lin2
     * techId : 768000433410019328
     * techName : nn
     * clubId : 601679316694081537
     * clubName : null
     * commentId : 850303117290373120
     * commentRate : 80
     * commentTagId : 12
     * commentTagName : 技师态度
     * commentTagType : 1
     * createTime : 1491563036000
     */

    public int id;
    public String userId;
    public String userName;
    public String techId;
    public String techName;
    public String clubId;
    public String clubName;
    public String commentId;
    public int commentRate;
    public int commentTagId;
    public String commentTagName;
    public int commentTagType;
    public long createTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.techId);
        dest.writeString(this.techName);
        dest.writeString(this.clubId);
        dest.writeString(this.clubName);
        dest.writeString(this.commentId);
        dest.writeInt(this.commentRate);
        dest.writeInt(this.commentTagId);
        dest.writeString(this.commentTagName);
        dest.writeInt(this.commentTagType);
        dest.writeLong(this.createTime);
    }

    public CommentRateBean() {
    }

    protected CommentRateBean(Parcel in) {
        this.id = in.readInt();
        this.userId = in.readString();
        this.userName = in.readString();
        this.techId = in.readString();
        this.techName = in.readString();
        this.clubId = in.readString();
        this.clubName = in.readString();
        this.commentId = in.readString();
        this.commentRate = in.readInt();
        this.commentTagId = in.readInt();
        this.commentTagName = in.readString();
        this.commentTagType = in.readInt();
        this.createTime = in.readLong();
    }

    public static final Creator<CommentRateBean> CREATOR = new Creator<CommentRateBean>() {
        @Override
        public CommentRateBean createFromParcel(Parcel source) {
            return new CommentRateBean(source);
        }

        @Override
        public CommentRateBean[] newArray(int size) {
            return new CommentRateBean[size];
        }
    };
}
