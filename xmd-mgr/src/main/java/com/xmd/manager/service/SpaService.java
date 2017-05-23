package com.xmd.manager.service;

import com.xmd.manager.beans.CheckCouponResult;
import com.xmd.manager.beans.CheckInfoList;
import com.xmd.manager.beans.IndexOrderData;
import com.xmd.manager.beans.OrderDetailResult;
import com.xmd.manager.beans.PaidCouponDetailResult;
import com.xmd.manager.beans.PayResult;
import com.xmd.manager.beans.RegisterDetailResult;
import com.xmd.manager.beans.VisitListResult;
import com.xmd.manager.service.response.AccountDataResult;
import com.xmd.manager.service.response.AddGroupResult;
import com.xmd.manager.service.response.AlbumUploadResult;
import com.xmd.manager.service.response.AppCommentListResult;
import com.xmd.manager.service.response.AwardVerificationResult;
import com.xmd.manager.service.response.BadCommentListResult;
import com.xmd.manager.service.response.BadCommentResult;
import com.xmd.manager.service.response.BaseListResult;
import com.xmd.manager.service.response.BaseResult;
import com.xmd.manager.service.response.BaseStringResult;
import com.xmd.manager.service.response.ChangeStatusResult;
import com.xmd.manager.service.response.CheckVerificationTypeResult;
import com.xmd.manager.service.response.ClubAuthConfigResult;
import com.xmd.manager.service.response.ClubCouponResult;
import com.xmd.manager.service.response.ClubCouponViewResult;
import com.xmd.manager.service.response.ClubEnterResult;
import com.xmd.manager.service.response.ClubListResult;
import com.xmd.manager.service.response.ClubResult;
import com.xmd.manager.service.response.CommentDeleteResult;
import com.xmd.manager.service.response.CouponDataResult;
import com.xmd.manager.service.response.CouponUseDataResult;
import com.xmd.manager.service.response.CustomerCouponsResult;
import com.xmd.manager.service.response.CustomerListResult;
import com.xmd.manager.service.response.CustomerOrdersResult;
import com.xmd.manager.service.response.CustomerResult;
import com.xmd.manager.service.response.CustomerSearchListResult;
import com.xmd.manager.service.response.DefaultVerificationDetailResult;
import com.xmd.manager.service.response.DeleteGroupResult;
import com.xmd.manager.service.response.FavourableActivityListResult;
import com.xmd.manager.service.response.GroupInfoResult;
import com.xmd.manager.service.response.GroupListResult;
import com.xmd.manager.service.response.GroupMessageResult;
import com.xmd.manager.service.response.GroupUserListResult;
import com.xmd.manager.service.response.JournalArticleDetailResult;
import com.xmd.manager.service.response.JournalArticlesResult;
import com.xmd.manager.service.response.JournalContentResult;
import com.xmd.manager.service.response.JournalContentTypeListResult;
import com.xmd.manager.service.response.JournalCouponActivityListResult;
import com.xmd.manager.service.response.JournalImageArticleTemplateList;
import com.xmd.manager.service.response.JournalListResult;
import com.xmd.manager.service.response.JournalPhotoUploadResult;
import com.xmd.manager.service.response.JournalSaveResult;
import com.xmd.manager.service.response.JournalTemplateListResult;
import com.xmd.manager.service.response.JournalUpdateStatusResult;
import com.xmd.manager.service.response.JournalVideoConfigResult;
import com.xmd.manager.service.response.JournalVideoDetailResult;
import com.xmd.manager.service.response.LineChartDataResult;
import com.xmd.manager.service.response.LoginResult;
import com.xmd.manager.service.response.LogoutResult;
import com.xmd.manager.service.response.MarketingIncomeListResult;
import com.xmd.manager.service.response.MarketingResult;
import com.xmd.manager.service.response.ModifyPasswordResult;
import com.xmd.manager.service.response.NewOrderCountResult;
import com.xmd.manager.service.response.OnlinePayListResult;
import com.xmd.manager.service.response.OrderDataResult;
import com.xmd.manager.service.response.OrderListResult;
import com.xmd.manager.service.response.OrderOrCouponResult;
import com.xmd.manager.service.response.OrderResult;
import com.xmd.manager.service.response.OrderSearchListResult;
import com.xmd.manager.service.response.PKActivityListResult;
import com.xmd.manager.service.response.PKPersonalListResult;
import com.xmd.manager.service.response.PKTeamListResult;
import com.xmd.manager.service.response.PaidOrderSwitchResult;
import com.xmd.manager.service.response.PayOrderDetailResult;
import com.xmd.manager.service.response.PropagandaDataResult;
import com.xmd.manager.service.response.RecordTypeListResult;
import com.xmd.manager.service.response.RegisterListResult;
import com.xmd.manager.service.response.RegisterStatisticsResult;
import com.xmd.manager.service.response.RegistryDataResult;
import com.xmd.manager.service.response.SaveChatUserResult;
import com.xmd.manager.service.response.SendGroupMessageResult;
import com.xmd.manager.service.response.ServiceItemListResult;
import com.xmd.manager.service.response.StatisticsHomeDataResult;
import com.xmd.manager.service.response.StatisticsMainPageResult;
import com.xmd.manager.service.response.TechBadCommentListResult;
import com.xmd.manager.service.response.TechListResult;
import com.xmd.manager.service.response.TechPKRankingResult;
import com.xmd.manager.service.response.TechRankDataResult;
import com.xmd.manager.service.response.TechRankingListResult;
import com.xmd.manager.service.response.TechnicianListResult;
import com.xmd.manager.service.response.TechnicianRankingListResult;
import com.xmd.manager.service.response.UseCouponResult;
import com.xmd.manager.service.response.UserCouponListResult;
import com.xmd.manager.service.response.UserCouponViewResult;
import com.xmd.manager.service.response.UserEditGroupResult;
import com.xmd.manager.service.response.UserGetCouponResult;
import com.xmd.manager.service.response.UserGroupDetailListResult;
import com.xmd.manager.service.response.UserGroupSaveResult;
import com.xmd.manager.service.response.VerificationCouponDetailResult;
import com.xmd.manager.service.response.VerificationRecordDetailResult;
import com.xmd.manager.service.response.VerificationRecordListResult;
import com.xmd.manager.service.response.VerificationSaveResult;
import com.xmd.manager.service.response.VerificationServiceCouponResult;
import com.xmd.manager.service.response.VisitDataResult;
import com.xmd.manager.service.response.WifiDataResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by sdcm on 16-1-11.
 */
