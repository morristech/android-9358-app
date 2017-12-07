package com.xmd.inner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-1.
 * 内网订单
 */

public class OrderInfo implements Parcelable{
    public int amount;    //账单价格,单位为分
    public String batchNo;
    public String businessNo;    //帐单编号
    public long id;    //账单ID

    public List<ConsumeInfo> itemList;    //账单项列表

    public String payNo;    //支付流水号
    public long roomId;    //房间ID
    public String roomName;    //房间名称
    public String roomTypeName;    //房间类型名称
    public long seatId;    //账单座位ID
    public String seatName;
    public String startTime;    //开始时间
    public String closeTime;
    public int status;    //账单状态
    public String userIdentify;    //用户手牌标识

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.amount);
        dest.writeString(this.batchNo);
        dest.writeString(this.businessNo);
        dest.writeLong(this.id);
        dest.writeList(this.itemList);
        dest.writeString(this.payNo);
        dest.writeLong(this.roomId);
        dest.writeString(this.roomName);
        dest.writeString(this.roomTypeName);
        dest.writeLong(this.seatId);
        dest.writeString(this.seatName);
        dest.writeString(this.startTime);
        dest.writeString(this.closeTime);
        dest.writeInt(this.status);
        dest.writeString(this.userIdentify);
    }

    public OrderInfo() {
    }

    protected OrderInfo(Parcel in) {
        this.amount = in.readInt();
        this.batchNo = in.readString();
        this.businessNo = in.readString();
        this.id = in.readLong();
        this.itemList = new ArrayList<ConsumeInfo>();
        in.readList(this.itemList, ConsumeInfo.class.getClassLoader());
        this.payNo = in.readString();
        this.roomId = in.readLong();
        this.roomName = in.readString();
        this.roomTypeName = in.readString();
        this.seatId = in.readLong();
        this.seatName = in.readString();
        this.startTime = in.readString();
        this.closeTime = in.readString();
        this.status = in.readInt();
        this.userIdentify = in.readString();
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel source) {
            return new OrderInfo(source);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };
}

