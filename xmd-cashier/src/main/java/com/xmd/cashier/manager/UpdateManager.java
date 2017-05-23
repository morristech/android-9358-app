package com.xmd.cashier.manager;

import android.content.Context;

import com.shidou.update.IUpdater;
import com.shidou.update.UpdateConfig;
import com.shidou.update.UpdaterController;
import com.xmd.cashier.MainApplication;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.net.NetworkSubscriber;
import com.xmd.cashier.dal.net.UpdateRetrofit;
import com.xmd.cashier.dal.net.response.CheckUpdateResult;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heyangya on 16-10-24.
 */

public class UpdateManager {
    private static UpdateManager instance = new UpdateManager();
    private Subscription mSubscription;

    private UpdateManager() {

    }

    public static UpdateManager getInstance() {
        return instance;
    }

    public void checkUpdate(final Context context, String appId, String userId, final Callback<Void> callback) {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = UpdateRetrofit
                .getService()
                .getAppUpdateConfig(appId, userId, String.valueOf(Utils.getAppVersionCode()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetworkSubscriber<CheckUpdateResult>() {
                    @Override
                    public void onCallbackSuccess(CheckUpdateResult result) {
                        if (!result.update) {
                            callback.onSuccess(null);
                            return;
                        }
                        UpdaterController updaterController = new UpdaterController(context);
                        UpdateConfig updateConfig = new UpdateConfig();
                        updateConfig.type = result.config.type;
                        updateConfig.info = result.config.info;
                        updateConfig.md5 = result.config.md5;
                        updateConfig.size = result.config.size;
                        updateConfig.url = result.config.url;
                        updateConfig.version = result.config.version;
                        updaterController.startUpdate(updateConfig, new IUpdater.UpdateListener() {
                            @Override
                            public void onUpdateError(UpdateConfig updateConfig, int i, String s) {
                                if (updateConfig.isForceUpdate()) {
                                    MainApplication.getInstance().exitApplication();
                                } else {
                                    callback.onError(s);
                                }
                            }

                            @Override
                            public void onUserCancel(UpdateConfig updateConfig) {
                                if (updateConfig.isForceUpdate()) {
                                    MainApplication.getInstance().exitApplication();
                                } else {
                                    callback.onError("用户取消");
                                }
                            }

                            @Override
                            public void onUpdateComplete(UpdateConfig updateConfig) {
                                if (updateConfig.isForceUpdate()) {
                                    MainApplication.getInstance().exitApplication();
                                } else {
                                    callback.onSuccess(null);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCallbackError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }
                });

    }

    public void cancel() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }
}
