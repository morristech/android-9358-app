package com.m.pk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 18-1-9.
 */

public class PkItemBean implements Parcelable {
    public static final Creator<PkItemBean> CREATOR = new Creator<PkItemBean>() {
        @Override
        public PkItemBean createFromParcel(Parcel source) {
            return new PkItemBean(source);
        }

        @Override
        public PkItemBean[] newArray(int size) {
            return new PkItemBean[size];
        }
    };
    public String key;
    public String value;

    public PkItemBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public PkItemBean() {
    }

    protected PkItemBean(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
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
}
