package com.xmd.manager.service;

import android.text.TextUtils;

import com.shidou.commonlibrary.helper.XLogger;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.m.network.EventTokenExpired;
import com.xmd.manager.common.Logger;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.BaseResult;

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
            if (200 == result.statusCode) {
                postResult(result);
            } else if (result.statusCode == RequestConstant.RESP_TOKEN_EXPIRED) {
                EventBus.getDefault().post(new EventTokenExpired("账号在其他地方登录，请重新登录"));
            } else {
                postError(result.msg);
            }
        } else if (204 == response.code()) {
            //No Content But OK
            postResult(response.body());
        } else if (401 == response.code()) {
            EventBus.getDefault().post(new EventTokenExpired("账号在其他地方登录，请重新登录"));
        } else {
            try {
                String errorStr = response.errorBody().string();
                postError(errorStr);
            } catch (Exception t) {
                Logger.v(t.getLocalizedMessage());
            }
        }
    }

    protected void postResult(T result) {
        RxBus.getInstance().post(result);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        postError(t.getLocalizedMessage());
    }

    protected void postError(String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg)) {
            XLogger.d("9358",errorMsg);
           XToast.show(ResourceUtils.getString(com.xmd.app.R.string.service_exception));
        }

        RxBus.getInstance().post(new Throwable(errorMsg));
    }
}
