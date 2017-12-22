package com.xmd.technician.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 17-6-22.
 */

public class PosterBean implements Parcelable {
    public static final Creator<PosterBean> CREATOR = new Creator<PosterBean>() {
        @Override
        public PosterBean createFromParcel(Parcel source) {
            return new PosterBean(source);
        }

        @Override
        public PosterBean[] newArray(int size) {
            return new PosterBean[size];
        }
    };
    /**
     * id : 14
     * techId : 748081899301244928
     * clubId : 621280172275933185
     * title : 大标题
     * subTitle : 小标题
     * imageId : 73748
     * name : 昵称
     * techNo : 12245
     * clubName : 93
     * style : null
     * status : 1
     * createTime : 1498216024000
     * modifyTime : 1498216024000
     * imageUrl : http://sdcm222:8489/s/group00/M00/01/75/ooYBAFlM9leAYxe4AAAAWyjLL245825490?st=-q1fvchJXjrwhKVg4jg1ZA&e=1500809980
     */

    public int id;
    public String techId;
    public String clubId;
    public String title;
    public String subTitle;
    public String imageId;
    public String name;
    public String techNo;
    public String clubName;
    public String style;
    public int status;
    public long createTime;
    public long modifyTime;
    public String imageUrl;
    public String qrCodeUrl;
    public String validDate;
    public String shareUrl;

    public PosterBean() {

    }

    protected PosterBean(Parcel in) {
        this.id = in.readInt();
        this.techId = in.readString();
        this.clubId = in.readString();
        this.title = in.readString();
        this.subTitle = in.readString();
        this.imageId = in.readString();
        this.name = in.readString();
        this.techNo = in.readString();
        this.clubName = in.readString();
        this.style = in.readString();
        this.status = in.readInt();
        this.createTime = in.readLong();
        this.modifyTime = in.readLong();
        this.imageUrl = in.readString();
        this.qrCodeUrl = in.readString();
        this.validDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.techId);
        dest.writeString(this.clubId);
        dest.writeString(this.title);
        dest.writeString(this.subTitle);
        dest.writeString(this.imageId);
        dest.writeString(this.name);
        dest.writeString(this.techNo);
        dest.writeString(this.clubName);
        dest.writeString(this.style);
        dest.writeInt(this.status);
        dest.writeLong(this.createTime);
        dest.writeLong(this.modifyTime);
        dest.writeString(this.imageUrl);
        dest.writeString(this.qrCodeUrl);
        dest.writeString(this.validDate);
    }
}
