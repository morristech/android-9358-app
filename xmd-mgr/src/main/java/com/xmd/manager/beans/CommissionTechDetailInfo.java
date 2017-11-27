package com.xmd.manager.beans;

/**
 * Created by zr on 17-11-24.
 */

public class CommissionTechDetailInfo {
    public String bellName;    //上钟类型名称	string
    public String businessName;    //业务名称	string	服务项目名称/商品名称/次卡名称/套餐名称
    public String businessType;    //业务类型	string
    public int count;    //数量	number
    public long createTime;    //下单时间	number
    public String dealNo;    //交易号	string	内部生成
    public String payChannelName;    //支付方式名称	string
    public int price;    //单价	number	单位分
    public String roomName;    //房间号	string
    public String techId;    //技师id	string
    public String techName;    //技师名称	string
    public String techNo;    //技师编号	string
    public long totalCommission;    //提成总额	number	单位分
    public String tradeNo;    //交易流水号	string	第三方返回
    public String userId;    //用户id	string
    public String userIdentify;    //手牌号	string
    public String userName;    //用户名称	string
}
