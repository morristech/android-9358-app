package com.xmd.technician.bean;

import com.xmd.technician.http.gson.BaseResult;

/**
 * Created by Administrator on 2016/7/7.
 */
public class ManagerDetailResult extends BaseResult {

    /**
     * respData : {"id":"601634063966539776","phoneNum":"13455465464","avatarUrl":"http://sdcm103.stonebean.com:8489/s/group00/M00/01/0D/oIYBAFhPo3aAdbW7AApWp8RJy5g827.png?st=kyi7gP3BUOs23jvC1mqBdA&e=1499323149","name":"刘德华","emchatId":"82c7161dac48dee61b6f92657f841a96","loginName":"刘德华"}
     */

    public RespDataBean respData;

    public static class RespDataBean {
        /**
         * id : 601634063966539776
         * phoneNum : 13455465464
         * avatarUrl : http://sdcm103.stonebean.com:8489/s/group00/M00/01/0D/oIYBAFhPo3aAdbW7AApWp8RJy5g827.png?st=kyi7gP3BUOs23jvC1mqBdA&e=1499323149
         * name : 刘德华
         * emchatId : 82c7161dac48dee61b6f92657f841a96
         * loginName : 刘德华
         */

        public String id;
        public String phoneNum;
        public String avatarUrl;
        public String name;
        public String emchatId;
        public String loginName;
    }
}
