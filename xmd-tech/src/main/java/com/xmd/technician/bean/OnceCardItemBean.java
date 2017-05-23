package com.xmd.technician.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 2017/2/20.
 */

public class OnceCardItemBean implements Parcelable{
    public String id;
    public String name;
    public String imageUrl;
    public boolean isPreferential;
    public String comboDescription;
    public String techRoyalty;
    public String price;
    public String shareUrl;
    public String shareDescription;
    public int selectedStatus;

    public OnceCardItemBean(String id,String name,String imageUrl, boolean isPreferential, String comboDescription,String shareDescription,String techRoyalty, String price,String shareUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isPreferential = isPreferential;
        this.comboDescription = comboDescription;
        this.techRoyalty = techRoyalty;
        this.price = price;
        this.shareUrl = shareUrl;
        this.shareDescription = shareDescription;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
        dest.writeByte(this.isPreferential ? (byte) 1 : (byte) 0);
        dest.writeString(this.comboDescription);
        dest.writeString(this.techRoyalty);
        dest.writeString(this.price);
        dest.writeString(this.shareUrl);
        dest.writeString(this.shareDescription);
        dest.writeInt(this.selectedStatus);
    }

    protected OnceCardItemBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imageUrl = in.readString();
        this.isPreferential = in.readByte() != 0;
        this.comboDescription = in.readString();
        this.techRoyalty = in.readString();
        this.price = in.readString();
        this.shareUrl = in.readString();
        this.shareDescription = in.readString();
        this.selectedStatus = in.readInt();
    }

    public static final Creator<OnceCardItemBean> CREATOR = new Creator<OnceCardItemBean>() {
        @Override
        public OnceCardItemBean createFromParcel(Parcel source) {
            return new OnceCardItemBean(source);
        }

        @Override
        public OnceCardItemBean[] newArray(int size) {
            return new OnceCardItemBean[size];
        }
    };
}
