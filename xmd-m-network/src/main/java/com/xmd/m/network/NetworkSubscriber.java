package com.xmd.m.network;

import com.shidou.commonlibrary.helper.XLogger;

import org.greenrobot.eventbus.EventBus;

import retrofit2.HttpException;
import rx.Subscriber;

/**
 * Created by heyangya on 16-8-23.
 * 统一检查token是否失效，失效则发送EventTokenExpired事件
 */

public abstract class NetworkSubscriber<T> extends Subscriber<T> {
    @Override
    public final void onCompleted() {

    }

    @Override
    public final void onError(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            if (httpException.code() == 401) {
                EventBus.getDefault().post(new EventTokenExpired("会话失效，用户可能在其他地方登录"));
            }
        }
        XLogger.e("Network error:" + e.getMessage());
        onCallbackError(new NetworkException(e.getMessage()));
    }

    @Override
    public final void onNext(T result) {
        if (!(result instanceof BaseBean)) {
            onCallbackError(new ServerException("数据格式错误，数据：" + result, 400));
            return;
        }
        try {
            BaseBean r = (BaseBean) result;
            if (r.getStatusCode() == 0 || (r.getStatusCode() >= 200 && r.getStatusCode() <= 299)) {
                onCallbackSuccess(result);
                return;
            }
            if (r.getStatusCode() == 401) {
                EventBus.getDefault().post(new EventTokenExpired("server return 401: " + r.getStatusCode()));
                onCallbackError(new ServerException(r.getMsg(), 401));
                return;
            }
            onCallbackError(new ServerException(r.getMsg(), r.getStatusCode()));
        } catch (Exception e) {
            e.printStackTrace();
            onCallbackError(new ServerException("网络结果处理错误", 400));
        }
    }

    //处理成功结果
    public abstract void onCallbackSuccess(T result);

    //处理失败结果
    public abstract void onCallbackError(Throwable e);
}
