package com.xmd.technician.http.gson;


import com.xmd.technician.bean.OnceCardBean;

import java.util.List;

/**
 * Created by Lhj on 2017/2/9.
 */

public class OnceCardResult extends BaseResult {


    /**
     * respData : {"total":2,"optimalActivity":{"id":37,"name":"单项次卡_02101629","startTime":null,"endTime":null,"totalCount":0,"personalLimit":0,"paidCount":13,"itemId":"628751936601989120","itemName":"其他理疗_1","status":1,"subStatus":2,"itemCardPlans":[{"id":81,"activityId":37,"clubId":"601578932046667776","name":"A","actAmount":1500,"paidCount":15,"giveCount":1,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":100},{"id":82,"activityId":37,"clubId":"601578932046667776","name":"B","actAmount":100,"paidCount":1,"giveCount":200,"itemId":"628751936601989120","optimal":"Y","itemAmount":100,"techAmount":100},{"id":83,"activityId":37,"clubId":"601578932046667776","name":"C","actAmount":2300,"paidCount":23,"giveCount":3,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":400}],"shareUrl":"http://w.url.cn/s/At96zZL","period":"1Y","description":"","image":"159454","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/02/ooYBAFideeWASFs6AAAl_gWJGeY974.jpg?st=vQDdGtwnlblHxIg4ulucwA&e=1488358167","statusName":"进行中"},"activityList":[{"id":37,"name":"单项次卡_02101629","startTime":null,"endTime":null,"totalCount":0,"personalLimit":0,"paidCount":13,"itemId":"628751936601989120","itemName":"其他理疗_1","status":1,"subStatus":2,"itemCardPlans":[{"id":81,"activityId":37,"clubId":"601578932046667776","name":"A","actAmount":1500,"paidCount":15,"giveCount":1,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":100},{"id":82,"activityId":37,"clubId":"601578932046667776","name":"B","actAmount":100,"paidCount":1,"giveCount":200,"itemId":"628751936601989120","optimal":"Y","itemAmount":100,"techAmount":100},{"id":83,"activityId":37,"clubId":"601578932046667776","name":"C","actAmount":2300,"paidCount":23,"giveCount":3,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":400}],"shareUrl":"http://w.url.cn/s/At96zZL","period":"1Y","description":"","image":"159454","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/02/ooYBAFideeWASFs6AAAl_gWJGeY974.jpg?st=vQDdGtwnlblHxIg4ulucwA&e=1488358167","statusName":"进行中"},{"id":36,"name":"单项次卡_02101628","startTime":null,"endTime":null,"totalCount":0,"personalLimit":0,"paidCount":0,"itemId":"621640756905385984","itemName":"按摩按摩按摩按摩按摩","status":1,"subStatus":2,"itemCardPlans":[{"id":80,"activityId":36,"clubId":"601578932046667776","name":"C","actAmount":200,"paidCount":1,"giveCount":100,"itemId":"621640756905385984","optimal":"Y","itemAmount":200,"techAmount":200}],"shareUrl":"http://w.url.cn/s/AqmCIoH","period":"1Y","description":"","image":"159453","imageUrl":"http://sdcm105.stonebean.com:8489/s/group00/M00/01/20/oIYBAFidebOAGSafAAAa6xtICxc357.jpg?st=bYezn2l2JmsW7HADrji3RA&e=1488358167","statusName":"进行中"}]}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        /**
         * total : 2
         * optimalActivity : {"id":37,"name":"单项次卡_02101629","startTime":null,"endTime":null,"totalCount":0,"personalLimit":0,"paidCount":13,"itemId":"628751936601989120","itemName":"其他理疗_1","status":1,"subStatus":2,"itemCardPlans":[{"id":81,"activityId":37,"clubId":"601578932046667776","name":"A","actAmount":1500,"paidCount":15,"giveCount":1,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":100},{"id":82,"activityId":37,"clubId":"601578932046667776","name":"B","actAmount":100,"paidCount":1,"giveCount":200,"itemId":"628751936601989120","optimal":"Y","itemAmount":100,"techAmount":100},{"id":83,"activityId":37,"clubId":"601578932046667776","name":"C","actAmount":2300,"paidCount":23,"giveCount":3,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":400}],"shareUrl":"http://w.url.cn/s/At96zZL","period":"1Y","description":"","image":"159454","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/02/ooYBAFideeWASFs6AAAl_gWJGeY974.jpg?st=vQDdGtwnlblHxIg4ulucwA&e=1488358167","statusName":"进行中"}
         * activityList : [{"id":37,"name":"单项次卡_02101629","startTime":null,"endTime":null,"totalCount":0,"personalLimit":0,"paidCount":13,"itemId":"628751936601989120","itemName":"其他理疗_1","status":1,"subStatus":2,"itemCardPlans":[{"id":81,"activityId":37,"clubId":"601578932046667776","name":"A","actAmount":1500,"paidCount":15,"giveCount":1,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":100},{"id":82,"activityId":37,"clubId":"601578932046667776","name":"B","actAmount":100,"paidCount":1,"giveCount":200,"itemId":"628751936601989120","optimal":"Y","itemAmount":100,"techAmount":100},{"id":83,"activityId":37,"clubId":"601578932046667776","name":"C","actAmount":2300,"paidCount":23,"giveCount":3,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":400}],"shareUrl":"http://w.url.cn/s/At96zZL","period":"1Y","description":"","image":"159454","imageUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/02/ooYBAFideeWASFs6AAAl_gWJGeY974.jpg?st=vQDdGtwnlblHxIg4ulucwA&e=1488358167","statusName":"进行中"},{"id":36,"name":"单项次卡_02101628","startTime":null,"endTime":null,"totalCount":0,"personalLimit":0,"paidCount":0,"itemId":"621640756905385984","itemName":"按摩按摩按摩按摩按摩","status":1,"subStatus":2,"itemCardPlans":[{"id":80,"activityId":36,"clubId":"601578932046667776","name":"C","actAmount":200,"paidCount":1,"giveCount":100,"itemId":"621640756905385984","optimal":"Y","itemAmount":200,"techAmount":200}],"shareUrl":"http://w.url.cn/s/AqmCIoH","period":"1Y","description":"","image":"159453","imageUrl":"http://sdcm105.stonebean.com:8489/s/group00/M00/01/20/oIYBAFidebOAGSafAAAa6xtICxc357.jpg?st=bYezn2l2JmsW7HADrji3RA&e=1488358167","statusName":"进行中"}]
         */

