package com.xmd.cashier.dal.bean;

import java.io.Serializable;

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

    public int payAmount;    //支付金额:单位为分
    public int originalAmount;  //原消费金额(*Pos买单新增)

    public String createTime;    //买单时间	yyyy-MM-dd HH:mm:ss
    public String description;    //买单描述

    public String modifyTime;    //最后操作时间	yyy-MM-dd HH:mm:ss
    public String operatorName;    //最后操作人

    // TODO 实收金额 优惠金额 实收金额

    public int tempNo;   //用来标识是此次列表中的第几个元素
    public String tempErrMsg;
}
