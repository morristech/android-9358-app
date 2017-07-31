package com.xmd.cashier.dal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 17-7-26.
 */

public class PayCouponInfo implements Serializable{
    public String actContent;    //券使用说明
    public String actDescription;    //券描述
    public String actId;    //券id
    public String actLogo;    //图片logo
    public String actLogoUrl;    //图片url
    public String actSubTitle;    //活动名称
    public String actTitle;    //券标题
    public int actValue;    //券优惠价
    public String businessNo;    //券编码
    public int consumeMoney;    //券原始价格
    public String consumeMoneyDescription;    //券详情描述
    public String couponNo;    //核销码
    public String couponPeriod;    //券有效期
    public String couponType;    //券类型
    public String couponTypeName;    //券类型名称
    public String endTime;    //券使用结束时段
    public String startTime; //券使用开始时段
    public String useDay;//券可使用日期
    public String useEndDate;//券有效截止时间
    public String useStartDate;//券有效开始时间
    public String useTimePeriod;//使用时段描述
    public String useType;//券来源
    public String useTypeName;//券来源名称
    public List<ServiceItem> items;

    public class ServiceItem implements Serializable{
        public String categoryId;//分类id
        public String categoryName;//	分类名称
        public String clubId;
        public String description;
        public String discountDescription;//	网店价说明
        public String discountPrice;//	网上店价
        public String duration;//	项目时长
        public String durationUnit;//	项目时长单位
        public String durationUnitPlus;
        public String id;//	项目id
        public String image;
        public String imageUrl;//	图片url
        public String name;//	项目名称
        public String price;//	项目价格
        public String pricePlus;
    }
}
