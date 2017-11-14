package com.xmd.cashier.dal.bean;

/**
 * Created by zr on 17-11-2.
 * 手牌
 */

public class InnerHandInfo {
    public int amount;    //账单总金额
    public String businessNo;    //账单编号
    public long id;    //账单ID
    public long roomId;    //房间ID
    public String startTime;    //开始时间
    public String userIdentify;    //用户手牌号

    public boolean selected;
}
