package com.xmd.m.comment.httprequest;


import com.xmd.app.constants.HttpRequestConstant;
import com.xmd.m.comment.bean.ClubEmployeeDetailResult;
import com.xmd.m.comment.bean.CommentBean;
import com.xmd.m.comment.bean.CommentListResult;
import com.xmd.m.comment.bean.CommentStatusResult;
import com.xmd.m.comment.bean.ConsumeListResult;
import com.xmd.m.comment.bean.ContactPermissionResult;
import com.xmd.m.comment.bean.DeleteCustomerResult;
import com.xmd.m.comment.bean.HelloCheckRecentlyResult;
import com.xmd.m.comment.bean.ManagerUserDetailResult;
import com.xmd.m.comment.bean.RewardListResult;
import com.xmd.m.comment.bean.TechConsumeListResult;
import com.xmd.m.comment.bean.TechListResult;
import com.xmd.m.comment.bean.TechRewardListResult;
import com.xmd.m.comment.bean.TechUserDetailResult;
import com.xmd.m.comment.bean.TechVisitorListResult;
import com.xmd.m.comment.bean.UserEditGroupResult;
import com.xmd.m.comment.bean.VisitorListResult;
import com.xmd.m.network.BaseBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lhj on 17-7-1.
 */

public interface NetService {


    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_COMMENT_LIST)
    Observable<CommentListResult> getCommentList(@Field("page") String page,
                                                 @Field("pageSize") String pageSize,
                                                 @Field("startDate") String startDate,
                                                 @Field("endDate") String endDate,
                                                 @Field("techId") String techId,
                                                 @Field("type") String type,
                                                 @Field("userName") String userName,
                                                 @Field("userId") String userId,
                                                 @Field("commentType") String commentType,
                                                 @Field("returnStatus") String returnStatus,
                                                 @Field("status") String status);

    @GET(HttpRequestConstant.URL_COMMENT_TECH_LIST)
    Observable<TechListResult> getTechList();

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_USER_BAD_COMMENT_STATUS_UPDATE)
    Observable<CommentStatusResult> updateCommentStatus(
            @Field("commentId") String commentId,
            @Field("status") String status);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_CUSTOMER_USER_DETAIL)
    Observable<ManagerUserDetailResult> getManagerUserDetail(
            @Field("userId") String userId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_USER_CONSUME_LIST)
    Observable<ConsumeListResult> getConsumeList(@Field("page") String page,
                                                 @Field("pageSize") String pageSize,
                                                 @Field("userId") String userId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_USER_SHOP_LIST)
    Observable<VisitorListResult> getVisitorList(@Field("page") String page,
                                                 @Field("pageSize") String pageSize,
                                                 @Field("userId") String userId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_USER_REWARD_LIST)
    Observable<RewardListResult> getRewardList(@Field("page") String page,
                                               @Field("pageSize") String pageSize,
                                               @Field("userId") String userId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_CUSTOMER_DETAIL)
    Observable<TechUserDetailResult> getTechUserDetail(
            @Field("id") String id
    );


    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_USER_CONSUME_LIST)
    Observable<TechConsumeListResult> getTechConsumeList(@Field("page") String page,
                                                         @Field("pageSize") String pageSize,
                                                         @Field("userId") String userId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_USER_SHOP_LIST)
    Observable<TechVisitorListResult> getTechVisitorList(@Field("page") String page,
                                                         @Field("pageSize") String pageSize,
                                                         @Field("userId") String userId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_USER_REWARD_LIST)
    Observable<TechRewardListResult> getTechRewardList(@Field("page") String page,
                                                       @Field("pageSize") String pageSize,
                                                       @Field("userId") String userId);

    @GET(HttpRequestConstant.URL_CLUB_COLLEAGUE_DETAIL)
    Observable<ClubEmployeeDetailResult> clubEmployeeDetail(@Query(RequestConstant.KEY_EMP_ID) String empId);

    // 查询联系状态
    @GET(HttpRequestConstant.URL_GET_CONTACT_PERMISSION)
    Observable<ContactPermissionResult> getContactPermission(@Path("id") String id,
                                                             @Query("idType") String idType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_DELETE_CONTACT)
    Observable<DeleteCustomerResult> doDeleteContact(@Field("id") String id);


    //用户设置分组页面
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_DO_GROUP_USER_EDIT_GROUP)
    Observable<UserEditGroupResult> userEditGroup(@Field(RequestConstant.KEY_USER_ID) String userId);

    @GET("/spa-manager/api/v2/comment/{commentId}")
    Observable<BaseBean<CommentBean>> getCommentDetail(@Path("commentId") String commentId);
    // 查询近期是否打过招呼
    @GET(HttpRequestConstant.URL_CHECK_HELLO_RECENTLY)
    Observable<HelloCheckRecentlyResult> checkHelloRecently(@Path(RequestConstant.KEY_NEW_CUSTOMER_ID) String customerId);
}
