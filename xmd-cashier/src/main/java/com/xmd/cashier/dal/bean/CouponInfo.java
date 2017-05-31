package com.xmd.cashier.dal.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.common.AppConstants;

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
    public String suaId;        //活动ID
    public String actId;        //券ID
    public String couponNo;     //优惠码

    public String couponPeriod; //券有效期
    public String useTimePeriod;    //可使用时间,使用时段

    public String actContent;   //使用说明
    public String consumeMoneyDescription;  //使用条件描述

    public String useType;      //卡券类型	coupon-优惠券;money-现金券
    public String useTypeName;  //卡券类型名称

    public String endDate;      //活动结束日期	yyyy-MM-dd
    public String getDate;      //领取时间	yyyy-MM-dd HH:mm:ss
    public String startDate;    //活动开始日期	yyyy-MM-dd
    public String endUseDate;

    public String couponType;   //券类别
    public String couponTypeName;   //券类别名称

    public String actStatus;    //券状态		online-在线;disable-失效;delete-删除;not_online-未上线;
    public String actStatusName;    //券状态名称

    public String actTitle;     //卡券名称
    public String actSubTitle;  //活动名称

    public int creditAmount;    //购买金额 项目券 积分
    public int actAmount;       //优惠金额		单位为分, 对于点着券为减免金额, 对于优惠券为优惠后金额
    public int consumeAmount;   //可核销金额	单位为分

    public String paidType;
    public List<String> itemNames;

    public String userName; //用户名
    public String userPhone;//用户手机

    public CouponInfo() {
    }

    protected CouponInfo(Parcel in) {
        suaId = in.readString();
        actId = in.readString();
        couponNo = in.readString();
        couponPeriod = in.readString();
        useTimePeriod = in.readString();
        actContent = in.readString();
        consumeMoneyDescription = in.readString();
        useType = in.readString();
        useTypeName = in.readString();
        endDate = in.readString();
        getDate = in.readString();
        startDate = in.readString();
        endUseDate = in.readString();
        couponType = in.readString();
        couponTypeName = in.readString();
        actStatus = in.readString();
        actStatusName = in.readString();
        actTitle = in.readString();
        actSubTitle = in.readString();
        creditAmount = in.readInt();
        actAmount = in.readInt();
        consumeAmount = in.readInt();
        paidType = in.readString();
        itemNames = in.createStringArrayList();
        userName = in.readString();
        userPhone = in.readString();
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
        return Integer.valueOf(couponNo.substring(couponNo.length() - 8, couponNo.length()));
    }

    //判断优惠券是否有效
    public boolean isTimeValid() {
        long now = System.currentTimeMillis();
        if (startDate != null && !startDate.equals("")) {
            try {
                long start = DateUtils.getSdf("yyyy-MM-dd HH:mm").parse(startDate).getTime();
                if (now < start) {
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (endDate != null && !endDate.equals("")) {
            try {
                long end = DateUtils.getSdf("yyyy-MM-dd HH:mm").parse(endDate).getTime();
                if (now > end) {
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
            return consumeAmount;
        } else {
            if ("coupon".equals(useType)) {
                // 优惠券
                return consumeAmount - actAmount;
            } else {
                return actAmount;
            }
        }
    }

    // 区分点钟券和优惠券
    public String getCustomType() {
        if ("paid".equals(couponType)) {
            return AppConstants.TYPE_PAID_COUPON;
        } else {
            return AppConstants.TYPE_COUPON;
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
        dest.writeString(couponPeriod);
        dest.writeString(useTimePeriod);
        dest.writeString(actContent);
        dest.writeString(consumeMoneyDescription);
        dest.writeString(useType);
        dest.writeString(useTypeName);
        dest.writeString(endDate);
        dest.writeString(getDate);
        dest.writeString(startDate);
        dest.writeString(endUseDate);
        dest.writeString(couponType);
        dest.writeString(couponTypeName);
        dest.writeString(actStatus);
        dest.writeString(actStatusName);
        dest.writeString(actTitle);
        dest.writeString(actSubTitle);
        dest.writeInt(creditAmount);
        dest.writeInt(actAmount);
        dest.writeInt(consumeAmount);
        dest.writeString(paidType);
        dest.writeStringList(itemNames);
        dest.writeString(userName);
        dest.writeString(userPhone);
    }
}
