package com.xmd.manager.beans;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.xmd.manager.common.DescribeMesaageUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lhj on 2016/12/8.
 */

public class VerificationCouponDetailBean implements Parcelable, Serializable {

    /**
     * startDate :
     * suaId : 839305069559029760
     * actId : 839304989334577152
     * couponNo : 107809347063
     * useType : coupon
     * couponType : ordinary
     * endUseDate :
     * useTypeName : 优惠券
     * endDate :
     * couponPeriod : 2017-03-08 10:00 至 长期有效
     * consumeAmount : 400
     * actContent : <ul><li>使用时，请出示手机号码或者优惠码。</li><li>每张券仅限一人使用，仅能使用一张。</li><li>使用此券，不可享受本店其他优惠。</li><li>提供免费WiFi。</li><li>提供免费停车位。</li><li>欢迎提前预约。</li></ul>
     * getDate : 2017-03-08 10:41:36
     * actStatus : online
     * itemNames : ["123456789012345","4565"]
     * couponTypeName : 普通券
     * useTimePeriod : 周一，周二，周三，周四，周五，周六，周日 10:00 - 11:00
     * actTitle : 优惠券03
     * actSubTitle : 优惠券03
     * actStatusName : 在线
     * actAmount : 300
     * paidType : null
     * consumeMoneyDescription : 现价3元，原价4元
     */

    public String startDate;
    public String suaId;
    public String actId;
    public String couponNo;
    public String useType;
    public String couponType;
    public String endUseDate;
    public String useTypeName;
    public String endDate;
    public String couponPeriod;
    public int consumeAmount;
    public String actContent;
    public String getDate;
    public String actStatus;
    public String couponTypeName;
    public String useTimePeriod;
    public String actTitle;
    public String actSubTitle;
    public String actStatusName;
    public int actAmount;
    public Object paidType;
    public String consumeMoneyDescription;
    public List<String> itemNames;


    protected VerificationCouponDetailBean(Parcel in) {
        startDate = in.readString();
        suaId = in.readString();
        actId = in.readString();
        couponNo = in.readString();
        useType = in.readString();
        couponType = in.readString();
        endUseDate = in.readString();
        useTypeName = in.readString();
        endDate = in.readString();
        couponPeriod = in.readString();
        consumeAmount = in.readInt();
        actContent = in.readString();
        getDate = in.readString();
        actStatus = in.readString();
        couponTypeName = in.readString();
        useTimePeriod = in.readString();
        actTitle = in.readString();
        actSubTitle = in.readString();
        actStatusName = in.readString();
        actAmount = in.readInt();
        consumeMoneyDescription = in.readString();
        itemNames = in.createStringArrayList();
    }

    public static final Creator<VerificationCouponDetailBean> CREATOR = new Creator<VerificationCouponDetailBean>() {
        @Override
        public VerificationCouponDetailBean createFromParcel(Parcel in) {
            return new VerificationCouponDetailBean(in);
        }

        @Override
        public VerificationCouponDetailBean[] newArray(int size) {
            return new VerificationCouponDetailBean[size];
        }
    };

    //绑定数据
    @BindingAdapter("useTimes")
    public static void bindUserTimes(TextView view, String times) {
        view.setText(DescribeMesaageUtil.getTimePeriodDes(times));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startDate);
        dest.writeString(suaId);
        dest.writeString(actId);
        dest.writeString(couponNo);
        dest.writeString(useType);
        dest.writeString(couponType);
        dest.writeString(endUseDate);
        dest.writeString(useTypeName);
        dest.writeString(endDate);
        dest.writeString(couponPeriod);
        dest.writeInt(consumeAmount);
        dest.writeString(actContent);
        dest.writeString(getDate);
        dest.writeString(actStatus);
        dest.writeString(couponTypeName);
        dest.writeString(useTimePeriod);
        dest.writeString(actTitle);
        dest.writeString(actSubTitle);
        dest.writeString(actStatusName);
        dest.writeInt(actAmount);
        dest.writeString(consumeMoneyDescription);
        dest.writeStringList(itemNames);
    }
}
