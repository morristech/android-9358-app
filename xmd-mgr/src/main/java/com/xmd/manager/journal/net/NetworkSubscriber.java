package com.xmd.manager.journal.net;


import com.xmd.manager.journal.Callback;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.BaseResult;
import com.xmd.manager.service.response.TokenExpiredResult;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;
import rx.Subscriber;

/**
 * Created by heyangya on 16-8-23.
 */

public class NetworkSubscriber<T> extends Subscriber<T> {
    private Callback mCallback;

    public NetworkSubscriber(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            if (httpException.code() == RequestConstant.RESP_TOKEN_EXPIRED) {
                RxBus.getInstance().post(new TokenExpiredResult(httpException.message()));
            }
            mCallback.onResult(new NetworkException(httpException.code() + "," + e.getLocalizedMessage()), null);
        } else if (e instanceof SocketTimeoutException) {
            mCallback.onResult(new NetworkException("服务器请求超时"), null);
        } else if (e instanceof ConnectException) {
            mCallback.onResult(new NetworkException("服务器请求错误"), null);
        } else {
            mCallback.onResult(new NetworkException(e.getLocalizedMessage()), null);
        }
    }

    @Override
    public void onNext(T result) {
        try {
            BaseResult r = (BaseResult) result;
            switch (r.statusCode) {
                case RequestConstant.RESP_OK:
                case RequestConstant.RESP_OK_NO_CONTENT:
                    mCallback.onResult(null, result);
                    break;
                case RequestConstant.RESP_TOKEN_EXPIRED:
                    //token失效,处理
                    RxBus.getInstance().post(new TokenExpiredResult(r.msg));
                    mCallback.onResult(new NetworkException(r.msg), null);
                    break;
                default:
                    mCallback.onResult(new NetworkException(r.statusCode + "," + r.msg), null);
                    break;
            }
        } catch (Exception e) {
            mCallback.onResult(new NetworkException("未知错误:" + e.getLocalizedMessage()), null);
        }
    }
}
