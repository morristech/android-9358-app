package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by zr on 17-5-19.
 */

public class CheckInfo implements Parcelable {
    private String typeName; //业务名称
    private String type; //业务类型,paid_coupon-点钟券,coupon-优惠券,service_item_coupon-项目券,order-付费预约
    private String title;
    private String description; //描述
    private String code; //核销码
    private Boolean valid; //当前能否使用（时间限制）
    private Object info;
    private int infoType; // 1--券，2--订单

    //核销结果
    private Boolean selected;
    private Boolean success;
    private int errorCode;
    private String errorMsg;

    protected CheckInfo(Parcel in) {
        typeName = in.readString();
        type = in.readString();
        title = in.readString();
        description = in.readString();
        code = in.readString();
        valid = in.readByte() == 1;
        selected = in.readByte() == 0;
        success = in.readByte() == 0;
        errorCode = in.readInt();
        infoType = in.readInt();
        errorMsg = in.readString();
        String infoString = in.readString();
        if (!infoString.equals("null")) {
            info = infoString;
        }
    }

    public static final Creator<CheckInfo> CREATOR = new Creator<CheckInfo>() {
        @Override
        public CheckInfo createFromParcel(Parcel in) {
            return new CheckInfo(in);
        }

        @Override
        public CheckInfo[] newArray(int size) {
            return new CheckInfo[size];
        }
    };

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getValid() {
        return valid == null ? false : valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public Boolean getSelected() {
        return selected == null ? false : selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getSuccess() {
        return success == null ? false : success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getInfoType() {
        return infoType;
    }

    public void setInfoType(int infoType){
        this.infoType = infoType;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(typeName);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(code);
        dest.writeByte((byte) (valid ? 1 : 0));
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(infoType);
        dest.writeInt(errorCode);
        dest.writeString(errorMsg);
        String infoString = "null";
        if (info != null) {
            infoString = new Gson().toJson(info);
        }
        dest.writeString(infoString);
    }

    @Override
    public int hashCode() {
        // TODO
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        // TODO
        return super.equals(o);
    }
}
