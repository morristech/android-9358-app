package com.xmd.technician.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lhj on 2017/2/9.
 */

public class ClubJournalBean implements Parcelable {


    public static final Creator<ClubJournalBean> CREATOR = new Creator<ClubJournalBean>() {
        @Override
        public ClubJournalBean createFromParcel(Parcel source) {
            return new ClubJournalBean(source);
        }

        @Override
        public ClubJournalBean[] newArray(int size) {
            return new ClubJournalBean[size];
        }
    };
    /**
     * journalId : 99
     * sequenceNo : 2
     * title : 啦啦
     * subTitle : 德玛西亚
     * modifyDate : 2017-02-22 15:43:17
     * templateId : 1
     * image : http://sdcm162.stonebean.com/spa-manager/img/journal/1.png
     * shareUrl : http://t.cn/RJdilKD
     */

    public String journalId;
    public int sequenceNo;
    public String title;
    public String subTitle;
    public String modifyDate;
    public int templateId;
    public String image;
    public String shareUrl;
    public int selectedStatus; //1可被选中且未被选中，2，可被选中且已被选中

    public ClubJournalBean() {
    }

    protected ClubJournalBean(Parcel in) {
        this.journalId = in.readString();
        this.sequenceNo = in.readInt();
        this.title = in.readString();
        this.subTitle = in.readString();
        this.modifyDate = in.readString();
        this.templateId = in.readInt();
        this.image = in.readString();
        this.shareUrl = in.readString();
        this.selectedStatus = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.journalId);
        dest.writeInt(this.sequenceNo);
        dest.writeString(this.title);
        dest.writeString(this.subTitle);
        dest.writeString(this.modifyDate);
        dest.writeInt(this.templateId);
        dest.writeString(this.image);
        dest.writeString(this.shareUrl);
        dest.writeInt(this.selectedStatus);
    }
}