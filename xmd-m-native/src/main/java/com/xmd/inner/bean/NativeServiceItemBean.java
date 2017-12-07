package com.xmd.inner.bean;

import java.util.List;

/**
 * Created by Lhj on 17-12-4.
 */

public class NativeServiceItemBean {
    /**
     * id : 628122082500157440
     * categoryId : 621629470339506176
     * name : 测试
     * status : online
     * description : ddd11<div>2323</div>
     * orders : 1
     * topOrders : 1
     * recommended : Y
     * cover : 145252
     * coverUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/00/2D/oIYBAFW_PuiAcSQSAABGkNm4MAw061.jpg?st=arRZ2ZjncejIH4ZFDpuNOA&e=1514455291
     * imageIdList : ["145252"]
     * imageUrlList : ["http://sdcm103.stonebean.com:8489/s/group00/M00/00/2D/oIYBAFW_PuiAcSQSAABGkNm4MAw061.jpg?st=arRZ2ZjncejIH4ZFDpuNOA&e=1514455291"]
     * scope : spa
     * priceList : [{"id":60,"name":"标准价","type":"standard","itemList":[{"id":16,"priceId":60,"type":"primary","price":100,"amount":1,"unitName":"分钟","duration":1},{"id":261,"priceId":60,"type":"extend","price":100,"amount":234,"unitName":"桶","duration":null}]}]
     */

    public String id;
    public String categoryId;
    public String name;
    public String status;
    public String description;
    public int orders;
    public int topOrders;
    public String recommended;
    public String cover;
    public String coverUrl;
    public String scope;
    public List<String> imageIdList;
    public List<String> imageUrlList;
    public List<PriceListBeanXX> priceList;
    public boolean isSelected;

    public static class PriceListBeanXX {
        /**
         * id : 60
         * name : 标准价
         * type : standard
         * itemList : [{"id":16,"priceId":60,"type":"primary","price":100,"amount":1,"unitName":"分钟","duration":1},{"id":261,"priceId":60,"type":"extend","price":100,"amount":234,"unitName":"桶","duration":null}]
         */

        public int id;
        public String name;
        public String type;
        public List<ItemListBeanXX> itemList;

        public static class ItemListBeanXX {
            /**
             * id : 16
             * priceId : 60
             * type : primary
             * price : 100
             * amount : 1
             * unitName : 分钟
             * duration : 1
             */

            public int id;
            public int priceId;
            public String type;
            public int price;
            public int amount;
            public String unitName;
            public int duration;
        }

    }
}
