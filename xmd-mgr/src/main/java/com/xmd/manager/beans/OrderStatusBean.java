package com.xmd.manager.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 17-9-20.
 */

public class OrderStatusBean implements Parcelable{
    public String orderStatusName;
    public String orderStatus;
    public int isSelected ;//0:未被选中，1,被选中

    public OrderStatusBean(String name,String orderStatus,int isSelected){
        this.orderStatus = orderStatus;
        this.orderStatusName = name;
        this.isSelected = isSelected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderStatusName);
        dest.writeString(this.orderStatus);
        dest.writeInt(this.isSelected);
    }

    protected OrderStatusBean(Parcel in) {
        this.orderStatusName = in.readString();
        this.orderStatus = in.readString();
        this.isSelected = in.readInt();
    }

    public static final Creator<OrderStatusBean> CREATOR = new Creator<OrderStatusBean>() {
        @Override
        public OrderStatusBean createFromParcel(Parcel source) {
            return new OrderStatusBean(source);
        }

        @Override
        public OrderStatusBean[] newArray(int size) {
            return new OrderStatusBean[size];
        }
    };
}
