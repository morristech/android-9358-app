package com.xmd.m.network;

import android.content.Context;
import android.content.SharedPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 17-5-25.
 * 基础网络模块
 */

public class XmdNetwork {
    private static XmdNetwork instance = new XmdNetwork();
    private boolean init;
    private String mServer;
    private SharedPreferences sharedPreferences;
    private final static String TOKEN = "token";

    private XmdNetwork() {

    }

    public static XmdNetwork getInstance() {
        return instance;
    }

    /**
     * 初始化模块
     *
     * @param context applicationContext
     * @param ua      user-agent
     * @param server  http://host:port
     */
    public void init(Context context, String ua, String server) {
        if (!init) {
            init = true;
            sharedPreferences = context.getSharedPreferences("xmd-network", Context.MODE_PRIVATE);
            mServer = server;
            OkHttpUtil.init(context.getFilesDir() + File.separator + "xmd-network", 10 * 1024 * 1024, 10000, 10000, 10000);
            OkHttpUtil.getInstance().setCommonHeader("User-Agent", ua);
            RetrofitFactory.setBaseUrl(server);
            OkHttpUtil.getInstance().setCommonHeader(TOKEN, sharedPreferences.getString(TOKEN, null));
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 调试模式时，会打印网络日志
     */
    public void setDebug(boolean debug) {
        OkHttpUtil.getInstance().setLog(debug);
    }


    /**
     * 切换服务器环境时调用
     *
     * @param server 服务器地址 : http://xxx
     */
    public void changeServer(String server) {
        if (mServer != null && !mServer.equals(server)) {
            mServer = server;
            RetrofitFactory.clear();
        } else {
            mServer = server;
        }
        RetrofitFactory.setBaseUrl(mServer);
    }

    /**
     * 获取retrofit服务
     *
     * @param serviceClass 定义了网张接口的interface
     */
    public <T> T getService(Class<T> serviceClass) {
        return RetrofitFactory.getService(serviceClass);
    }

    /**
     * 发送网络请求
     *
     * @param observable 使用getService返回接口调用产生
     * @param subscriber 结果回调. 可以为null
     * @return 返回调用观察对像，可用于取消网络请求操作
     */
    public <T> Subscription request(Observable<T> observable, NetworkSubscriber<T> subscriber) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    // 特殊处理
    public <T> Subscription request(Observable<T> observable, Subscriber<T> subscriber) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public <T> Subscription request(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<T>() {
                    @Override
                    public void onCallbackSuccess(T result) {

                    }

                    @Override
                    public void onCallbackError(Throwable e) {

                    }
                });
    }

    /**
     * 同步网络请求
     *
     * @param subscriber 结果回调
     * @return 返回调用观察对像，可用于取消网络请求操作
     */
    public <T> void requestSync(Call<T> call, NetworkSubscriber<T> subscriber) {
        try {
            Response<T> response = call.execute();
            subscriber.onNext(response.body());
        } catch (Exception e) {
            if (e instanceof IOException) {
                subscriber.onCallbackError(new NetworkException(e.getMessage()));
            } else {
                subscriber.onCallbackError(new ServerException(e.getMessage(), 400));
            }
        }
    }

    /**
     * 设置token到header,当有EventTokenExpired事件时，自动清除token
     */
    public void setToken(String token) {
        OkHttpUtil.getInstance().setCommonHeader(TOKEN, token);
    }

    @Subscribe
    public void onTokenExpired(EventTokenExpired event) {
        OkHttpUtil.getInstance().removeCommonHeader(TOKEN);
        sharedPreferences.edit().remove(TOKEN).apply();
    }

    public void setHeader(String key, String value) {
        OkHttpUtil.getInstance().setCommonHeader(key, value);
    }

    public void setRequestPreprocess(OkHttpUtil.RequestPreprocess requestPreprocess) {
        OkHttpUtil.getInstance().setRequestPreprocess(requestPreprocess);
    }
}
