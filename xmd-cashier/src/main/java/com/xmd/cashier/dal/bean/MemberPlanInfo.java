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


    public class PackageItem {
        public long id;
        public int itemCount;
        public String itemId;
        public String name;
        public int oriAmount;
        public int type;
    }

    public class PackagePlanItem {
        public int amount;
        public int commissionAmount;
        public long id;
        public String name;
        public int type;
        public List<PackageItem> packageItems;
    }
}
