package com.xmd.cashier.dal.net;

import com.xmd.cashier.dal.net.response.BillRecordResult;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.dal.net.response.ClubResult;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.GetMemberInfo;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.LoginResult;
import com.xmd.cashier.dal.net.response.LogoutResult;
import com.xmd.cashier.dal.net.response.MemberCardResult;
import com.xmd.cashier.dal.net.response.MemberListResult;
import com.xmd.cashier.dal.net.response.MemberPlanResult;
import com.xmd.cashier.dal.net.response.MemberRecordListResult;
import com.xmd.cashier.dal.net.response.MemberRecordResult;
import com.xmd.cashier.dal.net.response.MemberSettingResult;
import com.xmd.cashier.dal.net.response.MemberUrlResult;
import com.xmd.cashier.dal.net.response.OnlinePayCouponResult;
import com.xmd.cashier.dal.net.response.OnlinePayDetailResult;
import com.xmd.cashier.dal.net.response.OnlinePayListResult;
import com.xmd.cashier.dal.net.response.OnlinePayUrlResult;
import com.xmd.cashier.dal.net.response.OrderRecordListResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.PrizeResult;
import com.xmd.cashier.dal.net.response.ReportTradeDataResult;
import com.xmd.cashier.dal.net.response.SettleRecordResult;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.net.response.TechListResult;
import com.xmd.cashier.dal.net.response.VerifyRecordDetailResult;
import com.xmd.cashier.dal.net.response.VerifyRecordResult;
import com.xmd.cashier.dal.net.response.VerifyTypeResult;
import com.xmd.m.network.BaseBean;

