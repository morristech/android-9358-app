package com.xmd.manager.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 17-9-20.
 */

public class OrderTechNoBean implements Parcelable{

    /**
     * id : 602023060890980352177279
     * name : 啊希啊
     * telephone : 17727979916
     * techNo : 98989
     * avatar : 144259
     * avatarUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/03/60/ooYBAFW4Tb2AaNjCAAAP34Bfjrc962.jpg?st=a7OkPWrWv7-lXQV-DK16bg&e=1507453345
     */

    public String id;
    public String name;
    public String telephone;
    public String techNo;
    public String avatar;
    public String avatarUrl;
    public int isSelect ; //0：未被选中，1：被选中

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.telephone);
        dest.writeString(this.techNo);
        dest.writeString(this.avatar);
        dest.writeString(this.avatarUrl);
        dest.writeInt(this.isSelect);
    }

    public OrderTechNoBean() {
    }

    protected OrderTechNoBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.telephone = in.readString();
        this.techNo = in.readString();
        this.avatar = in.readString();
        this.avatarUrl = in.readString();
        this.isSelect = in.readInt();
    }

    public static final Creator<OrderTechNoBean> CREATOR = new Creator<OrderTechNoBean>() {
        @Override
        public OrderTechNoBean createFromParcel(Parcel source) {
            return new OrderTechNoBean(source);
        }

        @Override
        public OrderTechNoBean[] newArray(int size) {
            return new OrderTechNoBean[size];
        }
    };
}
