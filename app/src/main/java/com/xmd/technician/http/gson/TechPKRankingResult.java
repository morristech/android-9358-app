package com.xmd.technician.http.gson;

import com.xmd.technician.bean.RankingListBean;

import java.util.List;

/**
 * Created by sdcm on 17-3-18.
 */

public class TechPKRankingResult extends BaseResult {

    /**
     * respData : {"count":41453,"rankingList":[{"categoryName":"测试内容2cfa","activityName":"pk activity","avatar":"144304","avatarUrl":"http://sdcm105.stonebean.com:8489/s/group00/M00/00/41/oYYBAFW4Td6AS20GAAAY6mibrMk140.jpg?st=J4yzhecYMS8nzZ2CHEVQCQ&e=1489656406","endDate":"03-15","name":"dui1","pkActivityId":2,"startDate":"03-09","statValue":0,"status":"4","statusName":"进行中"},{"categoryName":"测试内容2cfa","activityName":"pk activity","avatar":"144278","avatarUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/2A/oIYBAFW4TciAEY-rAAALN3f4p3c274.jpg?st=so4v48KdW1EpSbYp_JwW6Q&e=1489656406","endDate":"03-15","name":"dui2","pkActivityId":2,"startDate":"03-09","statValue":9400,"status":"4","statusName":"进行中"}]}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        /**
         * count : 41453
         * rankingList : [{"categoryName":"测试内容2cfa","activityName":"pk activity","avatar":"144304","avatarUrl":"http://sdcm105.stonebean.com:8489/s/group00/M00/00/41/oYYBAFW4Td6AS20GAAAY6mibrMk140.jpg?st=J4yzhecYMS8nzZ2CHEVQCQ&e=1489656406","endDate":"03-15","name":"dui1","pkActivityId":2,"startDate":"03-09","statValue":0,"status":"4","statusName":"进行中"},{"categoryName":"测试内容2cfa","activityName":"pk activity","avatar":"144278","avatarUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/2A/oIYBAFW4TciAEY-rAAALN3f4p3c274.jpg?st=so4v48KdW1EpSbYp_JwW6Q&e=1489656406","endDate":"03-15","name":"dui2","pkActivityId":2,"startDate":"03-09","statValue":9400,"status":"4","statusName":"进行中"}]
         */

        public int count;//-1从来没有分组，0,没有正在进行的分组，1,存在正在进行的pk分组
        public List<RankingListBean> rankingList;


    }
}
