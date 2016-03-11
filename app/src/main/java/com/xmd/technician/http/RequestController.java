package com.xmd.technician.http;

import android.os.Message;


import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.http.gson.LoginResult;
import com.xmd.technician.http.gson.LogoutResult;
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
}
