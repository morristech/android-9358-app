package com.xmd.cashier.dal.net;

import com.xmd.cashier.common.RxBus;
import com.xmd.cashier.dal.net.response.BaseResult;
import com.xmd.cashier.dal.net.response.CheckUpdateResult;
import com.xmd.cashier.dal.net.response.LoginResult;
import com.xmd.cashier.exceptions.NetworkException;
import com.xmd.cashier.exceptions.ServerException;
import com.xmd.cashier.exceptions.TokenExpiredException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by heyangya on 16-8-23.
 */

public abstract class NetworkSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            if (httpException.code() == RequestConstant.RESP_TOKEN_EXPIRED) {
                RxBus.getDefault().post(new TokenExpiredException());
            }
            onCallbackError(new ServerException(e.getLocalizedMessage(), httpException.code()));
        } else if (e instanceof SocketTimeoutException) {
            onCallbackError(new NetworkException("服务器请求超时"));
        } else if (e instanceof ConnectException) {
            onCallbackError(new NetworkException("服务器请求错误"));
        } else {
            onCallbackError(new NetworkException(e.getLocalizedMessage()));
        }
    }

    @Override
    public void onNext(T result) {
        if (result instanceof LoginResult) {
            //由于历史原因，登录接口返回的结果代码为0时表示成功，所以需要特殊处理
            LoginResult loginResult = (LoginResult) result;
            if (loginResult.statusCode == 0 || !RequestConstant.RESP_STATUS_FAIL.equals(loginResult.status)) {
                onCallbackSuccess(result);
            } else {
                onCallbackError(new ServerException(loginResult.message, loginResult.statusCode));
            }
            return;
        }

        if (result instanceof CheckUpdateResult) {
            if (((CheckUpdateResult) result).statusCode == 0) {
                onCallbackSuccess(result);
            } else {
                onCallbackError(new ServerException(((CheckUpdateResult) result).msg, ((CheckUpdateResult) result).statusCode));
            }
            return;
        }

        try {
            BaseResult r = (BaseResult) result;
            switch (r.statusCode) {
                case RequestConstant.RESP_OK:
                case RequestConstant.RESP_OK_NO_CONTENT:
                    onCallbackSuccess(result);
                    break;
                case RequestConstant.RESP_TOKEN_EXPIRED:
                    //token失效,处理
                    onCallbackError(new TokenExpiredException());
                    RxBus.getDefault().post(new TokenExpiredException());
                    break;
                default:
                    onCallbackError(new ServerException(r.msg, r.statusCode));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            onCallbackError(new ServerException("网络结果处理错误", RequestConstant.RESP_EXCEPTION));
        }
    }

    //处理成功结果
    public abstract void onCallbackSuccess(T result);

    //处理失败结果
    public abstract void onCallbackError(Throwable e);
}
