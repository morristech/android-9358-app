package com.xmd.technician.http;

import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;


import com.hyphenate.chat.EMClient;
import com.xmd.technician.AppConfig;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.AddOrEditResult;
import com.xmd.technician.bean.ClubContactResult;
import com.xmd.technician.bean.CustomerDetailResult;
import com.xmd.technician.bean.CustomerListResult;
import com.xmd.technician.bean.DeleteContactResult;
import com.xmd.technician.bean.IsBindResult;
import com.xmd.technician.bean.ManagerDetailResult;
import com.xmd.technician.bean.TechDetailResult;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.DESede;
import com.xmd.technician.common.Logger;
import com.xmd.technician.http.gson.AccountMoneyResult;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AppUpdateConfigResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.CommentOrderRedPkResutlt;
import com.xmd.technician.http.gson.CommentResult;
import com.xmd.technician.http.gson.ConsumeDetailResult;
import com.xmd.technician.http.gson.CouponInfoResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.http.gson.FeedbackResult;
import com.xmd.technician.http.gson.InviteCodeResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.LogoutResult;
import com.xmd.technician.http.gson.ModifyPasswordResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.PaidCouponUserDetailResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.http.gson.RegisterResult;
import com.xmd.technician.http.gson.ResetPasswordResult;
import com.xmd.technician.http.gson.ServiceResult;
import com.xmd.technician.http.gson.TechCurrentResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.TokenExpiredResult;
import com.xmd.technician.http.gson.UpdateServiceResult;
import com.xmd.technician.http.gson.UpdateTechInfoResult;
import com.xmd.technician.http.gson.UpdateWorkStatusResult;
import com.xmd.technician.http.gson.UpdateWorkTimeResult;
import com.xmd.technician.http.gson.WorkTimeResult;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.RxBus;

