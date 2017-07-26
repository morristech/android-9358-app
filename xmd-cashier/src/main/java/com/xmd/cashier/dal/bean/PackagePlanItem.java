package com.xmd.cashier.dal.bean;

import java.util.List;

/**
 * Created by zr on 17-7-26.
 */

public class PackagePlanItem {
    public int amount;
    public int commissionAmount;
    public long id;
    public String name;
    public int type;
    public List<PackageItem> packageItems;

    public class PackageItem {
        public long id;
        public int itemCount;
        public String itemId;
        public String name;
        public int oriAmount;
        public int type;
    }
}