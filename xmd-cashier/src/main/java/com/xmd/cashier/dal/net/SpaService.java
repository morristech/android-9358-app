package com.xmd.cashier.dal.net;

import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.dal.net.response.BillRecordResult;
import com.xmd.cashier.dal.net.response.CheckInfoListResult;
import com.xmd.cashier.dal.net.response.ClubResult;
import com.xmd.cashier.dal.net.response.CommonVerifyResult;
import com.xmd.cashier.dal.net.response.CouponResult;
import com.xmd.cashier.dal.net.response.GetMemberInfo;
import com.xmd.cashier.dal.net.response.GetTradeNoResult;
import com.xmd.cashier.dal.net.response.GetTreatResult;
import com.xmd.cashier.dal.net.response.LoginResult;
import com.xmd.cashier.dal.net.response.LogoutResult;
import com.xmd.cashier.dal.net.response.MemberPayResult;
import com.xmd.cashier.dal.net.response.OnlinePayDetailResult;
import com.xmd.cashier.dal.net.response.OnlinePayListResult;
import com.xmd.cashier.dal.net.response.OrderOrCouponResult;
import com.xmd.cashier.dal.net.response.OrderRecordListResult;
import com.xmd.cashier.dal.net.response.OrderResult;
import com.xmd.cashier.dal.net.response.PrizeResult;
import com.xmd.cashier.dal.net.response.SettleRecordResult;
import com.xmd.cashier.dal.net.response.SettleSummaryResult;
import com.xmd.cashier.dal.net.response.StringResult;
import com.xmd.cashier.dal.net.response.UserCouponListResult;
import com.xmd.cashier.dal.net.response.VerifyRecordDetailResult;
import com.xmd.cashier.dal.net.response.VerifyRecordResult;
import com.xmd.cashier.dal.net.response.VerifyTypeResult;

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
     * 获取会所微信二维码
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_CLUB_WX_QRCODE)
    Observable<StringResult> getClubWXQrcode(@Field(RequestConstant.KEY_CLUB_ID) String userToken,
                                             @Field(RequestConstant.KEY_TRADE_NO) String tradeNo);


    /**
     * 使用手机号，获取用户的所有优惠券信息
     * 核销时使用该接口
     * ------------------------------------   old   --------------------------------------
     *
     * @param userToken
     * @param sessionType
     * @param phoneNumber
     * @param couponType  券类型:默认优惠券+项目券,coupon:优惠券(包含点钟券),service_item:项目券;
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_USER_COUPON_LIST)
    Observable<UserCouponListResult> listUserCoupons(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                     @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                     @Field(RequestConstant.KEY_PHONE_NUMBER) String phoneNumber,
                                                     @Field(RequestConstant.KEY_COUPON_TYPE) String couponType);

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
     * 获取请客详情
     * 收银时使用
     *
     * @param userToken   用户token
     * @param sessionType 会话类型
     * @param treatNo     请客授权码
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_TREAT_INFO)
    Observable<GetTreatResult> getTreatInfo(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                            @Field(RequestConstant.KEY_CODE) String treatNo);

    /**
     * 获取优惠券或者付费预约详情
     * 收银时使用
     *
     * @param userToken
     * @param sessionType
     * @param orderNo
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_GET_ORDER_COUPON_VIEW)
    Observable<OrderOrCouponResult> getOrderOrCouponView(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                                         @Field(RequestConstant.KEY_SESSION_TYPE) String sessionType,
                                                         @Field(RequestConstant.KEY_ORDER_NO) String orderNo);

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
    Observable<MemberPayResult> memberPay(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                          @Field(RequestConstant.KEY_MEMBER_TOKEN) String memberToken,
                                          @Field(RequestConstant.KEY_TRADE_NO) String tradeNo,
                                          @Field(RequestConstant.KEY_AMOUNT) int payMoney,
                                          @Field(RequestConstant.KEY_MEMBER_CAN_DISCOUNT) String canDisCount,
                                          @Field(RequestConstant.KEY_SIGN) String requestSign);

    /**
     * 现金消费增加积分
     *
     * @param userToken
     * @param tradeNo
     * @param phone
     * @param requestSign
     * @return
     */

    @FormUrlEncoded
    @POST(RequestConstant.URL_GAIN_POINTS)
    Observable<BaseResult> gainPoints(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                      @Field(RequestConstant.KEY_TRADE_NO) String tradeNo,
                                      @Field(RequestConstant.KEY_PHONE) String phone,
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
    Observable<BaseResult> verifyCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
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
    Observable<BaseResult> verifyPaidOrder(@Field(RequestConstant.KEY_TOKEN) String userToken,
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
    Observable<BaseResult> verifyLuckyWheel(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                            @Field(RequestConstant.KEY_VERIFY_CODE) String verifyCode);

    /**
     * 核销项目券
     *
     * @param couponNo 项目券优惠码
     * @return
     */
    @FormUrlEncoded
    @POST(RequestConstant.URL_VERIFY_SERVICE_ITEM)
    Observable<BaseResult> verifyServiceCoupon(@Field(RequestConstant.KEY_TOKEN) String userToken,
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
    Observable<BaseResult> verifyCommon(@Field(RequestConstant.KEY_TOKEN) String userToken,
                                        @Field(RequestConstant.KEY_AMOUNT) String amount,
                                        @Field(RequestConstant.KEY_CODE) String code,
                                        @Field(RequestConstant.KEY_TYPE) String type);


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
    Observable<BaseResult> updateOnlinePayStatus(@Field(RequestConstant.KEY_TOKEN) String userToken,
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
    Observable<BaseResult> updateOrderRecordStatus(@Field(RequestConstant.KEY_TOKEN) String userToken,
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
    Observable<BaseResult> saveSettle(@Field(RequestConstant.KEY_TOKEN) String userToken,
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
    /**
     * 获取扫码买单ID
     *
     * @param userToken
     * @return
     */
    @GET(RequestConstant.URL_GET_XMD_ONLINE_ORDER_ID)
    Observable<StringResult> getXMDOnlineOrderId(@Query(RequestConstant.KEY_TOKEN) String userToken);

    /**
     * 查询买单扫码状态
     *
     * @param userToken
     * @param orderId
     * @return
     */
    @GET(RequestConstant.URL_GET_XMD_ONLINE_SCAN_STATUS)
    Observable<StringResult> getXMDOnlineScanStatus(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                    @Query(RequestConstant.KEY_ORDER_ID) String orderId);

    /**
     * 获取Pos在线买单详情
     *
     * @param userToken
     * @param orderId
     * @return
     */
    @GET(RequestConstant.URL_GET_XMD_ONLINE_ORDER_DETAIL)
    Observable<OnlinePayDetailResult> getXMDOnlinePayDetail(@Query(RequestConstant.KEY_TOKEN) String userToken,
                                                            @Query(RequestConstant.KEY_ORDER_ID) String orderId);
}