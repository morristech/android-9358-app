package com.xmd.technician.http;

import android.os.Message;
import android.text.TextUtils;

import com.hyphenate.chat.EMConversation;
import com.xmd.technician.AppConfig;
import com.xmd.technician.Constant;
import com.xmd.technician.SharedPreferenceHelper;
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
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.bean.RecentlyVisitorResult;
import com.xmd.technician.bean.SaveChatUserResult;
import com.xmd.technician.bean.SendGameResult;
import com.xmd.technician.bean.UserGetCouponResult;
import com.xmd.technician.bean.UserSwitchesResult;
import com.xmd.technician.bean.VisitBean;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.http.gson.AccountMoneyResult;
import com.xmd.technician.http.gson.ActivityListResult;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AppUpdateConfigResult;
import com.xmd.technician.http.gson.AuditCancelResult;
import com.xmd.technician.http.gson.AuditConfirmResult;
import com.xmd.technician.http.gson.AuditModifyResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.CardShareListResult;
import com.xmd.technician.http.gson.CategoryListResult;
import com.xmd.technician.http.gson.ClubPositionResult;
import com.xmd.technician.http.gson.ConsumeDetailResult;
import com.xmd.technician.http.gson.ContactPermissionChatResult;
import com.xmd.technician.http.gson.ContactPermissionResult;
import com.xmd.technician.http.gson.ContactPermissionVisitorResult;
import com.xmd.technician.http.gson.CouponInfoResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.http.gson.DeleteTechPosterResult;
import com.xmd.technician.http.gson.DynamicListResult;
import com.xmd.technician.http.gson.FeedbackResult;
import com.xmd.technician.http.gson.GroupBuyListResult;
import com.xmd.technician.http.gson.HelloGetTemplateResult;
import com.xmd.technician.http.gson.HelloLeftCountResult;
import com.xmd.technician.http.gson.HelloRecordListResult;
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
import com.xmd.technician.http.gson.ModifyPasswordResult;
import com.xmd.technician.http.gson.NearbyCusCountResult;
import com.xmd.technician.http.gson.NearbyCusListResult;
import com.xmd.technician.http.gson.OnceCardResult;
import com.xmd.technician.http.gson.OrderCountResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.PKActivityListResult;
import com.xmd.technician.http.gson.PKPersonalListResult;
import com.xmd.technician.http.gson.PKTeamListResult;
import com.xmd.technician.http.gson.PaidCouponUserDetailResult;
import com.xmd.technician.http.gson.PayForMeListResult;
import com.xmd.technician.http.gson.PropagandaListResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.http.gson.ResetPasswordResult;
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
import com.xmd.technician.http.gson.UpdateServiceResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.http.gson.UpdateWorkStatusResult;
import com.xmd.technician.http.gson.UpdateWorkTimeResult;
import com.xmd.technician.http.gson.UploadTechPosterImageResult;
import com.xmd.technician.http.gson.WithdrawRuleResult;
import com.xmd.technician.http.gson.WorkTimeResult;
import com.xmd.technician.model.HelloSettingManager;
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
                throw new RuntimeException("not support!");
            case MsgDef.MSG_DEF_GET_TECH_EDIT_INFO:
                getTechEditInfo();
                break;
