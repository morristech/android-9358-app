package com.xmd.manager.beans;

/**
 * Created by zr on 17-11-24.
 */

public class CommissionTechInfo {
    public long bellId;    //上钟类型	number	根据此id来过滤上钟类型
    public String bellName;    //上钟名称	string	轮钟，点钟等描述
    public String businessCategory;    //服务类型	string	前端不用
    public String businessName;    //服务项目	string	只在服务提成中使用，是类型下面的项目名称
    public String businessType;    //提成业务类型	string
    public String businessTypeName;    //类型名称	string	在推销提成中使用，是类型下面的类型名称
    public int count;    //数量	number	前端可不用
    public long createTime;    //订单时间	number
    public String dealNo;    //单号（交易号）	string
    public String description;    //详情	string	APP端使用
    public long id;    //提成id	number
    public String payChannelName;    //支付方式名称	string
    public String remark;    //推销提成 详情	string	在推销提成中使用
    public long totalCommission;    //提成金额	number
    public String tradeNo;    //流水号	string
    public String typeImageUrl;    //类型图片URL	string
    public String userName;    //服务对象	string	服务对象名称
}
