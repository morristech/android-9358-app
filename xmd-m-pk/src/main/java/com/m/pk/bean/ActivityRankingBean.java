package com.m.pk.bean;

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

    private String startDate;
    private String status;
    private String statusName;
    private String endDate;
    private String activityName;
    private String pkActivityId;
    private String categoryName;
    private String categoryId;
    private List<RankingListBean> rankingList;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getPkActivityId() {
        return pkActivityId;
    }

    public void setPkActivityId(String pkActivityId) {
        this.pkActivityId = pkActivityId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<RankingListBean> getRankingList() {
        return rankingList;
    }

    public void setRankingList(List<RankingListBean> rankingList) {
        this.rankingList = rankingList;
    }



}
