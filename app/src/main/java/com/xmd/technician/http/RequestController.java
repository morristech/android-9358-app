package com.xmd.technician.http;

import android.os.Message;
import android.text.TextUtils;

import com.hyphenate.chat.EMMessage;
import com.xmd.technician.AppConfig;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.AddOrEditResult;
import com.xmd.technician.bean.ClubContactResult;
import com.xmd.technician.bean.CreditAccountDetailResult;
import com.xmd.technician.bean.CreditAccountResult;
import com.xmd.technician.bean.CreditApplicationsResult;
import com.xmd.technician.bean.CreditExchangeResult;
import com.xmd.technician.bean.CreditStatusResult;
import com.xmd.technician.bean.CurrentSelectPage;
import com.xmd.technician.bean.CustomerDetailResult;
import com.xmd.technician.bean.CustomerListResult;
import com.xmd.technician.bean.DeleteContactResult;
import com.xmd.technician.bean.GameResult;
import com.xmd.technician.bean.GiftListResult;
import com.xmd.technician.bean.ManagerDetailResult;
import com.xmd.technician.bean.MarkResult;
import com.xmd.technician.bean.RecentlyVisitorResult;
import com.xmd.technician.bean.SaveChatUserResult;
import com.xmd.technician.bean.SayHiResult;
import com.xmd.technician.bean.SendGameResult;
import com.xmd.technician.bean.TechDetailResult;
import com.xmd.technician.bean.UserGetCouponResult;
import com.xmd.technician.bean.UserSwitchesResult;
import com.xmd.technician.bean.VisitBean;
import com.xmd.technician.common.DESede;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.gson.AccountMoneyResult;
import com.xmd.technician.http.gson.ActivityListResult;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AppUpdateConfigResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.CardShareListResult;
import com.xmd.technician.http.gson.CommentResult;
import com.xmd.technician.http.gson.ConsumeDetailResult;
import com.xmd.technician.http.gson.CouponInfoResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.http.gson.DynamicListResult;
import com.xmd.technician.http.gson.FeedbackResult;
import com.xmd.technician.http.gson.JoinClubResult;
import com.xmd.technician.http.gson.JournalListResult;
import com.xmd.technician.http.gson.LimitGrabResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.LogoutResult;
import com.xmd.technician.http.gson.ModifyPasswordResult;
import com.xmd.technician.http.gson.OnceCardResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.PaidCouponUserDetailResult;
import com.xmd.technician.http.gson.PayForMeListResult;
import com.xmd.technician.http.gson.PropagandaListResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.http.gson.ResetPasswordResult;
import com.xmd.technician.http.gson.RewardListResult;
import com.xmd.technician.http.gson.ServiceResult;
import com.xmd.technician.http.gson.ShareCouponResult;
import com.xmd.technician.http.gson.TechCurrentResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.TechInfoResult;
import com.xmd.technician.http.gson.TechPersonalDataResult;
import com.xmd.technician.http.gson.TechRankDataResult;
import com.xmd.technician.http.gson.TechStatisticsDataResult;
import com.xmd.technician.http.gson.TokenExpiredResult;
import com.xmd.technician.http.gson.UnusedTechNoListResult;
import com.xmd.technician.http.gson.UpdateServiceResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.http.gson.UpdateWorkStatusResult;
import com.xmd.technician.http.gson.UpdateWorkTimeResult;
import com.xmd.technician.http.gson.WorkTimeResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.RxBus;

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

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MsgDef.MSG_DEF_LOGIN:
                doLogin((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_LOGIN_BY_TECH_NO:
                doLoginByTechNo((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_UNUSED_TECH_NO:
                doGetUnusedTechNo((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_LOGOUT:
                doLogout((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_MODIFY_PASSWORD:
                modifyPassWord((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_RESET_PASSWORD:
                resetPassWord((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_MANAGE_ORDER:
                doManageOrder((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_HIDE_ORDER:
                hideOrder(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_COMMENT_LIST:
                getCommentList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID:
                doBindGetuiClientId();
                break;
            case MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID:
                doUnbindGetuiClientId((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_ICODE:
                getICode((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_JOIN_CLUB:
                joinClub((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TOKEN_EXPIRE:
                doHandleTokenExpired(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_TECH_EDIT_INFO:
                getTechEditInfo();
                break;
            case MsgDef.MSG_DEF_GET_TECH_CURRENT_INFO:
                getTechCurrentInfo();
                break;
            case MsgDef.MSG_DEF_UPDATE_TECH_INFO:
                updateTechInfo((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_REGISTER:
                register((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_WORK_TIME:
                getWorkTime();
                break;
            case MsgDef.MSG_DEF_UPDATE_WORK_TIME:
                updateWorkTime((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_UPLOAD_AVATAR:
                uploadAvatar((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DELETE_ALBUM:
                deleteAlbum((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_UPLOAD_ALBUM:
                uploadAlbum((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SORT_ALBUM:
                sortAlbum((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SUBMIT_FEEDBACK:
                doSubmitFeedback((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_UPDATE_WORK_STATUS:
                updateWorkStatus((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST:
                getServiceList();
                break;
            case MsgDef.MSG_DEF_UPDATE_SERVICE_ITEM_LIST:
                updateServiceList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_TECH_PERSONAL_DATA:
                getTechPersonalData();
                break;
            case MsgDef.MSG_DEF_GET_ACCOUNT_MONEY:
                getAccountMoney();
                break;
            case MsgDef.MSG_DEF_GET_CONSUME_DETAIL:
                getConsumeDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_COUPON_LIST:
                getCouponList();
                break;
            case MsgDef.MSG_DEF_GET_COUPON_INFO:
                doGetCouponInfo(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_PAID_COUPON_USER_DETAIL:
                doGetPaidCouponUserDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_COUPON_SHARE_EVENT_COUNT:
                doCouponShareEventCount(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_QUIT_CLUB:
                quitClub((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_APP_UPDATE_CONFIG:
                doGetAppUpdateConfig((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CUSTOMER_LIST:
                doGetCustomerList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CUSTOMER_INFO_DETAIL:
                doGetCustomerInfoDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_TECH_INFO_DETAIL:
                doGetTechInfoDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_MANAGER_INFO_DETAIL:
                doGetManagerInfoDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_ADD_OR_EDIT_CUSTOMER:
                doAddOrEditCustomer((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CLUB_LIST:
                doGetClubList();
                break;
            case MsgDef.MSG_DEF_DELETE_CONTACT:
                doDeleteContact((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_USER_RECORDE:
                doGetUserRecords((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_SWITCH_STATUS:
                doGetUserCreditSwitchStatus();
                break;
            case MsgDef.MSG_DEF_GET_CREDIT_ACCOUNT:
                doGetUserCreditAccount();
                break;
            case MsgDef.MSG_DEF_DO_CREDIT_EXCHANGE:
                doExchangeCredit((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CREDIT_APPLICATIONS:
                getExchangeApplications((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CONTACT_MARK:
                doGetContactMark();
                break;
            case MsgDef.MSG_DEF_DO_INITIATE_GAME:
                doDiceGameSubmit((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DO_GAME_ACCEPT_OR_REJECT:
                doDiceGameAcceptOrReject((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_USER_CLUB_SWITCHES:
                doGetClubUserSwitches();
                break;
            case MsgDef.MSG_DEF_GET_RECENTLY_VISITOR:
                doGetRecentlyVisitorList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CREDIT_GIFT_LIST:
                doGetCreditGiftList();
                break;
            case MsgDef.MSG_DEF_DO_SAY_HI:
                doSayHi((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_VISIT_VIEW:
                doGetVisitView((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SAVE_CHAT_TO_CHONTACT:
                doSaveToContact((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSF_DEF_GET_TECH_INFO:
                getTechInfo();
                break;
            case MsgDef.MSF_DEF_GET_TECH_ORDER_LIST:
                getMainPageOrderList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSF_DEF_GET_TECH_STATISTICS_DATA:
                getTechStatisticsData();
                break;
            case MsgDef.MSF_DEF_GET_TECH_RANK_INDEX_DATA:
                getTechRankData();
                break;
            case MsgDef.MSF_DEF_GET_TECH_DYNAMIC_LIST:
                getDynamicList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSF_DEF_SET_PAGE_SELECTED:
                setPageSelected((int) msg.obj);
                break;
            case MsgDef.MSG_DEF_ORDER_INNER_READ:
                setOrderInnerRead((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_USER_GET_COUPON:
                getClubUserCoupon((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_PAY_NOTIFY:
                getPayNotify((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CARD_SHARE_LIST:
                getCardShareList();
                break;
            case MsgDef.MSG_DEF_GET_ACTIVITY_LIST:
                getActivityList();
                break;
            case MsgDef.MSG_DEF_GET_PROPAGANDA_LIST:
                getPropagandaList();
                break;
            case MsgDef.MSG_DEF_GET_ONCE_CARD_LIST_DETAIL:
                getOnceCardListDetail();
                break;
            case MsgDef.MSG_DEF_GET_CARD_LIST_DETAIL:
                getCardListDetail(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST_DETAIL:
                getServiceItemListDetail();
                break;
            case MsgDef.MSG_DEF_GET_REWARD_ACTIVITY_LIST:
                getRewardActivityList();
                break;
            case MsgDef.MSG_DEF_GET_CLUB_JOURNAL_LIST:
                getClubJournalList();
                break;
            case MsgDef.MSG_DEF_GET_PAY_FOR_ME_LIST_DETAIL:
                getPayForMeListDetail();
                break;
        }

        return true;
    }

    /**************************************** Called By Activities  *****************************/

    /**
     * Login Button Clicked in LoginActivity
     */
    private void doLogin(Map<String, String> params) {
        Call<LoginResult> call = getSpaService().login(
                params.get(RequestConstant.KEY_USERNAME),
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

    //使用技师编号登录
    private void doLoginByTechNo(Map<String, String> params) {
        Call<LoginResult> call = getSpaService().loginByTechNo(
                params.get(RequestConstant.KEY_CLUB_CODE),
                params.get(RequestConstant.KEY_TECH_No),
                params.get(RequestConstant.KEY_PASSWORD),
                params.get(RequestConstant.KEY_APP_VERSION),
                RequestConstant.DEFAULT_LOGIN_CHANNEL + AppConfig.getAppVersionCode());

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
                LoginResult result = new LoginResult();
                result.statusCode = 400;
                result.msg = t.getMessage();
                RxBus.getInstance().post(result);
            }
        });
    }

    //使用技师编号登录
    private void doGetUnusedTechNo(Map<String, String> params) {
        Call<UnusedTechNoListResult> call = getSpaService().getUnusedTechNoList(
                params.get(RequestConstant.KEY_TOKEN),
                params.get(RequestConstant.KEY_CLUB_CODE));

        call.enqueue(new Callback<UnusedTechNoListResult>() {
            @Override
            public void onResponse(Call<UnusedTechNoListResult> call, Response<UnusedTechNoListResult> response) {
                UnusedTechNoListResult result = response.body();
                if (result != null) {
                    RxBus.getInstance().post(result);
                } else {
                    try {
                        result = new UnusedTechNoListResult();
                        result.statusCode = 400;
                        result.msg = response.errorBody().string();
                        RxBus.getInstance().post(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UnusedTechNoListResult> call, Throwable t) {
                UnusedTechNoListResult result = new UnusedTechNoListResult();
                result.statusCode = 400;
                result.msg = t.getMessage();
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * Logout Button Clicked in PopupMoreWindow
     */
    private void doLogout(Map<String, String> params) {

        Call<LogoutResult> call = getSpaService().logout(params.get(RequestConstant.KEY_TOKEN),
                RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<LogoutResult>() {
            @Override
            protected void postResult(LogoutResult result) {
                RxBus.getInstance().post(result == null ? new LogoutResult() : result);
            }

            @Override
            protected void postError(String errorMsg) {
                RxBus.getInstance().post(new LogoutResult());
            }
        });
    }

    //不能正常工作me
    private void register(Map<String, String> params) {
        Call<RegisterResult> call = getSpaService().register(
                params.get(RequestConstant.KEY_MOBILE),
                params.get(RequestConstant.KEY_PASSWORD),
                params.get(RequestConstant.KEY_ICODE),
                params.get(RequestConstant.KEY_CLUB_CODE),
                params.get(RequestConstant.KEY_LOGIN_CHANEL),
                RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_SPARE_TECH_ID),
                RequestConstant.SESSION_TYPE);

        call.enqueue(new Callback<RegisterResult>() {
            @Override
            public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                RegisterResult result = response.body();
                if (result != null) {
                    RxBus.getInstance().post(result);
                } else {
                    try {
                        result = new RegisterResult();
                        result.statusCode = response.code();
                        result.msg = response.errorBody().string();
                        RxBus.getInstance().post(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResult> call, Throwable t) {
                RxBus.getInstance().post(t);
            }
        });
    }

    private void modifyPassWord(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().modifyPassword(params.get(RequestConstant.KEY_OLD_PASSWORD),
                params.get(RequestConstant.KEY_NEW_PASSWORD),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new ModifyPasswordResult());
            }
        });
    }

    //不能正常工作
    private void resetPassWord(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().resetPassword(params.get(RequestConstant.KEY_USERNAME),
                params.get(RequestConstant.KEY_PASSWORD),
                params.get(RequestConstant.KEY_ICODE),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);


        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                BaseResult result = response.body();
                if (response.code() == 204 || (result != null && result.statusCode == 200)) {
                    RxBus.getInstance().post(new ResetPasswordResult());
                } else {
                    try {
                        RxBus.getInstance().post(new Throwable(response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                RxBus.getInstance().post(t);
            }
        });
    }

    private void getCommentList(Map<String, String> params) {
        Call<CommentResult> call;
        if (params != null) {
            call = getSpaService().getCommentList(params.get(RequestConstant.KEY_PAGE),
                    params.get(RequestConstant.KEY_PAGE_SIZE),
                    params.get(RequestConstant.KEY_SORT_TYPE),
                    RequestConstant.SESSION_TYPE,
                    LoginTechnician.getInstance().getToken());
        } else {
            call = getSpaService().getCommentList(null, null, null,
                    RequestConstant.SESSION_TYPE,
                    LoginTechnician.getInstance().getToken());
        }

        call.enqueue(new TokenCheckedCallback<CommentResult>() {
            @Override
            protected void postResult(CommentResult result) {
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
        Call<BaseResult> call = getSpaService().manageOrder(LoginTechnician.getInstance().getToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_PROCESS_TYPE),
                params.get(RequestConstant.KEY_ID), params.get(RequestConstant.KEY_REASON));

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new OrderManageResult(params.get(RequestConstant.KEY_ID)));
            }

            /*@Override
            protected void postError(String errorMsg) {
                Logger.v("manageOrder: " + errorMsg);
            }*/
        });

    }

    private void hideOrder(String orderId) {
        Call<BaseResult> call = getSpaService().hideOrder(orderId,
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new OrderManageResult(orderId));
            }
        });
    }

    private void getICode(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().getICode(params.get(RequestConstant.KEY_MOBILE));

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("getICode: " + errorMsg);
            }
        });

    }

    private void joinClub(Map<String, String> params) {
        Call<JoinClubResult> call = getSpaService().joinClub(
                params.get(RequestConstant.KEY_TOKEN),
                params.get(RequestConstant.KEY_INVITE_CODE),
                params.get(RequestConstant.KEY_SPARE_TECH_ID),
                RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<JoinClubResult>() {
            @Override
            protected void postResult(JoinClubResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getTechEditInfo() {
        Call<TechEditResult> call = getSpaService().getTechEditInfo(LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<TechEditResult>() {
            @Override
            protected void postResult(TechEditResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getTechCurrentInfo() {
        Call<TechCurrentResult> call = getSpaService().getTechCurrentInfo(LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<TechCurrentResult>() {
            @Override
            protected void postResult(TechCurrentResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void updateTechInfo(Map<String, String> params) {
        Call<UpdateTechInfoResult> call = getSpaService().updateTechInfo(params.get(RequestConstant.KEY_USER),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<UpdateTechInfoResult>() {
            @Override
            protected void postResult(UpdateTechInfoResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getWorkTime() {
        Call<WorkTimeResult> call = getSpaService().getWorkTime(LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<WorkTimeResult>() {
            @Override
            protected void postResult(WorkTimeResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void updateWorkTime(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().updateWorkTime(params.get(RequestConstant.KEY_DAY_RANGE),
                params.get(RequestConstant.KEY_BEGIN_TIME), params.get(RequestConstant.KEY_END_TIME),
                params.get(RequestConstant.KEY_ID), params.get(RequestConstant.KEY_END_DAY),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new UpdateWorkTimeResult());
            }
        });
    }

    private void updateWorkStatus(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().updateWorkStatus(params.get(RequestConstant.KEY_STATUS),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new UpdateWorkStatusResult());
            }
        });
    }

    private void uploadAvatar(Map<String, String> params) {
        Call<AvatarResult> call = getSpaService().uploadAvatar(params.get(RequestConstant.KEY_IMG_FILE),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AvatarResult>() {
            @Override
            protected void postResult(AvatarResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void uploadAlbum(Map<String, String> params) {
        Call<AlbumResult> call = getSpaService().uploadAlbum(params.get(RequestConstant.KEY_IMG_FILE),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AlbumResult>() {
            @Override
            protected void postResult(AlbumResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void deleteAlbum(Map<String, String> params) {
        Call<AlbumResult> call = getSpaService().deleteAlbum(params.get(RequestConstant.KEY_ID),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AlbumResult>() {
            @Override
            protected void postResult(AlbumResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void sortAlbum(Map<String, String> params) {
        Call<AlbumResult> call = getSpaService().sortAlbum(params.get(RequestConstant.KEY_IDS),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AlbumResult>() {
            @Override
            protected void postResult(AlbumResult result) {
                RxBus.getInstance().post(new AlbumResult());
            }
        });
    }

    private void getServiceList() {
        Call<ServiceResult> call = getSpaService().getServiceList(LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<ServiceResult>() {
            @Override
            protected void postResult(ServiceResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void updateServiceList(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().updateServiceList(params.get(RequestConstant.KEY_IDS),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new UpdateServiceResult());
            }
        });
    }

    private void getTechPersonalData() {
        Call<TechPersonalDataResult> call = getSpaService().getTechPersonalData(LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE, RequestConstant.USER_TYPE_TECH);

        call.enqueue(new TokenCheckedCallback<TechPersonalDataResult>() {
            @Override
            protected void postResult(TechPersonalDataResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getAccountMoney() {
        Call<AccountMoneyResult> call = getSpaService().getAccountMoney(LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AccountMoneyResult>() {
            @Override
            protected void postResult(AccountMoneyResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getConsumeDetail(Map<String, String> params) {
        Call<ConsumeDetailResult> call = getSpaService().getConsumeDetail(params.get(RequestConstant.KEY_CONSUME_TYPE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<ConsumeDetailResult>() {
            @Override
            protected void postResult(ConsumeDetailResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getCouponList() {
        Call<CouponListResult> call = getSpaService().getCouponList(LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<CouponListResult>() {
            @Override
            protected void postResult(CouponListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("getCouponList: " + errorMsg);
                CouponListResult result = new CouponListResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetCouponInfo(String actId) {
        Call<CouponInfoResult> call = getSpaService().getCouponInfo(
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE, actId);

        call.enqueue(new TokenCheckedCallback<CouponInfoResult>() {
            @Override
            protected void postResult(CouponInfoResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                if ("红包已过期".equals(errorMsg)) {
                    CouponInfoResult result = new CouponInfoResult();
                    result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                    result.msg = errorMsg;
                    RxBus.getInstance().post(result);
                }

                Logger.v("doGetCouponInfo: " + errorMsg);
            }
        });
    }

    private void doGetPaidCouponUserDetail(Map<String, String> params) {
        Call<PaidCouponUserDetailResult> call = getSpaService().getPaidCouponUserDetail(
                params.get(RequestConstant.KEY_COUPON_STATUS), params.get(RequestConstant.KEY_ACT_ID),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<PaidCouponUserDetailResult>() {
            @Override
            protected void postResult(PaidCouponUserDetailResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * submit feedback
     *
     * @param params
     */
    private void doSubmitFeedback(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().submitFeedback(params.get(RequestConstant.KEY_COMMENTS),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new FeedbackResult());
            }
        });
    }

    private void quitClub(Map<String, String> params) {
        String password = params.get(RequestConstant.KEY_PASSWORD);
        Call<QuitClubResult> call = getSpaService().quitClub(LoginTechnician.getInstance().getToken(), password, RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<QuitClubResult>() {
            @Override
            protected void postResult(QuitClubResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doCouponShareEventCount(String actId) {
        Call<BaseResult> call = getSpaService().doCouponShareEventCount(actId, RequestConstant.USER_TYPE_TECH,
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doBindGetuiClientId() {

        if (TextUtils.isEmpty(LoginTechnician.getInstance().getToken())) {
            return;
        }

        /*if (!SettingFlags.getBoolean(SettingFlags.ORDER_NOTIFIATION_ON)) {
            return;
        }*/

        String clientId = AppConfig.sClientId;

        //并将其存由于之前获取过clientid，放在shared_preferences中了，而SDK在获取client_id的过程中可能存在延迟，
        // 此时可从shared_preferences中先获取，减少client_id为空的几率
        if (TextUtils.isEmpty(clientId)) {
            clientId = SharedPreferenceHelper.getClientId();
            if (TextUtils.isEmpty(clientId)) {
                return;
            }
        }

        String userId = SharedPreferenceHelper.getUserId();
        Logger.v("start bind client id : " + AppConfig.sClientId + " with user Id : " + userId);

        //String decryptPwd = appID + appSecret + telephone + appKey + masterSecret + clientId;
        StringBuilder sb = new StringBuilder();
        sb.append(AppConfig.sGetuiAppId)
                .append(AppConfig.sGetuiAppSecret)
                .append(userId)
                .append(AppConfig.sGetuiAppKey)
                .append(AppConfig.sGetuiMasterSecret)
                .append(AppConfig.sClientId);
        String secretBefore = sb.toString();
        String secret = DESede.encrypt(secretBefore);

        Call<BaseResult> call = getSpaService().bindGetuiClientId(userId, RequestConstant.USER_TYPE_TECH, RequestConstant.APP_TYPE_ANDROID,
                AppConfig.sClientId, secret);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                Logger.v("bind successful with client id : " + AppConfig.sClientId);
                AppConfig.sBindClientIdStatus = "bind Successful";
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("bind failed with " + errorMsg);
                AppConfig.sBindClientIdStatus = "bind failed";
            }
        });
    }

    /**
     * 解绑个推ClientID
     */
    private void doUnbindGetuiClientId(Map<String, String> params) {
        Logger.v("start unbind client id");
        Call<BaseResult> call = getSpaService().unbindGetuiClientId(RequestConstant.USER_TYPE_TECH,
                params.get(RequestConstant.KEY_TOKEN), RequestConstant.SESSION_TYPE, AppConfig.sClientId);
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                Logger.v("unbind successful");
                AppConfig.sBindClientIdStatus = "unbind successful";
            }

            @Override
            protected void postError(String errorMsg) {
                AppConfig.sBindClientIdStatus = "unbind failed";
            }
        });
    }

    /**
     * 添加联系人
     *
     * @param params
     */
    private void doAddOrEditCustomer(Map<String, String> params) {
        if (TextUtils.isEmpty(params.get(RequestConstant.KEY_ID))) {
            Call<BaseResult> call = getSpaService().addCustomer(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken(),
                    params.get(RequestConstant.KEY_PHONE_NUMBER), params.get(RequestConstant.KEY_REMARK), params.get(RequestConstant.KEY_NOTE_NAME), params.get(RequestConstant.KEY_MARK_IMPRESSION));
            call.enqueue(new TokenCheckedCallback<BaseResult>() {
                @Override
                protected void postResult(BaseResult result) {
                    RxBus.getInstance().post(new AddOrEditResult(result.msg, result.statusCode));
                }
            });
        } else {
            Call<BaseResult> call = getSpaService().editCustomer(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_ID),
                    params.get(RequestConstant.KEY_PHONE_NUMBER), params.get(RequestConstant.KEY_REMARK), params.get(RequestConstant.KEY_NOTE_NAME), params.get(RequestConstant.KEY_MARK_IMPRESSION));
            call.enqueue(new TokenCheckedCallback<BaseResult>() {
                @Override
                protected void postResult(BaseResult result) {
                    RxBus.getInstance().post(new AddOrEditResult(result.msg, result.statusCode));
                }
            });

        }
    }

    /**
     * 获取联系人列表
     *
     * @param
     */
    private void doGetCustomerList(Map<String, String> params) {
        Call<CustomerListResult> call = getSpaService().getCustomerList(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_CUSTOMER_TYPE));
        call.enqueue(new TokenCheckedCallback<CustomerListResult>() {
            @Override
            protected void postResult(CustomerListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 获取联系人详情
     *
     * @param
     */
    private void doGetCustomerInfoDetail(Map<String, String> params) {
        Call<CustomerDetailResult> call = getSpaService().getCustomerInfoDetail(RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_USER_ID), params.get(RequestConstant.KEY_ID), LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<CustomerDetailResult>() {
            @Override
            protected void postResult(CustomerDetailResult result) {

                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 获取技师联系人详情
     *
     * @param
     */
    private void doGetTechInfoDetail(Map<String, String> params) {
        Call<TechDetailResult> call = getSpaService().getTechInfoDetail(RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_ID), LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<TechDetailResult>() {
            @Override
            protected void postResult(TechDetailResult result) {

                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 获取管理者联系人详情
     *
     * @param
     */
    private void doGetManagerInfoDetail(Map<String, String> params) {
        Call<ManagerDetailResult> call = getSpaService().getManagerInfoDetail(RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_ID), LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<ManagerDetailResult>() {
            @Override
            protected void postResult(ManagerDetailResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 获取俱乐部联系人列表
     *
     * @param
     */
    private void doGetClubList() {
        Call<ClubContactResult> call = getSpaService().getClubList(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<ClubContactResult>() {
            @Override
            protected void postResult(ClubContactResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 删除联系人
     *
     * @param
     */
    private void doDeleteContact(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().doDeleteContact(RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_ID), LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new DeleteContactResult(result.msg, result.statusCode));
            }
        });
    }

    private void doGetUserRecords(Map<String, String> params) {
        Call<CreditAccountDetailResult> call = getSpaService().doGetUserRecordDetail(RequestConstant.SESSION_TYPE, SharedPreferenceHelper.getUserClubId(),
                LoginTechnician.getInstance().getToken(), RequestConstant.USER_TYPE_TECH, params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE)
        );
        call.enqueue(new TokenCheckedCallback<CreditAccountDetailResult>() {
            @Override
            protected void postResult(CreditAccountDetailResult result) {
                RxBus.getInstance().post(result);
            }
        });

    }

    private void doGetUserCreditSwitchStatus() {
        Call<CreditStatusResult> call = getSpaService().doGetCreditStatus(RequestConstant.SESSION_TYPE, SharedPreferenceHelper.getUserClubId(), LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<CreditStatusResult>() {
            @Override
            protected void postResult(CreditStatusResult result) {
                RxBus.getInstance().post(result);
            }
        });

    }

    private void doGetUserCreditAccount() {
        Call<CreditAccountResult> call = getSpaService().doGetCreditAccount(RequestConstant.USER_TYPE_TECH, SharedPreferenceHelper.getUserClubId(), LoginTechnician.getInstance().getToken(),
                RequestConstant.SESSION_TYPE
        );
        call.enqueue(new TokenCheckedCallback<CreditAccountResult>() {
            @Override
            protected void postResult(CreditAccountResult result) {
                RxBus.getInstance().post(result);
            }
        });

    }

    private void doExchangeCredit(Map<String, String> params) {
        Call<CreditExchangeResult> call = getSpaService().doExchangeCredit(RequestConstant.USER_TYPE_TECH, params.get(RequestConstant.KEY_UER_CREDIT_AMOUNT),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<CreditExchangeResult>() {
            @Override
            protected void postResult(CreditExchangeResult result) {
                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * 兑换积分
     *
     * @param params
     */
    private void getExchangeApplications(Map<String, String> params) {
        Call<CreditApplicationsResult> call = getSpaService().getExchangeApplications(LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_STATUS),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));

        call.enqueue(new TokenCheckedCallback<CreditApplicationsResult>() {
            @Override
            protected void postResult(CreditApplicationsResult result) {
                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * 获取联系人备注
     */
    private void doGetContactMark() {
        Call<MarkResult> call = getSpaService().getContactMark(RequestConstant.USER_TYPE_TECH, RequestConstant.TECH_CUSTOMER,
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<MarkResult>() {
            @Override
            protected void postResult(MarkResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 发起游戏
     *
     * @param params
     */
    private void doDiceGameSubmit(Map<String, String> params) {

        Call<SendGameResult> call = getSpaService().doDiceGameSubmit(params.get(RequestConstant.KEY_USER_CLUB_ID), params.get(RequestConstant.KEY_UER_CREDIT_AMOUNT),
                params.get(RequestConstant.KEY_GAME_USER_EMCHAT_ID), params.get(RequestConstant.KEY_DICE_GAME_TIME), RequestConstant.SESSION_TYPE,
                LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<SendGameResult>() {
            @Override
            protected void postResult(SendGameResult result) {
                RxBus.getInstance().post(result);
            }


        });
    }

    /**
     * 游戏开始或拒绝
     */
    private void doDiceGameAcceptOrReject(Map<String, String> params) {
        Call<GameResult> call = getSpaService().doDiceGameAcceptOrReject(RequestConstant.USER_TYPE_TECH, LoginTechnician.getInstance().getToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_DICE_GAME_ID), params.get(RequestConstant.KEY_DICE_GAME_STATUS));
        call.enqueue(new TokenCheckedCallback<GameResult>() {
            @Override
            protected void postResult(GameResult result) {

                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * 获取俱乐部开关
     */
    private void doGetClubUserSwitches() {
        Call<UserSwitchesResult> call = getSpaService().doGetUserSwitches(SharedPreferenceHelper.getUserClubId(), RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<UserSwitchesResult>() {
            @Override
            protected void postResult(UserSwitchesResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 最近访客
     *
     * @param params
     */

    private void doGetRecentlyVisitorList(Map<String, String> params) {
        Call<RecentlyVisitorResult> call = getSpaService().getRecentlyVisitorList(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_CUSTOMER_TYPE), params.get(RequestConstant.KEY_LAST_TIME));
        call.enqueue(new TokenCheckedCallback<RecentlyVisitorResult>() {
            @Override
            protected void postResult(RecentlyVisitorResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 礼物列表
     *
     * @param
     */
    private void doGetCreditGiftList() {
        Call<GiftListResult> call = getSpaService().getCreditGiftList(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<GiftListResult>() {
            @Override
            protected void postResult(GiftListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 打招呼
     *
     * @param
     */
    private void doSayHi(Map<String, String> params) {
        Call<SayHiResult> call = getSpaService().doSayHi(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_UPDATE_USER_ID));
        call.enqueue(new TokenCheckedCallback<SayHiResult>() {
            @Override
            protected void postResult(SayHiResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doGetVisitView(Map<String, String> params) {
        Call<VisitBean> call = getSpaService().doGetVisitView(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_UPDATE_USER_ID));
        call.enqueue(new TokenCheckedCallback<VisitBean>() {
            @Override
            protected void postResult(VisitBean result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 打招呼后保存
     *
     * @param
     */
    private void doSaveToContact(Map<String, String> params) {
        Call<SaveChatUserResult> call = getSpaService().doSaveContact(SharedPreferenceHelper.getEmchatId(),
                RequestConstant.USER_TYPE_TECH, params.get(RequestConstant.KEY_FRIEND_CHAT_ID), RequestConstant.USER_TYPE_USER, RequestConstant.KEY_MSG_TYPE_TEXT);
        call.enqueue(new TokenCheckedCallback<SaveChatUserResult>() {
            @Override
            protected void postResult(SaveChatUserResult result) {
                RxBus.getInstance().post(result);
                if (result.statusCode == 200) {
                    Logger.i(result.msg);
                }
            }
        });
    }

    /**
     * 首页技师信息
     *
     * @param
     */
    private void getTechInfo() {
        Call<TechInfoResult> call = getSpaService().getTechInfo(LoginTechnician.getInstance().getToken());

        call.enqueue(new TokenCheckedCallback<TechInfoResult>() {
            @Override
            protected void postResult(TechInfoResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 首页订单
     *
     * @param
     */
    private void getMainPageOrderList(Map<String, String> params) {
        Call<OrderListResult> call = getSpaService().getTechOrderList(LoginTechnician.getInstance().getToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_ORDER_STATUS), params.get(RequestConstant.KEY_IS_INDEX_PAGE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<OrderListResult>() {
            @Override
            protected void postResult(OrderListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 首页数据
     *
     * @param
     */
    private void getTechStatisticsData() {
        Call<TechStatisticsDataResult> call = getSpaService().getTechStatisticData(LoginTechnician.getInstance().getToken());

        call.enqueue(new TokenCheckedCallback<TechStatisticsDataResult>() {
            @Override
            protected void postResult(TechStatisticsDataResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }


    /**
     * 技师排行榜
     *
     * @param
     */
    private void getTechRankData() {
        Call<TechRankDataResult> call = getSpaService().getTechRankData(LoginTechnician.getInstance().getToken());

        call.enqueue(new TokenCheckedCallback<TechRankDataResult>() {
            @Override
            protected void postResult(TechRankDataResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 动态列表
     *
     * @param
     */
    private void getDynamicList(Map<String, String> params) {
        if (!TextUtils.isEmpty(LoginTechnician.getInstance().getClubId())) {
            Call<DynamicListResult> call = getSpaService().getDynamicList(LoginTechnician.getInstance().getToken(),
                    params.get(RequestConstant.KEY_TECH_DYNAMIC_TYPE), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
            call.enqueue(new TokenCheckedCallback<DynamicListResult>() {
                @Override
                protected void postResult(DynamicListResult result) {
                    RxBus.getInstance().post(result);
                }
            });
        } else {
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    DynamicListResult result = new DynamicListResult();
                    result.statusCode = 200;
                    result.respData = new ArrayList<>();
                    RxBus.getInstance().post(result);
                }
            });
        }
    }

    private void doHandleTokenExpired(String errorMsg) {
        RxBus.getInstance().post(new TokenExpiredResult(errorMsg));
    }

    private void setPageSelected(int obj) {
        RxBus.getInstance().post(new CurrentSelectPage(obj));
    }

    private void setOrderInnerRead(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().setOrderInnerRead(LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_ORDER_ID));
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new OrderManageResult(params.get(RequestConstant.KEY_ORDER_ID)));
            }
        });
    }

    private void getClubUserCoupon(Map<String, Object> params) {
        Call<UserGetCouponResult> call = getSpaService().clubUserCoupon(SharedPreferenceHelper.getUserToken(), (String) params.get(RequestConstant.KEY_USER_COUPON_ACT_ID),
                (String) params.get(RequestConstant.KEY_USER_COUPON_CHANEL), (String) params.get(RequestConstant.KEY_USER_COUPON_EMCHAT_ID), SharedPreferenceHelper.getInviteCode(), SharedPreferenceHelper.getInviteCode());
        call.enqueue(new TokenCheckedCallback<UserGetCouponResult>() {
            @Override
            protected void postResult(UserGetCouponResult result) {
                result.mMessage = (EMMessage) params.get(RequestConstant.KEY_USER_COUPON_EMCHAT_MESSAGE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                UserGetCouponResult result = new UserGetCouponResult((EMMessage) params.get(RequestConstant.KEY_USER_COUPON_EMCHAT_MESSAGE));
                RxBus.getInstance().post(result);
            }
        });

    }

    /**
     * 卡券列表
     */
    private void getCardShareList() {
        Call<CardShareListResult> call = getSpaService().cardShareList(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<CardShareListResult>() {
            @Override
            protected void postResult(CardShareListResult result) {
                RxBus.getInstance().post(result);
            }
        });


    }

    /**
     * 热门活动列表
     */
    private void getActivityList() {
        Call<ActivityListResult> call = getSpaService().activityList(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<ActivityListResult>() {
            @Override
            protected void postResult(ActivityListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 宣传列表
     * propagandaList
     */
    private void getPropagandaList() {
        Call<PropagandaListResult> call = getSpaService().propagandaList(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<PropagandaListResult>() {
            @Override
            protected void postResult(PropagandaListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 次卡列表
     */
    private void getOnceCardListDetail() {
        Call<OnceCardResult> call = getSpaService().onceCardListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<OnceCardResult>() {
            @Override
            protected void postResult(OnceCardResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 券列表
     *
     * @param type
     */
    private void getCardListDetail(String type) {
        Call<ShareCouponResult> call = getSpaService().cardListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId(), type);
        call.enqueue(new TokenCheckedCallback<ShareCouponResult>() {
            @Override
            protected void postResult(ShareCouponResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 限时抢列表
     */
    private void getServiceItemListDetail() {
        Call<LimitGrabResult> call = getSpaService().serviceItemListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<LimitGrabResult>() {
            @Override
            protected void postResult(LimitGrabResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 抽奖活动
     */
    private void getRewardActivityList() {
        Call<RewardListResult> call = getSpaService().rewardActivityListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<RewardListResult>() {
            @Override
            protected void postResult(RewardListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 期刊列表
     *
     */
    private void getClubJournalList(){
        Call<JournalListResult> call = getSpaService().clubJournalListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<JournalListResult>() {
            @Override
            protected void postResult(JournalListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 谁替我买单列表
     *
     */
    private void getPayForMeListDetail(){
        Call<PayForMeListResult> call = getSpaService().payForMeListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<PayForMeListResult>() {
            @Override
            protected void postResult(PayForMeListResult result) {
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
            public void onResponse(Call<AppUpdateConfigResult> c, Response<AppUpdateConfigResult> response) {
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
//                RxBus.getInstance().post(t);
                Logger.e("get app update config failed:" + t.getMessage());
            }
        });
    }


    private void getPayNotify(Map<String, Object> params) {
//        Call<GetPayNotifyListResult> call = getSpaService().getPayNotifyList(
//                SharedPreferenceHelper.getUserToken(),
//                (String) params.get(RequestConstant.KEY_START_DATE),
//                (String) params.get(RequestConstant.KEY_END_DATE),
//                "1", String.valueOf(Integer.MAX_VALUE));
//        call.enqueue(new TokenCheckedCallback<GetPayNotifyListResult>() {
//            @Override
//            protected void postResult(GetPayNotifyListResult result) {
//                RxBus.getInstance().post(result);
//            }
//
//            @Override
//            protected void postError(String errorMsg) {
//                GetPayNotifyListResult result = new GetPayNotifyListResult();
//                result.statusCode = 400;
//                result.msg = errorMsg;
//                RxBus.getInstance().post(result);
//            }
//        });
    }


}
