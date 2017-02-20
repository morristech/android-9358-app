package com.xmd.technician.http.gson;


import com.xmd.technician.bean.OnceCardBean;

import java.util.List;

/**
 * Created by Lhj on 2017/2/9.
 */

public class OnceCardResult extends BaseResult {


    /**
     * respData : {"total":1,"optimalActivity":{"id":1,"name":"测试次卡","startTime":null,"endTime":null,"totalCount":20,"paidCount":16,"itemId":"636121872290816000","itemName":"公主粉足","status":1,"onceCardPlans":[{"id":1,"activityId":1,"clubId":"601679316694081536","name":"A","actAmount":111000,"paidCount":5,"giveCount":2,"itemId":"636121872290816000","optimal":"Y","itemAmount":22200,"techAmount":1000}],"shareUrl":"http://t.cn/RJYkxrJ","period":"1Y","personalLimit":5,"description":"测试次卡的描述信息","image":null,"imageUrl":"","statusName":"进行中"},"activityList":[{"id":1,"name":"测试次卡","startTime":null,"endTime":null,"totalCount":20,"paidCount":16,"itemId":"636121872290816000","itemName":"公主粉足","status":1,"onceCardPlans":[{"id":1,"activityId":1,"clubId":"601679316694081536","name":"A","actAmount":111000,"paidCount":5,"giveCount":2,"itemId":"636121872290816000","optimal":"Y","itemAmount":22200,"techAmount":1000}],"shareUrl":"http://t.cn/RJYkxrJ","period":"1Y","personalLimit":5,"description":"测试次卡的描述信息","image":null,"imageUrl":"","statusName":"进行中"}]}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        /**
         * total : 1
         * optimalActivity : {"id":1,"name":"测试次卡","startTime":null,"endTime":null,"totalCount":20,"paidCount":16,"itemId":"636121872290816000","itemName":"公主粉足","status":1,"onceCardPlans":[{"id":1,"activityId":1,"clubId":"601679316694081536","name":"A","actAmount":111000,"paidCount":5,"giveCount":2,"itemId":"636121872290816000","optimal":"Y","itemAmount":22200,"techAmount":1000}],"shareUrl":"http://t.cn/RJYkxrJ","period":"1Y","personalLimit":5,"description":"测试次卡的描述信息","image":null,"imageUrl":"","statusName":"进行中"}
         * activityList : [{"id":1,"name":"测试次卡","startTime":null,"endTime":null,"totalCount":20,"paidCount":16,"itemId":"636121872290816000","itemName":"公主粉足","status":1,"onceCardPlans":[{"id":1,"activityId":1,"clubId":"601679316694081536","name":"A","actAmount":111000,"paidCount":5,"giveCount":2,"itemId":"636121872290816000","optimal":"Y","itemAmount":22200,"techAmount":1000}],"shareUrl":"http://t.cn/RJYkxrJ","period":"1Y","personalLimit":5,"description":"测试次卡的描述信息","image":null,"imageUrl":"","statusName":"进行中"}]
         */

        public int total;
        public OptimalActivityBean optimalActivity;
        public List<OnceCardBean> activityList;

        public static class OptimalActivityBean {
            /**
             * id : 1
             * name : 测试次卡
             * startTime : null
             * endTime : null
             * totalCount : 20
             * paidCount : 16
             * itemId : 636121872290816000
             * itemName : 公主粉足
             * status : 1
             * onceCardPlans : [{"id":1,"activityId":1,"clubId":"601679316694081536","name":"A","actAmount":111000,"paidCount":5,"giveCount":2,"itemId":"636121872290816000","optimal":"Y","itemAmount":22200,"techAmount":1000}]
             * shareUrl : http://t.cn/RJYkxrJ
             * period : 1Y
             * personalLimit : 5
             * description : 测试次卡的描述信息
             * image : null
             * imageUrl :
             * statusName : 进行中
             */

            public int id;
            public String name;
            public Object startTime;
            public Object endTime;
            public int totalCount;
            public int paidCount;
            public String itemId;
            public String itemName;
            public int status;
            public String shareUrl;
            public String period;
            public int personalLimit;
            public String description;
            public Object image;
            public String imageUrl;
            public String statusName;
            public List<OnceCardPlansBean> onceCardPlans;

            public static class OnceCardPlansBean {
                /**
                 * id : 1
                 * activityId : 1
                 * clubId : 601679316694081536
                 * name : A
                 * actAmount : 111000
                 * paidCount : 5
                 * giveCount : 2
                 * itemId : 636121872290816000
                 * optimal : Y
                 * itemAmount : 22200
                 * techAmount : 1000
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