//            case MsgDef.MSG_DEF_GET_TECH_CURRENT_INFO:
//                getTechCurrentInfo();
//                break;
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
            case MsgDef.MSG_DEF_DELETE_CONTACT:
                doDeleteContact((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_USER_RECORDE:
                doGetUserRecords((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CREDIT_STATUS:
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
                setPageSelected((Map<String, Integer>) msg.obj);
                break;
            case MsgDef.MSG_DEF_ORDER_INNER_READ:
                setOrderInnerRead((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_USER_GET_COUPON:
                getClubUserCoupon((Map<String, String>) msg.obj);
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
                getOnceCardListDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CARD_LIST_DETAIL:
                getCardListDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST_DETAIL:
                getServiceItemListDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_REWARD_ACTIVITY_LIST:
                getRewardActivityList();
                break;
            case MsgDef.MSG_DEF_GET_INVITATION_REWARD_ACTIVITY_LIST:
                getInvitationRewardActivityList();
                break;
            case MsgDef.MSG_DEF_GET_CLUB_JOURNAL_LIST:
                getClubJournalList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_PAY_FOR_ME_LIST_DETAIL:
                getPayForMeListDetail((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_JOURNAL_SHARE_COUNT:
                journalShareCount(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_TECH_ACCOUNT_LIST:
                getTechAccountList();
                break;
            case MsgDef.MSG_DEF_GET_ROLE_PERMISSION:
                getRolePermissionList();
                break;
            case MsgDef.MSG_DEF_GET_ROLE_LIST:
                getRoleList();
                break;

            // --------------------------------------> 附近的人 <------------------------------------
            case MsgDef.MSG_DEF_GET_CLUB_POSITION_INFO:
                getClubPosition();
                break;
            case MsgDef.MSG_DEF_GET_NEARBY_CUS_COUNT:
                getNearbyCusCount();
                break;
            case MsgDef.MSG_DEF_GET_NEARBY_CUS_LIST:
                getNearbyCusList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_HELLO_LEFT_COUNT:
                getHelloLeftCount();
                break;
            case MsgDef.MSG_DEF_GET_HELLO_RECORD_LIST:
                getHelloRecordList((Map<String, String>) msg.obj);
                break;
//            case MsgDef.MSG_DEF_CHECK_HELLO_RECENTLY:
//                checkHelloRecently((Map<String, String>) msg.obj);
//                break;
            case MsgDef.MSG_DEF_GET_CONTACT_PERMISSION:
                getContactPermission((Map<String, Object>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_SET_TEMPLATE:
                getSetTemplate();
                break;
            case MsgDef.MSG_DEF_SAVE_SET_TEMPLATE:
                saveSetTemplate((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_SYS_TEMPLATE_LIST:
                getSysTemplateList();
                break;
            case MsgDef.MSG_DEF_UPLOAD_HELLO_TEMPLATE_IMG:
                uploadHelloTemplateImg((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_DOWNLOAD_HELLO_IMAGE_CACHE:
                downloadHelloImageCache();
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
            // --------------------------------------> 聊天优化 <------------------------------------
            case MsgDef.MSG_DEF_MARK_CHAT_TO_USER:
                getMarkChatToUser();
                break;
            case MsgDef.MSG_DEF_GET_CHAT_CATEGORY_LIST:
                getCategoryList();
                break;
            case MsgDef.MSG_DEF_GET_TECH_MARKETING_LIST:
                getTechMarketingList();
                break;
            // --------------------------------------> 技师海报 <------------------------------------
            case MsgDef.MSG_DEF_TECH_POSTER_SAVE:
                doSaveTechPoster((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_POSTER_DELETE:
                doDeleteTechPoster(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_TECH_POSTER_IMAGE_UPLOAD:
                doUploadTechPosterImage((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_POSTER_LIST:
                getTechPosterList();
                break;
            case MsgDef.MSG_DEF_TECH_POSTER_DETAIL:
                getTechPosterDetail(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_TECH_ORDER_COUNT:
                getTechOrderCount(msg.obj.toString());
                break;
            case MsgDef.MSG_DEF_TECH_AUDIT_MODIFY:
                techAuditModify((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_AUDIT_CANCEL:
                techAuditCancel((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_AUDIT_CONFIRM:
                techAuditConfirm();
                break;
            case MsgDef.MSG_DEF_TECH_SHARE_COUNT_UPDATE:
                techShareCountUpdate((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_TECH_WITHDRAW_RULE:
                getWithDrawRule();
                break;
            case MsgDef.MSG_DEF_GROUP_BUY_ACTIVITY:
                getClubGroupBuyList();
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
                LoginResult loginResult = new LoginResult();
                loginResult.statusCode = 400;
                loginResult.status = t.getMessage();
                RxBus.getInstance().post(loginResult);
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

    private void register(Map<String, String> params) {
        Call<RegisterResult> call = getSpaService().register(
                params.get(RequestConstant.KEY_MOBILE),
                params.get(RequestConstant.KEY_PASSWORD),
                params.get(RequestConstant.KEY_ICODE),
                params.get(RequestConstant.KEY_ROLE_CODE),
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
                RxBus.getInstance().post(new OrderManageResult(params.get(RequestConstant.KEY_ID), params.get(RequestConstant.KEY_REASON)));
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
        Call<BaseResult> call = getSpaService().getICode(params.get(RequestConstant.KEY_MOBILE), RequestConstant.KEY_WHICH_VALUE, params.get(RequestConstant.KEY_SIGN));

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
                params.get(RequestConstant.KEY_ROLE_CODE),
                RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<JoinClubResult>() {
            @Override
            protected void postResult(JoinClubResult result) {
                result.role = params.get(RequestConstant.KEY_ROLE_CODE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                JoinClubResult result = new JoinClubResult();
                result.msg = errorMsg;
                result.statusCode = 400;
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

    @Deprecated
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

            @Override
            protected void postError(String errorMsg) {
                UpdateTechInfoResult result = new UpdateTechInfoResult();
                result.msg = errorMsg;
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
        Call<UpdateWorkStatusResult> call = getSpaService().updateWorkStatus(params.get(RequestConstant.KEY_STATUS),
                LoginTechnician.getInstance().getToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<UpdateWorkStatusResult>() {
            @Override
            protected void postResult(UpdateWorkStatusResult result) {
                result.targetStatus = params.get(RequestConstant.KEY_STATUS);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                UpdateWorkStatusResult result = new UpdateWorkStatusResult();
                result.msg = errorMsg;
                result.statusCode = RequestConstant.RESP_ERROR;
                result.targetStatus = params.get(RequestConstant.KEY_STATUS);
                RxBus.getInstance().post(result);
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

            @Override
            protected void postError(String errorMsg) {
                AvatarResult avatarResult = new AvatarResult();
                avatarResult.respData = errorMsg;
                RxBus.getInstance().post(avatarResult);
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
                RxBus.getInstance().post(result);
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

            @Override
            public void onFailure(Call<TechPersonalDataResult> call, Throwable t) {
                Logger.e("getTechPersonalData:" + t.getLocalizedMessage());
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

            @Override
            protected void postError(String errorMsg) {
                QuitClubResult result = new QuitClubResult();
                result.statusCode = 400;
                result.msg = errorMsg;
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
        throw new RuntimeException("do not call this!!");
//        if (TextUtils.isEmpty(LoginTechnician.getInstance().getToken())) {
//            return;
//        }
//
//        /*if (!SettingFlags.getBoolean(SettingFlags.ORDER_NOTIFIATION_ON)) {
//            return;
//        }*/
//
//        String clientId = AppConfig.sClientId;
//
//        //并将其存由于之前获取过clientid，放在shared_preferences中了，而SDK在获取client_id的过程中可能存在延迟，
//        // 此时可从shared_preferences中先获取，减少client_id为空的几率
//        if (TextUtils.isEmpty(clientId)) {
//            clientId = SharedPreferenceHelper.getClientId();
//            if (TextUtils.isEmpty(clientId)) {
//                return;
//            }
//        }
//
//        String userId = SharedPreferenceHelper.getUserId();
//        Logger.d("start bind client id : " + AppConfig.sClientId + " with user Id : " + userId);
//
//        //String decryptPwd = appID + appSecret + telephone + appKey + masterSecret + clientId;
//        StringBuilder sb = new StringBuilder();
//        sb.append(AppConfig.sGetuiAppId)
//                .append(AppConfig.sGetuiAppSecret)
//                .append(userId)
//                .append(AppConfig.sGetuiAppKey)
//                .append(AppConfig.sGetuiMasterSecret)
//                .append(AppConfig.sClientId);
//        String secretBefore = sb.toString();
//        String secret = DESede.encrypt(secretBefore);
//        Call<BaseResult> call = getSpaService().bindGetuiClientId(userId, RequestConstant.USER_TYPE_TECH, RequestConstant.APP_TYPE_ANDROID,
//                AppConfig.sClientId, secret);
//
//        call.enqueue(new TokenCheckedCallback<BaseResult>() {
//            @Override
//            protected void postResult(BaseResult result) {
//                Logger.i("bind successful with client id : " + AppConfig.sClientId);
//                AppConfig.sBindClientIdStatus = "bind Successful";
//            }
//
//            @Override
//            protected void postError(String errorMsg) {
//                Logger.e("bind failed with " + errorMsg);
//                AppConfig.sBindClientIdStatus = "bind failed";
//            }
//        });
    }

    /**
     * 解绑个推ClientID
     */
    private void doUnbindGetuiClientId(Map<String, String> params) {
        Logger.v("start unbind client id");
        throw new RuntimeException("do not call this!!");
//        Call<BaseResult> call = getSpaService().unbindGetuiClientId(RequestConstant.USER_TYPE_TECH,
//                params.get(RequestConstant.KEY_TOKEN), RequestConstant.SESSION_TYPE, AppConfig.sClientId);
//        call.enqueue(new TokenCheckedCallback<BaseResult>() {
//            @Override
//            protected void postResult(BaseResult result) {
//                Logger.v("unbind successful");
//                AppConfig.sBindClientIdStatus = "unbind successful";
//            }
//
//            @Override
//            protected void postError(String errorMsg) {
//                AppConfig.sBindClientIdStatus = "unbind failed";
//            }
//        });
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

//    /**
//     * 获取技师联系人详情
//     *
//     * @param
//     */
//    private void doGetTechInfoDetail(Map<String, String> params) {
//        Call<TechDetailResult> call = getSpaService().getTechInfoDetail(RequestConstant.SESSION_TYPE, params.create(RequestConstant.KEY_ID), LoginTechnician.getInstance().getToken());
//        call.enqueue(new TokenCheckedCallback<TechDetailResult>() {
//            @Override
//            protected void postResult(TechDetailResult result) {
//
//                RxBus.getInstance().post(result);
//            }
//        });
//    }

//    /**
//     * 获取管理者联系人详情
//     *
//     * @param
//     */
//    private void doGetManagerInfoDetail(Map<String, String> params) {
//        Call<ManagerDetailResult> call = getSpaService().getManagerInfoDetail(RequestConstant.SESSION_TYPE, params.create(RequestConstant.KEY_ID), LoginTechnician.getInstance().getToken());
//        call.enqueue(new TokenCheckedCallback<ManagerDetailResult>() {
//            @Override
//            protected void postResult(ManagerDetailResult result) {
//                RxBus.getInstance().post(result);
//            }
//        });
//    }
//
//    /**
//     * 获取俱乐部联系人列表
//     *
//     * @param
//     */
//    private void doGetClubList() {
//        Call<ClubContactResult> call = getSpaService().getClubList(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken());
//        call.enqueue(new TokenCheckedCallback<ClubContactResult>() {
//            @Override
//            protected void postResult(ClubContactResult result) {
//                RxBus.getInstance().post(result);
//            }
//        });
//    }

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
        Call<RecentlyVisitorResult> call = getSpaService().getRecentlyVisitorList(RequestConstant.SESSION_TYPE, LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_CUSTOMER_TYPE), params.get(RequestConstant.KEY_LAST_TIME),
                params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<RecentlyVisitorResult>() {
            @Override
            protected void postResult(RecentlyVisitorResult result) {
                result.isMainPage = params.get(RequestConstant.KEY_IS_MAIN_PAGE);
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
                RequestConstant.USER_TYPE_TECH, params.get(RequestConstant.KEY_FRIEND_CHAT_ID), RequestConstant.USER_TYPE_USER, params.get(RequestConstant.KEY_CHAT_MSG_ID), RequestConstant.KEY_MSG_TYPE_TEXT);
        call.enqueue(new TokenCheckedCallback<SaveChatUserResult>() {
            @Override
            protected void postResult(SaveChatUserResult result) {
                RxBus.getInstance().post(result);
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

            @Override
            public void onFailure(Call<TechInfoResult> call, Throwable t) {
                TechInfoResult result = new TechInfoResult();
                result.statusCode = 400;
                result.msg = t.getLocalizedMessage();
                RxBus.getInstance().post(result);
                RxBus.getInstance().post(t.getLocalizedMessage());
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
                result.isIndexPage = params.get(RequestConstant.KEY_IS_INDEX_PAGE);
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

    private void setPageSelected(Map<String, Integer> params) {
        RxBus.getInstance().post(new CurrentSelectPage(params.get(Constant.SWITCH_FRAGMENT_INDEX), params.get(Constant.SWITCH_FRAGMENT_ITEM_INDEX)));
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

    private void getClubUserCoupon(Map<String, String> params) {
        Call<UserGetCouponResult> call = getSpaService().clubUserCoupon(
                SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_USER_COUPON_ACT_ID),
                params.get(RequestConstant.KEY_USER_COUPON_CHANEL),
                params.get(RequestConstant.KEY_USER_COUPON_EMCHAT_ID),
                SharedPreferenceHelper.getInviteCode(),
                SharedPreferenceHelper.getInviteCode());
        call.enqueue(new TokenCheckedCallback<UserGetCouponResult>() {
            @Override
            protected void postResult(UserGetCouponResult result) {
                result.actId = params.get(RequestConstant.KEY_ACT_ID);
                result.content = params.get(RequestConstant.KEY_COUPON_CONTENT);
                result.techCode = params.get(RequestConstant.KEY_USER_TECH_CODE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                UserGetCouponResult result = new UserGetCouponResult(params.get(RequestConstant.KEY_COUPON_CONTENT), params.get(RequestConstant.KEY_ACT_ID), params.get(RequestConstant.KEY_USER_TECH_CODE));
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
    private void getOnceCardListDetail(Map<String, String> params) {
        Call<OnceCardResult> call = getSpaService().onceCardListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId(), "true", params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
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
     * @param params
     */
    private void getCardListDetail(Map<String, String> params) {
        Call<ShareCouponResult> call = getSpaService().cardListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId(), params.get(RequestConstant.KEY_COUPON_TYPE),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
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
    private void getServiceItemListDetail(Map<String, String> params) {
        Call<LimitGrabResult> call = getSpaService().serviceItemListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId(), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
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
     * 邀请有礼
     */
    private void getInvitationRewardActivityList() {
        Call<InvitationRewardResult> call = getSpaService().invitationRewardListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<InvitationRewardResult>() {
            @Override
            protected void postResult(InvitationRewardResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 期刊列表
     */
    private void getClubJournalList(Map<String, String> params) {
        Call<JournalListResult> call = getSpaService().clubJournalListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId(),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<JournalListResult>() {
            @Override
            protected void postResult(JournalListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 谁替我买单列表
     */
    private void getPayForMeListDetail(Map<String, String> params) {
        Call<PayForMeListResult> call = getSpaService().payForMeListDetail(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId(),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<PayForMeListResult>() {
            @Override
            protected void postResult(PayForMeListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * journalShareCount
     *
     * @param
     */
    private void journalShareCount(String journalId) {
        Call<BaseResult> call = getSpaService().journalShareCount(SharedPreferenceHelper.getUserToken(), journalId);
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 账户余额列表
     *
     * @param
     */
    private void getTechAccountList() {
        Call<TechAccountListResult> call = getSpaService().techAccountList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<TechAccountListResult>() {
            @Override
            protected void postResult(TechAccountListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * 首页技师排行榜
     *
     * @param
     */
    private void getTechPKRanking() {
        Call<TechPKRankingResult> call = getSpaService().techPKRanking(SharedPreferenceHelper.getUserToken());
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
        Call<PKActivityListResult> call = getSpaService().pkActivityList(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_PAGE),
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
        Call<PKTeamListResult> call = getSpaService().techPkTeamList(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_PK_ACTIVITY_ID), params.get(RequestConstant.KEY_SORT_KEY),
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
        Call<PKPersonalListResult> call = getSpaService().techPkPersonalList(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_PK_ACTIVITY_ID), params.get(RequestConstant.KEY_SORT_KEY),
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
        Call<TechRankingListResult> call = getSpaService().techPersonalRankingList(SharedPreferenceHelper.getUserToken(), RequestConstant.USER_TYPE_TECH, params.get(RequestConstant.KEY_TECH_RANKING_SOR_TYPE),
                params.get(RequestConstant.KEY_START_DATE), params.get(RequestConstant.KEY_END_DATE), params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<TechRankingListResult>() {
            @Override
            protected void postResult(TechRankingListResult result) {
                result.sortType = params.get(RequestConstant.KEY_TECH_RANKING_SOR_TYPE);
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
                        Logger.e("create app update config failed:" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AppUpdateConfigResult> call, Throwable t) {
                Logger.e("create app update config failed:" + t.getMessage());
            }
        });
    }

    private void getRolePermissionList() {
        Call<RolePermissionListResult> call = getSpaService().getRolePermissionList(LoginTechnician.getInstance().getToken(), RequestConstant.VALUE_PLATFORM_TECH);

        call.enqueue(new TokenCheckedCallback<RolePermissionListResult>() {
            @Override
            protected void postResult(RolePermissionListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("getRolePermissionList: " + errorMsg);
                RolePermissionListResult result = new RolePermissionListResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getRoleList() {
        Call<RoleListResult> call = getSpaService().getRoleList(LoginTechnician.getInstance().getToken());

        call.enqueue(new TokenCheckedCallback<RoleListResult>() {
            @Override
            protected void postResult(RoleListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("getRoleList: " + errorMsg);
                RolePermissionListResult result = new RolePermissionListResult();
                result.statusCode = RequestConstant.RESP_ERROR_CODE_FOR_LOCAL;
                result.msg = errorMsg;
                RxBus.getInstance().post(result);
            }
        });
    }


    private void getPayNotify(Map<String, Object> params) {
//        Call<GetPayNotifyListResult> call = getSpaService().getPayNotifyList(
//                SharedPreferenceHelper.getUserToken(),
//                (String) params.create(RequestConstant.KEY_START_DATE),
//                (String) params.create(RequestConstant.KEY_END_DATE),
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


    // -----------------------------------------> 附近的人 <-----------------------------------------
    // 获取会所位置信息
    private void getClubPosition() {
        Call<ClubPositionResult> call = getSpaService().getClubPosition(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<ClubPositionResult>() {
            @Override
            protected void postResult(ClubPositionResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    // 获取会所附近客户数量
    private void getNearbyCusCount() {
        Call<NearbyCusCountResult> call = getSpaService().getNearbyCusCount(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<NearbyCusCountResult>() {
            @Override
            protected void postResult(NearbyCusCountResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    // 获取会所附近客户列表
    private void getNearbyCusList(Map<String, String> params) {
        Call<NearbyCusListResult> call = getSpaService().getNearbyCusList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<NearbyCusListResult>() {
            @Override
            protected void postResult(NearbyCusListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    // 获取剩余打招呼次数
    private void getHelloLeftCount() {
        Call<HelloLeftCountResult> call = getSpaService().getHelloLeftCount(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<HelloLeftCountResult>() {
            @Override
            protected void postResult(HelloLeftCountResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    // 获取打招呼记录
    private void getHelloRecordList(Map<String, String> params) {
        Call<HelloRecordListResult> call = getSpaService().getHelloRecordList(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_PAGE),
                params.get(RequestConstant.KEY_PAGE_SIZE));
        call.enqueue(new TokenCheckedCallback<HelloRecordListResult>() {
            @Override
            protected void postResult(HelloRecordListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

//    // 查询同某个客户是否打过招呼
//    private void checkHelloRecently(Map<String, String> params) {
//        Call<HelloCheckRecentlyResult> call = getSpaService().checkHelloRecently(params.create(RequestConstant.KEY_NEW_CUSTOMER_ID),
//                SharedPreferenceHelper.getUserToken());
//        call.enqueue(new TokenCheckedCallback<HelloCheckRecentlyResult>() {
//            @Override
//            protected void postResult(HelloCheckRecentlyResult result) {
//                RxBus.getInstance().post(result);
//            }
//        });
//    }

    // 查询同客户的联系
    private void getContactPermission(Map<String, Object> params) {
        String tag = (String) params.get(RequestConstant.KEY_REQUEST_CONTACT_PERMISSION_TAG);
        Call<ContactPermissionResult> call = getSpaService().getContactPermission((String) params.get(RequestConstant.KEY_ID),
                SharedPreferenceHelper.getUserToken(),
                (String) params.get(RequestConstant.KEY_CONTACT_ID_TYPE));
        call.enqueue(new TokenCheckedCallback<ContactPermissionResult>() {
            @Override
            protected void postResult(ContactPermissionResult result) {
                switch (tag) {
                    case Constant.REQUEST_CONTACT_PERMISSION_DETAIL:
                        // DetailActivity
                        RxBus.getInstance().post(result);
                        break;
                    case Constant.REQUEST_CONTACT_PERMISSION_VISITOR:
                        // MainFragment 最近访客
                        ContactPermissionVisitorResult visitorResult = new ContactPermissionVisitorResult(result);
                        visitorResult.bean = (RecentlyVisitorBean) params.get(RequestConstant.KEY_RECENTLY_VISITOR_BEAN);
                        RxBus.getInstance().post(visitorResult);
                        break;
                    case Constant.REQUEST_CONTACT_PERMISSION_EMCHAT:
                        // ChatFragment 聊天列表
                        ContactPermissionChatResult chatResult = new ContactPermissionChatResult(result);
                        chatResult.emConversation = (EMConversation) params.get(RequestConstant.KEY_CHAT_CONVERSATION_BEAN);
                        RxBus.getInstance().post(chatResult);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    // 获取打招呼内容
    private void getSetTemplate() {
        Call<HelloGetTemplateResult> call = getSpaService().getSetTemplate(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<HelloGetTemplateResult>() {
            @Override
            protected void postResult(HelloGetTemplateResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    // 保存打招呼内容
    private void saveSetTemplate(Map<String, String> params) {
        Call<HelloSaveTemplateResult> call = getSpaService().saveSetTemplate(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_MSG_TYPE_TEXT),
                params.get(RequestConstant.KEY_TEMPLATE_IMAGE_ID),
                params.get(RequestConstant.KEY_HELLO_TEMPLATE_ID));
        call.enqueue(new TokenCheckedCallback<HelloSaveTemplateResult>() {
            @Override
            protected void postResult(HelloSaveTemplateResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    // 查询系统打招呼模版列表
    private void getSysTemplateList() {
        Call<HelloSysTemplateResult> call = getSpaService().getSysTemplateList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<HelloSysTemplateResult>() {
            @Override
            protected void postResult(HelloSysTemplateResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    // 上传打招呼图片
    private void uploadHelloTemplateImg(Map<String, String> params) {
        Call<HelloUploadImgResult> call = getSpaService().uploadTemplateImg(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_IMG_FILE));
        call.enqueue(new TokenCheckedCallback<HelloUploadImgResult>() {
            @Override
            protected void postResult(HelloUploadImgResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    // 下载打招呼图片进行缓存
    private void downloadHelloImageCache() {
        HelloSettingManager.getInstance().getCacheFilePath();
    }

    // -----------------------------------------> 聊天黑名单 <-----------------------------------------

    //技师是否在联系人聊天黑名单中
//    private void inUserBlacklist(String friendChatId) {
//        Call<InUserBlacklistResult> call = getSpaService().inUserBlacklist(friendChatId, LoginTechnician.getInstance().getToken());
//        call.enqueue(new TokenCheckedCallback<InUserBlacklistResult>() {
//            @Override
//            protected void postResult(InUserBlacklistResult result) {
//                RxBus.getInstance().post(result);
//            }
//        });
//    }

    //消息发送成功后回执
    private void getMarkChatToUser() {
        Call<MarkChatToUserResult> call = getSpaService().markChatToUser(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId(), "");
        call.enqueue(new TokenCheckedCallback<MarkChatToUserResult>() {
            @Override
            protected void postResult(MarkChatToUserResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取聊天显示内容列表
    private void getCategoryList() {
        Call<CategoryListResult> call = getSpaService().categoryList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<CategoryListResult>() {
            @Override
            protected void postResult(CategoryListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //各营销活动列表
    private void getTechMarketingList() {
        Call<MarketingListResult> call = getSpaService().techMarketingList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<MarketingListResult>() {
            @Override
            protected void postResult(MarketingListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //保存技师海报
    private void doSaveTechPoster(Map<String, String> params) {
        Call<SaveTechPosterResult> call = getSpaService().techPosterSave(LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_POSTER_CLUB_NAME), params.get(RequestConstant.KEY_POSTER_ID),
                params.get(RequestConstant.KEY_POSTER_IMAGE_ID), params.get(RequestConstant.KEY_POSTER_NAME), params.get(RequestConstant.KEY_POSTER_STYLE), params.get(RequestConstant.KEY_POSTER_SUB_TITLE),
                params.get(RequestConstant.KEY_POSTER_TECH_NO), params.get(RequestConstant.KEY_POSTER_TITLE));
        call.enqueue(new TokenCheckedCallback<SaveTechPosterResult>() {
            @Override
            protected void postResult(SaveTechPosterResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }


    //删除技师海报
    private void doDeleteTechPoster(String param) {
        Call<DeleteTechPosterResult> call = getSpaService().techPosterDelete(LoginTechnician.getInstance().getToken(), param);

        call.enqueue(new TokenCheckedCallback<DeleteTechPosterResult>() {
            @Override
            protected void postResult(DeleteTechPosterResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //上传技师海报图片
    private void doUploadTechPosterImage(Map<String, String> params) {
        Call<UploadTechPosterImageResult> call = getSpaService().techPosterImageUpload(LoginTechnician.getInstance().getToken(), params.get(RequestConstant.KEY_POSTER_IMAGE_CATEGORY),
                params.get(RequestConstant.KEY_POSTER_IMAGE_IMG_FILE));
        call.enqueue(new TokenCheckedCallback<UploadTechPosterImageResult>() {
            @Override
            protected void postResult(UploadTechPosterImageResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取技师海报详情
    private void getTechPosterDetail(String param) {
        Call<TechPosterDetailResult> call = getSpaService().techPosterDetail(LoginTechnician.getInstance().getToken(), param);
        call.enqueue(new TokenCheckedCallback<TechPosterDetailResult>() {
            @Override
            protected void postResult(TechPosterDetailResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //获取技师海报列表
    public void getTechPosterList() {
        Call<TechPosterListResult> call = getSpaService().techPosterList(LoginTechnician.getInstance().getToken());
        call.enqueue(new TokenCheckedCallback<TechPosterListResult>() {
            @Override
            protected void postResult(TechPosterListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * @param param：订单状态
     */
    //获取技师订单数
    public void getTechOrderCount(String param) {
        Call<OrderCountResult> call = getSpaService().getOrderCount(LoginTechnician.getInstance().getToken(), param);
        call.enqueue(new TokenCheckedCallback<OrderCountResult>() {
            @Override
            protected void postResult(OrderCountResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //修改加入会所信息
    public void techAuditModify(Map<String, String> params) {
        Call<AuditModifyResult> call = getSpaService().techAuditModify(SharedPreferenceHelper.getUserToken(),
                params.get(RequestConstant.KEY_ROLE_CODE), params.get(RequestConstant.KEY_SPARE_TECH_ID));
        call.enqueue(new TokenCheckedCallback<AuditModifyResult>() {
            @Override
            protected void postResult(AuditModifyResult result) {
                result.role = params.get(RequestConstant.KEY_ROLE_CODE);
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                AuditModifyResult result = new AuditModifyResult();
                result.msg = errorMsg;
                result.statusCode = 400;
                RxBus.getInstance().post(result);
            }
        });
    }

    //技师申请加入会所后取消
    public void techAuditCancel(Map<String, String> params) {
        Call<AuditCancelResult> call = getSpaService().techAuditCancel(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_PASSWORD));
        call.enqueue(new TokenCheckedCallback<AuditCancelResult>() {
            @Override
            protected void postResult(AuditCancelResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                AuditCancelResult cancelResult = new AuditCancelResult();
                cancelResult.statusCode = 400;
                cancelResult.msg = errorMsg;
                RxBus.getInstance().post(cancelResult);
            }
        });
    }

    //申请被会所拒绝后确认
    public void techAuditConfirm() {
        Call<AuditConfirmResult> call = getSpaService().techAuditConfirm(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<AuditConfirmResult>() {
            @Override
            protected void postResult(AuditConfirmResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    //统计技师分享次数
    public void techShareCountUpdate(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().updateTechShareCount(SharedPreferenceHelper.getUserToken(), params.get(RequestConstant.KEY_ACT_ID),
                params.get(RequestConstant.KEY_ACT_TYPE));
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {

            }
        });
    }

    //获取提现规则

    public void getWithDrawRule() {
        Call<WithdrawRuleResult> call = getSpaService().getWithdrawRule(SharedPreferenceHelper.getUserToken(), SharedPreferenceHelper.getUserClubId());
        call.enqueue(new TokenCheckedCallback<WithdrawRuleResult>() {
            @Override
            protected void postResult(WithdrawRuleResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    public void getClubGroupBuyList() {
        Call<GroupBuyListResult> call = getSpaService().getGroupBuyList(SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<GroupBuyListResult>() {
            @Override
            protected void postResult(GroupBuyListResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }


}
