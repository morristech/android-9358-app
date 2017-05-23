package com.xmd.manager.service.response;


import com.xmd.manager.beans.PKDetailListBean;

import java.util.List;

/**
 * Created by sdcm on 17-3-18.
 */

public class PKTeamListResult extends BaseResult {

    /**
     * respData : {"endDate":"2017-03-15","name":"pk activity","rankingList":[{"commentStat":67566,"customerStat":16247,"saleStat":46141,"avatar":"144278","avatarUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/2A/oIYBAFW4TciAEY-rAAALN3f4p3c274.jpg?st=so4v48KdW1EpSbYp_JwW6Q&e=1489656406","leader":"897【31232】","memberCount":4,"teamId":3,"teamMember":"220【0318】,席巴来咯弄他咯哦哦呢【1008】,792【】,897【31232】","teamName":"dui2"},{"commentStat":67566,"customerStat":16247,"saleStat":46141,"avatar":"144304","avatarUrl":"http://sdcm105.stonebean.com:8489/s/group00/M00/00/41/oYYBAFW4Td6AS20GAAAY6mibrMk140.jpg?st=J4yzhecYMS8nzZ2CHEVQCQ&e=1489656406","leader":"800【0316】","memberCount":3,"teamId":2,"teamMember":"铺子海贼王【108】,我看着越过【23011】,800【0316】","teamName":"dui1"}],"startDate":"2017-03-09","status":4,"statusName":"进行中"}
     */

    public RespDataBean respData;
    public String sortType;

    public static class RespDataBean {
        /**
         * endDate : 2017-03-15
         * name : pk activity
         * rankingList : [{"commentStat":67566,"customerStat":16247,"saleStat":46141,"avatar":"144278","avatarUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/00/2A/oIYBAFW4TciAEY-rAAALN3f4p3c274.jpg?st=so4v48KdW1EpSbYp_JwW6Q&e=1489656406","leader":"897【31232】","memberCount":4,"teamId":3,"teamMember":"220【0318】,席巴来咯弄他咯哦哦呢【1008】,792【】,897【31232】","teamName":"dui2"},{"commentStat":67566,"customerStat":16247,"saleStat":46141,"avatar":"144304","avatarUrl":"http://sdcm105.stonebean.com:8489/s/group00/M00/00/41/oYYBAFW4Td6AS20GAAAY6mibrMk140.jpg?st=J4yzhecYMS8nzZ2CHEVQCQ&e=1489656406","leader":"800【0316】","memberCount":3,"teamId":2,"teamMember":"铺子海贼王【108】,我看着越过【23011】,800【0316】","teamName":"dui1"}]
         * startDate : 2017-03-09
         * status : 4
         * statusName : 进行中
         */

        public String endDate;
        public String name;
        public String startDate;
        public int status;
        public String statusName;
        public List<PKDetailListBean> rankingList;


    }
}
