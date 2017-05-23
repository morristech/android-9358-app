package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zr on 16-11-23.
 * 交易流水信息
 */
public class BillInfo implements Parcelable {
    public String id;
    public String userId;
    public String clubId;
    //状态
    public int status;

    //优惠类型
    public int discountType;

    //优惠券优惠金额   ==  优惠金额
    public int couponMoney;
    public int couponDiscountMoney;
    // 手动设置优惠金额 == 手动减免
    public int userDiscountMoney;
    //由于会员卡有可能存在折扣，此为折扣金额   ==  会员折扣
    public String memberCardNo;
    public int memberPayDiscountMoney;
    //需要利用会员卡支付的金额(已扣除折扣，为实际支付金额)   ==  会员卡支付金额
    public int memberPayMoney;
    //pos机消费的金额，其形式为上面posPayType几种  ==  pos支付金额
    public int posPayMoney;
    //订单最初金额    ==  订单金额
    public int originMoney;

    //订单号   ==  交易号
    public String tradeNo;
    //交易类型  ==  POS收款方式
    //交易类型描述    1：现金，2：支付宝，3：微信，4：银联，5：会员	有可能 存在“微信/会员”这种，因为会员跟其他是可以共存的
    public int posPayType;
    //支付日期
    public String payDate;
    //操作人员  ==  对应收款人员
    public String payOperator;

    //退款单号
    public String refundNo;
    //退款时间
    public String refundDate;
    //退款人员
    public String refundOperator;
    //退款金额
    public int refundMoney;

    protected BillInfo(Parcel in) {
        id = in.readString();
        userId = in.readString();
        clubId = in.readString();
        status = in.readInt();
        discountType = in.readInt();
        couponMoney = in.readInt();
        couponDiscountMoney = in.readInt();
        userDiscountMoney = in.readInt();
        memberCardNo = in.readString();
        memberPayDiscountMoney = in.readInt();
        memberPayMoney = in.readInt();
        posPayMoney = in.readInt();
        originMoney = in.readInt();
        tradeNo = in.readString();
        posPayType = in.readInt();
        payDate = in.readString();
        payOperator = in.readString();
        refundNo = in.readString();
        refundDate = in.readString();
        refundOperator = in.readString();
        refundMoney = in.readInt();
    }

    public static final Creator<BillInfo> CREATOR = new Creator<BillInfo>() {
        @Override
        public BillInfo createFromParcel(Parcel in) {
            return new BillInfo(in);
        }

        @Override
        public BillInfo[] newArray(int size) {
            return new BillInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(clubId);
        dest.writeInt(status);
        dest.writeInt(discountType);
        dest.writeInt(couponMoney);
        dest.writeInt(couponDiscountMoney);
        dest.writeInt(userDiscountMoney);
        dest.writeString(memberCardNo);
        dest.writeInt(memberPayDiscountMoney);
        dest.writeInt(memberPayMoney);
        dest.writeInt(posPayMoney);
        dest.writeInt(originMoney);
        dest.writeString(tradeNo);
        dest.writeInt(posPayType);
        dest.writeString(payDate);
        dest.writeString(payOperator);
        dest.writeString(refundNo);
        dest.writeString(refundDate);
        dest.writeString(refundOperator);
        dest.writeInt(refundMoney);
    }
}
