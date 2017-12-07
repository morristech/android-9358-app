package com.xmd.inner.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-1.
 * 内网订单消费项
 */

public class ConsumeInfo implements Parcelable{
    public long createTime;    //创建时间
    public long endTime;        //预计结束时间
    public long id;    //账单项ID
    public int itemAmount;    //该项价格	单位为分
    public int itemCount;    //消费数量
    public String itemId;    //项目ID
    public String itemName;    //项目名称
    public String itemType;    //项目类型	spa-水疗项目;goods-实物商品
    public String itemUnit;     //项目单位
    public long modifyTime;    //修改时间
    public long orderId;    //关联账单ID
    public int status;    //账单项状态
    public List<EmployeeInfo> employeeList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.createTime);
        dest.writeLong(this.endTime);
        dest.writeLong(this.id);
        dest.writeInt(this.itemAmount);
        dest.writeInt(this.itemCount);
        dest.writeString(this.itemId);
        dest.writeString(this.itemName);
        dest.writeString(this.itemType);
        dest.writeString(this.itemUnit);
        dest.writeLong(this.modifyTime);
        dest.writeLong(this.orderId);
        dest.writeInt(this.status);
        dest.writeList(this.employeeList);
    }

    public ConsumeInfo() {
    }

    protected ConsumeInfo(Parcel in) {
        this.createTime = in.readLong();
        this.endTime = in.readLong();
        this.id = in.readLong();
        this.itemAmount = in.readInt();
        this.itemCount = in.readInt();
        this.itemId = in.readString();
        this.itemName = in.readString();
        this.itemType = in.readString();
        this.itemUnit = in.readString();
        this.modifyTime = in.readLong();
        this.orderId = in.readLong();
        this.status = in.readInt();
        this.employeeList = new ArrayList<EmployeeInfo>();
        in.readList(this.employeeList, EmployeeInfo.class.getClassLoader());
    }

    public static final Creator<ConsumeInfo> CREATOR = new Creator<ConsumeInfo>() {
        @Override
        public ConsumeInfo createFromParcel(Parcel source) {
            return new ConsumeInfo(source);
        }

        @Override
        public ConsumeInfo[] newArray(int size) {
            return new ConsumeInfo[size];
        }
    };
}
