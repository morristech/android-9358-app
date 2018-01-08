package com.m.pk.httprequest;


import com.m.pk.httprequest.response.PKActivityListResult;
import com.m.pk.httprequest.response.PKPersonalListResult;
import com.m.pk.httprequest.response.PKTeamListResult;
import com.m.pk.httprequest.response.TechRankingListResult;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Subscription;

/**
 * Created by Lhj on 18-1-4.
 */

public class DataManager {

    private static final DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {

    }

    private Subscription mPKActivityListSubscription;
    private Subscription mPKTeamListSubscription;
    private Subscription mPKPersonalListSubscription;
    private Subscription mTechRankingSubscription;

    /**
     * pk活动列表
     *
     * @param page
     * @param pageSize
     * @param listener
     */
    public void getPkActivityList(String page, String pageSize, NetworkSubscriber<PKActivityListResult> listener) {
        mPKActivityListSubscription = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class)
                .pkActivityList(page, pageSize), listener);
    }

    /**
     * Pk队伍列表
     *
     * @param activityId
     * @param sortKey
     * @param startDate
     * @param endDate
     * @param page
     * @param pageSize
     * @param listener
     */
    public void getPkTeamList(String activityId, String sortKey, String startDate, String endDate, String page, String pageSize,
                              NetworkSubscriber<PKTeamListResult> listener) {
        mPKTeamListSubscription = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class)
                .techPkTeamList(activityId, sortKey, startDate, endDate, page, pageSize), listener);
    }

    /**
     * Pk个人列表
     * @param activityId
     * @param sortKey
     * @param teamId
     * @param startDate
     * @param endDate
     * @param page
     * @param pageSize
     * @param listener
     */
    public void getPkPersonalList(String activityId, String sortKey, String teamId, String startDate, String endDate, String page,
                                  String pageSize, NetworkSubscriber<PKPersonalListResult> listener) {
        mPKPersonalListSubscription = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class)
                .techPkPersonalList(activityId, sortKey, teamId, startDate, endDate, page, pageSize), listener);
    }

    /**
     * 普通排行榜
     *
     * @param sortType
     * @param startDate
     * @param endDate
     * @param page
     * @param pageSize
     * @param listener
     */
    public void getTechRankingList(String userType, String sortType, String startDate, String endDate, String page, String pageSize,
                                   NetworkSubscriber<TechRankingListResult> listener){
        mTechRankingSubscription = XmdNetwork.getInstance().request(XmdNetwork.getInstance().getService(NetService.class)
        .techPersonalRankingList(userType,sortType,startDate,endDate,page,pageSize),listener);
    }



}
