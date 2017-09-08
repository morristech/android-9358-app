package com.xmd.cashier.dal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 17-9-8.
 * 买单有礼活动
 */

public class GiftActivityInfo implements Serializable {
    public String activityNo;   //活动编码
    public int curDateRcvTime;  //当天领取的次数
    public String endTime;    //活动结束时间:无值则不限;格式yyyy-MM-dd hh:mm
    public long id; //	活动id
    public String memberLimit;  //会员卡买单发放限制	N|不限 Y|会员卡买单不放发奖品
    public String name; //活动名称
    public String operatorId;   //操作人id
    public String operatorName; //操作人名称
    public String payCouponLimit;   //	买单用券发放限制	N|不限 Y|用券抵扣不放发奖品
    public int personDayTime;   //客户每天参与次数限制
    public int personTime;        //客户参与总次数限制
    public String startTime;    //活动结束时间:无值则不限;格式yyyy-MM-dd hh:mm
    public int status;    //活动状态	0-未上线;1-已上线;2-已下线;3-已删除
    public int subStatus;   //活动子状态	status=1时, 1-即将开始;2-运行中;3-已过期(已结束)
    public int totalRcvTime;    //累计领取次数
    public List<GiftActivityPackage> packageList;

    public class GiftActivityPackage implements Serializable {
        public int amount;  //领取奖品的买单金额:单位分
        public long id;    //套餐id
        public String name;    //套餐名称
        public List<GiftActivityPackageItem> packageItems;  //奖品项列表
    }

    public class GiftActivityPackageItem implements Serializable {
        public int count;     //数量
        public long id;    //奖品项id
        public String itemId;    //奖品选择项id
        public String itemName;    //奖品名称
        public int type;    //类型:0-积分;1-礼品;2-优惠券;3-项目;
    }
}
