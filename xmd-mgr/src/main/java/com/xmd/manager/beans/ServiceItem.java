package com.xmd.manager.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/11/11.
 */
public class ServiceItem implements Parcelable {
    public String id;
    public String name;
    public String imageUrl;

    protected ServiceItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<ServiceItem> CREATOR = new Creator<ServiceItem>() {
        @Override
        public ServiceItem createFromParcel(Parcel in) {
            return new ServiceItem(in);
        }

        @Override
        public ServiceItem[] newArray(int size) {
            return new ServiceItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ServiceItem)) {
            return false;
        }
        return id.equals(((ServiceItem) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