public interface SpaService {

    /***************************    Account     **************************/
    /**
     * @param username
     * @param password
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_LOGIN)
    Call<LoginResult> login(@Field(RequestConstant.KEY_USERNAME) String username,
                            @Field(RequestConstant.KEY_PASSWORD) String password,
                            @Field(RequestConstant.KEY_APP_VERSION) String appVersion,
                            @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_LOGOUT)
    Call<LogoutResult> logout(@Field(RequestConstant.KEY_TOKEN) String userToken,
                              @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);


    /**
     * 获取会所信息
     *
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_INFO)
    Call<ClubResult> getClubInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /***************************
     * Coupon
     **************************/

    /**
     * 获取会所所创建的优惠券
     *
     * @param userToken
     * @param sessionType
     * @param userType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_COUPON_LIST)
    Call<ClubCouponResult> getClubCoupons(@Field(RequestConstant.KEY_PAGE) String page,
                                          @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                          @Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                          @Field(RequestConstant.KEY_USER_TYPE) String userType);

    /**
     * @param userToken
     * @param sessionType
     * @param actId
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_COUPON_VIEW)
    Call<ClubCouponViewResult> getClubCouponView(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                 @Field(RequestConstant.KEY_ACT_ID) String actId);

    /**
     * @param userToken
     * @param sessionType
     * @param phoneNumber
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_USER_COUPON_LIST)
    Call<UserCouponListResult> getUserCouponList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                 @Field(RequestConstant.KEY_PHONE_NUMBER) String phoneNumber);

    /**
     * @param userToken
     * @param sessionType
     * @param suaId
     * @param couponNo
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_USER_COUPON_VIEW)
    Call<UserCouponViewResult> getUserCouponView(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                 @Field(RequestConstant.KEY_SUA_ID) String suaId,
                                                 @Field(RequestConstant.KEY_COUPON_NO) String couponNo);

    /**
     * @param userToken
     * @param sessionType
     * @param acdtId
     * @param page
     * @param pageSize
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_COUPON_USE_DATA)
    Call<CouponUseDataResult> getCouponUseData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                               @Field(RequestConstant.KEY_ACT_ID) String acdtId,
                                               @Field(RequestConstant.KEY_PAGE) String page,
                                               @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    /**
     * @param userToken
     * @param sessionType
     * @param suaId
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_USER_COUPON_USE)
    Call<UseCouponResult> useCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                    @Field(RequestConstant.KEY_SUA_ID) String suaId,
                                    @Field(RequestConstant.KEY_COUPON_NO) String couponNo);


    /***************************
     * Order
     **************************/

    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_PAID_ORDER_LIST)
    Call<OrderListResult> getPaidOrderList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                           @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                           @Field(RequestConstant.KEY_ORDER_STATUS) String orderType,
                                           @Field(RequestConstant.KEY_PAGE) String page,
                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    /**
     * @param userToken
     * @param sessionType
     * @param orderType
     * @param page
     * @param pageSize
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_ORDER_LIST)
    Call<OrderListResult> getOrderList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                       @Field(RequestConstant.KEY_ORDER_STATUS) String orderType,
                                       @Field(RequestConstant.KEY_SEARCH_TELEPHONE) String telephone,
                                       @Field(RequestConstant.KEY_PAGE) String page,
                                       @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                       @Field(RequestConstant.KEY_START_DATE) String startDate,
                                       @Field(RequestConstant.KEY_END_DATE) String endDate);


    /**
     * @param userToken
     * @param sessionType
     * @param orderType
     * @param page
     * @param pageSize
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_ORDER_LIST)
    Call<OrderSearchListResult> getOrderSearchList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                   @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                   @Field(RequestConstant.KEY_ORDER_STATUS) String orderType,
                                                   @Field(RequestConstant.KEY_SEARCH_TELEPHONE) String telephone,
                                                   @Field(RequestConstant.KEY_PAGE) String page,
                                                   @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                   @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                   @Field(RequestConstant.KEY_END_DATE) String endDate);

    /**
     * @param userToken
     * @param sessionType
     * @param processType
     * @param id
     * @param reason
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_MANAGE_ORDER)
    Call<BaseResult> manageOrder(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                 @Field(RequestConstant.KEY_PROCESS_TYPE) String processType,
                                 @Field(RequestConstant.KEY_ID) String id,
                                 @Field(RequestConstant.KEY_REASON) String reason);

    /**
     * @param userToken
     * @param sessionType
     * @param processType
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_USE_PAID_ORDER)
    Call<BaseResult> usePaidOrder(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                  @Field(RequestConstant.KEY_PROCESS_TYPE) String processType,
                                  @Field(RequestConstant.KEY_ORDER_NO) String orderNo,
                                  @Field(RequestConstant.KEY_ID) String id);

    /**
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_NEW_ORDER_COUNT)
    Call<NewOrderCountResult> getNewOrderCount(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * 检查会所是否开启付费预约
     *
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_CHECK_PAID_ORDER_SWITCH)
    Call<PaidOrderSwitchResult> checkPaidOrderSwitch(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * Retrieve the Paid Order info according to the order number
     *
     * @param userToken
     * @param sessionType
     * @param orderNo
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_USER_PAID_ORDER_VIEW)
    Call<OrderResult> getPaidOrder(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                   @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                   @Field(RequestConstant.KEY_ORDER_NO) String orderNo);


    /**************************** Feedback **************************/
    /**
     * @param userToken
     * @param sessionType
     * @param comments
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_FEEDBACK_CREATE)
    Call<BaseResult> submitFeedback(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                    @Field(RequestConstant.KEY_COMMENTS) String comments);


    /**************************** Push **************************/
    /**
     * @param userId
     * @param userType
     * @param appType
     * @param clientId
     * @param secret
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GETUI_BIND_CLIENT_ID)
    Call<BaseResult> bindGetuiClientId(@Field(RequestConstant.KEY_USER_ID) String userId,
                                       @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                       @Field(RequestConstant.KEY_APP_TYPE) String appType,
                                       @Field(RequestConstant.KEY_CLIENT_ID) String clientId,
                                       @Field(RequestConstant.KEY_SECRET) String secret);

    /**
     * @param userToken
     * @param sessionType
     * @param clientId
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GETUI_UNBIND_CLIENT_ID)
    Call<BaseResult> unbindGetuiClientId(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                         @Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                         @Field(RequestConstant.KEY_CLIENT_ID) String clientId);


    /**
     * get all the registered customers in this club
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CUSTOMERS)
    Call<CustomerListResult> getCustomers(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                          @Field(RequestConstant.KEY_CUSTOMER_USERNAME) String userName);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TECH_CUSTOMERS)
    Call<CustomerListResult> getTechCustomers(@Field(RequestConstant.KEY_SORT) String sort,
                                              @Field(RequestConstant.KEY_SORT_TYPE) String sortType,
                                              @Field(RequestConstant.KEY_TECH_ID) String techId,
                                              @Field(RequestConstant.KEY_PAGE) String page,
                                              @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                              @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                              @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CLUB_TECH_LIST)
    Call<TechListResult> getClubTechs(@Field(RequestConstant.KEY_PAGE) String page,
                                      @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                      @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                      @Field(RequestConstant.KEY_TOKEN) String userToken);

    /**
     * @param userToken
     * @param sessionType
     * @param userName
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CUSTOMERS)
    Call<CustomerSearchListResult> searchCustomers(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                   @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                   @Field(RequestConstant.KEY_CUSTOMER_USERNAME) String userName);

    /**
     * @param userToken
     * @param sessionType
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CUSTOMER_VIEW)
    Call<CustomerResult> getCustomerView(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                         @Field(RequestConstant.KEY_USER_ID) String userId);


    /**
     * @param userToken
     * @param sessionType
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CUSTOMER_ORDERS)
    Call<CustomerOrdersResult> getCustomerOrders(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                 @Field(RequestConstant.KEY_USER_ID) String userId,
                                                 @Field(RequestConstant.KEY_PAGE) String page,
                                                 @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    /**
     * @param userToken
     * @param sessionType
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CUSTOMER_COUPONS)
    Call<CustomerCouponsResult> getCustomerCoupons(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                   @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                   @Field(RequestConstant.KEY_USER_ID) String userId,
                                                   @Field(RequestConstant.KEY_PAGE) String page,
                                                   @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_STATISTICS_HOME_DATA)
    Call<StatisticsHomeDataResult> getStatisticsHomeData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                         @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                         @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                         @Field(RequestConstant.KEY_END_DATE) String endDate);

    /**
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_STATISTICS_FOR_MAIN_PAGE)
    Call<StatisticsMainPageResult> getStatisticsMainPageData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                             @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * @param userToken
     * @param sessionType
     * @param commentId
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_DELETE_CUSTOMER_COMMENT)
    Call<CommentDeleteResult> deleteUserComment(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                @Field(RequestConstant.KEY_COMMENT_ID) String commentId);

    /**
     * @param userToken
     * @param sessionType
     * @param orderNo
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_ORDER_COUPON_VIEW)
    Call<OrderOrCouponResult> getOrderOrCouponView(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                   @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                   @Field(RequestConstant.KEY_ORDER_NO) String orderNo);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_ORDER_DETAIL_DATA)
    Call<OrderDetailResult> getOrderDetailData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                               @Field(RequestConstant.KEY_START_DATE) String startDate,
                                               @Field(RequestConstant.KEY_END_DATE) String endDate);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_DIANZHONG_DETAIL_DATA)
    Call<PaidCouponDetailResult> getDianzhongDetailData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                        @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                        @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                        @Field(RequestConstant.KEY_END_DATE) String endDate);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_REGISTER_DETAIL_DATA)
    Call<RegisterDetailResult> getRegisterDetailData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                     @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                     @Field(RequestConstant.KEY_END_DATE) String endDate);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_REGISTER_STATISTICS_DATA)
    Call<RegisterStatisticsResult> getRegisterStatisticsData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                             @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                             @Field(RequestConstant.KEY_END_DATE) String endDate);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CLUB_LIST)
    Call<ClubListResult> getClubList(@Field(RequestConstant.KEY_PAGE) String page,
                                     @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                     @Field(RequestConstant.KEY_CLUB_NAME) String clubName,
                                     @Field(RequestConstant.KEY_TOKEN) String useToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_LOGIN_CLUB)
    Call<ClubEnterResult> enterClub(@Field(RequestConstant.KEY_CLUB_ID) String clubId,
                                    @Field(RequestConstant.KEY_APP_VERSION) String appVersion,
                                    @Field(RequestConstant.KEY_LOGIN_CHANEL) String loginChanel,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_CHECK_COUPON)
    Call<CheckCouponResult> getCheckCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                           @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                           @Field(RequestConstant.KEY_PAY_CODE) String code);

    @FormUrlEncoded
    @POST(RequestConstant.URL_TO_PAY)
    Call<PayResult> getPayResult(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                 @Field(RequestConstant.KEY_PAY_AMOUNT) String amount,
                                 @Field(RequestConstant.KEY_PAY_QRNO) String qrNo,
                                 @Field(RequestConstant.KEY_PAY_RID) String rid,
                                 @Field(RequestConstant.KEY_TIME) String time);

    @FormUrlEncoded
    @POST(RequestConstant.URL_TO_PAY_BY_CONSUME)
    Call<PayResult> getPayConsumeResult(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                        @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                        @Field(RequestConstant.KEY_PAY_CODE) String code,
                                        @Field(RequestConstant.KEY_PAY_AMOUNT_BY_CODE) String usedAmount);

    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_MENU_CONFIG)
    Call<ClubAuthConfigResult> getClubMenuConfig(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                 @Field(RequestConstant.KEY_PLATFORM) String platform);


    @FormUrlEncoded
    @POST(RequestConstant.URL_WIFI_REPORT_DATA_DETAIL)
    Call<LineChartDataResult> getWifiData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                          @Field(RequestConstant.KEY_START_DATE) String startDate,
                                          @Field(RequestConstant.KEY_END_DATE) String endDate);

    @FormUrlEncoded
    @POST(RequestConstant.URL_REGISTER_REPORT_DATA_DETAIL)
    Call<LineChartDataResult> getRegisterData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                              @Field(RequestConstant.KEY_START_DATE) String startDate,
                                              @Field(RequestConstant.KEY_END_DATE) String endDate);

    @FormUrlEncoded
    @POST(RequestConstant.URL_VISITOR_REPORT_DATA_DETAIL)
    Call<LineChartDataResult> getVisiterData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                             @Field(RequestConstant.KEY_START_DATE) String startDate,
                                             @Field(RequestConstant.KEY_END_DATE) String endDate,
                                             @Field(RequestConstant.KEY_PAGE) String page,
                                             @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(RequestConstant.URL_COUPON_REPORT_DATA_DETAIL)
    Call<LineChartDataResult> getCouponData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                            @Field(RequestConstant.KEY_START_DATE) String startDate,
                                            @Field(RequestConstant.KEY_END_DATE) String endDate);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_VISITOR_LIST_DATA)
    Call<VisitListResult> getVisitListData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                           @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                           @Field(RequestConstant.KEY_START_DATE) String startDate,
                                           @Field(RequestConstant.KEY_END_DATE) String endDate,
                                           @Field(RequestConstant.KEY_PAGE) String page,
                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_USER_REGISTER_LIST)
    Call<RegisterListResult> getRegisterListData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                 @Field(RequestConstant.KEY_END_DATE) String endDate,
                                                 @Field(RequestConstant.KEY_PAGE) String page,
                                                 @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_GROUP_MESSAGE_LIST)
    Call<GroupMessageResult> getGroupList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                          @Field(RequestConstant.KEY_PAGE) String page,
                                          @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_GROUP_MESSAGE_EDIT_INFO)
    Call<GroupInfoResult> getGroupInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GROUP_MESSAGE_SEND)
    Call<SendGroupMessageResult> doSendMessage(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_GROUP_ACT_ID) String actId,
                                               @Field(RequestConstant.KEY_GROUP_ACT_NAME) String actName,
                                               @Field(RequestConstant.KEY_GROUP_COUPON_CONTENT) String couponContent,
                                               @Field(RequestConstant.KEY_GROUP_IDS) String groupIds,
                                               @Field(RequestConstant.KEY_GROUP_IMAGE_ID) String imageId,
                                               @Field(RequestConstant.KEY_GROUP_MESSAGE_CONTENT) String messageContent,
                                               @Field(RequestConstant.KEY_GROUP_USER_GROUP_TYPE) String userGroupType,
                                               @Field(RequestConstant.KEY_GROUP_MESSAGE_TYEP) String msgType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_STATISTICS_WIFI_DATA)
    Call<WifiDataResult> getMainPageWifiData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                             @Field(RequestConstant.KEY_DATE) String date);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_STATISTICS_CLUB_VISIT_DATA)
    Call<VisitDataResult> getMainPageVisitData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                               @Field(RequestConstant.KEY_DATE) String date);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_STATISTICS_REGISTRY_DATA)
    Call<RegistryDataResult> getMainPageRegistryData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                     @Field(RequestConstant.KEY_DATE) String date);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_STATISTICS_COUPON_DATA_INDEX)
    Call<CouponDataResult> getMainPageCouponData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                 @Field(RequestConstant.KEY_DATE) String date);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_STATISTICS_ORDER_DATA)
    Call<OrderDataResult> getMainPageOrderData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_DATASTATISTICS_INDEX_ORDER_DATA)
    Call<IndexOrderData> getMainPageIndexOrderData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                   @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_STATISTICS_TECH_RANK_INDEX)
    Call<TechRankDataResult> getMainPageTechRankData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CLUB_FAVOURABLE_ACTIVITY)
    Call<FavourableActivityListResult> getFavourableActivityList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

//getMarketingItems

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_MARKETING_ITEMS)
    Call<MarketingResult> getMarketingItems(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * 电子期刊
     */
    //获取期刊列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_LIST)
    Observable<JournalListResult> getJournalList(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //更新期刊状态
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_UPDATE_STATUS)
    Observable<JournalUpdateStatusResult> updateJournalStatus(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                              @Field(RequestConstant.KEY_JOURNAL_ID) int journalId,
                                                              @Field(RequestConstant.KEY_STATUS) int status);

    //获取期刊数据
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_CONTENT)
    Observable<JournalContentResult> getJournalContent(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Field(RequestConstant.KEY_JOURNAL_ID) int journalId);

    //更新或者增加电子期刊内容
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_SAVE_CONTENT)
    Observable<JournalSaveResult> addOrUpdateJournal(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_ID) int id,
                                                     @Field(RequestConstant.KEY_TEMPLATE_ID) int templateId,
                                                     @Field(RequestConstant.KEY_TITLE) String title,
                                                     @Field(RequestConstant.KEY_SUB_TITLE) String subTitle,
                                                     @Field(RequestConstant.KEY_JOURNAL_ITEMS) String content);

    //获取期刊模板
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_TEMPLATE_LIST)
    Observable<JournalTemplateListResult> getJournalTemplateList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                                 @Field(RequestConstant.KEY_CLUB_ID) String clubId);

    //获取期刊模板中的版块
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_CONTENT_TYPES)
    Observable<JournalContentTypeListResult> getJournalContentTypes(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //获取技师列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_TECHNICIAN_LIST)
    Observable<TechnicianListResult> getTechnicianList(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //获取技师排行榜
    @FormUrlEncoded
    @POST(RequestConstant.URL_TECHNICIAN_RANKING_LIST)
    Observable<TechnicianRankingListResult> getTechnicianRankingList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                                     @Field(RequestConstant.KEY_TYPE) String type,
                                                                     @Field(RequestConstant.KEY_PAGE) int page,
                                                                     @Field(RequestConstant.KEY_PAGE_SIZE) int pageSize,
                                                                     @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                                     @Field(RequestConstant.KEY_END_DATE) String endDate);

    //获取项目列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_ITEM_BASE_LIST)
    Observable<ServiceItemListResult> getServiceList(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //获取差评列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_USER_BAD_COMMENT_LIST)
    Call<BadCommentListResult> getBadCommentList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_COMMENT_STATUS) String status,
                                                 @Field(RequestConstant.KEY_PAGE) String page,
                                                 @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);


    //差评详情

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_BAD_COMMENT_DETAIL)
    Call<BadCommentResult> getBadCommentDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_BAD_COMMENT_ID) String commentId);
    //修改差评状态

    @FormUrlEncoded
    @POST(RequestConstant.URL_USER_BAD_COMMENT_STATUS_UPDATE)
    Call<ChangeStatusResult> changeCommentStatus(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_BAD_COMMENT_ID) String commentId,
                                                 @Field(RequestConstant.KEY_COMMENT_STATUS) String status);

    //获取全部差评列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TECH_BAD_COMMENT_DATA)
    Call<TechBadCommentListResult> getBadCommentTechList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                         @Field(RequestConstant.KEY_SORT_TYPE) String sortType,
                                                         @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                         @Field(RequestConstant.KEY_END_DATE) String endDate);

    //changePassword
    @FormUrlEncoded
    @POST(RequestConstant.URL_CHANGE_USER_PASSWORD)
    Call<ModifyPasswordResult> changePassword(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_OLD_PASSWORD) String newPassword,
                                              @Field(RequestConstant.KEY_NEW_PASSWORD) String oldPassword);

    /**
     * 查询手机号关系的各种券
     *
     * @param number
     * @return
     */
    @GET(RequestConstant.URL_CHECK_INFO)
    Call<CheckInfoList> getCheckInfoListByNumber(@Path(RequestConstant.KEY_NUMBER) String number);

    //获取核销码类型
    //verificationType
    @FormUrlEncoded
    @POST(RequestConstant.URL_CHECK_INFO_TYPE_GET)
    Call<CheckVerificationTypeResult> getCurrentVerificationType(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                                 @Field(RequestConstant.KEY_VERIFICATION_CODE) String code);

    //获取付费预约订单详情
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_PAY_ORDER_DETAIL)
    Call<PayOrderDetailResult> getPayOrderDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_VERIFICATION_PAY_ORDER_NO) String orderNo);


    //获取核销优惠券详情
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_COUPON_DETAIL)
    Call<VerificationCouponDetailResult> verificationCouponDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                                  @Field(RequestConstant.KEY_VERIFICATION_COUPON_NO) String couponNo);

    //获取核销项目券详情
    @FormUrlEncoded
    @POST(RequestConstant.URL_SERVICE_ITEM_COUPON_DETAIL)
    Call<VerificationServiceCouponResult> verificationServiceCouponDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                                          @Field(RequestConstant.KEY_VERIFICATION_COUPON_NO) String couponNo);

    @FormUrlEncoded
    @POST(RequestConstant.URL_CHECK_INFO_LUCKY_WHEEL_DETAIL)
    Call<AwardVerificationResult> verificationAwardDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                          @Field(RequestConstant.KEY_VERIFICATION_VERIFY_CODE) String verifyCode);

    //获取默认核销详情
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_VERIFICATION_COMMON_DETAIL)
    Call<DefaultVerificationDetailResult> defaultVerificationDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                                    @Field(RequestConstant.KEY_VERIFICATION_CODE) String code,
                                                                    @Field(RequestConstant.KEY_VERIFICATION_AMOUNT) String amount,
                                                                    @Field(RequestConstant.KEY_VERIFICATION_TYPE) String type);


    //核销付费订单
    @FormUrlEncoded
    @POST(RequestConstant.URL_CHECK_INFO_ORDER_SAVE)
    Call<VerificationSaveResult> verificationOrderSave(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Field(RequestConstant.KEY_ORDER_NO) String orderNo,
                                                       @Field(RequestConstant.KEY_PAY_ORDER_PROCESS_TYPE) String processType);

    //核销默认核销码
    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFICATION_COMMON_SAVE)
    Call<VerificationSaveResult> verificationCommonSave(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                        @Field(RequestConstant.KEY_VERIFICATION_CODE) String code,
                                                        @Field(RequestConstant.KEY_VERIFICATION_AMOUNT) String amount,
                                                        @Field(RequestConstant.KEY_VERIFICATION_TYPE) String type);


    //核销优惠券
    @FormUrlEncoded
    @POST(RequestConstant.URL_CHECK_INFO_COUPON_SAVE)
    Call<VerificationSaveResult> verificationCouponSave(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                        @Field(RequestConstant.KEY_VERIFICATION_COUPON_NO) String couponNo);

    //核销项目券
    @FormUrlEncoded
    @POST(RequestConstant.URL_CHECK_INFO_SERVICE_ITEM_COUPON_SAVE)
    Call<VerificationSaveResult> verificationServiceItemCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                               @Field(RequestConstant.KEY_VERIFICATION_COUPON_NO) String couponNo);

    //核销奖品
    @FormUrlEncoded
    @POST(RequestConstant.URL_CHECK_INFO_AWARD_SAVE)
    Call<VerificationSaveResult> verificationAwardSave(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Field(RequestConstant.KEY_VERIFICATION_VERIFY_CODE) String verifyCode);

    //上传照片
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_UPLOAD_PHOTO)
    Observable<JournalPhotoUploadResult> uploadPhoto(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_IMG_FILE) String imgFile);

    //使用图片ID获取图片URL
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_PHOTO_IDS_TO_URLS)
    Observable<BaseListResult<String>> getUrlByPhotoId(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Field(RequestConstant.KEY_ALBUMIDS) String ids);

    //使用图片ID获取图片URL
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_DELETE_PHOTO)
    Observable<BaseListResult<String>> deleteJournalPhoto(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                          @Field(RequestConstant.KEY_IMAGE_ID) String imageId);

    //获取期刊优惠活动信息
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_COUPON_ACTIVITIES)
    Observable<JournalCouponActivityListResult> getJournalCouponActivities(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //获取视频上传签名
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_VIDEO_CONFIG)
    Observable<JournalVideoConfigResult> getJournalVideoConfig(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //获取视频上传签名
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_VIDEO_UPLOAD_SIGN)
    Observable<BaseStringResult> getJournalVideoUploadSign(@Field(RequestConstant.KEY_TOKEN) String userToken);


    //删除视频
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_DELETE_VIDEO)
    Observable<BaseResult> deleteJournalVideo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_RESOURCE_PATH) String resourcePath);

    //获取视频封面和详情
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_VIDEO_DETAIL)
    Observable<JournalVideoDetailResult> getJournalVideoDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                               @Field(RequestConstant.KEY_RESOURCE_PATH) String resourcePath);

    //获取期刊养生文章列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_JOURNAL_ARTICLES)
    Observable<JournalArticlesResult> getJournalArticles(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //获取期刊养生文章详情
    @GET
    Observable<JournalArticleDetailResult> getJournalArticleDetail(@Url String url);

    //分组列表查询
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_GROUP_LIST)
    Call<GroupListResult> clubGroupList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                        @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    //保存用户分组
    @FormUrlEncoded
    @POST(RequestConstant.URL_DO_GROUP_SAVE)
    Call<AddGroupResult> groupSave(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                   @Field(RequestConstant.KEY_GROUP_DESCRIPTION) String description,
                                   @Field(RequestConstant.KEY_GROUP_ID) String groupId,
                                   @Field(RequestConstant.KEY_GROUP_NAME) String groupName,
                                   @Field(RequestConstant.KEY_GROUP_USER_ID) String userIds);

    //分组新增用户列表查询
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_GROUP_USER_LIST)
    Call<GroupUserListResult> groupUserList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_GROUP_USER_NAME) String userName,
                                            @Field(RequestConstant.KEY_PAGE) String page,
                                            @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //删除用户分组
    @FormUrlEncoded
    @POST(RequestConstant.URL_DO_GROUP_DELETE)
    Call<DeleteGroupResult> groupDelete(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                        @Field(RequestConstant.KEY_GROUP_ID) String groupId);

    //用户分组详情明细查询
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_GROUP_DETAILS)
    Call<UserGroupDetailListResult> groupDetails(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_GROUP_ID) String groupId);

    //用户设置分组保存
    @FormUrlEncoded
    @POST(RequestConstant.URL_DO_USER_ADD_GROUP)
    Call<UserGroupSaveResult> userAddGroup(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                           @Field(RequestConstant.KEY_GROUP_ID) String groupId,
                                           @Field(RequestConstant.KEY_USER_ID) String userId);

    //用户设置分组页面
    @FormUrlEncoded
    @POST(RequestConstant.URL_DO_GROUP_USER_EDIT_GROUP)
    Call<UserEditGroupResult> userEditGroup(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_USER_ID) String userId);

    //群发消息上传图片
    @FormUrlEncoded
    @POST(RequestConstant.URL_DO_GROUP_MESSAGE_ALBUM_UPLOAD)
    Call<AlbumUploadResult> groupMessageAlbumUpload(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                    @Field(RequestConstant.KEY_GROUP_IMG_FILE) String imgFile);