        public int total;
        public OptimalActivityBean optimalActivity;
        public List<OnceCardBean> activityList;

        public static class OptimalActivityBean {
            /**
             * id : 37
             * name : 单项次卡_02101629
             * startTime : null
             * endTime : null
             * totalCount : 0
             * personalLimit : 0
             * paidCount : 13
             * itemId : 628751936601989120
             * itemName : 其他理疗_1
             * status : 1
             * subStatus : 2
             * itemCardPlans : [{"id":81,"activityId":37,"clubId":"601578932046667776","name":"A","actAmount":1500,"paidCount":15,"giveCount":1,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":100},{"id":82,"activityId":37,"clubId":"601578932046667776","name":"B","actAmount":100,"paidCount":1,"giveCount":200,"itemId":"628751936601989120","optimal":"Y","itemAmount":100,"techAmount":100},{"id":83,"activityId":37,"clubId":"601578932046667776","name":"C","actAmount":2300,"paidCount":23,"giveCount":3,"itemId":"628751936601989120","optimal":"N","itemAmount":100,"techAmount":400}]
             * shareUrl : http://w.url.cn/s/At96zZL
             * period : 1Y
             * description :
             * image : 159454
             * imageUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/00/02/ooYBAFideeWASFs6AAAl_gWJGeY974.jpg?st=vQDdGtwnlblHxIg4ulucwA&e=1488358167
             * statusName : 进行中
             */

            public int id;
            public String name;
            public Object startTime;
            public Object endTime;
            public int totalCount;
            public int personalLimit;
            public int paidCount;
            public String itemId;
            public String itemName;
            public int status;
            public int subStatus;
            public String shareUrl;
            public String period;
            public String description;
            public String image;
            public String imageUrl;
            public String statusName;
            public List<ItemCardPlansBean> itemCardPlans;

            public static class ItemCardPlansBean {
                /**
                 * id : 81
                 * activityId : 37
                 * clubId : 601578932046667776
                 * name : A
                 * actAmount : 1500
                 * paidCount : 15
                 * giveCount : 1
                 * itemId : 628751936601989120
                 * optimal : N
                 * itemAmount : 100
                 * techAmount : 100
                 */

                public int id;
                public int activityId;
                public String clubId;
                public String name;
                public int actAmount;
                public int paidCount;
                public int giveCount;
                public String itemId;
                public String optimal;
                public int itemAmount;
                public int techAmount;
            }
        }
    }

}