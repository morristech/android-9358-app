package com.xmd.salary.bean;

import java.util.List;

/**
 * Created by Lhj on 17-11-23.
 */

public class MemberActivityDetailBean {
    /**
     * id : 30
     * name : 12332
     * type : 1
     * startTime : null
     * endTime : null
     * status : 1
     * operatorId : 770559670128480256
     * operatorName : null
     * businessNo : MC171110002
     * packageList : [{"id":68,"activityId":30,"type":1,"name":"1","clubId":"770559669897797632","amount":1000,"commissionAmount":100,"packageItems":[{"id":97,"packageId":68,"type":4,"name":"100","itemId":"","oriAmount":10000,"itemCount":1},{"id":98,"packageId":68,"type":3,"name":"按摩比赛的向１","itemId":"873024640442703872","oriAmount":200,"itemCount":1}]},{"id":69,"activityId":30,"type":1,"name":"2","clubId":"770559669897797632","amount":10000,"commissionAmount":1000,"packageItems":[{"id":99,"packageId":69,"type":1,"name":"礼品券副标题","itemId":"897298622775828480","oriAmount":0,"itemCount":1},{"id":100,"packageId":69,"type":2,"name":"折扣券8折","itemId":"897298460233965568","oriAmount":0,"itemCount":1}]},{"id":70,"activityId":30,"type":1,"name":"3","clubId":"770559669897797632","amount":20000,"commissionAmount":10000,"packageItems":[]}]
     * subStatus : 2
     */

    public int id;
    public String name;
    public int type;
    public String startTime;
    public String endTime;
    public int status;
    public String operatorId;
    public String operatorName;
    public String businessNo;
    public int subStatus;
    public List<PackageListBean> packageList;

}
