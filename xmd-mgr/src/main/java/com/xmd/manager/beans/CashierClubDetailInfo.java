package com.xmd.manager.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zr on 17-11-29.
 * 会所买单收银明细信息
 */

public class CashierClubDetailInfo implements Serializable {
    public String batchNo;    //单号	string
    public int count;    //(scope==goods)数量	number
    public String orderTime;    //开单时间	string
    public String payChannel;    //支付方式	string
    public String payNo;    //交易流水号	string
    public String roomName;    //房间名	string
    public String scope;    //spa|goods	string
    public String userId;    //客户id，没有则‘’	string
    public String userIdentify;    //手牌号	string
    public String userName;    //客户名字，没有则‘’
    public long amount;     //单价
    public long totalAmount;    //订单金额
    public String itemName; //项目名
    public String url;  //图标URL

    public List<CashierTechInfo> techList;

    public class CashierTechInfo implements Serializable {
        public String bellName;    //(scope==spa)上种类型	string
        public String techId;    //服务技师id	string
        public String techName;    //技师名字	string
        public String techNo;    //编号	string
    }
}
