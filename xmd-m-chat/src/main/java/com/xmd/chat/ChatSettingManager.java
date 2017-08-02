package com.xmd.chat;

import com.shidou.commonlibrary.Callback;
import com.shidou.commonlibrary.helper.DiskCacheManager;
import com.shidou.commonlibrary.helper.XLogger;
import com.xmd.chat.beans.DiceGameSetting;
import com.xmd.chat.beans.FastReplySetting;
import com.xmd.chat.beans.Location;
import com.xmd.m.network.BaseBean;
import com.xmd.m.network.NetworkSubscriber;
import com.xmd.m.network.XmdNetwork;

import rx.Observable;

/**
 * Created by mo on 17-7-22.
 */

public class ChatSettingManager {
    private static final ChatSettingManager ourInstance = new ChatSettingManager();

    public static ChatSettingManager getInstance() {
        return ourInstance;
    }

    private Location clubLocation;
    private FastReplySetting fastReplySetting;
    private long diceExpireTime;

    private ChatSettingManager() {
        fastReplySetting = (FastReplySetting) DiskCacheManager.getInstance().get(ChatConstants.CACHE_CHAT_FAST_REPLY_SETTING);
        diceExpireTime = XmdChat.getInstance().getSp().getLong(ChatConstants.SP_GAME_DICE_EXPIRE_TIME, 3600 * 1000);
    }

    public FastReplySetting getFastReplySetting() {
        return fastReplySetting;
    }

    public void clear() {
        fastReplySetting = null;
        DiskCacheManager.getInstance().put(ChatConstants.CACHE_CHAT_FAST_REPLY_SETTING, null);
        diceExpireTime = 0;
        XmdChat.getInstance().getSp().edit().remove(ChatConstants.SP_GAME_DICE_EXPIRE_TIME).apply();
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

    public void loadClubLocation(boolean forceNetwork, final Callback<Location> callback) {
        if (!forceNetwork && clubLocation != null) {
            callback.onResponse(clubLocation, null);
            return;
        }
        Observable<BaseBean<Location>> observable = XmdNetwork.getInstance()
                .getService(NetService.class)
                .getClubLocation();
        XmdNetwork.getInstance().request(observable, new NetworkSubscriber<BaseBean<Location>>() {
            @Override
            public void onCallbackSuccess(BaseBean<Location> result) {
                clubLocation = result.getRespData();
                if (callback != null) {
                    callback.onResponse(clubLocation, null);
                }
            }

            @Override
            public void onCallbackError(Throwable e) {
                XLogger.e(XmdChat.TAG, "加载会所位置失败：" + e.getMessage());
                if (callback != null) {
                    callback.onResponse(null, e);
                }
            }
        });
    }
}