//clubUserCoupon

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CLUB_USER_GET_COUPON)
    Call<UserGetCouponResult> clubUserCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_USER_COUPON_ACT_ID) String actId,
                                             @Field(RequestConstant.KEY_USER_COUPON_CHANEL) String chanel,
                                             @Field(RequestConstant.KEY_USER_COUPON_EMCHAT_ID) String emchatId);

    //获取图文版块模板
    @GET(RequestConstant.URL_JOURNAL_IMAGE_ARTICLE_TEMPLATE)
    Observable<JournalImageArticleTemplateList> getJournalImageArticleTemplateList();

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CHECK_INFO_RECORD_LIST)
    Call<VerificationRecordListResult> checkInfoRecordList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                           @Field(RequestConstant.KEY_VERIFICATION_TYPE) String type,
                                                           @Field(RequestConstant.KEY_SEARCH_TELEPHONE) String telephone,
                                                           @Field(RequestConstant.KEY_IS_TIME) String isTime,
                                                           @Field(RequestConstant.KEY_PAGE) String page,
                                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                           @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                           @Field(RequestConstant.KEY_END_DATE) String endDate);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CHECK_INFO_RECORD_DETAIL)
    Call<VerificationRecordDetailResult> checkInfoRecordDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                               @Field(RequestConstant.KEY_VERIFICATION_RECORD_ID) String recordId);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CHECK_INFO_TYPE_LIST)
    Call<RecordTypeListResult> checkInfoTypeList(@Field(RequestConstant.KEY_TOKEN) String userToken);

    /**
     * @param userToken
     * @param sessionType
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_APP_COMMENT_LIST)
    Call<AppCommentListResult> appCommentList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                              @Field(RequestConstant.KEY_USER_ID) String userId,
                                              @Field(RequestConstant.KEY_PAGE) String page,
                                              @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //首页宣传数据
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_MANAGER_DATA_STATISTICS_WIFI_DATA)
    Call<PropagandaDataResult> statisticsWifiData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                  @Field(RequestConstant.KEY_DATE) String date);

    //首页线上流水
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_MANAGER_DATA_STATISTICS_ACCOUNT_DATA)
    Call<AccountDataResult> accountData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                        @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                        @Field(RequestConstant.KEY_DATE) String date);

    //在线买单列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_MANAGER_FASTPAY_ORDER_LIST)
    Call<OnlinePayListResult> fastPayOrderList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_STATUS) String status,
                                               @Field(RequestConstant.KEY_ONLINE_PAY_TECH_NAME) String techName,
                                               @Field(RequestConstant.KEY_START_DATE) String startDate,
                                               @Field(RequestConstant.KEY_END_DATE) String endDate,
                                               @Field(RequestConstant.KEY_PAGE) String page,
                                               @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //营销收入列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_DATA_STATISTICS_SALE_DATA)
    Call<MarketingIncomeListResult> saleDataStatistics(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                       @Field(RequestConstant.KEY_END_DATE) String endDate,
                                                       @Field(RequestConstant.KEY_PAGE) String page,
                                                       @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);
//首页pK

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TECH_PK_ACTIVITY_RANKING)
    Call<TechPKRankingResult> techPKRanking(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_USER_TYPE) String userType);

    //pk列表pkActivityList
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TECH_PK_ACTIVITY_LIST)
    Call<PKActivityListResult> pkActivityList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                              @Field(RequestConstant.KEY_PAGE) String page,
                                              @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //pk队伍排行列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_PK_TEAM_RANKING_LIST)
    Call<PKTeamListResult> techPkTeamList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                          @Field(RequestConstant.KEY_PK_ACTIVITY_ID) String pkActivityId,
                                          @Field(RequestConstant.KEY_SORT_KEY) String sortKey,
                                          @Field(RequestConstant.KEY_START_DATE) String startDate,
                                          @Field(RequestConstant.KEY_END_DATE) String endDate,
                                          @Field(RequestConstant.KEY_PAGE) String pager,
                                          @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //pk个人排行列表
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_PK_PERSONAL_RANKING_LIST)
    Call<PKPersonalListResult> techPkPersonalList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                  @Field(RequestConstant.KEY_PK_ACTIVITY_ID) String pkActivityId,
                                                  @Field(RequestConstant.KEY_SORT_KEY) String sortKey,
                                                  @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                  @Field(RequestConstant.KEY_END_DATE) String endDate,
                                                  @Field(RequestConstant.KEY_PAGE) String pager,
                                                  @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);


    //技师排行榜
    @GET(RequestConstant.URL_GET_PERSONAL_RANKING_LIST)
    Call<TechRankingListResult> techPersonalRankingList(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                        @Query(RequestConstant.KEY_USER_TYPE) String userType,
                                                        @Query(RequestConstant.KEY_TECH_RANKING_SOR_TYPE) String type,
                                                        @Query(RequestConstant.KEY_START_DATE) String startDate,
                                                        @Query(RequestConstant.KEY_END_DATE) String endDate,
                                                        @Query(RequestConstant.KEY_PAGE) String pager,
                                                        @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(RequestConstant.URL_SAVE_CONTACT_MARK_CHATTO_USER)
    Call<SaveChatUserResult> doSaveContact(@Field(RequestConstant.KEY_CURRENT_CHAT_ID) String currentChatId,
                                           @Field(RequestConstant.KEY_CURRENT_USER_TYPE) String currentUserType,
                                           @Field(RequestConstant.KEY_FRIEND_CHAT_ID) String friendChatId,
                                           @Field(RequestConstant.KEY_FRIEND_USER_TYPE) String friendUserType,
                                           @Field(RequestConstant.KEY_CHAT_MSG_ID) String msgId,
                                           @Field(RequestConstant.KEY_FRIEND_MESSAGE_TYPE) String msgType
    );
}

