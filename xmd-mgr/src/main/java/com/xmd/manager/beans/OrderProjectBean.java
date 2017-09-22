package com.xmd.manager.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 17-9-20.
 */

public class OrderProjectBean implements Parcelable{

    /**
     * id : 751224719469977600
     * name : 8888
     * price : 1
     * duration : 344
     * durationUnit : 分钟
     */

    public String id;
    public String name;
    public String price;
    public String duration;
    public String durationUnit;
    public int isSelect ; //0：未被选中，1：被选中

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.duration);
        dest.writeString(this.durationUnit);
        dest.writeInt(this.isSelect);
    }

    public OrderProjectBean() {
    }

    protected OrderProjectBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.price = in.readString();
        this.duration = in.readString();
        this.durationUnit = in.readString();
        this.isSelect = in.readInt();
    }

    public static final Creator<OrderProjectBean> CREATOR = new Creator<OrderProjectBean>() {
        @Override
        public OrderProjectBean createFromParcel(Parcel source) {
            return new OrderProjectBean(source);
        }

        @Override
        public OrderProjectBean[] newArray(int size) {
            return new OrderProjectBean[size];
        }
    };
}
