package com.xmd.technician.http;


import android.text.TextUtils;

import com.xmd.m.network.EventTokenExpired;
import com.xmd.technician.R;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.gson.BaseResult;
import com.xmd.technician.http.gson.QuitClubResult;
import com.xmd.technician.msgctrl.RxBus;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sdcm on 16-1-11.
 */
public abstract class TokenCheckedCallback<T extends BaseResult> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (200 == response.code() && response.body() != null) {
            //OK
            T result = response.body();
            if (result.statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                //token 失效
                EventBus.getDefault().post(new EventTokenExpired(result.msg));
                RxBus.getInstance().post(new EventTokenExpired(result.msg));
            } else if (result.statusCode == RequestConstant.RESP_ERROR && !(result instanceof QuitClubResult)) {
                postError(result.msg);
            } else {
                postResult(result);
            }
        } else if (204 == response.code()) {
            //No Content But OK
            postResult(response.body());
        } else if (401 == response.code()) {
            //token 失效
            EventBus.getDefault().post(new EventTokenExpired(ResourceUtils.getString(R.string.token_expired_error_msg)));
            RxBus.getInstance().post(new EventTokenExpired(ResourceUtils.getString(R.string.token_expired_error_msg)));
        } else {
            try {
                String errorStr = response.errorBody().string();
                postError(errorStr);
            } catch (Exception t) {
                if (!TextUtils.isEmpty(t.getLocalizedMessage())) {
                    Logger.v(t.getLocalizedMessage());
                }
            }
        }
    }

    protected abstract void postResult(T result);

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        postError(t.getLocalizedMessage());
    }

    protected void postError(String errorMsg) {
        RxBus.getInstance().post(new Throwable(errorMsg));
    }
}