import okhttp3.ResponseBody;
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

    /**
     * 登录接口
     *
     * @param username
     * @param password
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_LOGIN)
    Observable<LoginResult> login(@Field(RequestConstant.KEY_USERNAME) String username,
                                  @Field(RequestConstant.KEY_PASSWORD) String password,
                                  @Field(RequestConstant.KEY_APP_VERSION) String appVersion,
                                  @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * 退出登录接口
     *
     * @param userToken
     * @param sessionType
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_LOGOUT)
    Observable<LogoutResult> logout(@Field(RequestConstant.KEY_TOKEN) String userToken,
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
    Observable<ClubResult> getClubInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType);

    /**
     * 获取核销列表: 券+付费预约
     *
     * @param number
     * @param userToken
     * @return
     */
    @GET(RequestConstant.URL_USER_CHECK_INFO_LIST)
    Observable<CheckInfoListResult> getCheckInfoList(@Path(RequestConstant.KEY_NUMBER) String number,
                                                     @Query(RequestConstant.KEY_TOKEN) String userToken);

    /**
     * 获取订单号
     *
     * @param userToken
     * @param originMoney
     * @param couponList
     * @param requestSign
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TRADE_NO)
    Observable<GetTradeNoResult> getTradeNo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_ORIGIN_MONEY) int originMoney,
                                            @Field(RequestConstant.KEY_COUPON_LIST) String couponList,
                                            @Field(RequestConstant.KEY_SIGN) String requestSign);

    /**
     * 获取会员信息
     *
     * @param userToken
     * @param memberToken
     * @param requestSign
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_MEMBER_INFO)
    Observable<GetMemberInfo> getMemberInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_MEMBER_TOKEN) String memberToken,
                                            @Field(RequestConstant.KEY_SIGN) String requestSign);

    /**
     * 会员支付
     *
     * @param userToken
     * @param memberToken
     * @param tradeNo
     * @param payMoney
     * @param requestSign
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_MEMBER_PAY)
    Observable<MemberRecordResult> memberPay(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_MEMBER_TOKEN) String memberToken,
                                             @Field(RequestConstant.KEY_TRADE_NO) String tradeNo,
                                             @Field(RequestConstant.KEY_AMOUNT) int payMoney,
                                             @Field(RequestConstant.KEY_MEMBER_CAN_DISCOUNT) String canDisCount,
                                             @Field(RequestConstant.KEY_SIGN) String requestSign);

    /**
     * 获取交易流水记录
     *
     * @param userToken  用户标识
     * @param billStart  起始时间
     * @param billEnd    结束时间
     * @param payType    支付类型
     * @param billStatus 交易状态
     * @param pageStart  开始页
     * @param pageSize   每页记录数
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_BILL_LIST)
    Observable<BillRecordResult> getBill(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_START_DATE) String billStart,
                                         @Field(RequestConstant.KEY_END_DATE) String billEnd,
                                         @Field(RequestConstant.KEY_PAY_TYPE) int payType,
                                         @Field(RequestConstant.KEY_STATUS) int billStatus,
                                         @Field(RequestConstant.KEY_PAGE_START) int pageStart,
                                         @Field(RequestConstant.KEY_PAGE_SIZE) int pageSize);

    /**
     * 搜索交易流水记录
     *
     * @param userToken 用户标识
     * @param tradeNo   订单号（模糊匹配）
     * @param pageStart 开始页
     * @param pageSize  每页记录数
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_BILL_LIST)
    Observable<BillRecordResult> searchBill(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_TRADE_NO) String tradeNo,
                                            @Field(RequestConstant.KEY_PAGE_START) int pageStart,
                                            @Field(RequestConstant.KEY_PAGE_SIZE) int pageSize);

    /**
     * 查询核销码类型
     *
     * @param code 核销码
     * @return
     */
    @GET(RequestConstant.URL_GET_VERIFY_TYPE)
    Observable<StringResult> getVerifyType(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                           @Query(RequestConstant.KEY_CODE) String code);

    /**
     * 查询核销详情——优惠券
     *
     * @param couponNo 优惠码
     * @return
     */
    @GET(RequestConstant.URL_INFO_COUPON)
    Observable<CouponResult> getCouponInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                           @Query(RequestConstant.KEY_COUPON_NO) String couponNo);

    /**
     * 查询核销详情——付费预约
     *
     * @param orderNo 订单号
     * @return
     */
    @GET(RequestConstant.URL_INFO_PAID_ORDER)
    Observable<OrderResult> getPaidOrderInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                             @Query(RequestConstant.KEY_ORDER_NO) String orderNo);

    /**
     * 查询核销详情——奖品
     *
     * @param verifyCode 兑换码
     * @return
     */
    @GET(RequestConstant.URL_INFO_LUCKY_WHEEL)
    Observable<PrizeResult> getPrizeInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                         @Query(RequestConstant.KEY_VERIFY_CODE) String verifyCode);

    /**
     * 查询核销详情——项目券
     *
     * @param couponNO 核销码
     * @return
     */
    @GET(RequestConstant.URL_INFO_SERVICE_ITEM)
    Observable<CouponResult> getServiceCouponInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Query(RequestConstant.KEY_COUPON_NO) String couponNO);

    /**
     * 查询核销详情——默认
     *
     * @param code 核销码
     * @return
     */
    @GET(RequestConstant.URL_INFO_COMMON)
    Observable<CommonVerifyResult> getCommonVerifyInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Query(RequestConstant.KEY_CODE) String code,
                                                       @Query(RequestConstant.KEY_TYPE) String type);

    /**
     * 核销优惠券
     *
     * @param couponNo 优惠券优惠码
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_COUPON)
    Observable<BaseBean> verifyCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                      @Field(RequestConstant.KEY_COUPON_NO) String couponNo);

    /**
     * 核销付费预约
     *
     * @param orderNo     订单号
     * @param processType 处理类型：expire | verified
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_PAID_ORDER)
    Observable<BaseBean> verifyPaidOrder(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_ORDER_NO) String orderNo,
                                         @Field(RequestConstant.KEY_PROCESS_TYPE) String processType);

    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_PAID_ORDER)
    Call<BaseBean> verifyPaidOrderCall(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_ORDER_NO) String orderNo,
                                       @Field(RequestConstant.KEY_PROCESS_TYPE) String processType);

    /**
     * 核销转盘奖品
     *
     * @param verifyCode 奖品兑换码
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_LUCKY_WHEEL)
    Observable<BaseBean> verifyLuckyWheel(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_VERIFY_CODE) String verifyCode);

    /**
     * 核销项目券
     *
     * @param couponNo 项目券优惠码
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_SERVICE_ITEM)
    Observable<BaseBean> verifyServiceCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                             @Field(RequestConstant.KEY_COUPON_NO) String couponNo);

    /**
     * 核销默认
     * 请客授权属于此类
     *
     * @param amount 核销金额
     * @param code   核销码
     * @param type   核销码类型
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_COMMON)
    Observable<BaseBean> verifyWithMoney(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                         @Field(RequestConstant.KEY_AMOUNT) String amount,
                                         @Field(RequestConstant.KEY_CODE) String code,
                                         @Field(RequestConstant.KEY_TYPE) String type);

    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_COMMON)
    Call<BaseBean> verifyWithMoneyCall(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                       @Field(RequestConstant.KEY_AMOUNT) String amount,
                                       @Field(RequestConstant.KEY_CODE) String code,
                                       @Field(RequestConstant.KEY_TYPE) String type);

    /**
     * 任意核销
     *
     * @param userToken
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_COMMON)
    Observable<BaseBean> verifyCommon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                      @Field(RequestConstant.KEY_CODE) String code);

    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_COMMON)
    Call<BaseBean> verifyCommonCall(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_CODE) String code);

    /*****************************************
     * Pos接单提醒
     ****************************************/
    /**
     * 获取在线买单列表
     *
     * @param userToken
     * @param page
     * @param pageSize
     * @param techName
     * @param status
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_ONLINE_PAY_LIST)
    Observable<OnlinePayListResult> getOnlinePayList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_PAGE_START) String page,
                                                     @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                     @Field(RequestConstant.KEY_IS_POS) String isPos,
                                                     @Field(RequestConstant.KEY_TECH_NAME) String techName, // 搜索条件:技师名称或者编号
                                                     @Field(RequestConstant.KEY_STATUS) String status);     // 筛选条件:在线买单状态

    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_ONLINE_PAY_LIST)
    Call<OnlinePayListResult> getOnlinePayCount(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                @Field(RequestConstant.KEY_PAGE_START) String page,
                                                @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                @Field(RequestConstant.KEY_IS_POS) String isPos,
                                                @Field(RequestConstant.KEY_TECH_NAME) String techName, // 搜索条件:技师名称或者编号
                                                @Field(RequestConstant.KEY_STATUS) String status);     // 筛选条件:在线买单状态

    @GET(RequestConstant.URL_GET_DISCOUNT_COUPON_DETAIL)
    Observable<OnlinePayCouponResult> getDiscountCoupon(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                        @Query(RequestConstant.KEY_CODE) String code);

    /**
     * 修改在线买单状态
     *
     * @param userToken
     * @param orderId
     * @param status    paid pass unpass
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_ONLINE_PAY_STATUS_UPDATE)
    Observable<BaseBean> updateOnlinePayStatus(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                               @Field(RequestConstant.KEY_ORDER_ID) String orderId,
                                               @Field(RequestConstant.KEY_STATUS) String status);

    /**
     * 获取会所预约订单列表
     *
     * @param userToken
     * @param page
     * @param pageSize
     * @param telephone
     * @param status
     * @return
     */
    @GET(RequestConstant.URL_GET_ORDER_RECORD_LIST)
    Observable<OrderRecordListResult> getOrderRecordList(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                         @Query(RequestConstant.KEY_PAGE_START) String page,
                                                         @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                         @Query(RequestConstant.KEY_TELEPHONE) String telephone,    // 搜索条件:客户手机号或者技师编号
                                                         @Query(RequestConstant.KEY_STATUS) String status);     // 筛选条件:订单状态

    @GET(RequestConstant.URL_GET_ORDER_RECORD_LIST)
    Call<OrderRecordListResult> getOrderRecordCount(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                    @Query(RequestConstant.KEY_PAGE_START) String page,
                                                    @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                    @Query(RequestConstant.KEY_TELEPHONE) String telephone,    // 搜索条件:客户手机号或者技师编号
                                                    @Query(RequestConstant.KEY_STATUS) String status);     // 筛选条件:订单状态

    /**
     * 修改付费预约状态
     *
     * @param userToken
     * @param sessionType
     * @param processType
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_ORDER_RECORD_STATUS_UPDATE)
    Observable<BaseBean> updateOrderRecordStatus(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                 @Field(RequestConstant.KEY_PROCESS_TYPE) String processType,
                                                 @Field(RequestConstant.KEY_ID) String id);

    /****************************************
     * Pos结算 *
     ****************************************/

    /**
     * 保存结算记录
     *
     * @param userToken
     * @param settleRecord Json串
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_SETTLE_SAVE)
    Observable<BaseBean> saveSettle(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                    @Field(RequestConstant.KEY_SETTLE_RECORD) String settleRecord);

    /**
     * 获取当前未结算的交易汇总
     *
     * @param userToken
     * @return
     */
    @GET(RequestConstant.URL_SETTLE_GET_CURRENT_SUMMARY)
    Observable<SettleSummaryResult> getSettleCurrent(@Query(RequestConstant.KEY_TOKEN) String userToken);

    /**
     * 根据结算记录ID获取结算详情
     *
     * @param userToken
     * @param recordId
     * @return
     */
    @GET(RequestConstant.URL_SETTLE_GET_RECORD_DETAIL)
    Observable<SettleSummaryResult> getSettleDetail(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                    @Query(RequestConstant.KEY_RECORD_ID) String recordId);

    /**
     * 获取结算记录
     * 1.分页获取所有
     * 2.按月获取
     *
     * @param userToken
     * @param page
     * @param pageSize
     * @param settleYm
     * @return
     */
    @GET(RequestConstant.URL_SETTLE_GET_RECORD_LIST)
    Observable<SettleRecordResult> getSettleRecord(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                   @Query(RequestConstant.KEY_PAGE_START) String page,
                                                   @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                   @Query(RequestConstant.KEY_SETTLE_YEAR_MONTH) String settleYm);


    /********************************************  核销记录 **************************************/
    /**
     * 获取核销码类型列表
     *
     * @param userToken
     * @return
     */
    @GET(RequestConstant.URL_GET_VERIFY_TYPE_LIST)
    Observable<VerifyTypeResult> getVerifyTypeList(@Query(RequestConstant.KEY_TOKEN) String userToken);

    /**
     * 获取核销记录列表
     *
     * @param userToken
     * @param page
     * @param pageSize
     * @param telephone
     * @param isTime
     * @param startDate
     * @param endDate
     * @return
     */
    @GET(RequestConstant.URL_GET_VERIFY_RECORD_LIST)
    Observable<VerifyRecordResult> getVerifyRecordList(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                       @Query(RequestConstant.KEY_PAGE_START) String page,
                                                       @Query(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                       @Query(RequestConstant.KEY_TELEPHONE) String telephone,
                                                       @Query(RequestConstant.KEY_TYPE) String type,
                                                       @Query(RequestConstant.KEY_IS_TIME) String isTime,
                                                       @Query(RequestConstant.KEY_START_DATE) String startDate,
                                                       @Query(RequestConstant.KEY_END_DATE) String endDate);

    /**
     * 获取核销明细
     *
     * @param userToken
     * @param recordId
     * @return
     */
    @GET(RequestConstant.URL_GET_VERIFY_RECORD_DETAIL)
    Observable<VerifyRecordDetailResult> getVerifyRecordDetail(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                               @Query(RequestConstant.KEY_RECORD_ID) String recordId);


    /******************************************** Pos在线买单 **************************************/
    //获取扫码买单ID
    @GET(RequestConstant.URL_GET_XMD_ONLINE_ORDER_ID)
    Observable<StringResult> getXMDOnlineOrderId(@Query(RequestConstant.KEY_TOKEN) String userToken);

    //查询买单扫码状态
    @GET(RequestConstant.URL_GET_XMD_ONLINE_SCAN_STATUS)
    Observable<StringResult> getXMDOnlineScanStatus(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                    @Query(RequestConstant.KEY_ORDER_ID) String orderId);

    //获取Pos在线买单详情
    @GET(RequestConstant.URL_GET_XMD_ONLINE_ORDER_DETAIL)
    Observable<OnlinePayDetailResult> getXMDOnlinePayDetail(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                            @Query(RequestConstant.KEY_ORDER_ID) String orderId);

    //扫码买单二维码URL
    @GET(RequestConstant.URL_GET_XMD_ONLINE_QRCODE_URL)
    Observable<OnlinePayUrlResult> getXMDOnlineQrcodeUrl(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                         @Query(RequestConstant.KEY_TOTAL) String total,
                                                         @Query(RequestConstant.KEY_DISCOUNT) String discount);

    // 取消买单
    @FormUrlEncoded
    @POST(RequestConstant.URL_DELETE_XMD_ONLINE_ORDER_ID)
    Observable<BaseBean> deleteXMDOnlineOrderId(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                @Field(RequestConstant.KEY_ORDER_ID) String orderId);

    /******************************************************POS会员**********************************************************/
    // 会员设置信息获取
    @GET(RequestConstant.URL_GET_MEMBER_SETTING_CONFIG)
    Call<MemberSettingResult> getMemberSettingConfig(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 获取会员充值套餐
    @GET(RequestConstant.URL_GET_MEMBER_ACT_PLAN)
    Observable<MemberPlanResult> getMemberPlanList(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 开卡:根据手机号验证会员信息
    @GET(RequestConstant.URL_CHECK_MEMBER_CARD_PHONE)
    Observable<StringResult> checkMemberPhone(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                              @Query(RequestConstant.KEY_TELEPHONE) String telephone);

    // 手机卡号查会员信息
    @GET(RequestConstant.URL_GET_MEMBER_INFO)
    Observable<MemberListResult> getMemberInfo(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                               @Query(RequestConstant.KEY_CARD_NO) String cardNo);

    // 会员开卡
    @FormUrlEncoded
    @POST(RequestConstant.URL_REQUEST_MEMBER_CARD)
    Observable<MemberCardResult> cardMemberInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                @Field(RequestConstant.KEY_BIRTH) String birth,
                                                @Field(RequestConstant.KEY_GENDER) String gender,
                                                @Field(RequestConstant.KEY_CARD_NO) String cardNo,
                                                @Field(RequestConstant.KEY_TELEPHONE) String telephone,
                                                @Field(RequestConstant.KEY_USER_NAME) String userName);

    // 会员充值
    @FormUrlEncoded
    @POST(RequestConstant.URL_REQUEST_MEMBER_RECHARGE)
    Observable<MemberUrlResult> rechargeMemberInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                   @Field(RequestConstant.KEY_ORDER_AMOUNT) String orderAmount,
                                                   @Field(RequestConstant.KEY_DISCOUNT_AMOUNT) String discountAmount,   //自定义赠送金额
                                                   @Field(RequestConstant.KEY_DESCRIPTION) String description,
                                                   @Field(RequestConstant.KEY_MEMBER_ID) String memberId,
                                                   @Field(RequestConstant.KEY_MEMBER_PACKAGE_ID) String packageId,
                                                   @Field(RequestConstant.KEY_TECH_ID) String techId,
                                                   @Field(RequestConstant.KEY_PASSWORD) String password,
                                                   @Field(RequestConstant.KEY_SIGN) String sign);

    // 获取会员账户记录
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_MEMBER_RECORD_LIST)
    Observable<MemberRecordListResult> getMemberRecordList(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                           @Field(RequestConstant.KEY_PAGE_START) String page,
                                                           @Field(RequestConstant.KEY_PAGE_SIZE) String pageSize,
                                                           @Field(RequestConstant.KEY_TYPE) String type,
                                                           @Field(RequestConstant.KEY_USER_NAME) String userName);

    // 会员支付
    @GET(RequestConstant.URL_REQUEST_MEMBER_PAYMENT)
    Observable<MemberRecordResult> requestMemberPayment(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                        @Query(RequestConstant.KEY_AMOUNT) String amount,
                                                        @Query(RequestConstant.KEY_BIZ_CATEGORY) String category,
                                                        @Query(RequestConstant.KEY_DESCRIPTION) String description,
                                                        @Query(RequestConstant.KEY_MEMBER_ID) String memberId,
                                                        @Query(RequestConstant.KEY_TRADE_NO) String tradeNO,
                                                        @Query(RequestConstant.KEY_TRADE_TYPE) String tradeType,
                                                        @Query(RequestConstant.KEY_PASSWORD) String password);

    // 获取会所技师列表
    @GET(RequestConstant.URL_GET_CLUB_TECH_LIST)
    Observable<TechListResult> getTechList(@Query(RequestConstant.KEY_TOKEN) String userToken);

    // 充值回调
    @FormUrlEncoded
    @POST(RequestConstant.URL_REPORT_MEMBER_RECHARGE_TRADE)
    Call<MemberRecordResult> reportMemberRecharge(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Field(RequestConstant.KEY_ORDER_ID) String orderId,
                                                  @Field(RequestConstant.KEY_PAY_CHANNEL) String payChannel,
                                                  @Field(RequestConstant.KEY_TRADE_NO) String tradeNo,
                                                  @Field(RequestConstant.KEY_SIGN) String requestSign);

    @FormUrlEncoded
    @POST(RequestConstant.URL_REPORT_MEMBER_RECHARGE_TRADE)
    Observable<MemberRecordResult> doMemberRecharge(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                    @Field(RequestConstant.KEY_ORDER_ID) String orderId,
                                                    @Field(RequestConstant.KEY_PAY_CHANNEL) String payChannel,
                                                    @Field(RequestConstant.KEY_TRADE_NO) String tradeNo,
                                                    @Field(RequestConstant.KEY_SIGN) String requestSign);

    // 充值详情
    @GET(RequestConstant.URL_GET_MEMBER_RECHARGE_DETAIL)
    Call<MemberRecordResult> detailMemberRecharge(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                  @Query(RequestConstant.KEY_ORDER_ID) String orderId);

    // 更新会员信息
    @FormUrlEncoded
    @POST(RequestConstant.URL_UPDATE_MEMBER_INFO)
    Observable<BaseBean> updateMemberInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_MEMBER_ID) String memberId,
                                          @Field(RequestConstant.KEY_TELEPHONE) String telephone);
    /**************************************************************************************************************************/
    /**
     * 获取会所微信二维码
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_WX_QRCODE)
    Call<StringResult> getClubWXQrcodeURL(@Field(RequestConstant.KEY_CLUB_ID) String userToken);

    @FormUrlEncoded
    @POST(RequestConstant.URL_TRADE_QR_CODE)
    Call<StringResult> getTradeQrcode(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                      @Field(RequestConstant.KEY_TRADE_NO) String tradeNo,
                                      @Field(RequestConstant.KEY_SIGN) String sign);

    @GET
    Call<ResponseBody> getClubQrcodeByWX(@Url String url);

    // 现金收银汇报
    @FormUrlEncoded
    @POST(RequestConstant.URL_REPORT_TRADE_DATA)
    Observable<ReportTradeDataResult> reportCash(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                 @Field(RequestConstant.KEY_USER_ID) String userId,
                                                 @Field(RequestConstant.KEY_CLUB_ID) String clubId,
                                                 @Field(RequestConstant.KEY_TRADE_NO) String tradeNo,
                                                 @Field(RequestConstant.KEY_STATUS) String tradeStatus,
                                                 @Field(RequestConstant.KEY_ORIGIN_MONEY) String originMoney,
                                                 @Field(RequestConstant.KEY_COUPON_LIST) String couponList,
                                                 @Field(RequestConstant.KEY_COUPON_RESULT) String couponResult,
                                                 @Field(RequestConstant.KEY_COUPON_MONEY) String couponMoney,
                                                 @Field(RequestConstant.KEY_DISCOUNT_TYPE) String discountType,
                                                 @Field(RequestConstant.KEY_COUPON_DISCOUNT_MONEY) String couponDiscountMoney,
                                                 @Field(RequestConstant.KEY_USER_DISCOUNT_MONEY) String userDiscountMoney,
                                                 @Field(RequestConstant.KEY_PAY_DATE) String payDate,
                                                 @Field(RequestConstant.KEY_POS_PAY_MONEY) String posPayMoney,
                                                 @Field(RequestConstant.KEY_POS_PAY_TYPE) String posPayType,
                                                 @Field(RequestConstant.KEY_POS_PAY_RESULT) String posPayResult,
                                                 @Field(RequestConstant.KEY_REPORT_TAG) String reportTag,
                                                 @Field(RequestConstant.KEY_SIGN) String sign);
}
