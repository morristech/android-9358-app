package com.m.pk.httprequest;


import com.m.pk.httprequest.response.PKActivityListResult;
import com.m.pk.httprequest.response.PKPersonalListResult;
import com.m.pk.httprequest.response.PKTeamListResult;
import com.m.pk.httprequest.response.TechRankingListResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lhj on 18-1-4.
 */

public interface NetService {
    //首页pK
//    @FormUrlEncoded
//    @POST(RequestConstant.URL_GET_TECH_PK_ACTIVITY_RANKING)
//    Observable<TechPKRankingResult> techPKRanking(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //pk列表pkActivityList
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TECH_PK_ACTIVITY_LIST)
    Observable<PKActivityListResult> pkActivityList(@Field(RequestConstant.KEY_PAGE) String page,
                                                    @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //pk队伍排行列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_PK_TEAM_RANKING_LIST)
    Observable<PKTeamListResult> techPkTeamList(@Field(RequestConstant.KEY_PK_ACTIVITY_ID) String pkActivityId,
                                                @Field(RequestConstant.KEY_SORT_KEY) String sortKey,
                                                @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                @Field(RequestConstant.KEY_END_DATE) String endDate,
                                                @Field(RequestConstant.KEY_PAGE) String pager,
                                                @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //pk个人排行列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_PK_PERSONAL_RANKING_LIST)
    Observable<PKPersonalListResult> techPkPersonalList(@Field(RequestConstant.KEY_PK_ACTIVITY_ID) String pkActivityId,
                                                        @Field(RequestConstant.KEY_SORT_KEY) String sortKey,
                                                        @Field(RequestConstant.KEY_TEAM_ID) String teamId,
                                                        @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                        @Field(RequestConstant.KEY_END_DATE) String endDate,
                                                        @Field(RequestConstant.KEY_PAGE) String pager,
                                                        @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);


    //技师排行榜
    @GET(RequestConstant.URL_GET_PERSONAL_RANKING_LIST)
    Observable<TechRankingListResult> techPersonalRankingList(@Query(RequestConstant.KEY_USER_TYPE) String userType,
                                                        @Query(RequestConstant.KEY_TECH_RANKING_SOR_TYPE) String type,
                                                        @Query(RequestConstant.KEY_START_DATE) String startDate,
                                                        @Query(RequestConstant.KEY_END_DATE) String endDate,
                                                        @Query(RequestConstant.KEY_PAGE) String pager,
                                                        @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize);
}
