package com.xmd.m.network;

import com.shidou.commonlibrary.helper.XLogger;

import org.greenrobot.eventbus.EventBus;

import java.io.EOFException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
        XLogger.e("Network error:" + e.getMessage());
        e.printStackTrace();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            if (httpException.code() == 401) {
                EventBus.getDefault().post(new EventTokenExpired("会话失效，用户可能在其他地方登录"));
            }
            onCallbackError(new NetworkException("请求失败(" + httpException.code() + ")，请联系系统管理员"));
        } else if (e instanceof SocketTimeoutException) {   //超时
            onCallbackError(new NetworkException("服务器请求超时，请检查网络后重试"));
        } else if (e instanceof ConnectException) { //服务器拒绝等
            onCallbackError(new NetworkException("服务器连接异常，请重试"));
        } else if (e instanceof UnknownHostException) { //无法解析主机地址
            onCallbackError(new NetworkException("无法连接服务器，请检查网络后重试"));
        } else if (e instanceof EOFException) { //底层网络库异常等
            onCallbackError(new NetworkException("网络请求异常，请检查网络后重试"));
        } else {    //其他异常情况
            onCallbackError(new NetworkException("服务器请求异常，请重试"));
        }
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
            XLogger.e("Network next exception:" + e.getMessage());
            e.printStackTrace();
            onCallbackError(new ServerException("网络结果处理错误", 400));
        }
    }

    //处理成功结果
    public abstract void onCallbackSuccess(T result);

    //处理失败结果
    public abstract void onCallbackError(Throwable e);
}
