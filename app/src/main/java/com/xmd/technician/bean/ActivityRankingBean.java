package com.xmd.technician.bean;

import java.util.List;

/**
 * Created by Lhj on 17-3-18.
 */

public class ActivityRankingBean {


        /**
         * startDate : 03-10
         * status : 4
         * statusName : 取消连期
         * rankingList : [{"pkActivityId":0,"name":"dui3","statValue":0,"avatar":"144303","categoryName":"拓客","status":null,"activityName":null,"startDate":null,"endDate":null,"avatarUrl":"http://sdcm105.stonebean.com:8489/s/group00/M00/00/2A/oIYBAFW4TeOAE23dAAAO4o1Syvc573.jpg?st=r6TLaJ7QtpqWdLnKllpSOA&e=1490172962","statusName":""},{"pkActivityId":0,"name":"dui3","statValue":0,"avatar":"144303","categoryName":"服务","status":null,"activityName":null,"startDate":null,"endDate":null,"avatarUrl":"http://sdcm105.stonebean.com:8489/s/group00/M00/00/2A/oIYBAFW4TeOAE23dAAAO4o1Syvc573.jpg?st=r6TLaJ7QtpqWdLnKllpSOA&e=1490172962","statusName":""}]
         * endDate : 03-10
         * activityName : pk game
         * pkActivityId : 1
         */

        public String startDate;
        public String status;
        public String statusName;
        public String endDate;
        public String activityName;
        public String pkActivityId;
        public String categoryName;
        public String categoryId;
        public List<RankingListBean> rankingList;



}
