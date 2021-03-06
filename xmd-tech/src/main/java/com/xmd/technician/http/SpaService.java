package com.xmd.technician.http;

import com.xmd.app.constants.HttpRequestConstant;
import com.xmd.m.network.BaseBean;
import com.xmd.technician.bean.CreditAccountDetailResult;
import com.xmd.technician.bean.CreditAccountResult;
import com.xmd.technician.bean.CreditApplicationsResult;
import com.xmd.technician.bean.CreditExchangeResult;
import com.xmd.technician.bean.CreditStatusResult;
import com.xmd.technician.bean.CustomerDetailResult;
import com.xmd.technician.bean.CustomerListResult;
import com.xmd.technician.bean.GameResult;
import com.xmd.technician.bean.GiftListResult;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.RecentlyVisitorResult;
import com.xmd.technician.bean.SaveChatUserResult;
import com.xmd.technician.bean.SayHiResult;
import com.xmd.technician.bean.SendGameResult;
import com.xmd.technician.bean.UserGetCouponResult;
import com.xmd.technician.bean.UserSwitchesResult;
import com.xmd.technician.bean.VisitBean;
import com.xmd.technician.http.gson.AccountMoneyResult;
import com.xmd.technician.http.gson.ActivityListResult;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AuditCancelResult;
import com.xmd.technician.http.gson.AuditConfirmResult;
import com.xmd.technician.http.gson.AuditModifyResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.CardShareListResult;
import com.xmd.technician.http.gson.CategoryListResult;
import com.xmd.technician.http.gson.CheckPayNotifyResult;
import com.xmd.technician.http.gson.ClubPositionResult;
import com.xmd.technician.http.gson.ConsumeDetailResult;
import com.xmd.technician.http.gson.ContactPermissionResult;
import com.xmd.technician.http.gson.CouponInfoResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.http.gson.DeleteTechPosterResult;
import com.xmd.technician.http.gson.DynamicListResult;
import com.xmd.technician.http.gson.GetPayNotifyListResult;
import com.xmd.technician.http.gson.GroupBuyListResult;
import com.xmd.technician.http.gson.HelloGetTemplateResult;
import com.xmd.technician.http.gson.HelloLeftCountResult;
import com.xmd.technician.http.gson.HelloRecordListResult;
import com.xmd.technician.http.gson.HelloReplyResult;
import com.xmd.technician.http.gson.HelloSaveTemplateResult;
import com.xmd.technician.http.gson.HelloSysTemplateResult;
import com.xmd.technician.http.gson.HelloUploadImgResult;
import com.xmd.technician.http.gson.InvitationRewardResult;
import com.xmd.technician.http.gson.JoinClubResult;
import com.xmd.technician.http.gson.JournalListResult;
import com.xmd.technician.http.gson.LimitGrabResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.LogoutResult;
import com.xmd.technician.http.gson.MarkChatToUserResult;
import com.xmd.technician.http.gson.MarketingListResult;
import com.xmd.technician.http.gson.NearbyCusCountResult;
import com.xmd.technician.http.gson.NearbyCusListResult;
import com.xmd.technician.http.gson.OnceCardResult;
import com.xmd.technician.http.gson.OrderCountResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.PKActivityListResult;
import com.xmd.technician.http.gson.PKPersonalListResult;
import com.xmd.technician.http.gson.PKTeamListResult;
import com.xmd.technician.http.gson.PaidCouponUserDetailResult;
import com.xmd.technician.http.gson.PayForMeListResult;
import com.xmd.technician.http.gson.PropagandaListResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.http.gson.RewardListResult;
import com.xmd.technician.http.gson.RoleListResult;
import com.xmd.technician.http.gson.RolePermissionListResult;
import com.xmd.technician.http.gson.SaveTechPosterResult;
import com.xmd.technician.http.gson.ServiceResult;
import com.xmd.technician.http.gson.ShareCouponResult;
import com.xmd.technician.http.gson.TechAccountListResult;
import com.xmd.technician.http.gson.TechCurrentResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.TechPKRankingResult;
import com.xmd.technician.http.gson.TechPersonalDataResult;
import com.xmd.technician.http.gson.TechPosterDetailResult;
import com.xmd.technician.http.gson.TechPosterListResult;
import com.xmd.technician.http.gson.TechRankDataResult;
import com.xmd.technician.http.gson.TechRankingListResult;
import com.xmd.technician.http.gson.TechStatisticsDataResult;
import com.xmd.technician.http.gson.UnusedTechNoListResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.http.gson.UpdateWorkStatusResult;
import com.xmd.technician.http.gson.UploadTechPosterImageResult;
import com.xmd.technician.http.gson.WithdrawRuleResult;
import com.xmd.technician.http.gson.WorkTimeResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
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
    @POST(HttpRequestConstant.URL_LOGIN)
    Call<LoginResult> login(@Field(RequestConstant.KEY_USERNAME) String username,
                            @Field(RequestConstant.KEY_PASSWORD) String password,
                            @Field(RequestConstant.KEY_APP_VERSION) String appVersion,
                            @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_LOGIN_BY_TECH_NO)
    Call<LoginResult> loginByTechNo(@Field(RequestConstant.KEY_CLUB_CODE) String clubCode,
                                    @Field(RequestConstant.KEY_TECH_No) String techNo,
                                    @Field(RequestConstant.KEY_PASSWORD) String password,
                                    @Field(RequestConstant.KEY_APP_VERSION) String appVersion,
                                    @Field(RequestConstant.KEY_LOGIN_CHANNEL) String loginChannel);

    /**
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_LOGOUT)
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
    @POST(HttpRequestConstant.URL_FEEDBACK_CREATE)
    Call<BaseResult> submitFeedback(@Field(RequestConstant.KEY_COMMENTS) String comments,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_REGISTER)
    Call<RegisterResult> register(@Field(RequestConstant.KEY_MOBILE) String mobile,
                                  @Field(RequestConstant.KEY_PASSWORD) String passWord,
                                  @Field(RequestConstant.KEY_ICODE) String iCode,
                                  @Field(RequestConstant.KEY_ROLE_CODE) String roleCode,
                                  @Field(RequestConstant.KEY_CLUB_CODE) String clubCode,
                                  @Field(RequestConstant.KEY_LOGIN_CHANEL) String loginChannel,
                                  @Field(RequestConstant.KEY_CHANEL) String channel,
                                  @Field(RequestConstant.KEY_SPARE_TECH_ID) String techId,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @GET(HttpRequestConstant.URL_EDIT_INFO)
    Call<TechEditResult> getTechEditInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                         @Query(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @Deprecated
    @GET(HttpRequestConstant.URL_CURRENT_INFO)
    Call<TechCurrentResult> getTechCurrentInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                               @Query(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_UPDATE_TECH_INFO)
    Call<UpdateTechInfoResult> updateTechInfo(@Field(RequestConstant.KEY_USER) String user,
                                              @Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_PERSONAL_DATA)
    Call<TechPersonalDataResult> getTechPersonalData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                     @Field(RequestConstant.KEY_USER_TYPE) String userType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_MODIFY_PASSWORD)
    Call<BaseResult> modifyPassword(@Field(RequestConstant.KEY_OLD_PASSWORD) String oldPassword,
                                    @Field(RequestConstant.KEY_NEW_PASSWORD) String newPassword,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_RESET_PASSWORD)
    Call<BaseResult> resetPassword(@Field(RequestConstant.KEY_USERNAME) String username,
                                   @Field(RequestConstant.KEY_PASSWORD) String passWord,
                                   @Field(RequestConstant.KEY_ICODE) String iCode,
                                   @Field(RequestConstant.KEY_TOKEN) String userToken,
                                   @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);


    /**
     * @param userToken
     * @param sessionType
     * @param processType
     * @param id
     * @param reason
     * @return
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_MANAGE_ORDER)
    Call<BaseResult> manageOrder(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                 @Field(RequestConstant.KEY_PROCESS_TYPE) String processType,
                                 @Field(RequestConstant.KEY_ID) String id,
                                 @Field(RequestConstant.KEY_REASON) String reason);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_HIDE_ORDER)
    Call<BaseResult> hideOrder(@Field(RequestConstant.KEY_ORDER_ID) String orderId,
                               @Field(RequestConstant.KEY_TOKEN) String userToken,
                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_ICODE)
    Call<BaseResult> getICode(@Field(RequestConstant.KEY_MOBILE) String mobile,
                              @Field(RequestConstant.KEY_WHICH) String which,
                              @Field(RequestConstant.KEY_SIGN) String sign);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_JOIN_CLUB)
    Call<JoinClubResult> joinClub(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_INVITE_CODE) String inviteCode,
                                  @Field(RequestConstant.KEY_SPARE_TECH_ID) String techId,
                                  @Field(RequestConstant.KEY_ROLE_CODE) String roleCode,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_QUIT_CLUB)
    Call<QuitClubResult> quitClub(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_PASSWORD) String password,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_WORKTIME)
    Call<WorkTimeResult> getWorkTime(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                     @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_UPDATE_WORKTIME)
    Call<BaseResult> updateWorkTime(@Field(RequestConstant.KEY_DAY_RANGE) String dayRange,
                                    @Field(RequestConstant.KEY_BEGIN_TIME) String beginTime,
                                    @Field(RequestConstant.KEY_END_TIME) String endTime,
                                    @Field(RequestConstant.KEY_ID) String id,
                                    @Field(RequestConstant.KEY_END_DAY) String endDay,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_UPLOAD_AVATAR)
    Call<AvatarResult> uploadAvatar(@Field(RequestConstant.KEY_IMG_FILE) String imgFile,
                                    @Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_UPLOAD_ALBUM)
    Call<AlbumResult> uploadAlbum(@Field(RequestConstant.KEY_IMG_FILE) String imgFile,
                                  @Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_DELETE_ALBUM)
    Call<AlbumResult> deleteAlbum(@Field(RequestConstant.KEY_ID) String imgFile,
                                  @Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_SORT_ALBUM)
    Call<AlbumResult> sortAlbum(@Field(RequestConstant.KEY_IDS) String ids,
                                @Field(RequestConstant.KEY_TOKEN) String userToken,
                                @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_UPDATE_WORK_STATUS)
    Call<UpdateWorkStatusResult> updateWorkStatus(@Field(RequestConstant.KEY_STATUS) String status,
                                                  @Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_SERVICE_LIST)
    Call<ServiceResult> getServiceList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_UPDATE_SERVICE_LIST)
    Call<BaseResult> updateServiceList(@Field(RequestConstant.KEY_IDS) String ids,
                                       @Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_ACCOUNT_MONEY)
    Call<AccountMoneyResult> getAccountMoney(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_CONSUME_DETAIL)
    Call<ConsumeDetailResult> getConsumeDetail(@Field(RequestConstant.KEY_CONSUME_TYPE) String consumeType,
                                               @Field(RequestConstant.KEY_PAGE) String page,
                                               @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                               @Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_COUPON_LIST)
    Call<CouponListResult> getCouponList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_COUPON_INFO)
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
    @POST(HttpRequestConstant.URL_PAID_COUPON_USER_DETAIL)
    Call<PaidCouponUserDetailResult> getPaidCouponUserDetail(
            @Field(RequestConstant.KEY_COUPON_STATUS) String couponStatus,
            @Field(RequestConstant.KEY_ACT_ID) String actId,
            @Field(RequestConstant.KEY_PAGE) String page,
            @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
            @Field(RequestConstant.KEY_TOKEN) String userToken,
            @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_COUPON_SHARE_EVENT_COUNT)
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
    @POST(HttpRequestConstant.URL_GETUI_BIND_CLIENT_ID)
    Call<BaseResult> bindGetuiClientId(@Field(RequestConstant.KEY_TOKEN) String token,
                                       @Field(RequestConstant.KEY_USER_ID) String userId,
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
    @POST(HttpRequestConstant.URL_GETUI_UNBIND_CLIENT_ID)
    Call<BaseResult> unbindGetuiClientId(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                         @Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                         @Field(RequestConstant.KEY_CLIENT_ID) String clientId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CUSTOMER_LIST)
    Call<CustomerListResult> getCustomerList(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                             @Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_CUSTOMER_TYPE) String customerType
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CUSTOMER_INFO_DETAIL)
    Call<CustomerDetailResult> getCustomerInfoDetail(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                     @Field(RequestConstant.KEY_USER_ID) String userId,
                                                     @Field(RequestConstant.KEY_ID) String id,
                                                     @Field(RequestConstant.KEY_TOKEN) String userToken);


    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_DELETE_CONTACT)
    Call<BaseResult> doDeleteContact(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                     @Field(RequestConstant.KEY_ID) String id,
                                     @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CREDIT_USER_RECORDS)
    Call<CreditAccountDetailResult> doGetUserRecordDetail(@Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                          @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                                          @Field(RequestConstant.KEY_TOKEN) String userToken,
                                                          @Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                          @Field(RequestConstant.KEY_PAGE) String page,
                                                          @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CREDIT_SWITCH_STATUS)
    Call<CreditStatusResult> doGetCreditStatus(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                               @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                               @Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CREDIT_USER_ACCOUNT)
    Call<CreditAccountResult> doGetCreditAccount(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                 @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                                 @Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CREDIT_EXCHANGE_APPLY)
    Call<CreditExchangeResult> doExchangeCredit(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                @Field(RequestConstant.KEY_UER_CREDIT_AMOUNT) String amount,
                                                @Field(RequestConstant.KEY_TOKEN) String userToken,
                                                @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CREDIT_USER_EXCHANGE_APPLICATIONS)
    Call<CreditApplicationsResult> getExchangeApplications(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                           @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                           @Field(RequestConstant.KEY_STATUS) String status,
                                                           @Field(RequestConstant.KEY_PAGE) String page,
                                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GAME_DICE_SUBMIT)
    Call<SendGameResult> doDiceGameSubmit(@Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                          @Field(RequestConstant.KEY_UER_CREDIT_AMOUNT) String amount,
                                          @Field(RequestConstant.KEY_GAME_USER_EMCHAT_ID) String emchatId,
                                          @Field(RequestConstant.KEY_DICE_GAME_TIME) String timestamp,
                                          @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                          @Field(RequestConstant.KEY_TOKEN) String userToken
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GAME_DICE_ACCEPT_OR_REJECT)
    Call<GameResult> doDiceGameAcceptOrReject(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                              @Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                              @Field(RequestConstant.KEY_DICE_GAME_ID) String gameId,
                                              @Field(RequestConstant.KEY_DICE_GAME_STATUS) String status
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_USER_SWITCHES)
    Call<UserSwitchesResult> doGetUserSwitches(@Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                               @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                               @Field(RequestConstant.KEY_TOKEN) String userToken
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_RECENTLY_VISITOR_LIST)
    Call<RecentlyVisitorResult> getRecentlyVisitorList(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                                       @Field(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Field(RequestConstant.KEY_CUSTOMER_TYPE) String customerType,
                                                       @Field(RequestConstant.KEY_LAST_TIME) String lastTime,
                                                       @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CREDIT_GIFT_LIST)
    Call<GiftListResult> getCreditGiftList(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                           @Field(RequestConstant.KEY_TOKEN) String userToken
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_CUSTOMER_VIEW_VISIT)
    Call<VisitBean> doGetVisitView(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                   @Field(RequestConstant.KEY_TOKEN) String userToken,
                                   @Field(RequestConstant.KEY_UPDATE_USER_ID) String userId
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_SAVE_CONTACT_MARK_CHATTO_USER)
    Call<SaveChatUserResult> doSaveContact(@Field(RequestConstant.KEY_CURRENT_CHAT_ID) String currentChatId,
                                           @Field(RequestConstant.KEY_CURRENT_USER_TYPE) String currentUserType,
                                           @Field(RequestConstant.KEY_FRIEND_CHAT_ID) String friendChatId,
                                           @Field(RequestConstant.KEY_FRIEND_USER_TYPE) String friendUserType,
                                           @Field(RequestConstant.KEY_CHAT_MSG_ID) String msgId,
                                           @Field(RequestConstant.KEY_FRIEND_MESSAGE_TYPE) String msgType
    );

    /**
     * @param userToken
     * @param userType
     * @param phoneNum
     * @param remark
     * @param noteName
     * @return
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_ADD_CUSTOMER)
    Call<BaseResult> addCustomer(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                 @Field(RequestConstant.KEY_TOKEN) String userToken,
                                 @Field(RequestConstant.KEY_PHONE_NUMBER) String phoneNum,
                                 @Field(RequestConstant.KEY_REMARK) String remark,
                                 @Field(RequestConstant.KEY_NOTE_NAME) String noteName,
                                 @Field(RequestConstant.KEY_MARK_IMPRESSION) String impression
    );

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_ADD_CUSTOMER)
    Call<BaseResult> editCustomer(@Field(RequestConstant.KEY_USER_TYPE) String userType,
                                  @Field(RequestConstant.KEY_TOKEN) String userToken,
                                  @Field(RequestConstant.KEY_ID) String custtomerId,
                                  @Field(RequestConstant.KEY_PHONE_NUMBER) String phoneNum,
                                  @Field(RequestConstant.KEY_REMARK) String remark,
                                  @Field(RequestConstant.KEY_NOTE_NAME) String noteName,
                                  @Field(RequestConstant.KEY_MARK_IMPRESSION) String impression);


    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_TECH_INFO)
    Call<TechInfoResult> getTechInfo(@Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_TECH_STATISTICS_DATA)
    Call<TechStatisticsDataResult> getTechStatisticData(@Field(RequestConstant.KEY_TOKEN) String userToken);

    /**
     * @param userToken
     * @param sessionType
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_TECH_ORDER_LIST)
    Call<OrderListResult> getTechOrderList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                           @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                           @Field(RequestConstant.KEY_ORDER_STATUS) String orderStatus,
                                           @Field(RequestConstant.KEY_IS_INDEX_PAGE) String isIndexPage,
                                           @Field(RequestConstant.KEY_PAGE) String page,
                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_TECH_RANK_INDEX)
    Call<TechRankDataResult> getTechRankData(@Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_RECENT_DYNAMICS_LIST)
    Call<DynamicListResult> getDynamicList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                           @Field(RequestConstant.KEY_TECH_DYNAMIC_TYPE) String bizType,
                                           @Field(RequestConstant.KEY_PAGE) String page,
                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_ORDER_INNER_READ)
    Call<BaseResult> setOrderInnerRead(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_ORDER_ID) String orderId);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_UNUSED_TECH_NO)
    Call<UnusedTechNoListResult> getUnusedTechNoList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_CLUB_CODE) String clubCode);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CLUB_USER_GET_COUPON)
    Call<UserGetCouponResult> clubUserCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_USER_COUPON_ACT_ID) String actId,
                                             @Field(RequestConstant.KEY_USER_COUPON_CHANEL) String chanel,
                                             @Field(RequestConstant.KEY_USER_COUPON_EMCHAT_ID) String emchatId,
                                             @Field(RequestConstant.KEY_USER_TECH_CODE) String techCode,
                                             @Field(RequestConstant.KEY_USER_CODE) String userCode);

    //获取买单通知数据
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_PAY_NOTIFY_LIST)
    Call<GetPayNotifyListResult> getPayNotifyList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                  @Field(RequestConstant.KEY_END_DATE) String endDate,
                                                  @Field(RequestConstant.KEY_PAGE_NUMBER) String pageNumber,
                                                  @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //检查是否有新的买单通知数据

    /**
     * @param userToken token
     * @param type      fast_pay:在线买单
     * @return
     */
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_CHECK_PAY_NOTIFY)
    Call<CheckPayNotifyResult> checkPayNotifyData(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_TYPE) String type);

    //营销—卡券列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CARD_SHARE_LIST_INFO)
    Call<CardShareListResult> cardShareList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId);

    //营销—活动列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_ACTIVITY_LIST_INFO)
    Call<ActivityListResult> activityList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId);

    //营销—宣传列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_PROPAGANDA_LIST_INFO)
    Call<PropagandaListResult> propagandaList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId);

    //次卡列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_ONCE_CARD_LIST_DETAIL)
    Call<OnceCardResult> onceCardListDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                            @Field(RequestConstant.KEY_IS_SHARE) String isShare,
                                            @Field(RequestConstant.KEY_PAGE) String page,
                                            @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //券列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CARD_LIST_DETAIL)
    Call<ShareCouponResult> cardListDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                           @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                           @Field(RequestConstant.KEY_COUPON_TYPE) String couponType,
                                           @Field(RequestConstant.KEY_PAGE) String page,
                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //限时抢列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_SERVICE_ITEM_LIST)
    Call<LimitGrabResult> serviceItemListDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                                @Field(RequestConstant.KEY_PAGE) String page,
                                                @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //抽奖活动列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_REWARD_ACTIVITY_LIST)
    Call<RewardListResult> rewardActivityListDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                    @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId);
    //邀请有礼

    @GET(HttpRequestConstant.URL_GET_INVITATION_REWARD_ACTIVITY_LIST)
    Call<InvitationRewardResult> invitationRewardListDetail(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                            @Query(RequestConstant.KEY_USER_CLUB_ID) String clubId);

    //期刊列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_CLUB_JOURNAL_LIST_DETAIL)
    Call<JournalListResult> clubJournalListDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                                  @Field(RequestConstant.KEY_PAGE) String page,
                                                  @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //谁替我买单列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_PAY_FOR_ME_LIST)
    Call<PayForMeListResult> payForMeListDetail(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                @Field(RequestConstant.KEY_USER_CLUB_ID) String clubId,
                                                @Field(RequestConstant.KEY_PAGE) String page,
                                                @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //期刊分享加1
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_DO_USER_JOURNAL_SHARE_COUNT)
    Call<BaseResult> journalShareCount(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_JOURNAL_ID) String journalId);

    //技师账户列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_PROFILE_TECH_ACCOUNT_LIST)
    Call<TechAccountListResult> techAccountList(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //技师账户列表
    @GET(HttpRequestConstant.URL_ROLE_PERMISSION)
    Call<RolePermissionListResult> getRolePermissionList(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                         @Query(RequestConstant.KEY_PLATFORM) String platform);


    // ------------------------------------------> 附近的人 <----------------------------------------
    // 查看会所位置
    @GET(HttpRequestConstant.URL_GET_CLUB_POSITION)
    Call<ClubPositionResult> getClubPosition(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 获取会所附近客户数量
    @GET(HttpRequestConstant.URL_GET_NEARBY_CUS_COUNT)
    Call<NearbyCusCountResult> getNearbyCusCount(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 获取会所附近客户列表
    @GET(HttpRequestConstant.URL_GET_NEARBY_CUS_LIST)
    Call<NearbyCusListResult> getNearbyCusList(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                               @Query(RequestConstant.KEY_PAGE) String page,
                                               @Query(RequestConstant.KEY_PAGE_SIZE) String page_size);

    // 获取剩余打招呼次数
    @GET(HttpRequestConstant.URL_GET_HELLO_LEFT_COUNT)
    Call<HelloLeftCountResult> getHelloLeftCount(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 获取招呼记录
    @GET(HttpRequestConstant.URL_GET_HELLO_RECORD_LIST)
    Call<HelloRecordListResult> getHelloRecordList(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                   @Query(RequestConstant.KEY_PAGE) String page,
                                                   @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    // 查询联系状态
    @GET(HttpRequestConstant.URL_GET_CONTACT_PERMISSION)
    Call<ContactPermissionResult> getContactPermission(@Path(RequestConstant.KEY_ID) String id,
                                                       @Query(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Query(RequestConstant.KEY_CONTACT_ID_TYPE) String idType);

    // 获取打招呼内容
    @GET(HttpRequestConstant.URL_GET_HELLO_TEMPLATE)
    Call<HelloGetTemplateResult> getSetTemplate(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 获取最新回复列表
    @GET(HttpRequestConstant.URL_CHECK_HELLO_REPLY)
    Call<HelloReplyResult> checkHelloReply(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 保存打招呼内容
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_SAVE_HELLO_TEMPLATE)
    Call<HelloSaveTemplateResult> saveSetTemplate(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_MSG_TYPE_TEXT) String text,
                                                  @Field(RequestConstant.KEY_TEMPLATE_IMAGE_ID) String imageId,
                                                  @Field(RequestConstant.KEY_HELLO_TEMPLATE_ID) String templateId);

    // 查询系统模版列表
    @GET(HttpRequestConstant.URL_GET_HELLO_TEMPLATE_LIST)
    Call<HelloSysTemplateResult> getSysTemplateList(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 上传打招呼图片
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_UPLOAD_HELLO_TEMPLATE_IMAGE)
    Call<HelloUploadImgResult> uploadTemplateImg(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_IMG_FILE) String imgFile);

    // 查询角色列表
    @GET(HttpRequestConstant.URL_ROLE_LIST)
    Call<RoleListResult> getRoleList(@Query(RequestConstant.KEY_TOKEN) String userToken);

    //首页pK
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_TECH_PK_ACTIVITY_RANKING)
    Call<TechPKRankingResult> techPKRanking(@Field(RequestConstant.KEY_TOKEN) String userToken);

    //pk列表pkActivityList
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_TECH_PK_ACTIVITY_LIST)
    Call<PKActivityListResult> pkActivityList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_PAGE) String page,
                                              @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //pk队伍排行列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_PK_TEAM_RANKING_LIST)
    Call<PKTeamListResult> techPkTeamList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_PK_ACTIVITY_ID) String pkActivityId,
                                          @Field(RequestConstant.KEY_SORT_KEY) String sortKey,
                                          @Field(RequestConstant.KEY_START_DATE) String startDate,
                                          @Field(RequestConstant.KEY_END_DATE) String endDate,
                                          @Field(RequestConstant.KEY_PAGE) String pager,
                                          @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    //pk个人排行列表
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_PK_PERSONAL_RANKING_LIST)
    Call<PKPersonalListResult> techPkPersonalList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_PK_ACTIVITY_ID) String pkActivityId,
                                                  @Field(RequestConstant.KEY_SORT_KEY) String sortKey,
                                                  @Field(RequestConstant.KEY_TEAM_ID) String teamId,
                                                  @Field(RequestConstant.KEY_START_DATE) String startDate,
                                                  @Field(RequestConstant.KEY_END_DATE) String endDate,
                                                  @Field(RequestConstant.KEY_PAGE) String pager,
                                                  @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize);


    //技师排行榜
    @GET(HttpRequestConstant.URL_GET_PERSONAL_RANKING_LIST)
    Call<TechRankingListResult> techPersonalRankingList(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                        @Query(RequestConstant.KEY_USER_TYPE) String userType,
                                                        @Query(RequestConstant.KEY_TECH_RANKING_SOR_TYPE) String type,
                                                        @Query(RequestConstant.KEY_START_DATE) String startDate,
                                                        @Query(RequestConstant.KEY_END_DATE) String endDate,
                                                        @Query(RequestConstant.KEY_PAGE) String pager,
                                                        @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_MARK_CHAT_TO_USER)
    Call<MarkChatToUserResult> markChatToUser(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_ID) String id,
                                              @Field(RequestConstant.KEY_MAP_SIZE) String size);

    //categoryList
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_TECH_CHAT_CATEGORY_LIST)
    Call<CategoryListResult> categoryList(@Field(RequestConstant.KEY_TOKEN) String userToken);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_GET_TECH_MARKETING_ITEM_LIST)
    Call<MarketingListResult> techMarketingList(@Field(RequestConstant.KEY_TOKEN) String userToken);

    // ------------------------------------------> 技师海报 <----------------------------------------

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_POSTER_SAVE)
    Call<SaveTechPosterResult> techPosterSave(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                              @Field(RequestConstant.KEY_POSTER_CLUB_NAME) String clubName,
                                              @Field(RequestConstant.KEY_POSTER_ID) String id,
                                              @Field(RequestConstant.KEY_POSTER_IMAGE_ID) String imageId,
                                              @Field(RequestConstant.KEY_POSTER_NAME) String name,
                                              @Field(RequestConstant.KEY_POSTER_STYLE) String style,
                                              @Field(RequestConstant.KEY_POSTER_SUB_TITLE) String subTitle,
                                              @Field(RequestConstant.KEY_POSTER_TECH_NO) String techNo,
                                              @Field(RequestConstant.KEY_POSTER_TITLE) String title);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_POSTER_DELETE)
    Call<DeleteTechPosterResult> techPosterDelete(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_POSTER_ID) String id);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_POSTER_IMAGE_UPLOAD)
    Call<UploadTechPosterImageResult> techPosterImageUpload(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                            @Field(RequestConstant.KEY_POSTER_IMAGE_CATEGORY) String category,
                                                            @Field(RequestConstant.KEY_POSTER_IMAGE_IMG_FILE) String imgFile);

    @GET(HttpRequestConstant.URL_TECH_POSTER_LIST)
    Call<TechPosterListResult> techPosterList(@Query(RequestConstant.KEY_TOKEN) String userToken);

    @GET(HttpRequestConstant.URL_TECH_POSTER_DETAIL)
    Call<TechPosterDetailResult> techPosterDetail(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Query(RequestConstant.KEY_POSTER_ID) String id);

    //修改申请信息
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_AUDIT_MODIFY)
    Call<AuditModifyResult> techAuditModify(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_ROLE_CODE) String roleCode,
                                            @Field(RequestConstant.KEY_SPARE_TECH_ID) String spareTechId);
    //取消加入会所申请

    @GET(HttpRequestConstant.URL_TECH_AUDIT_CANCEL)
    Call<AuditCancelResult> techAuditCancel(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                            @Query(RequestConstant.KEY_PASSWORD) String password);

    //被会所拒绝后确认
    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_AUDIT_CONFIRM)
    Call<AuditConfirmResult> techAuditConfirm(@Field(RequestConstant.KEY_TOKEN) String userToken);


    //获取订单详情
    @GET("/spa-manager/api/v2/tech/profile/order/{orderId}")
    Observable<BaseBean<Order>> getOrderDetail(@Path("orderId") String orderId);

    //打招呼
    @FormUrlEncoded
    @POST("/spa-manager/api/v2/tech/hello/{customerId}")
    Observable<BaseBean<SayHiResult>> sayHiToUser(@Path("customerId") String customerId,
                                                  @Field("templateId") long templateId);


    @GET("/spa-manager/api/v2/tech/club/position/invite/count")
    Observable<BaseBean<Integer>> getClubInviteCount(@Query("status") String status);

    @GET(HttpRequestConstant.URL_GET_TECH_ORDER_COUNT)
    Call<OrderCountResult> getOrderCount(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                         @Query(RequestConstant.KEY_ORDER_STATUS) String orderStatus);

    @FormUrlEncoded
    @POST(HttpRequestConstant.URL_TECH_SHARE_COUNT_UPDATE)
    Call<BaseResult> updateTechShareCount(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_ACT_ID) String actId,
                                          @Field(RequestConstant.KEY_ACT_TYPE) String type);
    //提现说明
    @GET(HttpRequestConstant.URL_TECH_WITHDRAW_RULE)
    Call<WithdrawRuleResult> getWithdrawRule(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                             @Query(RequestConstant.KEY_USER_CLUB_ID) String clubId);
    //拼团活动列表
    @GET(HttpRequestConstant.URL_GET_GROUP_BUY_ONLINE_LIST)
    Call<GroupBuyListResult> getGroupBuyList(@Query(RequestConstant.KEY_TOKEN) String userToken);
}
