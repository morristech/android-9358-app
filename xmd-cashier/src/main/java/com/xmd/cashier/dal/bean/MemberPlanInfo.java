package com.xmd.cashier.dal.bean;

import java.util.List;

/**
 * Created by zr on 17-7-17.
 * 会员活动套餐
 */

public class MemberPlanInfo {
    public String businessNo;
    public String endTime;
    public String startTime;
    public int type;
    public long id;
    public String name;
    public List<PackagePlanItem> packageList;
}
