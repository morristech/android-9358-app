package com.xmd.chat;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.xmd.chat.beans.DiceGameSetting;
import com.xmd.chat.beans.FastReplySetting;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;

/**
 * Created by mo on 17-7-22.
 */

public class SettingManager {
    private static final SettingManager ourInstance = new SettingManager();

    public static SettingManager getInstance() {
        return ourInstance;
    }

    private FastReplySetting fastReplySetting;

    private long diceExpireTime;

    private SettingManager() {
        fastReplySetting = (FastReplySetting) DiskCacheManager.getInstance().get(ChatConstants.CACHE_CHAT_FAST_REPLY_SETTING);
        diceExpireTime = XmdChat.getInstance().getSp().getLong(ChatConstants.SP_GAME_DICE_EXPIRE_TIME, 3600 * 1000);
    }

    public FastReplySetting getFastReplySetting() {
        return fastReplySetting;
    }

    //加载快速回复
    public void loadFastReply(final Callback<FastReplySetting> callback) {
        if (callback != null && fastReplySetting != null) {
            callback.onResponse(fastReplySetting, null);
            return;
        }
        Observable<BaseBean<FastReplySetting>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .getFastReplySetting();
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<FastReplySetting>>() {
            @Override
            public void onCallbackSuccess(BaseBean<FastReplySetting> result) {
                fastReplySetting = result.getRespData();
                DiskCacheManager.getInstance().put(ChatConstants.CACHE_CHAT_FAST_REPLY_SETTING, fastReplySetting);
                if (callback != null) {
                    callback.onResponse(fastReplySetting, null);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                if (callback != null) {
                    callback.onResponse(null, e);
                }
            }
        });
    }

    //保存快捷回复
    public void saveFastReply(final FastReplySetting setting, final Callback<Void> callback) {
        Observable<BaseBean> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .setFastReplySetting(setting);
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean>() {
            @Override
            public void onCallbackSuccess(BaseBean result) {
                fastReplySetting = setting;
                DiskCacheManager.getInstance().put(ChatConstants.CACHE_CHAT_FAST_REPLY_SETTING, fastReplySetting);
                callback.onResponse(null, null);
            }

            @Override
            public void onCallbackError(Throwable e) {
                callback.onResponse(null, e);
            }
        });
    }

    public long getDiceExpireTime() {
        return diceExpireTime;
    }

    //加载骰子游戏超时时间
    public void loadDiceExpireTime() {
        Observable<BaseBean<DiceGameSetting>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .getGameSetting();
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<DiceGameSetting>>() {
            @Override
            public void onCallbackSuccess(BaseBean<DiceGameSetting> result) {
                diceExpireTime = result.getRespData().diceGameExpireTime;
                XmdChat.getInstance().getSp().edit().putLong(ChatConstants.SP_GAME_DICE_EXPIRE_TIME, diceExpireTime).apply();
            }

            @Override
            public void onCallbackError(Throwable e) {

            }
        });
    }
}
