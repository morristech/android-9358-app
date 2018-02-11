package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-11-3.
 * 支付方式
 */

public class TradeChannelInfo {
    public String clubId;
    public long createTime;
    public boolean custom;      //是否为自定义
    public long id;
    public boolean immutable;   //是否可以修改状态
    public String mark;         //备注
    public long modifyTime; //修改时间
    public String name;     //名称
    public String operatorId;
    public String status;    //状态	string
    public String type;      //'account','ali','cash','union','wx','custom_XXX'
}
