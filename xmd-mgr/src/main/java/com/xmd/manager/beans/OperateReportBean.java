package com.xmd.manager.beans;

/**
 * Created by Lhj on 17-9-11.
 */

public class OperateReportBean {

    /**
     * endTime : 2017-10-02 00:00:00
     * id : 10
     * name : 周年庆
     * read : 0
     * separateTime : 02:00:00
     * shareId : ab56c678c4b4c7e67828756347382572
     * startTime : 2017-10-01 12:00:00
     */

    public String endTime;
    public int id;
    public String name;
    public int read;
    public String separateTime;
    public String shareId;
    public String startTime;

    public OperateReportBean(){

    }

    public OperateReportBean(String endTime, int id, String name, int read, String separateTime, String shareId, String startTime) {
        this.endTime = endTime;
        this.id = id;
        this.name = name;
        this.read = read;
        this.separateTime = separateTime;
        this.shareId = shareId;
        this.startTime = startTime;
    }


}
