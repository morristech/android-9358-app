package com.xmd.manager.service;

import android.os.Message;
import android.text.TextUtils;

import com.xmd.manager.AppConfig;
import com.xmd.manager.Constant;
import com.xmd.manager.SettingFlags;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.beans.CheckCouponResult;
import com.xmd.manager.beans.CheckInfoList;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.Customer;
import com.xmd.manager.beans.IndexOrderData;
import com.xmd.manager.beans.OrderDetailResult;
import com.xmd.manager.beans.PaidCouponDetailResult;
import com.xmd.manager.beans.PayResult;
import com.xmd.manager.beans.RegisterDetailResult;
import com.xmd.manager.beans.SwitchIndex;
import com.xmd.manager.beans.VerificationSomeBean;
import com.xmd.manager.beans.VisitListResult;
import com.xmd.manager.common.DESede;
import com.xmd.manager.common.DateUtil;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.AbstractController;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sdcm on 15-10-23.
 */
public class RequestController extends AbstractController {

    public RequestController() {
        super();
    }

    private SpaService getSpaService() {
        return RetrofitServiceFactory.getSpaService();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MsgDef.MSG_DEF_LOGIN:
                doLogin((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_LOGOUT:
                doLogout();
                break;
            case MsgDef.MSG_DEF_GET_CLUB_INFO:
                doGetClubInfo();
                break;
            case MsgDef.MSG_DEF_GET_CLUB_LIST:
                doGetClubList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_ENTER_CLUB_VIEW:
                doEnterClub((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CLUB_COUPON_LIST:
                doGetClubAndCoupon((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_USE_COUPON:
                doUseCoupon((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TOKEN_EXPIRE:
                doHandleTokenExpired(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_ORDER_LIST:
                doGetOrderList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_SEARCH_ORDER_LIST:
                doGetSearchOrderList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_PAID_ORDER_LIST:
                doGetPaidOrderList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_FILTER_ORDER_LIST:
                doFilterOrderList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_MANAGE_ORDER:
                doManageOrder((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT:
                doGetNewOrderCount();
                break;
            case MsgDef.MSG_DEF_SUBMIT_FEEDBACK:
                doSubmitFeedback((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID:
                doBindGetuiClientId();
                break;
            case MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID:
                doUnbindGetuiClientId();
                break;
            case MsgDef.MSG_DEF_GET_CLUB_COUPON_VIEW:
                doGetClubCouponView((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_USER_COUPON_LIST:
                doGetUserCouponList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_USER_COUPON_VIEW:
                doGetUserCouponView((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_COUPON_USE_DATA:
                doGetCouponUseData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_PAID_ORDER_USE:
                doUsePaidOrder((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_CHECK_PAID_ORDER_SWITCH:
                doCheckPaidOrderSwitch();
                break;
            case MsgDef.MSG_DEF_PAID_ORDER_VIEW:
                doGetPaidOrder(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_SEARCH_CUSTOMERS:
                doSearchCustomers(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_CUSTOMER_LIST:
                doGetCustomers();
                break;
            case MsgDef.MSG_DEF_GET_TECH_CUSTOMER_LIST:
                doGetTechCustomers((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CLUB_TECH_LIST:
                doGetClubTechs((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_FILTER_CUSTOMER_TYPE:
                doCustomerFilter((String) msg.obj);
                break;
            case MsgDef.MSG_DEF_CUSTOMER_SORT_COMPLETED:
                doCustomerSortCompleted();
                break;
            case MsgDef.MSG_DEF_GET_CUSTOMER_ORDERS:
                doGetCustomerOrders((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CUSTOMER_COUPONS:
                doGetCustomerCoupons((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_STATISTICS_HOME_DATA:
                doGetStatisticsHomeData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_STATISTICS_MAIN_PAGE_DATA:
                doGetStatisticsMainPageData();
                break;
            case MsgDef.MSG_DEF_DELETE_USE_COMMENT:
                doDeleteUserComment(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_CUSTOMER_VIEW:
                doGetCustomerView(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_ORDER_OR_COUPON_VIEW:
                doGetOrderOrCouponView(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_ORDER_DETAIL:
                doGetOrderDetailData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_DELIVERY_DETAIL:
                doGetDianzhongDetailData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEG_GET_REGISTER_DETAIL:
                doGetRegisterStatisticsData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_APP_UPDATE_CONFIG:
                doGetAppUpdateConfig((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_PAY_DETAIL:
                doPayConfig((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_COUPON_CHECK:
                doGetCouponCheck((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_PAY_BY_CONSUME:
                doPayByConsume((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_AUTH_CONFIG:
                doGetClubAuthConfig();
                break;
            case MsgDef.MSG_DEF_GET_WIFI_REPORT:
                doGetWifiDataDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_VISIT_REPORT:
                doGetVisitDataDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_REGISTER_REPORT:
                doGetRegisterDataDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_COUPON_REPORT:
                doGetCouponDataDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_VISIT_LIST:
                doGetVisitList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_GROUP_LIST:
                doGetGroupList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_GROUP_STAT_SWITCH:
                getGMessageStatSwitch();
                break;
            case MsgDef.MSG_DEF_GET_GROUP_INFO:
                doGetGroupInfo((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GROUP_MESSAGE_SEND:
                doSendGroupMessage((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_WIFI_DATA:
                doGetMainPageWifiData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CLUB_VISIT_DATA:
                doGetMainPageClubVisitData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_COUPON_DATA_INDEX:
                doGetMainPageCouponData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_REGISTRY_DATA:
                doGetMainPageRegistryData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_ORDER_DATA:
                doGetMainPageOrderData();
                break;
            case MsgDef.MSG_DEF_GET__INDEX_ORDER_DATA:
                doGetMainPageIndexOrderData();
                break;
            case MsgDef.MSG_DEF_GET_TECH_RANK_DATA:
                doGetMainPageRankData();
                break;
            case MsgDef.MSG_DEF_GET_ClUB_FAVOURABLE_ACTIVITY:
                doGetFavourableActivity((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_MARKETING_ITEMS:
                getMarketingItems();
                break;

            case MsgDef.MSG_DEF_BAD_COMMENT_DETAIL:
                getBadCommentDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_CHANGE_COMMENT_STATUS:
                doChangeCommentStatus((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_BAD_COMMENT:
                getTechBadCommentList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_MODIFY_PASSWORD:
                doChangePassword((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CHECK_INFO_TYPE_GET:
                getVerificationType(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_PAY_ORDER_DETAIL:
                getPayOrderDetail(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_CHECK_INFO_COUPON_DETAIL:
                getVerificationCouponDetail(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_DO_VERIFICATION_SERVICE_ITEM_COUPON:
                getVerificationServiceCouponDetail(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_VERIFICATION_COMMON_DETAIL:
                getDefaultVerificationDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_CHECK_INFO_ORDER_SAVE:
                doVerificationOrderSave((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DO_VERIFICATION_COMMON_SAVE:
                doVerificationCommonSave((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DO_VERIFICATION_COUPON_SAVE:
                doVerificationCouponSave(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_DO_VERIFICATION_SERVICE_ITEM_COUPON_SAVE:
                doVerificationServiceItemSave(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_VERIFICATION_AWARD_DETAIL:
                getVerificationAwardDetail(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_DO_VERIFICATION_AWARD_SAVE:
                doVerificationSave(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_SWITCH_INDEX:
                doSwitchIndex((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_ClUB_GROUP_LIST:
                getClubGroupList();
                break;
            case MsgDef.MSG_DEF_GET_GROUP_TAG_LIST:
                getGroupTagList();
                break;
            case MsgDef.MSG_DEF_DO_GROUP_SAVE:
                doGroupSave((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_GROUP_USER_LIST:
                getGroupUserList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DO_GROUP_DELETE:
                doGroupDelete(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_GROUP_DETAILS:
                getGroupDetails(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_DO_USER_ADD_GROUP:
                doUserAddGroup((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DO_USER_EDIT_GROUP:
                doUserEditGroup(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_DO_GROUP_MESSAGE_ALBUM_UPLOAD:
                doGroupMessageAlbumUpload(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_USER_GET_COUPON:
                getClubUserCoupon((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_USER_REGISTER_LIST:
                doGetRegisterList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_CHECK_INFO_TYPE_LIST:
                getCheckInfoTypeList();
                break;
            case MsgDef.MSG_DEF_CHECK_INFO_RECORD_LIST:
                getCheckInfoRecordList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_CHECK_INFO_RECORD_DETAIL:
                getCheckInfoRecordDetail(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_APP_COMMENT_LIST:
                getAppCommentList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DATA_STATISTICS_WIFI_DATA:
                getStatisticsWifiData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DATA_STATISTICS_ACCOUNT_DATA:
                getAccountData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_FAST_PAY_ORDER_LIST:
                getFastPayOrderList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DATA_STATISTICS_SALE_DATA:
                getSaleDataStatistics((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_PK_RANKING:
                getTechPKRanking();
                break;
            case MsgDef.MSG_DEF_TECH_PK_ACTIVITY_LIST:
                getTechPKActivityList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_PK_TEAM_RANKING_LIST:
                getTechPkTeamList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_PK_PERSONAL_RANKING_LIST:
                getTechPKPersonalList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_RANKING_LIST:
                getTechPersonalList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT:
                doSaveToContact((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CHECK_INFO:
                doGetCheckInfo((String) msg.obj);
                break;
            case MsgDef.MSG_DEF_BAD_COMMENT_AND_COMPLAINT_LIST:
                getBadCommentAndComplaint();
                break;
            case MsgDef.MSG_DEF_GET_SUMMARY_ORDER_DATA:
                getSummaryOrderData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_TECH_ORDER_RANK:
                getStaffDataTechOrderRank((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CLUB_ORDER_TECHNICIAN_LIST:
                getOrderTechnicianList();
                break;
            case MsgDef.MSG_DEF_GET_CLUB_ORDER_PROJECT_LIST:
                getOrderProjectList();
                break;
            case MsgDef.MSG_DEF_GET_ORDER_FILTER_LIST:
                getOrderListFilter((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_REPORT_NEWS:
                getFinancialReportNews();
                break;
            case MsgDef.MSG_DEF_GET_REPORT_LIST:
                getFinancialReportList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_REPORT_CONFIG:
                setFinancialReportConfig((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SETTING_REPORT_CONFIG:
                getFinancialReportConfig();
                break;
            case MsgDef.MSG_DEF_REPORT_READ:
                makeReportRead((String) msg.obj);
                break;
            case MsgDef.MSG_DEF_DELETE_REPORT:
                deleteFinancialReportById(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_CREATE_REPORT_CUSTOM:
                createFinancialReport((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_COUPON_OPERATE_LIST_DATA:
                getCouponOperateData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_COUPON_OPERATE_DATA_TOTAL:
                getCouponOperateTotal((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_COUPON_RECORD_DATA:
                getCouponRecordData((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_COUPON_LIST_DATA:
                getCouponListData((Map<String, String>) msg.obj);
                break;

            case MsgDef.MSG_DEF_GET_TECH_BASE_LIST:
                getTechBaseList();
                break;
            case MsgDef.MSG_DEF_GET_COMMISSION_SUM_LIST:    //获取指定时间段技师工资汇总列表
                getCommissionSumList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_COMMISSION_SUM_AMOUNT:  //获取指定时间段会所提成汇总金额
                getCommissionSumAmount((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_ALL_TECH_COMMISSION_LIST:   //获取具体某天会所所有技师提成列表
                getCommissionAmountList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_TECH_COMMISSION_AMOUNT: //获取具体某天某技师提成汇总金额
                getTechCommissionAmount((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_TECH_COMMISSION_DETAIL_LIST:    //获取具体某天某技师提成明细列表
                getTechCommissionDetailList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_TECH_COMMISSION_DETAIL_INFO:    //获取提成明细的具体详情
                getTechCommissionDetailInfo((String) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CASHIER_STATISTIC_INFO: //获取买单收银日汇总
                getCashierStatisticInfo((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CASHIER_CLUB_DETAIL_LIST:   //获取买单收银明细列表
                getCashierClubDetailList((Map<String, String>) msg.obj);
                break;

        }
        return true;
    }


    private void doSwitchIndex(Map<String, String> params) {
        RxBus.getInstance().post(new SwitchIndex(Integer.parseInt(params.get(Constant.SWITCH_INDEX)), Integer.parseInt(params.get(Constant.PARAM_RANGE))
                , params.get(Constant.ORDER_START_TIME), params.get(Constant.ORDER_END_TIME)));
    }

    /**************************************** Called By Activities  *****************************/

    /**
     * Need to replace the doGetClubAndCoupon for club information
     */
    private void doGetClubInfo() {
        Call<ClubResult> call = getSpaService().getClubInfo(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<ClubResult>() {
            @Override
            protected void postResult(ClubResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                ClubResult result = new ClubResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * Need to replace the doGetClubAndCoupon for club information
     */
    private void doGetClubList(Map<String, String> params) {
        Call<ClubListResult> call = getSpaService().getClubList(params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_CLUB_NAME), SharedPreferenceHelper.getMultiClubToken());

        call.enqueue(new TokenCheckedCallback<ClubListResult>() {

            @Override
            protected void postResult(ClubListResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });
    }

    private void doEnterClub(Map<String, String> params) {
        Call<ClubEnterResult> call = getSpaService().enterClub(params.get(RequestConstant.KEY_CLUB_ID),
                params.get(RequestConstant.KEY_APP_VERSION),
                RequestConstant.CLUB_ANDROID_CHANEL, SharedPreferenceHelper.getMultiClubToken());

        call.enqueue(new TokenCheckedCallback<ClubEnterResult>() {
        });
    }

    /**
     * Get club and coupon information when user login
     */
    private void doGetClubAndCoupon(Map<String, String> params) {
        Call<ClubCouponResult> call = getSpaService().getClubCoupons(params.get(RequestConstant.KEY_PAGE),
                params.get(RequestConstant.KEY_PAGE_SIZE),
                SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, RequestConstant.USER_TYPE_MANAGER);
        call.enqueue(new TokenCheckedCallback<ClubCouponResult>() {
            @Override
            protected void postResult(ClubCouponResult result) {
                if (result != null) {
                    RxBus.getInstance().post(result);
                }
            }

            @Override
            protected void postError(String errorMsg) {

                ClubCouponResult result = new ClubCouponResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;

                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * Login Button Clicked in LoginActivity
     */
    private void doLogin(Map<String, String> params) {
        Call<LoginResult> call = getSpaService().login(params.get(RequestConstant.KEY_USERNAME),
                params.get(RequestConstant.KEY_PASSWORD),
                params.get(RequestConstant.KEY_APP_VERSION),
                RequestConstant.SESSION_TYPE);

        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                LoginResult loginResult = response.body();
                if (loginResult != null) {
                    RxBus.getInstance().post(loginResult);
                } else {
                    try {
                        RxBus.getInstance().post(new Throwable(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                RxBus.getInstance().post(t);
            }
        });
    }

    /**
     * Logout Button Clicked in PopupMoreWindow, just tell the server that we logout and do nothing in return
     */
    private void doLogout() {
        Call<LogoutResult> call = getSpaService().logout(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<LogoutResult>() {
            @Override
            protected void postResult(LogoutResult result) {
                Logger.v("RequestController.doLogout : Success");
                SharedPreferenceHelper.clearUserInfo();
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("RequestController.doLogout : error");
                SharedPreferenceHelper.clearUserInfo();
            }
        });
    }

    /**
     * Coupon Consume Click in Consume Activity
     */
    private void doUseCoupon(Map<String, String> params) {
        Call<UseCouponResult> call = getSpaService().useCoupon(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_SUA_ID),
                params.get(RequestConstant.KEY_COUPON_NO));
        call.enqueue(new TokenCheckedCallback<UseCouponResult>() {
        });
    }


    /**
     * 付费预约
     * Retrieve the paid order list from the backend services
     *
     * @param params
     */
    private void doGetPaidOrderList(Map<String, String> params) {
        Call<OrderListResult> call = getSpaService().getPaidOrderList(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_ORDER_STATUS),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));

        call.enqueue(new TokenCheckedCallback<OrderListResult>() {
            @Override
            protected void postResult(OrderListResult result) {
                result.type = params.get(RequestConstant.KEY_ORDER_STATUS);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                OrderListResult result = new OrderListResult();
                result.type = params.get(RequestConstant.KEY_ORDER_STATUS);
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * Retrieve the order list from the backend services
     * 获取订单
     *
     * @param params
     */
    private void doGetOrderList(Map<String, String> params) {
        Call<OrderListResult> call = getSpaService().getOrderList(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_ORDER_STATUS), params.get(RequestConstant.KEY_SEARCH_TELEPHONE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE), params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<OrderListResult>() {
            @Override
            protected void postResult(OrderListResult result) {
                result.type = params.get(RequestConstant.KEY_ORDER_STATUS);
                result.startTime = params.get(RequestConstant.KEY_START_DATE);
                result.endTime = params.get(RequestConstant.KEY_END_DATE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                OrderListResult result = new OrderListResult();
                result.type = params.get(RequestConstant.KEY_ORDER_STATUS);
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetSearchOrderList(Map<String, String> params) {
        Call<OrderSearchListResult> call = getSpaService().getOrderSearchList(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_ORDER_STATUS), params.get(RequestConstant.KEY_SEARCH_TELEPHONE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE), params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<OrderSearchListResult>() {
            @Override
            protected void postResult(OrderSearchListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                OrderSearchListResult result = new OrderSearchListResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * Update the order's status
     *
     * @param params
     */
    private void doManageOrder(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().manageOrder(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_PROCESS_TYPE),
                params.get(RequestConstant.KEY_ID), params.get(RequestConstant.KEY_REASON));

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new OrderManageResult(true, "操作成功"));
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("doManageOrder: " + errorMsg);
                RxBus.getInstance().post(new OrderManageResult(false, "操作失败"));
            }
        });

    }

    /**
     * 核销或者过期“付费预约”
     *
     * @param params
     */
    private void doUsePaidOrder(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().usePaidOrder(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_PROCESS_TYPE),
                params.get(RequestConstant.KEY_ORDER_NO),
                params.get(RequestConstant.KEY_ID));

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new OrderManageResult(true, "操作成功"));
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("doUsePaidOrder: " + errorMsg);
                RxBus.getInstance().post(new OrderManageResult(false, "操作失败"));
            }
        });

    }

    /**
     * submit feedback
     *
     * @param params
     */
    private void doSubmitFeedback(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().submitFeedback(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_COMMENTS));

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new FeedbackResult());
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("submitFeedback: " + errorMsg);
            }
        });
    }

    /**
     * 会所未接受订单数
     */
    private void doGetNewOrderCount() {
        Call<NewOrderCountResult> call =
                getSpaService().getNewOrderCount(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<NewOrderCountResult>() {
            @Override
            protected void postError(String errorMsg) {
                Logger.v("getNewOrderCount: " + errorMsg);
            }
        });
    }

    /**
     *
     */
    private void doBindGetuiClientId() {

        if (Utils.isEmpty(SharedPreferenceHelper.getUserToken())) {
            return;
        }

        if (!SettingFlags.getBoolean(SettingFlags.ORDER_NOTIFIATION_ON)) {
            return;
        }

        String clientId = AppConfig.sClientId;

        //由于之前获取过clientid，并将其存放在shared_preferences中了，而SDK在获取client_id的过程中可能存在延迟，
        // 此时可从shared_preferences中先获取，减少client_id为空的几率
        if (Utils.isEmpty(clientId)) {
            clientId = SharedPreferenceHelper.getClientId();
            if (Utils.isEmpty(clientId)) {
                return;
            }
        }

        String userId = SharedPreferenceHelper.getUserId();
        Logger.v("start bind client id : " + AppConfig.sClientId + " with user Id : " + userId);

        //String decryptPwd = appID + appSecret + userId + appKey + masterSecret + clientId;
        StringBuilder sb = new StringBuilder();
        sb.append(AppConfig.sGetuiAppId)
                .append(AppConfig.sGetuiAppSecret)
                .append(userId)
                .append(AppConfig.sGetuiAppKey)
                .append(AppConfig.sGetuiMasterSecret)
                .append(AppConfig.sClientId);
        String secretBefore = sb.toString();
        String secret = DESede.encrypt(secretBefore);

        Call<BaseResult> call = getSpaService().bindGetuiClientId(userId, RequestConstant.USER_TYPE_MANAGER, RequestConstant.APP_TYPE_ANDROID,
                AppConfig.sClientId, secret);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                Logger.v("bind successful with client id : " + AppConfig.sClientId);
                AppConfig.sBindClientIdStatus = "bind Successful";
                AppConfig.sGetuiClientIdBound = true;
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("bind failed with " + errorMsg);
                AppConfig.sBindClientIdStatus = "bind failed";
                AppConfig.sGetuiClientIdBound = false;
            }
        });
    }

    /**
     * 解绑个推ClientID
     */
    private void doUnbindGetuiClientId() {
        Logger.v("start unbind client id");
        Call<BaseResult> call = getSpaService().unbindGetuiClientId(RequestConstant.USER_TYPE_MANAGER,
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, AppConfig.sClientId);
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                Logger.v("unbind successful");
                AppConfig.sBindClientIdStatus = "unbind successful";
                AppConfig.sGetuiClientIdBound = false;
            }

            @Override
            protected void postError(String errorMsg) {
                AppConfig.sBindClientIdStatus = "unbind failed";
                AppConfig.sGetuiClientIdBound = true;
            }
        });
    }

    /**
     * 会所优惠券详情
     *
     * @param params
     */
    private void doGetClubCouponView(Map<String, String> params) {
        Call<ClubCouponViewResult> call = getSpaService().getClubCouponView(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_ACT_ID));
        call.enqueue(new TokenCheckedCallback<ClubCouponViewResult>() {
            @Override
            protected void postError(String errorMsg) {

                ClubCouponViewResult result = new ClubCouponViewResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;

                RxBus.getInstance().post(result);

            }
        });
    }

    /**
     * 获取用户领取的优惠券列表
     *
     * @param params
     */
    private void doGetUserCouponList(Map<String, String> params) {
        Call<UserCouponListResult> call = getSpaService().getUserCouponList(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_PHONE_NUMBER));
        call.enqueue(new TokenCheckedCallback<UserCouponListResult>() {
            @Override
            protected void postError(String errorMsg) {
                UserCouponListResult result = new UserCouponListResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);

            }
        });
    }

    /**
     * 用户优惠券详情
     *
     * @param params
     */
    private void doGetUserCouponView(Map<String, String> params) {
        Call<UserCouponViewResult> call = getSpaService().getUserCouponView(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_SUA_ID),
                params.get(RequestConstant.KEY_COUPON_NO));

        call.enqueue(new TokenCheckedCallback<UserCouponViewResult>() {
            @Override
            protected void postError(String errorMsg) {
                UserCouponViewResult result = new UserCouponViewResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 用户优惠券使用情况
     *
     * @param params
     */
    private void doGetCouponUseData(Map<String, String> params) {
        Call<CouponUseDataResult> call = getSpaService().getCouponUseData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_ACT_ID), params.get(RequestConstant.KEY_PAGE),
                params.get(RequestConstant.KEY_PAGE_SIZE));

        call.enqueue(new TokenCheckedCallback<CouponUseDataResult>() {
            @Override
            protected void postError(String errorMsg) {
                CouponUseDataResult result = new CouponUseDataResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;

                RxBus.getInstance().post(result);
            }
        });
    }

    private void doFilterOrderList(Map<String, String> params) {
        RxBus.getInstance().post(new OrderFilterChangeResult(params.get(Constant.ORDER_STATUS_TYPE), params.get(Constant.ORDER_START_TIME),
                params.get(Constant.ORDER_END_TIME)));
    }

    /**
     * @param errorMsg
     */
    private void doHandleTokenExpired(String errorMsg) {
        RxBus.getInstance().post(new TokenExpiredResult(errorMsg));
    }

    /**
     * 检查会所是否开启付费预约
     */
    private void doCheckPaidOrderSwitch() {
        Call<PaidOrderSwitchResult> call = getSpaService().checkPaidOrderSwitch(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<PaidOrderSwitchResult>() {
            @Override
            protected void postResult(PaidOrderSwitchResult result) {
                if (Constant.ON.equals(result.respData.openStatus)) {
                    RxBus.getInstance().post(result);
                }
            }
        });
    }

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo
     */
    private void doGetPaidOrder(String orderNo) {
        Call<OrderResult> call = getSpaService().getPaidOrder(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, orderNo);
        call.enqueue(new TokenCheckedCallback<OrderResult>() {
        });
    }

    private void doGetStatisticsMainPageData() {
        Call<StatisticsMainPageResult> call = getSpaService().getStatisticsMainPageData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<StatisticsMainPageResult>() {
            @Override
            protected void postResult(StatisticsMainPageResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetCustomers() {
        Call<CustomerListResult> call = getSpaService().getCustomers(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, "");
        call.enqueue(new TokenCheckedCallback<CustomerListResult>() {
            @Override
            protected void postError(String errorMsg) {
                CouponUseDataResult result = new CouponUseDataResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;

                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetTechCustomers(Map<String, String> params) {
        Call<CustomerListResult> call = getSpaService().getTechCustomers(params.get(RequestConstant.KEY_SORT), params.get(RequestConstant.KEY_SORT_TYPE),
                params.get(RequestConstant.KEY_TECH_ID), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_USER_TYPE), SharedPreferenceHelper.getUserToken());

        call.enqueue(new TokenCheckedCallback<CustomerListResult>() {
            @Override
            protected void postResult(CustomerListResult result) {
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CustomerListResult result = new CustomerListResult(new ArrayList<Customer>());
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                result.type = params.get(RequestConstant.KEY_TYPE);

                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetClubTechs(Map<String, String> params) {
        Call<TechListResult> call = getSpaService().getClubTechs(params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_USER_TYPE), SharedPreferenceHelper.getUserToken());


        call.enqueue(new TokenCheckedCallback<TechListResult>() {
            @Override
            protected void postResult(TechListResult result) {

                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                TechListResult result = new TechListResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doCustomerFilter(String userType) {
        RxBus.getInstance().post(new CustomerFilterResult(userType));
    }

    private void doSearchCustomers(String userName) {
        Call<CustomerSearchListResult> call = getSpaService().searchCustomers(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, userName);
        call.enqueue(new TokenCheckedCallback<CustomerSearchListResult>() {
        });
    }


    /**
     * @param params
     */
    private void doGetCustomerOrders(Map<String, String> params) {
        Call<CustomerOrdersResult> call = getSpaService().getCustomerOrders(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_USER_ID), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<CustomerOrdersResult>() {
        });
    }

    /**
     * @param params
     */
    private void doGetCustomerCoupons(Map<String, String> params) {
        Call<CustomerCouponsResult> call = getSpaService().getCustomerCoupons(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_USER_ID), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<CustomerCouponsResult>() {
        });
    }


    /**
     * 获取数据统计首页所有数据
     */
    private void doGetStatisticsHomeData(Map<String, String> params) {
        Call<StatisticsHomeDataResult> call = getSpaService().getStatisticsHomeData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<StatisticsHomeDataResult>() {
            @Override
            protected void postResult(StatisticsHomeDataResult result) {
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                StatisticsHomeDataResult result = new StatisticsHomeDataResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * @param commentId
     */
    private void doDeleteUserComment(String commentId) {
        Call<CommentDeleteResult> call = getSpaService().deleteUserComment(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, commentId);
        call.enqueue(new TokenCheckedCallback<CommentDeleteResult>() {
            @Override
            protected void postError(String errorMsg) {
                CommentDeleteResult result = new CommentDeleteResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetCustomerView(String userId) {
        Call<CustomerResult> call = getSpaService().getCustomerView(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, userId);
        call.enqueue(new TokenCheckedCallback<CustomerResult>() {
            @Override
            protected void postError(String errorMsg) {
                CustomerResult result = new CustomerResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetOrderOrCouponView(String orderNo) {
        Call<OrderOrCouponResult> call = getSpaService().getOrderOrCouponView(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, orderNo);
        call.enqueue(new TokenCheckedCallback<OrderOrCouponResult>() {
        });
    }

    private void doCustomerSortCompleted() {
        RxBus.getInstance().post(new CustomerSortCompletedResult());
    }

    /**
     * 获取订单页详情
     *
     * @param params
     */
    private void doGetOrderDetailData(Map<String, String> params) {
        Call<OrderDetailResult> call = getSpaService().getOrderDetailData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<OrderDetailResult>() {
            @Override
            protected void postResult(OrderDetailResult result) {
                result.type = params.get(RequestConstant.KEY_TYPE);
                result.starTime = params.get(RequestConstant.KEY_START_DATE);
                result.endTime = params.get(RequestConstant.KEY_END_DATE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                OrderDetailResult result = new OrderDetailResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 获取点钟页详情
     *
     * @param params
     */
    private void doGetDianzhongDetailData(Map<String, String> params) {
        Call<PaidCouponDetailResult> call = getSpaService().getDianzhongDetailData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<PaidCouponDetailResult>() {
            @Override
            protected void postResult(PaidCouponDetailResult result) {
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                PaidCouponDetailResult result = new PaidCouponDetailResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 获取注册详情
     *
     * @param params
     */
    private void doGetRegisterDetailData(Map<String, String> params) {
        Call<RegisterDetailResult> call = getSpaService().getRegisterDetailData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<RegisterDetailResult>() {
            @Override
            protected void postResult(RegisterDetailResult result) {
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                RegisterDetailResult result = new RegisterDetailResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetRegisterStatisticsData(Map<String, String> params) {
        Call<RegisterStatisticsResult> call = getSpaService().getRegisterStatisticsData(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<RegisterStatisticsResult>() {
            @Override
            protected void postResult(RegisterStatisticsResult result) {
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                RegisterDetailResult result = new RegisterDetailResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                result.type = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doPayConfig(Map<String, String> params) {
        Call<PayResult> call = getSpaService().getPayResult(
                SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_PAY_AMOUNT),
                params.get(RequestConstant.KEY_PAY_QRNO),
                params.get(RequestConstant.KEY_PAY_RID),
                params.get(RequestConstant.KEY_TIME)
        );

        call.enqueue(new TokenCheckedCallback<PayResult>() {
            @Override
            protected void postResult(PayResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                //error_code为自定义错误码
                PayResult payResult = new PayResult(RequestConstant.PAY_RESULT_ERROR_CODE, errorMsg);
                RxBus.getInstance().post(payResult);
            }

            @Override
            public void onFailure(Call<PayResult> call, Throwable t) {
                super.onFailure(call, t);
                t.printStackTrace();
            }
        });
    }

    private void doGetCouponCheck(Map<String, String> params) {
        Call<CheckCouponResult> call = getSpaService().getCheckCoupon(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_PAY_CODE));
        call.enqueue(new TokenCheckedCallback<CheckCouponResult>() {
            @Override
            protected void postResult(CheckCouponResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CheckCouponResult result = new CheckCouponResult("", errorMsg);
                RxBus.getInstance().post(result);
            }

        });
    }

    private void doPayByConsume(Map<String, String> params) {
        Call<PayResult> call = getSpaService().getPayConsumeResult(
                SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_PAY_CODE),
                params.get(RequestConstant.KEY_PAY_AMOUNT_BY_CODE)
        );
        call.enqueue(new TokenCheckedCallback<PayResult>() {
            @Override
            protected void postResult(PayResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                PayResult payResult = new PayResult(RequestConstant.PAY_RESULT_ERROR_CODE, errorMsg);
                RxBus.getInstance().post(payResult);
            }

        });
    }


    /**
     * 获取会所菜单配置
     */
    private void doGetClubAuthConfig() {
        Call<ClubAuthConfigResult> call = getSpaService().getClubMenuConfig(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, RequestConstant.PLATFORM_TYPE_ADMIN_APP);
        call.enqueue(new TokenCheckedCallback<ClubAuthConfigResult>() {
            @Override
            protected void postResult(ClubAuthConfigResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                ClubAuthConfigResult result = new ClubAuthConfigResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
            }
        });
    }

    /**
     * Wifi宣传
     *
     * @param params
     */
    private void doGetWifiDataDetail(Map<String, String> params) {
        Call<LineChartDataResult> call = getSpaService().getWifiData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<LineChartDataResult>() {
            @Override
            protected void postResult(LineChartDataResult result) {

                RxBus.getInstance().post(result);

            }

            @Override
            protected void postError(String errorMsg) {
                LineChartDataResult result = new LineChartDataResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 网店访问
     *
     * @param params
     */
    private void doGetVisitDataDetail(Map<String, String> params) {
        Call<LineChartDataResult> call = getSpaService().getVisiterData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));

        call.enqueue(new TokenCheckedCallback<LineChartDataResult>() {
            @Override
            protected void postResult(LineChartDataResult result) {

                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                LineChartDataResult result = new LineChartDataResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 新增用户
     *
     * @param params
     */
    private void doGetRegisterDataDetail(Map<String, String> params) {
        Call<LineChartDataResult> call = getSpaService().getRegisterData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<LineChartDataResult>() {
            @Override
            protected void postResult(LineChartDataResult result) {

                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                LineChartDataResult result = new LineChartDataResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 优惠券发放
     *
     * @param params
     */
    private void doGetCouponDataDetail(Map<String, String> params) {
        Call<LineChartDataResult> call = getSpaService().getCouponData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<LineChartDataResult>() {
            @Override
            protected void postResult(LineChartDataResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                LineChartDataResult result = new LineChartDataResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);

            }
        });
    }

    /**
     * 网店访问详情
     *
     * @param params
     */
    private void doGetVisitList(Map<String, String> params) {
        Call<VisitListResult> call = getSpaService().getVisitListData(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));

        call.enqueue(new TokenCheckedCallback<VisitListResult>() {
            @Override
            protected void postResult(VisitListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                VisitListResult result = new VisitListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);

            }
        });
    }

    /**
     * 新增客户详情
     *
     * @param params
     */
    private void doGetRegisterList(Map<String, String> params) {
        Call<RegisterListResult> call = getSpaService().getRegisterListData(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_PAGE),
                params.get(RequestConstant.KEY_PAGE_SIZE));

        call.enqueue(new TokenCheckedCallback<RegisterListResult>() {
            @Override
            protected void postResult(RegisterListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                RegisterListResult result = new RegisterListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);

            }
        });
    }

    /**
     * 群消息列表
     *
     * @param params
     */

    private void doGetGroupList(Map<String, String> params) {
        Call<GroupMessageResult> call = getSpaService().getGroupList(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));

        call.enqueue(new TokenCheckedCallback<GroupMessageResult>() {
            @Override
            protected void postResult(GroupMessageResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.e(errorMsg);
                GroupMessageResult result = new GroupMessageResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);

            }
        });
    }

    private void getGMessageStatSwitch() {
        Call<GMessageStatSwitchResult> call = getSpaService().getGMessageStatSwitch(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<GMessageStatSwitchResult>() {
            @Override
            protected void postResult(GMessageStatSwitchResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 群消息剩余发送次数
     *
     * @param params
     */

    private void doGetGroupInfo(Map<String, String> params) {
        Call<GroupInfoResult> call = getSpaService().getGroupInfo(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<GroupInfoResult>() {
            @Override
            protected void postResult(GroupInfoResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                GroupInfoResult result = new GroupInfoResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);

            }
        });
    }

    /**
     * 发送群消息
     *
     * @param params
     */

    private void doSendGroupMessage(Map<String, String> params) {
        Call<SendGroupMessageResult> call = getSpaService().doSendMessage(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_GROUP_ACT_ID), params.get(RequestConstant.KEY_GROUP_ACT_NAME),
                params.get(RequestConstant.KEY_GROUP_COUPON_CONTENT), params.get(RequestConstant.KEY_GROUP_IDS), params.get(RequestConstant.KEY_GROUP_IMAGE_ID), params.get(RequestConstant.KEY_GROUP_MESSAGE_CONTENT),
                params.get(RequestConstant.KEY_GROUP_USER_GROUP_TYPE), params.get(RequestConstant.KEY_GROUP_SUB_GROUP_LABELS), params.get(RequestConstant.KEY_GROUP_MESSAGE_TYEP));

        call.enqueue(new TokenCheckedCallback<SendGroupMessageResult>() {
            @Override
            protected void postResult(SendGroupMessageResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                SendGroupMessageResult result = new SendGroupMessageResult();
                result.msg = errorMsg;
                result.statusCode = 400;
                RxBus.getInstance().post(result);

            }
        });
    }

    /**
     * 首页Wifi宣传
     *
     * @param
     */
    private void doGetMainPageWifiData(Map<String, String> params) {
        Call<WifiDataResult> call = getSpaService().getMainPageWifiData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_DATE));
        call.enqueue(new TokenCheckedCallback<WifiDataResult>() {
            @Override
            protected void postResult(WifiDataResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 首页网店访问
     *
     * @param
     */
    private void doGetMainPageClubVisitData(Map<String, String> params) {
        Call<VisitDataResult> call = getSpaService().getMainPageVisitData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_DATE));
        call.enqueue(new TokenCheckedCallback<VisitDataResult>() {
            @Override
            protected void postResult(VisitDataResult result) {

                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 首页新增客户
     *
     * @param
     */
    private void doGetMainPageRegistryData(Map<String, String> params) {
        Call<RegistryDataResult> call = getSpaService().getMainPageRegistryData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_DATE));
        call.enqueue(new TokenCheckedCallback<RegistryDataResult>() {
            @Override
            protected void postResult(RegistryDataResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 首页领券数量
     *
     * @param
     */
    private void doGetMainPageCouponData(Map<String, String> params) {
        Call<CouponDataResult> call = getSpaService().getMainPageCouponData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_DATE));
        call.enqueue(new TokenCheckedCallback<CouponDataResult>() {
            @Override
            protected void postResult(CouponDataResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });


    }


    /**
     * 首页订单
     *
     * @param
     */
    private void doGetMainPageOrderData() {
        Call<OrderDataResult> call = getSpaService().getMainPageOrderData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<OrderDataResult>() {
            @Override
            protected void postResult(OrderDataResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 首页订单最新
     *
     * @param
     */
    private void doGetMainPageIndexOrderData() {
        Call<IndexOrderData> call = getSpaService().getMainPageIndexOrderData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<IndexOrderData>() {
            @Override
            protected void postResult(IndexOrderData result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 首页排行榜
     *
     * @param
     */
    private void doGetMainPageRankData() {
        Call<TechRankDataResult> call = getSpaService().getMainPageTechRankData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<TechRankDataResult>() {
            @Override
            protected void postResult(TechRankDataResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 会所优惠活动列表
     *
     * @param params
     */
    private void doGetFavourableActivity(Map<String, String> params) {
        Call<FavourableActivityListResult> call = getSpaService().getFavourableActivityList(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<FavourableActivityListResult>() {
            @Override
            protected void postResult(FavourableActivityListResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });
    }

    /**
     * 营销列表
     *
     * @param
     */
    private void getMarketingItems() {
        Call<MarketingResult> call = getSpaService().getMarketingItems(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<MarketingResult>() {
            @Override
            protected void postResult(MarketingResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });
    }


    /**
     * 获取差评列表
     *
     * @param
     */
    private void getBadCommentList(Map<String, String> params) {
        Call<BadCommentListResult> call = getSpaService().getBadCommentList(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_COMMENT_STATUS), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<BadCommentListResult>() {
            @Override
            protected void postResult(BadCommentListResult result) {
                result.commentState = params.get(RequestConstant.KEY_COMMENT_STATUS);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });
    }


    /**
     * 获取差评详情
     *
     * @param
     */
    private void getBadCommentDetail(Map<String, String> params) {
        Call<BadCommentResult> call = getSpaService().getBadCommentDetail(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_COMMENT_ID));
        call.enqueue(new TokenCheckedCallback<BadCommentResult>() {
            @Override
            protected void postResult(BadCommentResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });
    }

    /**
     * 修改差评状态
     *
     * @param
     */
    private void doChangeCommentStatus(Map<String, String> params) {
        Call<ChangeStatusResult> call = getSpaService().changeCommentStatus(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_COMMENT_ID), params.get(RequestConstant.KEY_COMMENT_STATUS));
        call.enqueue(new TokenCheckedCallback<ChangeStatusResult>() {
            @Override
            protected void postResult(ChangeStatusResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });
    }

    /**
     * 所有技师差评列表
     *
     * @param
     */
    private void getTechBadCommentList(Map<String, String> params) {
        Call<TechBadCommentListResult> call = getSpaService().getBadCommentTechList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_SORT_TYPE), params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<TechBadCommentListResult>() {
            @Override
            protected void postResult(TechBadCommentListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                VisitListResult result = new VisitListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);

            }
        });
    }

    /**
     * 修改密码
     *
     * @param
     */
    private void doChangePassword(Map<String, String> params) {
        Call<ModifyPasswordResult> call = getSpaService().changePassword(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_OLD_PASSWORD), params.get(RequestConstant.KEY_NEW_PASSWORD));

        call.enqueue(new TokenCheckedCallback<ModifyPasswordResult>() {
            @Override
            protected void postResult(ModifyPasswordResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                ModifyPasswordResult result = new ModifyPasswordResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);

            }
        });
    }

    private void doGetCheckInfo(String number) {
        Call<CheckInfoList> call = getSpaService().getCheckInfoListByNumber(number);
        call.enqueue(new TokenCheckedCallback<CheckInfoList>() {
            @Override
            protected void postResult(CheckInfoList result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CheckInfoList result = new CheckInfoList();
                result.statusCode = 400;
                result.msg = errorMsg;
                result.respData = new ArrayList<>();
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 查询核销码类型
     *
     * @param code
     */
    private void getVerificationType(String code) {
        Call<CheckVerificationTypeResult> call = getSpaService().getCurrentVerificationType(SharedPreferenceHelper.getUserToken(), code);
        call.enqueue(new TokenCheckedCallback<CheckVerificationTypeResult>() {
            @Override
            protected void postResult(CheckVerificationTypeResult result) {
                CheckVerificationTypeResult mResult = new CheckVerificationTypeResult(result.statusCode, result.respData, code, result.msg);
                RxBus.getInstance().post(mResult);
            }
        });
    }

    /**
     * 付费预约订单
     *
     * @param code
     */
    private void getPayOrderDetail(String code) {
        Call<PayOrderDetailResult> call = getSpaService().getPayOrderDetail(SharedPreferenceHelper.getUserToken(), code);
        call.enqueue(new TokenCheckedCallback<PayOrderDetailResult>() {
            @Override
            protected void postResult(PayOrderDetailResult result) {
                RxBus.getInstance().post(result);
            }

        });
    }

    /**
     * 优惠券
     */
    private void getVerificationCouponDetail(String couponNo) {
        Call<VerificationCouponDetailResult> call = getSpaService().verificationCouponDetail(SharedPreferenceHelper.getUserToken(), couponNo);
        call.enqueue(new TokenCheckedCallback<VerificationCouponDetailResult>() {
            @Override
            protected void postResult(VerificationCouponDetailResult result) {
                super.postResult(result);
            }
        });
    }
//

    /**
     * 项目券
     */
    private void getVerificationServiceCouponDetail(String couponNo) {
        Call<VerificationServiceCouponResult> call = getSpaService().verificationServiceCouponDetail(SharedPreferenceHelper.getUserToken(), couponNo);
        call.enqueue(new TokenCheckedCallback<VerificationServiceCouponResult>() {
            @Override
            protected void postResult(VerificationServiceCouponResult result) {
                super.postResult(result);
            }
        });
    }

    /**
     * 奖品券
     */
    private void getVerificationAwardDetail(String verifyCode) {
        Call<AwardVerificationResult> call = getSpaService().verificationAwardDetail(SharedPreferenceHelper.getUserToken(), verifyCode);
        call.enqueue(new TokenCheckedCallback<AwardVerificationResult>() {
            @Override
            protected void postResult(AwardVerificationResult result) {
                super.postResult(result);
            }
        });
    }

    /**
     * 默认核销详情查询
     *
     * @param params
     */
    private void getDefaultVerificationDetail(Map<String, String> params) {
        Call<DefaultVerificationDetailResult> call = getSpaService().defaultVerificationDetail(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_VERIFICATION_CODE),
                params.get(RequestConstant.KEY_VERIFICATION_AMOUNT), params.get(RequestConstant.KEY_VERIFICATION_TYPE));
        call.enqueue(new TokenCheckedCallback<DefaultVerificationDetailResult>() {
            @Override
            protected void postResult(DefaultVerificationDetailResult result) {
                RxBus.getInstance().post(result);
            }

        });
    }

    /**
     * 核销付费预约
     *
     * @param params
     */
    private void doVerificationOrderSave(Map<String, String> params) {
        Call<VerificationSaveResult> call = getSpaService().verificationOrderSave(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_ORDER_NO),
                params.get(RequestConstant.KEY_PAY_ORDER_PROCESS_TYPE));
        call.enqueue(new TokenCheckedCallback<VerificationSaveResult>() {
            @Override
            protected void postResult(VerificationSaveResult result) {
                super.postResult(result);
                RxBus.getInstance().post(new OrderManageResult(true, "操作成功"));
            }

            @Override
            protected void postError(String errorMsg) {
                VerificationSaveResult result = new VerificationSaveResult(errorMsg);
                RxBus.getInstance().post(result);
                RxBus.getInstance().post(new OrderManageResult(true, "操作失败"));
            }
        });
    }

    /**
     * 核销默认核销
     *
     * @param params
     */
    private void doVerificationCommonSave(Map<String, String> params) {
        Call<VerificationSaveResult> call = getSpaService().verificationCommonSave(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_VERIFICATION_CODE),
                params.get(RequestConstant.KEY_VERIFICATION_AMOUNT),
                params.get(RequestConstant.KEY_VERIFICATION_TYPE));
        call.enqueue(new TokenCheckedCallback<VerificationSaveResult>() {
            @Override
            protected void postResult(VerificationSaveResult result) {
                if (Utils.isNotEmpty(params.get(RequestConstant.KEY_VERIFICATION_SOME))) {
                    VerificationSomeBean someBean = new VerificationSomeBean(params.get(RequestConstant.KEY_VERIFICATION_CODE), "核销成功", true);
                    RxBus.getInstance().post(someBean);
                } else {
                    RxBus.getInstance().post(result);
                }

            }

            @Override
            protected void postError(String errorMsg) {
                if (Utils.isNotEmpty(params.get(RequestConstant.KEY_VERIFICATION_SOME))) {
                    VerificationSomeBean someBean = new VerificationSomeBean(params.get(RequestConstant.KEY_VERIFICATION_CODE), errorMsg, false);
                    RxBus.getInstance().post(someBean);
                } else {
                    VerificationSaveResult result = new VerificationSaveResult(errorMsg);
                    RxBus.getInstance().post(result);
                }

            }
        });
    }

    /**
     * 优惠券核销
     */
    private void doVerificationCouponSave(String couponNo) {
        Call<VerificationSaveResult> call = getSpaService().verificationCouponSave(SharedPreferenceHelper.getUserToken(), couponNo);
        call.enqueue(new TokenCheckedCallback<VerificationSaveResult>() {
            @Override
            protected void postResult(VerificationSaveResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                VerificationSaveResult result = new VerificationSaveResult(errorMsg);
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 项目券核销
     */
    private void doVerificationServiceItemSave(String couponNo) {
        Call<VerificationSaveResult> call = getSpaService().verificationServiceItemCoupon(SharedPreferenceHelper.getUserToken(), couponNo);
        call.enqueue(new TokenCheckedCallback<VerificationSaveResult>() {
            @Override
            protected void postResult(VerificationSaveResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                VerificationSaveResult result = new VerificationSaveResult(errorMsg);
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 奖品核销
     */
    private void doVerificationSave(String verifyCode) {
        Call<VerificationSaveResult> call = getSpaService().verificationAwardSave(SharedPreferenceHelper.getUserToken(), verifyCode);
        call.enqueue(new TokenCheckedCallback<VerificationSaveResult>() {
            @Override
            protected void postResult(VerificationSaveResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                VerificationSaveResult result = new VerificationSaveResult(errorMsg);
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 用户分组列表查询
     *
     * @param
     */
    private void getClubGroupList() {
        Call<GroupListResult> call = getSpaService().clubGroupList(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<GroupListResult>() {
            @Override
            protected void postResult(GroupListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });
    }

    /**
     * 保存用户分组
     *
     * @param params
     */
    private void doGroupSave(Map<String, String> params) {
        Call<AddGroupResult> call = getSpaService().groupSave(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_GROUP_DESCRIPTION),
                params.get(RequestConstant.KEY_GROUP_ID), params.get(RequestConstant.KEY_GROUP_NAME), params.get(RequestConstant.KEY_GROUP_USER_ID));
        call.enqueue(new TokenCheckedCallback<AddGroupResult>() {
            @Override
            protected void postResult(AddGroupResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                AddGroupResult result = new AddGroupResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 分组新增用户列表查询
     *
     * @param params
     */

    private void getGroupUserList(Map<String, String> params) {
        Call<GroupUserListResult> call = getSpaService().groupUserList(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_GROUP_USER_NAME),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<GroupUserListResult>() {
            @Override
            protected void postResult(GroupUserListResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                GroupUserListResult result = new GroupUserListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 删除用户分组
     *
     * @param params
     */
    private void doGroupDelete(String params) {
        Call<DeleteGroupResult> call = getSpaService().groupDelete(SharedPreferenceHelper.getUserToken(), params);
        call.enqueue(new TokenCheckedCallback<DeleteGroupResult>() {
            @Override
            protected void postResult(DeleteGroupResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                DeleteGroupResult result = new DeleteGroupResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 用户分组详情明细
     *
     * @param params
     */

    private void getGroupDetails(String params) {
        Call<UserGroupDetailListResult> call = getSpaService().groupDetails(SharedPreferenceHelper.getUserToken(), params);
        call.enqueue(new TokenCheckedCallback<UserGroupDetailListResult>() {
            @Override
            protected void postResult(UserGroupDetailListResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                UserGroupDetailListResult result = new UserGroupDetailListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 用户设置分组保存
     *
     * @param
     */

    private void doUserAddGroup(Map<String, String> params) {
        Call<UserGroupSaveResult> call = getSpaService().userAddGroup(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_GROUP_ID), params.get(RequestConstant.KEY_USER_ID));
        call.enqueue(new TokenCheckedCallback<UserGroupSaveResult>() {
            @Override
            protected void postResult(UserGroupSaveResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                UserGroupSaveResult result = new UserGroupSaveResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * 保存设置分组页面
     *
     * @param params
     */
    private void doUserEditGroup(String params) {
        Call<UserEditGroupResult> call = getSpaService().userEditGroup(SharedPreferenceHelper.getUserToken(), params);
        call.enqueue(new TokenCheckedCallback<UserEditGroupResult>() {
            @Override
            protected void postResult(UserEditGroupResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                UserEditGroupResult result = new UserEditGroupResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 群发消息上传图片
     *
     * @param imgFile
     */
    private void doGroupMessageAlbumUpload(String imgFile) {
        Call<AlbumUploadResult> call = getSpaService().groupMessageAlbumUpload(SharedPreferenceHelper.getUserToken(), imgFile);
        call.enqueue(new TokenCheckedCallback<AlbumUploadResult>() {
            @Override
            protected void postResult(AlbumUploadResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                AlbumUploadResult result = new AlbumUploadResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 发送优惠券，自动领取
     *
     * @param params
     */
    private void getClubUserCoupon(Map<String, Object> params) {
        Call<UserGetCouponResult> call = getSpaService().clubUserCoupon(SharedPreferenceHelper.getUserToken(), (String) params.get(RequestConstant.KEY_USER_COUPON_ACT_ID),
                (String) params.get(RequestConstant.KEY_USER_COUPON_CHANEL), (String) params.get(RequestConstant.KEY_USER_COUPON_EMCHAT_ID));
        call.enqueue(new TokenCheckedCallback<UserGetCouponResult>() {
            @Override
            protected void postResult(UserGetCouponResult result) {
                UserGetCouponResult resultDate = new UserGetCouponResult((CouponInfo) params.get(RequestConstant.KEY_USER_COUPON_INFO), result.respData);
                RxBus.getInstance().post(resultDate);
            }

            @Override
            protected void postError(String errorMsg) {
                UserGetCouponResult result = new UserGetCouponResult((CouponInfo) params.get(RequestConstant.KEY_USER_COUPON_INFO), null);
                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * 核销详情筛选类型
     */
    public void getCheckInfoTypeList() {
        Call<RecordTypeListResult> call = getSpaService().checkInfoTypeList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<RecordTypeListResult>() {
            @Override
            protected void postResult(RecordTypeListResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                RecordTypeListResult result = new RecordTypeListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 核销列表
     */
    private void getCheckInfoRecordList(Map<String, String> params) {
        Call<VerificationRecordListResult> call = getSpaService().checkInfoRecordList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_VERIFICATION_TYPE), params.get(RequestConstant.KEY_SEARCH_TELEPHONE), params.get(RequestConstant.KEY_IS_TIME),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE), params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));

        call.enqueue(new TokenCheckedCallback<VerificationRecordListResult>() {
            @Override
            protected void postResult(VerificationRecordListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                VerificationRecordListResult result = new VerificationRecordListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 核销详情
     */
    private void getCheckInfoRecordDetail(String recordId) {
        Call<VerificationRecordDetailResult> call = getSpaService().checkInfoRecordDetail(SharedPreferenceHelper.getUserToken(), recordId);
        call.enqueue(new TokenCheckedCallback<VerificationRecordDetailResult>() {
            @Override
            protected void postResult(VerificationRecordDetailResult result) {
                super.postResult(result);
            }

            @Override
            protected void postError(String errorMsg) {
                VerificationRecordDetailResult result = new VerificationRecordDetailResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 新版客户评论列表
     *
     * @param params
     */
    private void getAppCommentList(Map<String, String> params) {
        Call<AppCommentListResult> call = getSpaService().appCommentList(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_USER_ID), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<AppCommentListResult>() {
        });
    }

    /**
     * 首页技师排行榜
     *
     * @param
     */
    private void getTechPKRanking() {
        Call<TechPKRankingResult> call = getSpaService().techPKRanking(SharedPreferenceHelper.getUserToken(), RequestConstant.USER_TYPE_MANAGER);
        call.enqueue(new TokenCheckedCallback<TechPKRankingResult>() {
            @Override
            protected void postResult(TechPKRankingResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * pk活动列表
     *
     * @param params
     */
    private void getTechPKActivityList(Map<String, String> params) {
        Call<PKActivityListResult> call = getSpaService().pkActivityList(SharedPreferenceHelper.getUserToken(), RequestConstant.USER_TYPE_MANAGER, params.get(RequestConstant.KEY_PAGE),
                params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<PKActivityListResult>() {
            @Override
            protected void postResult(PKActivityListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * pk队伍排行
     *
     * @param params
     */
    private void getTechPkTeamList(Map<String, String> params) {
        Call<PKTeamListResult> call = getSpaService().techPkTeamList(SharedPreferenceHelper.getUserToken(), RequestConstant.USER_TYPE_MANAGER, params.get(RequestConstant.KEY_PK_ACTIVITY_ID), params.get(RequestConstant.KEY_SORT_KEY),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<PKTeamListResult>() {
            @Override
            protected void postResult(PKTeamListResult result) {
                result.sortType = params.get(RequestConstant.KEY_SORT_KEY);
                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * pk个人排行
     *
     * @param params
     */
    private void getTechPKPersonalList(Map<String, String> params) {
        Call<PKPersonalListResult> call = getSpaService().techPkPersonalList(SharedPreferenceHelper.getUserToken(), RequestConstant.USER_TYPE_MANAGER, params.get(RequestConstant.KEY_PK_ACTIVITY_ID), params.get(RequestConstant.KEY_SORT_KEY),
                params.get(RequestConstant.KEY_TEAM_ID), params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<PKPersonalListResult>() {
            @Override
            protected void postResult(PKPersonalListResult result) {
                result.type = params.get(RequestConstant.KEY_SORT_KEY);
                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * 技师个人排行
     *
     * @param params
     */
    private void getTechPersonalList(Map<String, String> params) {
        Call<TechRankingListResult> call = getSpaService().techPersonalRankingList(SharedPreferenceHelper.getUserToken(), RequestConstant.USER_TYPE_MANAGER, params.get(RequestConstant.KEY_TECH_RANKING_SOR_TYPE),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<TechRankingListResult>() {
            @Override
            protected void postResult(TechRankingListResult result) {
                result.sortType = params.get(RequestConstant.KEY_TECH_RANKING_SOR_TYPE);
                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * 首页Wifi宣传
     *
     * @param
     */
    private void getStatisticsWifiData(Map<String, String> params) {
        Call<PropagandaDataResult> call = getSpaService().statisticsWifiData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_DATE));
        call.enqueue(new TokenCheckedCallback<PropagandaDataResult>() {
            @Override
            protected void postResult(PropagandaDataResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 首页Wifi宣传
     *
     * @param
     */
    private void getAccountData(Map<String, String> params) {
        Call<AccountDataResult> call = getSpaService().accountData(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_DATE));
        call.enqueue(new TokenCheckedCallback<AccountDataResult>() {
            @Override
            protected void postResult(AccountDataResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 在线买单列表
     *
     * @param params
     */
    private void getFastPayOrderList(Map<String, String> params) {
        Call<OnlinePayListResult> call = getSpaService().fastPayOrderList(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_STATUS), params.get(RequestConstant.KEY_ONLINE_PAY_TECH_NAME), params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<OnlinePayListResult>() {
            @Override
            protected void postResult(OnlinePayListResult result) {
                result.isSearch = params.get(RequestConstant.KEY_IS_SEARCH);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 营销统计
     *
     * @param params
     */
    private void getSaleDataStatistics(Map<String, String> params) {
        Call<MarketingIncomeListResult> call = getSpaService().saleDataStatistics(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<MarketingIncomeListResult>() {
            @Override
            protected void postResult(MarketingIncomeListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                super.postError(errorMsg);
            }
        });

    }

    /**
     * 环信消息发送后回调
     *
     * @param params
     */
    private void doSaveToContact(Map<String, String> params) {
        Call<SaveChatUserResult> call = getSpaService().doSaveContact(SharedPreferenceHelper.getEmchatId(),
                RequestConstant.USER_TYPE_MANAGER, params.get(RequestConstant.KEY_FRIEND_CHAT_ID), RequestConstant.USER_TYPE_USER, params.get(RequestConstant.KEY_CHAT_MSG_ID), RequestConstant.KEY_MSG_TYPE_TEXT);
        call.enqueue(new TokenCheckedCallback<SaveChatUserResult>() {
            @Override
            protected void postResult(SaveChatUserResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //订单数据统计
    private void getSummaryOrderData(Map<String, String> params) {
        Call<OrderSummaryResult> call = getSpaService().getOrderSummaryData(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));
        call.enqueue(new TokenCheckedCallback<OrderSummaryResult>() {
            @Override
            protected void postResult(OrderSummaryResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //数据统计技师排行
    private void getStaffDataTechOrderRank(Map<String, String> params) {
        Call<StaffDataResult> call = getSpaService().getStaffOrderRankData(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));
        call.enqueue(new TokenCheckedCallback<StaffDataResult>() {
            @Override
            protected void postResult(StaffDataResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //订单过滤技师列表
    private void getOrderTechnicianList() {
        Call<OrderTechListResult> call = getSpaService().getSelectTechnicianList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<OrderTechListResult>() {
            @Override
            protected void postResult(OrderTechListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //订单过滤项目列表
    private void getOrderProjectList() {
        Call<OrderProjectListResult> call = getSpaService().getSelectProjectList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<OrderProjectListResult>() {
            @Override
            protected void postResult(OrderProjectListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }
    //

    private void getOrderListFilter(Map<String, String> params) {
        Call<OrderListFilterResult> call = getSpaService().orderListFilter(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE), params.get(RequestConstant.KEY_ORDER_FILTER_START_DATE),
                params.get(RequestConstant.KEY_ORDER_FILTER_END_DATE), params.get(RequestConstant.KEY_ORDER_FILTER_STATUS), params.get(RequestConstant.KEY_ORDER_FILTER_ITEM_ID),
                params.get(RequestConstant.KEY_ORDER_FILTER_TECH_ID), params.get(RequestConstant.KEY_ORDER_FILTER_TELEPHONE));

        call.enqueue(new TokenCheckedCallback<OrderListFilterResult>() {
            @Override
            protected void postResult(OrderListFilterResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {

            }
        });
    }

    //新报表提示
    private void getFinancialReportNews() {
        Call<ReportNewsResult> call = getSpaService().getReportNews(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<ReportNewsResult>() {
            @Override
            protected void postResult(ReportNewsResult result) {
                super.postResult(result);
            }
        });
    }

    //报表列表
    private void getFinancialReportList(Map<String, String> params) {
        Call<FinancialReportResult> call = getSpaService().getFinancialReportList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_DATE), params.get(RequestConstant.KEY_TYPE));
        call.enqueue(new TokenCheckedCallback<FinancialReportResult>() {
            @Override
            protected void postResult(FinancialReportResult result) {
                result.dateType = params.get(RequestConstant.KEY_TYPE);
                RxBus.getInstance().post(result);
            }
        });

    }

    //报表标记为已读
    private void makeReportRead(String reportId) {
        Call<ReportReadResult> call = getSpaService().makeReportRead(reportId);
        call.enqueue(new TokenCheckedCallback<ReportReadResult>() {
            @Override
            protected void postResult(ReportReadResult result) {
                super.postResult(result);
            }
        });
    }

    //删除报表
    private void deleteFinancialReportById(String reportId) {
        Call<ReportDeleteResult> call = getSpaService().deleteReport(SharedPreferenceHelper.getUserToken(), reportId);
        call.enqueue(new TokenCheckedCallback<ReportDeleteResult>() {
            @Override
            protected void postResult(ReportDeleteResult result) {
                super.postResult(result);
            }
        });

    }

    //设置报表 未确定
    private void setFinancialReportConfig(Map<String, String> params) {
        Call<ReportSettingResult> call = getSpaService().settingReport(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_TIME));
        call.enqueue(new TokenCheckedCallback<ReportSettingResult>() {
            @Override
            protected void postResult(ReportSettingResult result) {
                super.postResult(result);
            }
        });

    }

    //获取报表时间
    private void getFinancialReportConfig() {
        Call<ReportSettingResult> call = getSpaService().getReportSetting(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<ReportSettingResult>() {
            @Override
            protected void postResult(ReportSettingResult result) {
                super.postResult(result);
            }
        });
    }

    //创建自定义报表
    private void createFinancialReport(Map<String, String> params) {
        Call<ReportCreateResult> call = getSpaService().createReport(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_REPORT_CUSTOM_START_TIME),
                params.get(RequestConstant.KEY_REPORT_CUSTOM_END_TIME), params.get(RequestConstant.KEY_REPORT_CUSTOM_NAME));
        call.enqueue(new TokenCheckedCallback<ReportCreateResult>() {
            @Override
            protected void postResult(ReportCreateResult result) {
                super.postResult(result);
            }
        });

    }

    //优惠券数据统计总数
    private void getCouponOperateTotal(Map<String, String> params) {
        Call<CouponOperateDataResult> call = getSpaService().getOperateDataTotal(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_COUPON_ID), params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));
        call.enqueue(new TokenCheckedCallback<CouponOperateDataResult>() {
            @Override
            protected void postResult(CouponOperateDataResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //优惠券数据统计
    private void getCouponOperateData(Map<String, String> params) {
        Call<CouponOperateDataListResult> call = getSpaService().getOperateDataList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE), params.get(RequestConstant.KEY_COUPON_ID), params.get(RequestConstant.KEY_START_DATE),
                params.get(RequestConstant.KEY_END_DATE));
        call.enqueue(new TokenCheckedCallback<CouponOperateDataListResult>() {
            @Override
            protected void postResult(CouponOperateDataListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }
    //优惠券记录

    private void getCouponRecordData(Map<String, String> params) {
        Call<CouponRecordResult> call = getSpaService().getCouponRecordData(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_COUPON_START_TIME), params.get(RequestConstant.KEY_COUPON_END_TIME), params.get(RequestConstant.KEY_COUPON_ID), params.get(RequestConstant.KEY_COUPON_PHONE_NUM_OR_COUPON_NO),
                params.get(RequestConstant.KEY_COUPON_STATUS), params.get(RequestConstant.KEY_COUPON_TIME_TYPE));
        call.enqueue(new TokenCheckedCallback<CouponRecordResult>() {
            @Override
            protected void postResult(CouponRecordResult result) {
                if (TextUtils.isEmpty(params.get(RequestConstant.KEY_COUPON_SEARCH_MARK))) {
                    result.isSearch = false;
                } else {
                    result.isSearch = true;
                }
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CouponRecordResult recordResult = new CouponRecordResult();
                recordResult.msg = errorMsg;
                RxBus.getInstance().post(recordResult);
            }
        });
    }

    //
    private void getCouponListData(Map<String, String> params) {
        Call<CouponListResult> call = getSpaService().getCouponListData(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                params.get(RequestConstant.KEY_COUPON_TYPE), params.get(RequestConstant.KEY_COUPON_ONLINE));
        call.enqueue(new TokenCheckedCallback<CouponListResult>() {
            @Override
            protected void postResult(CouponListResult result) {
                result.onLineType = params.get(RequestConstant.KEY_COUPON_LIST_TYPE);
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取升级配置
    private void doGetAppUpdateConfig(Map<String, String> params) {
        String version = params.get(RequestConstant.KEY_UPDATE_VERSION);
        String userId = params.get(RequestConstant.KEY_UPDATE_USER_ID);
        String appId = params.get(RequestConstant.KEY_UPDATE_APP_ID);
        final Call<AppUpdateConfigResult> call =
                RetrofitServiceFactory.getAppUpdateService().getAppUpdateConfig(appId, userId, version);
        call.enqueue(new Callback<AppUpdateConfigResult>() {
            @Override
            public void onResponse(Call<AppUpdateConfigResult> call, Response<AppUpdateConfigResult> response) {
                AppUpdateConfigResult result = response.body();
                if (result != null) {
                    RxBus.getInstance().post(result);
                } else {
                    try {
                        //RxBus.getInstance().post(new Throwable(response.errorBody().string()));
                        Logger.e("get app update config failed:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AppUpdateConfigResult> call, Throwable t) {
                RxBus.getInstance().post(t);
                Logger.e("get app update config failed:" + t.getMessage());
            }
        });
    }

    //会所标签
    private void getGroupTagList() {
        Call<GroupTagListResult> call = getSpaService().getGroupTagList(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<GroupTagListResult>() {
            @Override
            protected void postResult(GroupTagListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                GroupTagListResult result = new GroupTagListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }


    /**
     * @FormUrlEncoded
     * @POST(RequestConstant.URL_COMMENT_LIST) Observable<CommentListResult> getCommentList(@Field("page") String page,
     * @Field("pageSize") String pageSize,
     * @Field("startDate") String startDate,
     * @Field("endDate") String endDate,
     * @Field("techId") String techId,
     * @Field("type") String type,
     * @Field("userName") String userName,
     * @Field("returnStatus") String returnStatus,
     * @Field("status") String status,
     * @Field("commentType") String commentType);
     */
    private void getBadCommentAndComplaint() {
        Call<CommentAndComplaintListResult> call = getSpaService().getCommentAndComplaintList(SharedPreferenceHelper.getUserToken(), "1", "2", "2015-01-01", DateUtil.getCurrentDate(),
                "", "1", "", "N", "valid", "");

        call.enqueue(new TokenCheckedCallback<CommentAndComplaintListResult>() {
            @Override
            protected void postResult(CommentAndComplaintListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
//                VisitListResult result = new VisitListResult();
//                result.msg = errorMsg;
//                RxBus.getInstance().post(result);

            }
        });
    }


    //*********************************************技师工资报表***************************************
    //获取指定时间段技师工资汇总列表
    private void getCommissionSumList(Map<String, String> params) {
        Call<CommissionNormalListResult> call = getSpaService().getCommissionSumList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_TYPE));
        call.enqueue(new TokenCheckedCallback<CommissionNormalListResult>() {
            @Override
            protected void postResult(CommissionNormalListResult result) {
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CommissionNormalListResult result = new CommissionNormalListResult();
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取指定时间段会所提成汇总金额
    private void getCommissionSumAmount(Map<String, String> params) {
        Call<CommissionAmountResult> call = getSpaService().getCommissionSumAmount(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));
        call.enqueue(new TokenCheckedCallback<CommissionAmountResult>() {
            @Override
            protected void postResult(CommissionAmountResult result) {
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CommissionAmountResult result = new CommissionAmountResult();
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取具体某天会所所有技师提成列表
    private void getCommissionAmountList(Map<String, String> params) {
        Call<CommissionAmountListResult> call = getSpaService().getCommissionAmountList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));
        call.enqueue(new TokenCheckedCallback<CommissionAmountListResult>() {
            @Override
            protected void postResult(CommissionAmountListResult result) {
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CommissionAmountListResult result = new CommissionAmountListResult();
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取具体某天某技师提成汇总金额
    private void getTechCommissionAmount(Map<String, String> params) {
        Call<CommissionAmountResult> call = getSpaService().getTechCommissionAmount(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_TECH_ID));
        call.enqueue(new TokenCheckedCallback<CommissionAmountResult>() {
            @Override
            protected void postResult(CommissionAmountResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CommissionAmountResult result = new CommissionAmountResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取具体某天某技师提成明细列表
    private void getTechCommissionDetailList(Map<String, String> params) {
        Call<TechCommissionListResult> call = getSpaService().getTechCommissionDetailList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_TECH_ID),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE),
                params.get(RequestConstant.KEY_TYPE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<TechCommissionListResult>() {
            @Override
            protected void postResult(TechCommissionListResult result) {
                result.requestType = params.get(RequestConstant.KEY_REQUEST_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                TechCommissionListResult result = new TechCommissionListResult();
                result.requestType = params.get(RequestConstant.KEY_REQUEST_TYPE);
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取提成明细的具体详情
    private void getTechCommissionDetailInfo(String id) {
        Call<TechCommissionDetailResult> call = getSpaService().getTechCommissionDetailInfo(SharedPreferenceHelper.getUserToken(), id);
        call.enqueue(new TokenCheckedCallback<TechCommissionDetailResult>() {
            @Override
            protected void postResult(TechCommissionDetailResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                TechCommissionDetailResult result = new TechCommissionDetailResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    // 获取会所技师列表
    private void getTechBaseList() {
        Call<TechBaseListResult> call = getSpaService().getTechList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<TechBaseListResult>() {
            @Override
            protected void postResult(TechBaseListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                TechBaseListResult result = new TechBaseListResult();
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    //********************************************* 买单收银报表***************************************
    private void getCashierStatisticInfo(Map<String, String> params) {
        Call<CashierStatisticResult> call = getSpaService().getCashierStatisticInfo(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE));
        call.enqueue(new TokenCheckedCallback<CashierStatisticResult>() {
            @Override
            protected void postResult(CashierStatisticResult result) {
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CashierStatisticResult result = new CashierStatisticResult();
                result.msg = errorMsg;
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getCashierClubDetailList(Map<String, String> params) {
        Call<CashierClubDetailListResult> call = getSpaService().getCashierClubDetailList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE), params.get(RequestConstant.KEY_SCOPE));
        call.enqueue(new TokenCheckedCallback<CashierClubDetailListResult>() {
            @Override
            protected void postResult(CashierClubDetailListResult result) {
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                result.requestType = params.get(RequestConstant.KEY_REQUEST_TYPE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                CashierClubDetailListResult result = new CashierClubDetailListResult();
                result.msg = errorMsg;
                result.eventType = params.get(RequestConstant.KEY_EVENT_TYPE);
                result.requestType = params.get(RequestConstant.KEY_REQUEST_TYPE);
                RxBus.getInstance().post(result);
            }
        });
    }

}
