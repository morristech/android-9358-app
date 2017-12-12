package com.xmd.cashier.dal.bean;

import java.util.List;

/**
 * Created by zr on 17-12-11.
 */

public class ItemStatisticsInfo {
    public String categoryName;    //分类名称
    public List<CategoryItem> list; //array<object>
    public String type;    //分类(goods|spa)	如果是spa的话,则有钟数;如果是goods,只有name和sum,amount
    public int totalSum;
    public long totalAmount;


    public class CategoryItem {
        public long amount;    //金额(分)	number
        public List<CategoryItemBell> bellList; //array<object> type为spa时才有
        public String id;    //项目id	string
        public String name;    //项目名称	string
        public int sum;    //项目数	number
    }

    public class CategoryItemBell {
        public int bellCount;    //上钟数	number
        public String bellName;    //上钟名称	string
    }
}
