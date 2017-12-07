package com.xmd.inner.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zr on 17-12-1.
 * 消费项目服务技师
 */

public class EmployeeInfo implements Parcelable{
    public String employeeId;
    public String employeeNo;
    public String bellType;
    public Integer bellId;
    public String bellName;
    public int commissionAmount;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.employeeId);
        dest.writeString(this.employeeNo);
        dest.writeString(this.bellType);
        dest.writeValue(this.bellId);
        dest.writeString(this.bellName);
        dest.writeInt(this.commissionAmount);
    }

    public EmployeeInfo() {
    }

    protected EmployeeInfo(Parcel in) {
        this.employeeId = in.readString();
        this.employeeNo = in.readString();
        this.bellType = in.readString();
        this.bellId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.bellName = in.readString();
        this.commissionAmount = in.readInt();
    }

    public static final Creator<EmployeeInfo> CREATOR = new Creator<EmployeeInfo>() {
        @Override
        public EmployeeInfo createFromParcel(Parcel source) {
            return new EmployeeInfo(source);
        }

        @Override
        public EmployeeInfo[] newArray(int size) {
            return new EmployeeInfo[size];
        }
    };
}
