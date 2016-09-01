package com.xmd.technician.http;

import com.xmd.technician.bean.ClubContactResult;
import com.xmd.technician.bean.CreditAccountDetailResult;
import com.xmd.technician.bean.CreditAccountResult;
import com.xmd.technician.bean.CreditApplicationsResult;
import com.xmd.technician.bean.CreditExchangeResult;
import com.xmd.technician.bean.CreditStatusResult;
import com.xmd.technician.bean.CustomerDetailResult;
import com.xmd.technician.bean.CustomerListResult;
import com.xmd.technician.bean.GameResult;
import com.xmd.technician.bean.IsBindResult;
import com.xmd.technician.bean.ManagerDetailResult;
import com.xmd.technician.bean.MarkResult;
import com.xmd.technician.bean.SendGameResult;
import com.xmd.technician.bean.TechDetailResult;
import com.xmd.technician.http.gson.AccountMoneyResult;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.CommentOrderRedPkResutlt;
import com.xmd.technician.http.gson.CommentResult;
import com.xmd.technician.http.gson.ConsumeDetailResult;
import com.xmd.technician.http.gson.CouponInfoResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.http.gson.InviteCodeResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.LogoutResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.PaidCouponUserDetailResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.http.gson.ServiceResult;
import com.xmd.technician.http.gson.TechCurrentResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.WorkTimeResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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


    /**************************** Feedback **************************/
    /**
     * @param userToken
     * @param sessionType
     * @param comments
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_FEEDBACK_CREATE)
    Call<BaseResult> submitFeedback(@Field(RequestConstant.KEY_COMMENTS) String comments,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_REGISTER)
    Call<RegisterResult> register(@Field(RequestConstant.KEY_MOBILE) String mobile,
                                  @Field(RequestConstant.KEY_PASSWORD) String passWord,
                                  @Field(RequestConstant.KEY_ICODE) String iCode,
                                  @Field(RequestConstant.KEY_CLUB_CODE) String clubCode,
                                  @Field(RequestConstant.KEY_LOGIN_CHANEL) String loginChannel,
                                  @Field(RequestConstant.KEY_CHANEL) String channel,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @GET(RequestConstant.URL_EDIT_INFO)
    Call<TechEditResult> getTechEditInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                         @Query(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @GET(RequestConstant.URL_CURRENT_INFO)
    Call<TechCurrentResult> getTechCurrentInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                               @Query(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_UPDATE_TECH_INFO)
    Call<BaseResult> updateTechInfo(@Field(RequestConstant.KEY_USER) String user,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_COMMENT_LIST)
    Call<CommentResult> getCommentList(@Field(RequestConstant.KEY_PAGE) String pageNumber,
                                       @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                       @Field(RequestConstant.KEY_SORT_TYPE) String sortType,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                       @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_COMMENT_ORDER_REDPK_COUNT)
    Call<CommentOrderRedPkResutlt> getCommentOrderRedPkCount(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                             @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                             @Field(RequestConstant.KEY_USER_TYPE) String userType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_MODIFY_PASSWORD)
    Call<BaseResult> modifyPassword(@Field(RequestConstant.KEY_OLD_PASSWORD) String oldPassword,
                                    @Field(RequestConstant.KEY_NEW_PASSWORD) String newPassword,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_RESET_PASSWORD)
    Call<BaseResult> resetPassword(@Field(RequestConstant.KEY_USERNAME) String username,
                                   @Field(RequestConstant.KEY_PASSWORD) String passWord,
                                   @Field(RequestConstant.KEY_ICODE) String iCode,
                                   @Field(RequestConstant.KEY_TOKEN) String userToken,
                                   @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);


    /**
     * @param userToken
     * @param sessionType
     * @param filterOrder
     * @param page
     * @param pageSize
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_ORDER_LIST)
    Call<OrderListResult> getOrderList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                       @Field(RequestConstant.KEY_FILTER_ORDER) String filterOrder,
                                       @Field(RequestConstant.KEY_PAGE) String page,
                                       @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

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

    @FormUrlEncoded
    @POST(RequestConstant.URL_HIDE_ORDER)
    Call<BaseResult> hideOrder(@Field(RequestConstant.KEY_ORDER_ID) String orderId,
                               @Field(RequestConstant.KEY_TOKEN) String userToken,
                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_ICODE)
    Call<BaseResult> getICode(@Field(RequestConstant.KEY_MOBILE) String mobile);

    @FormUrlEncoded
    @POST(RequestConstant.URL_INVITE_CODE)
    Call<InviteCodeResult> submitInviteCode(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                            @Field(RequestConstant.KEY_INVITE_CODE) String inviteCode);

    @FormUrlEncoded
    @POST(RequestConstant.URL_QUIT_CLUB)
    Call<QuitClubResult> quitClub(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_WORKTIME)
    Call<WorkTimeResult> getWorkTime(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                     @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_UPDATE_WORKTIME)
    Call<BaseResult> updateWorkTime(@Field(RequestConstant.KEY_DAY_RANGE) String dayRange,
                                    @Field(RequestConstant.KEY_BEGIN_TIME) String beginTime,
                                    @Field(RequestConstant.KEY_END_TIME) String endTime,
                                    @Field(RequestConstant.KEY_ID) String id,
                                    @Field(RequestConstant.KEY_END_DAY) String endDay,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_UPLOAD_AVATAR)
    Call<AvatarResult> uploadAvatar(@Field(RequestConstant.KEY_IMG_FILE) String imgFile,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_UPLOAD_ALBUM)
    Call<AlbumResult> uploadAlbum(@Field(RequestConstant.KEY_IMG_FILE) String imgFile,
                                  @Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_DELETE_ALBUM)
    Call<AlbumResult> deleteAlbum(@Field(RequestConstant.KEY_ID) String imgFile,
                                  @Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_SORT_ALBUM)
    Call<AlbumResult> sortAlbum(@Field(RequestConstant.KEY_IDS) String ids,
                                @Field(RequestConstant.KEY_TOKEN) String userToken,
                                @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_UPDATE_WORKSTATUS)
    Call<BaseResult> updateWorkStatus(@Field(RequestConstant.KEY_STATUS) String status,
                                      @Field(RequestConstant.KEY_TOKEN) String userToken,
                                      @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_SERVICE_LIST)
    Call<ServiceResult> getServiceList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_UPDATE_SERVICE_LIST)
    Call<BaseResult> updateServiceList(@Field(RequestConstant.KEY_IDS) String ids,
                                       @Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_ACCOUNT_MONEY)
    Call<AccountMoneyResult> getAccountMoney(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_CONSUME_DETAIL)
    Call<ConsumeDetailResult> getConsumeDetail(@Field(RequestConstant.KEY_CONSUME_TYPE) String consumeType,
                                               @Field(RequestConstant.KEY_PAGE) String page,
                                               @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                               @Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_COUPON_LIST)
    Call<CouponListResult> getCouponList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_COUPON_INFO)
    Call<CouponInfoResult> getCouponInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                         @Field(RequestConstant.KEY_ACT_ID) String actId);

    /**
     * 点钟券用户使用情况
     *
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_PAID_COUPON_USER_DETAIL)
    Call<PaidCouponUserDetailResult> getPaidCouponUserDetail(
            @Field(RequestConstant.KEY_COUPON_STATUS) String couponStatus,
            @Field(RequestConstant.KEY_ACT_ID) String actId,
            @Field(RequestConstant.KEY_PAGE) String page,
            @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
            @Field(RequestConstant.KEY_TOKEN) String userToken,
            @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_COUPON_SHARE_EVENT_COUNT)
    Call<BaseResult> doCouponShareEventCount(@Field(RequestConstant.KEY_ACT_ID) String actId,
                                             @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                             @Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

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

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CUSTOMER_LIST)
    Call<CustomerListResult> getCustomerList(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                             @Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_CUSTOMER_TYPE) String customerType
    );

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CUSTOMER_INFO_DETAIL)
    Call<CustomerDetailResult> getCustomerInfoDetail(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                     @Field(RequestConstant.KEY_ID) String id,
                                                     @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TECH_INFO_DETAIL)
    Call<TechDetailResult> getTechInfoDetail(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                             @Field(RequestConstant.KEY_ID) String id,
                                             @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_MANAGER_INFO_DETAIL)
    Call<ManagerDetailResult> getManagerInfoDetail(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                   @Field(RequestConstant.KEY_ID) String id,
                                                   @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_DELETE_CONTACT)
    Call<BaseResult> doDeleteContact(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                     @Field(RequestConstant.KEY_ID) String id,
                                     @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CREDIT_USER_RECORDS)
    Call<CreditAccountDetailResult> doGetUserRecordDetail(@Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                          @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                                          @Field(RequestConstant.KEY_TOKEN) String userToken,
                                                          @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                          @Field(RequestConstant.KEY_PAGE) String page,
                                                          @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize
    );

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CREDIT_SWITCH_STATUS)
    Call<CreditStatusResult> doGetCreditStatus(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                               @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                               @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CREDIT_USER_ACCOUNT)
    Call<CreditAccountResult> doGetCreditAccount(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                 @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                                 @Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType
    );

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CREDIT_EXCHANGE_APPLY)
    Call<CreditExchangeResult> doExchangeCredit(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                @Field(RequestConstant.KEY_UER_CREDIT_AMOUNT) String amount,
                                                @Field(RequestConstant.KEY_TOKEN) String userToken,
                                                @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType
    );

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CREDIT_USER_EXCHANGE_APPLICATIONS)
    Call<CreditApplicationsResult> getExchangeApplications(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                           @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                           @Field(RequestConstant.KEY_STATUS) String status,
                                                           @Field(RequestConstant.KEY_PAGE) String page,
                                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize
    );

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CONTACT_MARK)
    Call<MarkResult> getContactMark(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                    @Field(RequestConstant.KEY_TAG_TYPE) String tagType,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType
    );

    @FormUrlEncoded
    @POST(RequestConstant.URL_GAME_DICE_SUBMIT)
    Call<SendGameResult> doDiceGameSubmit(@Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                          @Field(RequestConstant.KEY_UER_CREDIT_AMOUNT) String amount,
                                          @Field(RequestConstant.KEY_GAME_USER_EMCHAT_ID) String emchatId,
                                          @Field(RequestConstant.KEY_DICE_GAME_TIME) String timestamp,
                                          @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                          @Field(RequestConstant.KEY_TOKEN) String userToken
    );

    @FormUrlEncoded
    @POST(RequestConstant.URL_GAME_DICE_ACCEPT_OR_REJECT)
    Call<GameResult> doDiceGameAcceptOrReject(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                              @Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                              @Field(RequestConstant.KEY_DICE_GAME_ID) String gameId,
                                              @Field(RequestConstant.KEY_DICE_GAME_STATUS) String status
    );



  /*  @FormUrlEncoded
    @POST(RequestConstant.URL_DO_DRAW_MONEY)
    Call<BaseResult> doDrawMoney(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                 @Field(RequestConstant.KEY_TOKEN) String userToken,
                                 @Field(RequestConstant.KEY_TRADE_AMOUNT) String tradeAmount
                                );

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_USER_WX_OPEN_ID)
    Call<BaseResult> doBindWX(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                 @Field(RequestConstant.KEY_TOKEN) String userToken,
                                 @Field(RequestConstant.KEY_USER_WX_PAGE_URL) String pageUrl,
                                 @Field(RequestConstant.KEY_USER_WX_SCOPE) String scope,
                                 @Field(RequestConstant.KEY_USER_WX_STATE) String state,
                                 @Field(RequestConstant.KEY_USER_WX_WXMP) String wxmp);
      */;


    /**
     * @param userToken
     * @param userType
     * @param phoneNum
     * @param remark
     * @param noteName
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_ADD_CUSTOMER)
    Call<BaseResult> addCustomer(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                 @Field(RequestConstant.KEY_TOKEN) String userToken,
                                 @Field(RequestConstant.KEY_PHONE_NUMBER) String phoneNum,
                                 @Field(RequestConstant.KEY_REMARK) String remark,
                                 @Field(RequestConstant.KEY_NOTE_NAME) String noteName,
                                 @Field(RequestConstant.KEY_MARK_IMPRESSION) String impression
    );

    @FormUrlEncoded
    @POST(RequestConstant.URL_ADD_CUSTOMER)
    Call<BaseResult> editCustomer(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                  @Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_ID) String custtomerId,
                                  @Field(RequestConstant.KEY_PHONE_NUMBER) String phoneNum,
                                  @Field(RequestConstant.KEY_REMARK) String remark,
                                  @Field(RequestConstant.KEY_NOTE_NAME) String noteName,
                                  @Field(RequestConstant.KEY_MARK_IMPRESSION) String impression);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_CLUB_LIST)
    Call<ClubContactResult> getClubList(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                        @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_USER_IS_BIND_WX)
    Call<IsBindResult> getIsBindWXResult(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                         @Field(RequestConstant.KEY_TOKEN) String userToken);
}
