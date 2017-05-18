package com.xmd.technician.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sdcm on 17-5-15.
 */

public class MarketingChatShareBean implements Parcelable{
        /**
         * actId : 859972404716269569
         * actName : 推背
         * actPrice : 1
         * unitPrice : 1
         * image : http://sdcm103.stonebean.com:8489/s/group00/M00/00/2D/oIYBAFW_PuiAcSQSAABGkNm4MAw061.jpg?st=msbhQNb2LbTQ6HMVB8ebLQ&e=1497168703
         * shareUrl : http://t.cn/RaaGyBO
         */
        /**
         * amount : 1
         * image : http://sdcm103.stonebean.com:8489/s/group00/M00/00/40/oYYBAFW1-XKAQMNXAABcSndzckM882.jpg?st=ihLLKGnpUzvQW5n5xGF5aA&e=1497168703
         * id : 859967781284638720
         * itemId : 751224719469977600
         * price : 546
         * usePeriod : 1,2,3,4,5,6,0
         * itemName : 88888
         * shareUrl : http://t.cn/RaaGy1O
         * credits : 0
         * limitedUse : false
         */
        /**
         * actId : 63
         * actName : 测试在线转盘
         * endTime : 2017-06-08 16:51:28
         * startTime : 2017-05-10 16:51:28
         * firstPrizeName : 100积分
         * image : http://dev.xiaomodo.com/spa-manager/img/luckywheel/luckywheel.png
         * shareUrl : null
         */

        public int actPrice;
        public int unitPrice;
        public int amount;
        public String id;
        public String itemId;
        public String price;
        public String usePeriod;
        public String itemName;
        public int credits;
        public boolean limitedUse;
        public String actId;
        public String actName;
        public String endTime;
        public String startTime;
        public String firstPrizeName;
        public String image;
        public String shareUrl;
        public int selectedStatus; //1可被选中且未被选中，2，可被选中且已被选中


        @Override
        public int describeContents() {
                return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.actPrice);
                dest.writeInt(this.unitPrice);
                dest.writeInt(this.amount);
                dest.writeString(this.id);
                dest.writeString(this.itemId);
                dest.writeString(this.price);
                dest.writeString(this.usePeriod);
                dest.writeString(this.itemName);
                dest.writeInt(this.credits);
                dest.writeByte(this.limitedUse ? (byte) 1 : (byte) 0);
                dest.writeString(this.actId);
                dest.writeString(this.actName);
                dest.writeString(this.endTime);
                dest.writeString(this.startTime);
                dest.writeString(this.firstPrizeName);
                dest.writeString(this.image);
                dest.writeString(this.shareUrl);
                dest.writeInt(this.selectedStatus);
        }

        public MarketingChatShareBean() {
        }

        protected MarketingChatShareBean(Parcel in) {
                this.actPrice = in.readInt();
                this.unitPrice = in.readInt();
                this.amount = in.readInt();
                this.id = in.readString();
                this.itemId = in.readString();
                this.price = in.readString();
                this.usePeriod = in.readString();
                this.itemName = in.readString();
                this.credits = in.readInt();
                this.limitedUse = in.readByte() != 0;
                this.actId = in.readString();
                this.actName = in.readString();
                this.endTime = in.readString();
                this.startTime = in.readString();
                this.firstPrizeName = in.readString();
                this.image = in.readString();
                this.shareUrl = in.readString();
                this.selectedStatus = in.readInt();
        }

        public static final Creator<MarketingChatShareBean> CREATOR = new Creator<MarketingChatShareBean>() {
                @Override
                public MarketingChatShareBean createFromParcel(Parcel source) {
                        return new MarketingChatShareBean(source);
                }

                @Override
                public MarketingChatShareBean[] newArray(int size) {
                        return new MarketingChatShareBean[size];
                }
        };
}
