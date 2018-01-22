package com.xmd.cashier.dal.bean;

import java.util.List;

/**
 * Created by zr on 18-1-17.
 */

public class SettleBusinessInfo {
    public String businessName;
    public String businessType;
    public long amount;
    public int count;
    public List<SettleDetailInfo> detailList;
    public List<SettleDetailInfo> remarkList;
}
