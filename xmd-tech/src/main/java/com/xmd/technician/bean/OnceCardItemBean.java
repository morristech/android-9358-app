package com.xmd.technician.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 2017/2/20.
 */

public class OnceCardItemBean implements Parcelable{

    public String id;
    public String cardType;  //类型 tem_card-单项次卡;item_package-混合套餐；credit_gift-积分礼品
    public String name;     //名称
    public String imageUrl; //图片
    public String comboDescription;//套餐描述
    public String techRoyalty; //技师提成
 //   public String price; //商品价格
    public String shareUrl; //分享链接
    public String shareDescription; //分享描述语言
    public String sellingPrice; //销售价格
    public String depositRate;//折扣率
    public int type;
    public int position;
    public int selectedStatus;

    public OnceCardItemBean() {

    }

    public OnceCardItemBean(String id,String cardType,int type,int position,String name,String imageUrl,String comboDescription,String shareDescription,String techRoyalty,String shareUrl,String sellingPrice,String depositRate) {
        this.id = id;
        this.cardType = cardType;
        this.type = type;
        this.position = position;
        this.name = name;
        this.imageUrl = imageUrl;
        this.comboDescription = comboDescription;
        this.techRoyalty = techRoyalty;
        this.shareUrl = shareUrl;
        this.shareDescription = shareDescription;
        this.sellingPrice = sellingPrice;
        this.depositRate = depositRate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.cardType);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
        dest.writeString(this.comboDescription);
        dest.writeString(this.techRoyalty);
        dest.writeString(this.shareUrl);
        dest.writeString(this.shareDescription);
        dest.writeString(this.sellingPrice);
        dest.writeString(this.depositRate);
        dest.writeInt(this.type);
        dest.writeInt(this.position);
        dest.writeInt(this.selectedStatus);
    }

    protected OnceCardItemBean(Parcel in) {
        this.id = in.readString();
        this.cardType = in.readString();
        this.name = in.readString();
        this.imageUrl = in.readString();
        this.comboDescription = in.readString();
        this.techRoyalty = in.readString();
        this.shareUrl = in.readString();
        this.shareDescription = in.readString();
        this.sellingPrice = in.readString();
        this.depositRate = in.readString();
        this.type = in.readInt();
        this.position = in.readInt();
        this.selectedStatus = in.readInt();
    }

    public static final Creator<OnceCardItemBean> CREATOR = new Creator<OnceCardItemBean>() {
        @Override
        public OnceCardItemBean createFromParcel(Parcel source) {
            return new OnceCardItemBean(source);
        }

        @Override
        public OnceCardItemBean[] newArray(int size) {
            return new OnceCardItemBean[size];
        }
    };
}
