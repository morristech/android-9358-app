package com.xmd.cashier.dal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 17-4-11.
 * 在线买单信息 :
 */

public class OnlinePayInfo implements Serializable {
    public String id;       //买单记录ID
    public String status;   //确认状态:paid-未确认;pass-确认通过;unpass-确认异常  temp:error
    public String payId;    //交易支付号

    public String userAvatarUrl;    //用户头像
    public String telephone;    //用户手机号
    public String userId;    //用户ID
    public String userName; // 用户名称

    public String techId;    //技师ID
    public String techName;  //技师名称
    public String techNo;    //技师编号
    public String otherTechNames;    //合并技师

    public String payChannel;   //wx:微信 ali:支付宝
    public int payAmount;    //支付金额:单位为分
    public int originalAmount;  //原消费金额(*Pos买单新增)

    public String createTime;    //买单时间	yyyy-MM-dd HH:mm:ss
    public String description;    //买单描述

    public String modifyTime;    //最后操作时间	yyy-MM-dd HH:mm:ss
    public String operatorName;    //最后操作人

    public int tempNo;   //用来标识是此次列表中的第几个元素
    public String tempErrMsg;
    public List<OnlinePayDiscountInfo> orderDiscountList;

    public class OnlinePayDiscountInfo implements Serializable {
        public int amount;//	抵扣金额		单位分
        public String bizId;//	业务id
        public String bizName;//	名称
        public String clubId;//	会所id
        public String createTime;//	创建时间
        public long id;
        public String orderId;//	订单id
        public String type;//	抵扣类型		coupon优惠券;paid_order付费预约
        public String verifyCode;//	核销码
    }

    public PayCouponInfo payCouponInfo;
}
