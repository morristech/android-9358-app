package com.xmd.technician.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 17-3-23.
 */

public class PKItemBean implements Parcelable{
    public String key;
    public String value;

    public PKItemBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    public PKItemBean() {
    }

    protected PKItemBean(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
    }

    public static final Creator<PKItemBean> CREATOR = new Creator<PKItemBean>() {
        @Override
        public PKItemBean createFromParcel(Parcel source) {
            return new PKItemBean(source);
        }

        @Override
        public PKItemBean[] newArray(int size) {
            return new PKItemBean[size];
        }
    };
}
