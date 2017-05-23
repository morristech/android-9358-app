package com.xmd.manager.service;

import com.xmd.manager.R;
import com.xmd.manager.common.Logger;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.response.BaseResult;

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
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TOKEN_EXPIRE, result.msg);
            } else {
                postError(result.msg);
            }
        } else if (204 == response.code()) {
            //No Content But OK
            postResult(response.body());
        } else if (401 == response.code()) {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TOKEN_EXPIRE, ResourceUtils.getString(R.string.token_expired_error_msg));
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
        RxBus.getInstance().post(new Throwable(errorMsg));
    }
}
