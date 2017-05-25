package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.shidou.commonlibrary.util.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sdcm on 15-10-27.
 * 优惠券 | 项目券
 */
public class CouponInfo implements Parcelable {

    /**
     * 优惠券分成四个类型：
     * 注册有礼券： 满XXX元可以使用
     * 分享有礼券： 满XXX元可以使用
     * 普通优惠券： 满XXX元可以使用
     * 点钟券： XX元抵扣XXX元
     */
    // common filed
    public String suaId;        //活动ID
    public String actId;        //券ID
    public String couponNo;     //优惠码
    public String actTitle;     //卡券名称
    public String couponType;   //券类别
    public String actContent;   //使用说明
    public String couponPeriod; //券有效期
    public String useTimePeriod;    //可使用时间,使用时段
    public String consumeMoneyDescription;  //使用条件描述
    public String useTypeName;  //卡券类型名称
    public String endDate;      //活动结束日期	yyyy-MM-dd
    public String getDate;      //领取时间	yyyy-MM-dd HH:mm:ss
    public String startDate;    //活动开始日期	yyyy-MM-dd
    public String useType;      //卡券类型	coupon-优惠券;money-现金券
    public String couponTypeName;   //券类别名称
    public String actStatus;    //券状态		online-在线;disable-失效;delete-删除;not_online-未上线;
    public String actStatusName;    //券状态名称
    public String actSubTitle;  //活动名称

    // old filed
    // ------------------------------begin-----------------------------
    public int actValue;
    public int consumeMoney;    //优惠金额
    public String useStartDate; //优惠券开始日期 对应 startDate
    public String useEndDate;   //优惠券结束日期 对应 endDate
    public String modifyDate;   //yyyy-MM-dd HH:mm:ss   对应 getDate
    public String couponPaid;   //支付状态，"Y":已支付，"N"：未支付
    // -----------------------------end---------------------------------

    // new filed
    // ------------------------------begin------------------------------
    public int creditAmount;    //购买金额 项目券 积分
    public int actAmount;       //优惠金额		单位为分, 对于点着券为减免金额, 对于优惠券为优惠后金额
    public int consumeAmount;   //可核销金额	单位为分
    // -----------------------------end---------------------------------

    public String paidType;
    public String endUseDate;
    public List<String> itemNames;

    public CouponInfo() {

    }


    protected CouponInfo(Parcel in) {
        suaId = in.readString();
        actId = in.readString();
        couponNo = in.readString();
        actTitle = in.readString();
        couponType = in.readString();
        actContent = in.readString();
        couponPeriod = in.readString();
        useTimePeriod = in.readString();
        consumeMoneyDescription = in.readString();
        useTypeName = in.readString();
        endDate = in.readString();
        getDate = in.readString();
        startDate = in.readString();
        useType = in.readString();
        couponTypeName = in.readString();
        actStatus = in.readString();
        actStatusName = in.readString();
        actSubTitle = in.readString();
        actValue = in.readInt();
        consumeMoney = in.readInt();
        useStartDate = in.readString();
        useEndDate = in.readString();
        modifyDate = in.readString();
        couponPaid = in.readString();
        creditAmount = in.readInt();
        actAmount = in.readInt();
        consumeAmount = in.readInt();
        paidType = in.readString();
        endUseDate = in.readString();
        itemNames = in.createStringArrayList();
    }

    public static final Creator<CouponInfo> CREATOR = new Creator<CouponInfo>() {
        @Override
        public CouponInfo createFromParcel(Parcel in) {
            return new CouponInfo(in);
        }

        @Override
        public CouponInfo[] newArray(int size) {
            return new CouponInfo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        // TODO
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof CouponInfo)) {
            return false;
        }
        CouponInfo that = (CouponInfo) o;
        return suaId.equals(that.suaId) && couponNo.equals(that.couponNo);
    }

    @Override
    public int hashCode() {
        // TODO
        return Integer.valueOf(couponNo.substring(couponNo.length() - 8, couponNo.length()));
    }

    public boolean isPaidValid() {
        switch (couponType) {
            case "paid":
                return couponPaid.contains("Y");
            default:
                return true;
        }
    }

    //判断优惠券是否有效
    public boolean isTimeValid() {
        // TODO
        long now = System.currentTimeMillis();
        if (useStartDate != null && !useStartDate.equals("")) {
            try {
                long startDate = DateUtils.getSdf("yyyy-MM-dd HH:mm").parse(useStartDate).getTime();
                if (now < startDate) {
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (useEndDate != null && !useEndDate.equals("")) {
            try {
                long endDate = DateUtils.getSdf("yyyy-MM-dd HH:mm").parse(useEndDate).getTime();
                if (now > endDate) {
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (useTimePeriod != null) {
            if (useTimePeriod.equals("不限")) {
                return true;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (!useTimePeriod.contains(getWeekInZh(dayOfWeek))) {
                return false;
            }
            String[] times = useTimePeriod.split(" ");
            String startTime = null;
            String endTime = null;
            for (String s : times) {
                if (s.contains(":")) {
                    if (startTime == null) {
                        startTime = s;
                    } else {
                        endTime = s;
                        break;
                    }
                }
            }
            if (startTime != null && endTime != null) {
                int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
                int nowMinute = calendar.get(Calendar.MINUTE);
                String nowHM = nowHour + ":" + nowMinute;
                if (nowHM.compareTo(startTime) < 0 || nowHM.compareTo(endTime) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getWeekInZh(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "周日";
            case Calendar.MONDAY:
                return "周一";
            case Calendar.TUESDAY:
                return "周二";
            case Calendar.WEDNESDAY:
                return "周三";
            case Calendar.THURSDAY:
                return "周四";
            case Calendar.FRIDAY:
                return "周五";
            case Calendar.SATURDAY:
                return "周六";
        }
        return "错误";
    }

    //返回实际的减扣金额
    public int getReallyCouponMoney() {
        if ("paid".equals(couponType)) {
            // 点钟券
            return consumeMoney;
        } else {
            if ("coupon".equals(useType)) {
                // 优惠券
                return consumeMoney - actValue;
            } else {
                return actValue;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(suaId);
        dest.writeString(actId);
        dest.writeString(couponNo);
        dest.writeString(actTitle);
        dest.writeString(couponType);
        dest.writeString(actContent);
        dest.writeString(couponPeriod);
        dest.writeString(useTimePeriod);
        dest.writeString(consumeMoneyDescription);
        dest.writeString(useTypeName);
        dest.writeString(endDate);
        dest.writeString(getDate);
        dest.writeString(startDate);
        dest.writeString(useType);
        dest.writeString(couponTypeName);
        dest.writeString(actStatus);
        dest.writeString(actStatusName);
        dest.writeString(actSubTitle);
        dest.writeInt(actValue);
        dest.writeInt(consumeMoney);
        dest.writeString(useStartDate);
        dest.writeString(useEndDate);
        dest.writeString(modifyDate);
        dest.writeString(couponPaid);
        dest.writeInt(creditAmount);
        dest.writeInt(actAmount);
        dest.writeInt(consumeAmount);
        dest.writeString(paidType);
        dest.writeString(endUseDate);
        dest.writeStringList(itemNames);
    }
}
