package com.xmd.manager.beans;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;

/**
 * Created by heyangya on 17-5-11.
 * 核销信息
 */
public class CheckInfo implements Parcelable {
    public static final int INFO_TYPE_TICKET = 1;
    public static final int INFO_TYPE_ORDER = 2;

    private String typeName; //业务名称
    private String type; //业务类型,paid_coupon-点钟券,coupon-优惠券,service_item_coupon-项目券,order-付费预约
    private String title;
    private String description; //描述
    private String code; //核销码
    private Boolean valid; //当前能否使用（时间限制）
    private Integer infoType; // 1--券，2--订单
    private Object info;
    private Boolean selected;

    private Boolean showDetail;


    public CheckInfo() {

    }

    protected CheckInfo(Parcel in) {
        typeName = in.readString();
        type = in.readString();
        title = in.readString();
        description = in.readString();
        code = in.readString();
        valid = in.readByte() == 1;

        infoType = in.readInt();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public Integer getInfoType() {
        return infoType;
    }

    public void setInfoType(Integer infoType) {
        this.infoType = infoType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getValid() {
        return valid == null ? false : valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getShowDetail() {
        return showDetail;
    }

    public void setShowDetail(Boolean showDetail) {
        this.showDetail = showDetail;
    }

    @Override
    public String toString() {
        return "CheckInfo{" +
                "typeName='" + typeName + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", valid=" + valid +
                ", info=" + info +
                ", selected=" + selected +
                '}';
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
        dest.writeInt(infoType);
        String infoString = "null";
        if (info != null) {
            infoString = new Gson().toJson(info);
        }
        dest.writeString(infoString);
    }


    @BindingAdapter("status")
    public static void bindStatus(TextView view, Boolean canUse) {
        if (canUse != null) {
            if (canUse) {
                view.setTextColor(ResourceUtils.getColor(R.color.colorClubItemBody));
                view.setText(ResourceUtils.getString(R.string.coupon_usable));
            } else {
                view.setTextColor(ResourceUtils.getColor(R.color.amount_color));
                view.setText(ResourceUtils.getString(R.string.coupon_unusable));
            }
        }
    }
}