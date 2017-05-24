package com.xmd.cashier.dal.bean;

import java.io.Serializable;

/**
 * Created by zr on 17-4-11.
 * 预约订单信息
 */

public class OrderRecordInfo implements Serializable {
    public String id;    //订单ID
    public String status;    //订单状态         temp:error
    public String orderType;    //订单类型
    public boolean isExpire;    //是否过期

    public String headImgUrl;
    public String customerName;    //客户名称
    public String phoneNum;    //客户手机号
    public String gender;   //客户性别

    public String techId;    //技师ID
    public String techName;    //技师名称
    public String techSerialNo;    //技师编号

    public String appointTime;    //预约时间    展示格式mm-dd hh:mm
    public String createdAt;    //创建时间  展示格式yyyy-mm-dd hh:mm
    public String serviceTime;

    public String itemId;    //项目ID
    public String itemName;    //项目名称

    public int downPayment;

    public int tempNo;  //用来标识item为此次列表中的第几个元素
    public String tempErrMsg;
}
