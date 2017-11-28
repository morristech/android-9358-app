package com.xmd.salary.bean;

import java.util.List;

/**
 * Created by Lhj on 17-11-23.
 */

public class PackageListBean {
            /**
         * id : 68
         * activityId : 30
         * type : 1
         * name : 1
         * clubId : 770559669897797632
         * amount : 1000
         * commissionAmount : 100
         * packageItems : [{"id":97,"packageId":68,"type":4,"name":"100","itemId":"","oriAmount":10000,"itemCount":1},{"id":98,"packageId":68,"type":3,"name":"按摩比赛的向１","itemId":"873024640442703872","oriAmount":200,"itemCount":1}]
         */

        public int id;
        public int activityId;
        public int type;
        public String name;
        public String clubId;
        public int amount;
        public int commissionAmount;
        public List<PackageItemsBean> packageItems;
}
