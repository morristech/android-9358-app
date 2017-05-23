package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zr on 17-4-10.
 */

public class SettleSummaryInfo implements Parcelable {
    public int id;

    public int aliIncome;   //支付宝实收
    public int aliRefund;   //支付宝退款

    public int bankCardIncome;  //银行卡实收
    public int bankCardRefund;  //银行卡退款

    public int wechatIncome;    //微信实收
    public int wechatRefund;    //微信退款

    public int moneyIncome; //现金实收
    public int moneyRefund; //现金退款

    public int memberPayIncome; //会员支付实收
    public int memberPayRefund; //会员支付退款

    public int orderTotalMoney;     //订单总金额
    public int incomeTotalMoney;    //实收总金额
    public int refundTotalMoney;    //退款总金额
    public int deductTotalMoney;    //减免总金额

    public int discountDeduct;  //会员折扣减免
    public int manualDeduct;    //手动减免
    public int preferentialDeduct;  //优惠减免

    public String cashierId;    //收银员ID
    public String cashierName;  //收银员姓名

    public String operatorId;   //结算人员ID
    public String operatorName; //结算人员姓名

    public String clubId;       //会所ID
    public String clubName;     //会所名称

    public int orderCount;  //订单数量
    public int refundCount; //退款订单数量

    public String startTime;    //结算开始时间
    public String endTime;      //结算截止时间
    public String createTime;   //创建时间

    protected SettleSummaryInfo(Parcel in) {
        id = in.readInt();
        aliIncome = in.readInt();
        aliRefund = in.readInt();
        bankCardIncome = in.readInt();
        bankCardRefund = in.readInt();
        wechatIncome = in.readInt();
        wechatRefund = in.readInt();
        moneyIncome = in.readInt();
        moneyRefund = in.readInt();
        memberPayIncome = in.readInt();
        memberPayRefund = in.readInt();
        orderTotalMoney = in.readInt();
        incomeTotalMoney = in.readInt();
        refundTotalMoney = in.readInt();
        deductTotalMoney = in.readInt();
        discountDeduct = in.readInt();
        manualDeduct = in.readInt();
        preferentialDeduct = in.readInt();
        cashierId = in.readString();
        cashierName = in.readString();
        operatorId = in.readString();
        operatorName = in.readString();
        clubId = in.readString();
        clubName = in.readString();
        orderCount = in.readInt();
        refundCount = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        createTime = in.readString();
    }

    public static final Creator<SettleSummaryInfo> CREATOR = new Creator<SettleSummaryInfo>() {
        @Override
        public SettleSummaryInfo createFromParcel(Parcel in) {
            return new SettleSummaryInfo(in);
        }

        @Override
        public SettleSummaryInfo[] newArray(int size) {
            return new SettleSummaryInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(aliIncome);
        dest.writeInt(aliRefund);
        dest.writeInt(bankCardIncome);
        dest.writeInt(bankCardRefund);
        dest.writeInt(wechatIncome);
        dest.writeInt(wechatRefund);
        dest.writeInt(moneyIncome);
        dest.writeInt(moneyRefund);
        dest.writeInt(memberPayIncome);
        dest.writeInt(memberPayRefund);
        dest.writeInt(orderTotalMoney);
        dest.writeInt(incomeTotalMoney);
        dest.writeInt(refundTotalMoney);
        dest.writeInt(deductTotalMoney);
        dest.writeInt(discountDeduct);
        dest.writeInt(manualDeduct);
        dest.writeInt(preferentialDeduct);
        dest.writeString(cashierId);
        dest.writeString(cashierName);
        dest.writeString(operatorId);
        dest.writeString(operatorName);
        dest.writeString(clubId);
        dest.writeString(clubName);
        dest.writeInt(orderCount);
        dest.writeInt(refundCount);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(createTime);
    }
}
