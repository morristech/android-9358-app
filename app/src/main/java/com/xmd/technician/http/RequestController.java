package com.xmd.technician.http;

import android.os.Message;
import android.text.TextUtils;


import com.xmd.technician.AppConfig;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.common.DESede;
import com.xmd.technician.common.Logger;
import com.xmd.technician.http.gson.AlbumResult;
import com.xmd.technician.http.gson.AvatarResult;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.CommentOrderRedPkResutlt;
import com.xmd.technician.http.gson.CommentResult;
import com.xmd.technician.http.gson.FeedbackResult;
import com.xmd.technician.http.gson.InviteCodeResult;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.LogoutResult;
import com.xmd.technician.http.gson.ModifyPasswordResult;
import com.xmd.technician.http.gson.OrderListResult;
import com.xmd.technician.http.gson.OrderManageResult;
import com.xmd.technician.http.gson.ServiceResult;
import com.xmd.technician.http.gson.TechCurrentResult;
import com.xmd.technician.http.gson.TechEditResult;
import com.xmd.technician.http.gson.TokenExpiredResult;
import com.xmd.technician.http.gson.UpdateWorkStatusResult;
import com.xmd.technician.http.gson.UpdateWorkTimeResult;
import com.xmd.technician.http.gson.WorkTimeResult;
import com.xmd.technician.msgctrl.AbstractController;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.RxBus;

import org.w3c.dom.Text;

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
            case MsgDef.MSG_DEF_MANAGE_ORDER:
                doManageOrder((Map<String, String>) msg.obj);
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
            case MsgDef.MSG_DEF_SUBMIT_FEEDBACK:
                doSubmitFeedback((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_UPDATE_WORK_STATUS:
                updateWorkStatus((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_SERVICE_ITEM_LIST:
                getServiceList((Map<String, String>) msg.obj);
                break;
            case MsgDef.MSG_DEF_GET_NEW_ORDER_COUNT:
                getCommentOrderRedPkCount();
                break;
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

    //不能正常工作
    private void register(Map<String, String> params){
        Call<BaseResult> call = getSpaService().register(params.get(RequestConstant.KEY_MOBILE),
                params.get(RequestConstant.KEY_PASSWORD),
                params.get(RequestConstant.KEY_ICODE), RequestConstant.SESSION_TYPE);

        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                BaseResult result = response.body();
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
            public void onFailure(Call<BaseResult> call, Throwable t) {
                RxBus.getInstance().post(t);
            }
        });
    }

    //不能正常工作
    private void modifyPassWord(Map<String, String> params){
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

    private void getCommentList(Map<String, String> params){
        Call<CommentResult> call;
        if(params != null){
            call = getSpaService().getCommentList(params.get(RequestConstant.KEY_PAGE_NUMBER),
                    params.get(RequestConstant.KEY_PAGE_SIZE),
                    params.get(RequestConstant.KEY_SORT_TYPE),
                    RequestConstant.SESSION_TYPE,
                    SharedPreferenceHelper.getUserToken());
        }else {
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
                RxBus.getInstance().post(new OrderManageResult());
            }

            @Override
            protected void postError(String errorMsg) {
                Logger.v("manageOrder: " + errorMsg);
            }
        });

    }

    private void getICode(Map<String, String> params){
        Call<BaseResult> call = getSpaService().getICode(params.get(RequestConstant.KEY_MOBILE));

        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(result);
            }
        });

    }

    private void submitInviteCode(Map<String, String> params){
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

    private void getTechEditInfo(){
        Call<TechEditResult> call = getSpaService().getTechEditInfo(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<TechEditResult>() {
            @Override
            protected void postResult(TechEditResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getTechCurrentInfo(){
        Call<TechCurrentResult> call = getSpaService().getTechCurrentInfo(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<TechCurrentResult>() {
            @Override
            protected void postResult(TechCurrentResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getWorkTime(){
        Call<WorkTimeResult> call = getSpaService().getWorkTime(SharedPreferenceHelper.getUserToken(),RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<WorkTimeResult>() {
            @Override
            protected void postResult(WorkTimeResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void updateWorkTime(Map<String, String> params){
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

    private void updateWorkStatus(Map<String, String> params){
        Call<BaseResult> call = getSpaService().updateWorkStatus(params.get(RequestConstant.KEY_STATUS),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);
        call.enqueue(new TokenCheckedCallback<BaseResult>() {
            @Override
            protected void postResult(BaseResult result) {
                RxBus.getInstance().post(new UpdateWorkStatusResult());
            }
        });
    }

    private void uploadAvatar(Map<String, String> params){
        Call<AvatarResult> call = getSpaService().uploadAvatar(params.get(RequestConstant.KEY_IMG_FILE),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AvatarResult>() {
            @Override
            protected void postResult(AvatarResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void uploadAlbum(Map<String, String> params){
        Call<AlbumResult> call = getSpaService().uploadAlbum(params.get(RequestConstant.KEY_IMG_FILE),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AlbumResult>() {
            @Override
            protected void postResult(AlbumResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void deleteAlbum(Map<String, String> params){
        Call<AlbumResult> call = getSpaService().deleteAlbum(params.get(RequestConstant.KEY_ID),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<AlbumResult>() {
            @Override
            protected void postResult(AlbumResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getServiceList(Map<String, String> params){
        Call<ServiceResult> call = getSpaService().getServiceList(params.get(RequestConstant.KEY_TIME_STAMP),
                SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<ServiceResult>() {
            @Override
            protected void postResult(ServiceResult result) {
                RxBus.getInstance().post(result);
            }
        });
    }

    private void getCommentOrderRedPkCount(){
        Call<CommentOrderRedPkResutlt> call = getSpaService().getCommentOrderRedPkCount(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE);

        call.enqueue(new TokenCheckedCallback<CommentOrderRedPkResutlt>() {
            @Override
            protected void postResult(CommentOrderRedPkResutlt result) {
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

        //String decryptPwd = appID + appSecret + userId + appKey + masterSecret + clientId;
        StringBuilder sb = new StringBuilder();
        sb.append(AppConfig.sGetuiAppId)
                .append(AppConfig.sGetuiAppSecret)
                .append(userId)
                .append(AppConfig.sGetuiAppKey)
                .append(AppConfig.GETUI_MASTER_SECRET)
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
        Call<BaseResult> call = getSpaService().unbindGetuiClientId(SharedPreferenceHelper.getUserToken(), RequestConstant.SESSION_TYPE, AppConfig.sClientId);
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

    private void doHandleTokenExpired(String errorMsg) {
        RxBus.getInstance().post(new TokenExpiredResult(errorMsg));
    }
}