import java.io.IOException;
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
            case MsgDef.MSG_DEF_LOGOUT:
                doLogout();
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
            case MsgDef.MSG_DEF_GET_ORDER_LIST:
                doGetOrderList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_COMMENT_LIST:
                getCommentList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GETUI_BIND_CLIENT_ID:
                doBindGetuiClientId();
                break;
            case MsgDef.MSG_DEF_GETUI_UNBIND_CLIENT_ID:
                doUnbindGetuiClientId();
                break;
            case MsgDef.MSG_DEF_GET_ICODE:
                getICode((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_SUBMIT_INVITE_CODE:
                submitInviteCode((Map<String, String>) msg.obj);
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
            case MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT:
                getCommentOrderRedPkCount();
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
                quitClub();
                break;
            case MsgDef.MSG_DEF_GET_APP_UPDATE_CONFIG:
                doGetAppUpdateConfig((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CUSTOMER_LIST:
                doGetCustomerList();
                break;
            case MsgDef.MSG_DEF_GET_CUSTOMER_INFO_DETAIL:
                doGetCustomerInfoDetail((Map<String,String>)msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_TECH_INFO_DETAIL:
                doGetTechInfoDetail((Map<String,String>)msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_MANAGER_INFO_DETAIL:
                doGetManagerInfoDetail((Map<String,String>)msg.obj);
                break;
            case MsgDef.MSG_DEF_ADD_OR_EDIT_CUSTOMER:
             doAddOrEditCustomer((Map<String,String>)msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_CLUB_LIST:
                doGetClubList();
                break;
            case MsgDef.MSG_DEF_DELETE_CONTACT:
               doDeleteContact((Map<String,String>)msg.obj);
               break;
//            case MsgDef.MSG_DEF_DO_DRAW_MONEY:
//                doDrawMoney((Map<String,String>)msg.obj);
//                break;
//            case MsgDef.MSG_DEF_BIND_WX:
//                doBindWX((Map<String,String>)msg.obj);
//                break;
//            case MsgDef.MSG_DEF_IS_BIND_WX:
//                getIsBindWXR();
//                break;



        }

        return true;
    }

    /**************************************** Called By Activities  *****************************/

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
     * Logout Button Clicked in PopupMoreWindow
     */
    private void doLogout() {
        UserProfileProvider.getInstance().reset();
        EMClient.getInstance().logout(true);
        Call<LogoutResult> call = getSpaService().logout(SharedPreferenceHelper.getUserToken(),
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
        Call<RegisterResult> call = getSpaService().register(params.get(RequestConstant.KEY_MOBILE),
                params.get(RequestConstant.KEY_PASSWORD), params.get(RequestConstant.KEY_ICODE),
                params.get(RequestConstant.KEY_CLUB_CODE), params.get(RequestConstant.KEY_LOGIN_CHANEL),
                RequestConstant.SESSION_TYPE, RequestConstant.SESSION_TYPE);

        call.enqueue(new Callback<RegisterResult>() {
            @Override
            public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {
                RegisterResult result = response.body();
                if (result != null) {
                    RxBus.getInstance().post(result);
                } else {
                    try {
                        RxBus.getInstance().post(new Throwable(response.errorBody().string()));
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
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

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
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);


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
                    SharedPreferenceHelper.getUserToken());
        } else {
            call = getSpaService().getCommentList(null, null, null,
                    RequestConstant.SESSION_TYPE,
                    SharedPreferenceHelper.getUserToken());
        }

        call.enqueue(new TokenCheckedCallback<CommentResult>() {
            @Override
            protected void postResult(CommentResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     * Retrieve the order list from the backend services
     *
     * @param params
     */
    private void doGetOrderList(Map<String, String> params) {
        Call<OrderListResult> call = getSpaService().getOrderList(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE, params.get(RequestConstant.KEY_FILTER_ORDER),
                params.get(RequestConstant.KEY_PAGE), params.get(RequestConstant.KEY_PAGE_SIZE));

        call.enqueue(new TokenCheckedCallback<OrderListResult>() {
            @Override
            protected void postResult(OrderListResult result) {
                RxBus.getInstance().post(result);
            }

            @Override
            protected void postError(String errorMsg) {
                OrderListResult result = new OrderListResult();
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
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

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

    private void submitInviteCode(Map<String, String> params) {
        Call<InviteCodeResult> call = getSpaService().submitInviteCode(SharedPreferenceHelper.getUserToken(),
                RequestConstant.SESSION_TYPE,
                params.get(RequestConstant.KEY_INVITE_CODE));

        call.enqueue(new TokenCheckedCallback<InviteCodeResult>() {
            @Override
            protected void postResult(InviteCodeResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getTechEditInfo() {
        Call<TechEditResult> call = getSpaService().getTechEditInfo(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<TechEditResult>() {
            @Override
            protected void postResult(TechEditResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getTechCurrentInfo() {
        Call<TechCurrentResult> call = getSpaService().getTechCurrentInfo(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<TechCurrentResult>() {
            @Override
            protected void postResult(TechCurrentResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void updateTechInfo(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().updateTechInfo(params.get(RequestConstant.KEY_USER),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new UpdateTechInfoResult());
            }
        });
    }

    private void getWorkTime() {
        Call<WorkTimeResult> call = getSpaService().getWorkTime(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

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
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new UpdateWorkTimeResult());
            }
        });
    }

    private void updateWorkStatus(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().updateWorkStatus(params.get(RequestConstant.KEY_STATUS),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new UpdateWorkStatusResult());
            }
        });
    }

    private void uploadAvatar(Map<String, String> params) {
        Call<AvatarResult> call = getSpaService().uploadAvatar(params.get(RequestConstant.KEY_IMG_FILE),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AvatarResult>() {
            @Override
            protected void postResult(AvatarResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void uploadAlbum(Map<String, String> params) {
        Call<AlbumResult> call = getSpaService().uploadAlbum(params.get(RequestConstant.KEY_IMG_FILE),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AlbumResult>() {
            @Override
            protected void postResult(AlbumResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void deleteAlbum(Map<String, String> params) {
        Call<AlbumResult> call = getSpaService().deleteAlbum(params.get(RequestConstant.KEY_ID),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AlbumResult>() {
            @Override
            protected void postResult(AlbumResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void sortAlbum(Map<String, String> params) {
        Call<AlbumResult> call = getSpaService().sortAlbum(params.get(RequestConstant.KEY_IDS),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AlbumResult>() {
            @Override
            protected void postResult(AlbumResult result) {
                RxBus.getInstance().post(new AlbumResult());
            }
        });
    }

    private void getServiceList() {
        Call<ServiceResult> call = getSpaService().getServiceList(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<ServiceResult>() {
            @Override
            protected void postResult(ServiceResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void updateServiceList(Map<String, String> params) {
        Call<BaseResult> call = getSpaService().updateServiceList(params.get(RequestConstant.KEY_IDS),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new UpdateServiceResult());
            }
        });
    }

    private void getCommentOrderRedPkCount() {
        Call<CommentOrderRedPkResutlt> call = getSpaService().getCommentOrderRedPkCount(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<CommentOrderRedPkResutlt>() {
            @Override
            protected void postResult(CommentOrderRedPkResutlt result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getAccountMoney() {
        Call<AccountMoneyResult> call = getSpaService().getAccountMoney(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

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
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<ConsumeDetailResult>() {
            @Override
            protected void postResult(ConsumeDetailResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getCouponList() {
        Call<CouponListResult> call = getSpaService().getCouponList(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

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
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, actId);

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
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

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
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new FeedbackResult());
            }
        });
    }

    private void quitClub() {
        Call<QuitClubResult> call = getSpaService().quitClub(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<QuitClubResult>() {
            @Override
            protected void postResult(QuitClubResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doCouponShareEventCount(String actId) {
        Call<BaseResult> call = getSpaService().doCouponShareEventCount(actId, RequestConstant.USER_TYPE_TECH,
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void doBindGetuiClientId() {

        if (TextUtils.isEmpty(SharedPreferenceHelper.getUserToken())) {
            return;
        }

        /*if (!SettingFlags.getBoolean(SettingFlags.ORDER_NOTIFIATION_ON)) {
            return;
        }*/

        String clientId = AppConfig.sClientId;

        //由于之前获取过clientid，并将其存放在shared_preferences中了，而SDK在获取client_id的过程中可能存在延迟，
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
    private void doUnbindGetuiClientId() {
        Logger.v("start unbind client id");
        Call<BaseResult> call = getSpaService().unbindGetuiClientId(RequestConstant.USER_TYPE_TECH,
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, AppConfig.sClientId);
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
     * @param params
     */
    private void doAddOrEditCustomer(Map<String,String> params){
        if(TextUtils.isEmpty(params.get(RequestConstant.KEY_ID))){
            Call<BaseResult> call = getSpaService().addCustomer(RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken(),
                    params.get(RequestConstant.KEY_PHONE_NUMBER),params.get(RequestConstant.KEY_REMARK),params.get(RequestConstant.KEY_NOTE_NAME));
            call.enqueue(new TokenCheckedCallback<BaseResult>() {
                @Override
                protected void postResult(BaseResult result) {
                    RxBus.getInstance().post(new AddOrEditResult(result.msg,result.statusCode));
                }
            });
        } else{
            Call<BaseResult> call = getSpaService().editCustomer(RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken(),params.get(RequestConstant.KEY_ID),
                    params.get(RequestConstant.KEY_PHONE_NUMBER),params.get(RequestConstant.KEY_REMARK),params.get(RequestConstant.KEY_NOTE_NAME));
            call.enqueue(new TokenCheckedCallback<BaseResult>() {
                @Override
                protected void postResult(BaseResult result) {
                    RxBus.getInstance().post(new AddOrEditResult(result.msg,result.statusCode));
                }
            });

        }
    }
    /**
     *  获取联系人列表
     * @param
     */
    private void  doGetCustomerList(){
        Call<CustomerListResult> call = getSpaService().getCustomerList(RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<CustomerListResult>() {
            @Override
            protected void postResult(CustomerListResult result) {
              RxBus.getInstance().post(result);
            }
        });
    }
    /**
     *  获取联系人详情
     * @param
     */
    private void  doGetCustomerInfoDetail(Map<String,String> params){
        Call<CustomerDetailResult> call = getSpaService().getCustomerInfoDetail(RequestConstant.SESSION_TYPE,params.get(RequestConstant.KEY_ID),SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<CustomerDetailResult>() {
            @Override
            protected void postResult(CustomerDetailResult result) {

                RxBus.getInstance().post(result);
            }
        });
    }
    /**
     *  获取技师联系人详情
     * @param
     */
    private void  doGetTechInfoDetail(Map<String,String> params){
        Call<TechDetailResult> call = getSpaService().getTechInfoDetail(RequestConstant.SESSION_TYPE,params.get(RequestConstant.KEY_ID),SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<TechDetailResult>() {
            @Override
            protected void postResult(TechDetailResult result) {

                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     *  获取管理者联系人详情
     * @param
     */
    private void  doGetManagerInfoDetail(Map<String,String> params){
        Call<ManagerDetailResult> call = getSpaService().getManagerInfoDetail(RequestConstant.SESSION_TYPE,params.get(RequestConstant.KEY_ID),SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<ManagerDetailResult>() {
            @Override
            protected void postResult(ManagerDetailResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    /**
     *  获取俱乐部联系人列表
     * @param
     */
    private void  doGetClubList(){
        Call<ClubContactResult> call = getSpaService().getClubList(RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<ClubContactResult>() {
            @Override
            protected void postResult(ClubContactResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }
    /**
     *  删除联系人
     * @param
     */
    private void  doDeleteContact(Map<String,String> params){
        Call<BaseResult> call = getSpaService().doDeleteContact(RequestConstant.SESSION_TYPE,params.get(RequestConstant.KEY_ID),SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new DeleteContactResult(result.msg,result.statusCode));
            }
        });
    }
    private void doDrawMoney(Map<String,String> params){
        Call<BaseResult> call = getSpaService().doDrawMoney(RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken(),
               params.get(RequestConstant.KEY_TRADE_AMOUNT));
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                Log.i("TAGG","result>>"+result.toString());
            }
        });



    }
    private void doBindWX(Map<String,String> params){
//        Call<BaseResult> call = getSpaService().doBindWX(RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken(),params.get(RequestConstant.KEY_USER_WX_CODE),
//                params.get(RequestConstant.KEY_USER_WX_SCOPE), params.get(RequestConstant.KEY_USER_WX_STATE),params.get(RequestConstant.KEY_USER_WX_WXMP));
        Call<BaseResult> call = getSpaService().doBindWX(RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken(),params.get(RequestConstant.KEY_USER_WX_PAGE_URL),
                params.get(RequestConstant.KEY_USER_WX_SCOPE), params.get(RequestConstant.KEY_USER_WX_STATE),params.get(RequestConstant.KEY_USER_WX_WXMP));
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                result.toString();
            }
        });
    }

    private void doHandleTokenExpired(String errorMsg) {
        RxBus.getInstance().post(new TokenExpiredResult(errorMsg));
    }

    private void  getIsBindWXR(){
        Call<IsBindResult> call = getSpaService().getIsBindWXResult(RequestConstant.SESSION_TYPE,SharedPreferenceHelper.getUserToken());
        call.enqueue(new TokenCheckedCallback<IsBindResult>() {
            @Override
            protected void postResult(IsBindResult result) {
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

}
