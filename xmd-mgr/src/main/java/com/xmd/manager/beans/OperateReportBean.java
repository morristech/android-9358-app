package com.xmd.manager.beans;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperateReportBean {


    /**
     * id : 3
     * startTime : 1506787200000
     * shareId : 12345678901234567890123456789012345678901234567
     * separateTime :
     * name : 自定义
     * shareUrl : http://sdcm103:9880/spa-manager/reportForms/#/report?id=12345678901234567890123456789012345678901234567
     * read : 0
     * endTime : 1509811200000
     */

    public int id;
    public long startTime;
    public String shareId;
    public String separateTime;
    public String name;
    public String shareUrl;
    public int read; //0：未读 1:已读
    public long endTime;
    public String type;
}
